/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "PixelBuffer.h"

#include "Debug.h"
#include "Extensions.h"
#include "Properties.h"
#include "renderstate/RenderState.h"
#include "utils/GLUtils.h"

#include <utils/Log.h>

namespace android {
namespace uirenderer {

///////////////////////////////////////////////////////////////////////////////
// CPU pixel buffer
///////////////////////////////////////////////////////////////////////////////

class CpuPixelBuffer: public PixelBuffer {
public:
    CpuPixelBuffer(GLenum format, uint32_t width, uint32_t height);

    uint8_t* map(AccessMode mode = kAccessMode_ReadWrite) override;

    uint8_t* getMappedPointer() const override;

    void upload(uint32_t x, uint32_t y, uint32_t width, uint32_t height, int offset) override;

protected:
    void unmap() override;

private:
    std::unique_ptr<uint8_t[]> mBuffer;
};

CpuPixelBuffer::CpuPixelBuffer(GLenum format, uint32_t width, uint32_t height)
        : PixelBuffer(format, width, height)
        , mBuffer(new uint8_t[width * height * formatSize(format)]) {
}

uint8_t* CpuPixelBuffer::map(AccessMode mode) {
    if (mAccessMode == kAccessMode_None) {
        mAccessMode = mode;
    }
    return mBuffer.get();
}

void CpuPixelBuffer::unmap() {
    mAccessMode = kAccessMode_None;
}

uint8_t* CpuPixelBuffer::getMappedPointer() const {
    return mAccessMode == kAccessMode_None ? nullptr : mBuffer.get();
}

void CpuPixelBuffer::upload(uint32_t x, uint32_t y, uint32_t width, uint32_t height, int offset) {
    glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height,
            mFormat, GL_UNSIGNED_BYTE, &mBuffer[offset]);
}

///////////////////////////////////////////////////////////////////////////////
// GPU pixel buffer
///////////////////////////////////////////////////////////////////////////////

class GpuPixelBuffer: public PixelBuffer {
public:
    GpuPixelBuffer(GLenum format, uint32_t width, uint32_t height);
    ~GpuPixelBuffer();

    uint8_t* map(AccessMode mode = kAccessMode_ReadWrite) override;

    uint8_t* getMappedPointer() const override;

    void upload(uint32_t x, uint32_t y, uint32_t width, uint32_t height, int offset) override;

protected:
    void unmap() override;

private:
    GLuint mBuffer;
    uint8_t* mMappedPointer;
    Caches& mCaches;
};

GpuPixelBuffer::GpuPixelBuffer(GLenum format,
        uint32_t width, uint32_t height)
        : PixelBuffer(format, width, height)
        , mMappedPointer(nullptr)
        , mCaches(Caches::getInstance()){
    glGenBuffers(1, &mBuffer);

    mCaches.pixelBufferState().bind(mBuffer);
    glBufferData(GL_PIXEL_UNPACK_BUFFER, getSize(), nullptr, GL_DYNAMIC_DRAW);
    mCaches.pixelBufferState().unbind();
}

GpuPixelBuffer::~GpuPixelBuffer() {
    glDeleteBuffers(1, &mBuffer);
}

uint8_t* GpuPixelBuffer::map(AccessMode mode) {
    if (mAccessMode == kAccessMode_None) {
        mCaches.pixelBufferState().bind(mBuffer);
        mMappedPointer = (uint8_t*) glMapBufferRange(GL_PIXEL_UNPACK_BUFFER, 0, getSize(), mode);
        if (CC_UNLIKELY(!mMappedPointer)) {
            GLUtils::dumpGLErrors();
            LOG_ALWAYS_FATAL("Failed to map PBO");
        }
        mAccessMode = mode;
        mCaches.pixelBufferState().unbind();
    }

    return mMappedPointer;
}

void GpuPixelBuffer::unmap() {
    if (mAccessMode != kAccessMode_None) {
        if (mMappedPointer) {
            mCaches.pixelBufferState().bind(mBuffer);
            GLboolean status = glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
            if (status == GL_FALSE) {
                ALOGE("Corrupted GPU pixel buffer");
            }
        }
        mAccessMode = kAccessMode_None;
        mMappedPointer = nullptr;
    }
}

uint8_t* GpuPixelBuffer::getMappedPointer() const {
    return mMappedPointer;
}

void GpuPixelBuffer::upload(uint32_t x, uint32_t y, uint32_t width, uint32_t height, int offset) {
    // If the buffer is not mapped, unmap() will not bind it
    mCaches.pixelBufferState().bind(mBuffer);
    unmap();
    glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height, mFormat,
            GL_UNSIGNED_BYTE, reinterpret_cast<void*>(offset));
    mCaches.pixelBufferState().unbind();
}

///////////////////////////////////////////////////////////////////////////////
// Factory
///////////////////////////////////////////////////////////////////////////////

PixelBuffer* PixelBuffer::create(GLenum format,
        uint32_t width, uint32_t height, BufferType type) {
    if (type == kBufferType_Auto && Caches::getInstance().gpuPixelBuffersEnabled) {
        return new GpuPixelBuffer(format, width, height);
    }
    return new CpuPixelBuffer(format, width, height);
}

#include <stdio.h>
#include <errno.h>

int SchrodPixelBuffer::openSchrobuf(void) {
	if (mFd >= 0) {
		ALOGD("%s device file is already open (mFd = %d)\n", __func__, mFd);
		return 0;
	}

	mFd = open("/dev/schrobuf", O_RDWR | O_NONBLOCK);
	if (mFd < 0) {
		ALOGD("%s Can't open device file: %d errno=%s\n", __func__, mFd, strerror(errno));
		return -1;
	}
	
	return 0;
}

int SchrodPixelBuffer::closeSchrobuf(void) {
	if (mFd < 0) {
		ALOGD("%s device file is not open (mFd = %d)\n", __func__, mFd);
		return 0;
	}

	close(mFd);
	mFd = -1;	
	
	return 0;
}

#define SCHROBUF_REGISTER	5
#define SCHROBUF_UNREGISTER	6
#define SCHROBUF_RESOLVE	7

struct schrobuf_register_ioctl {
    unsigned long buffers_mem;	// addr to glyphbook
    unsigned int num_buffers;	// glyphbook size
    unsigned int buffer_size;	// size of each glyph in glyphbook
    unsigned long encrypted_text;
    unsigned int text_len;
    unsigned int text_buf_size;
	unsigned long char_widths; 	// addr to array describing pixel width of each char
	unsigned int char_widths_size; // size of char_widths
};

struct schrobuf_resolve_ioctl {
    unsigned long dst_addr;
    unsigned int text_pos;
	unsigned int px;			// pixel coordinate on x-axis
	unsigned int fb_bytespp;
    bool conditional_char;
	bool trust_addr;			// tell Xen to composite text at dst_addr, do not perform adjustment (set true for monospaced fonts, false otherwise)
	bool last_res;
};

int SchrodPixelBuffer::registerSchrobuf(int* charWidths, int charWidthsSize) {
    int ret;	
    struct schrobuf_register_ioctl data;
    
    if (mFd < 0) {
        ALOGD("%s device file is not open (mFd = %d)\n", __func__, mFd);
        return 0;
    }

    data.buffers_mem = (unsigned long) mBuffersMem;
    data.num_buffers = mNumBuffers;
    data.buffer_size = mBufferSize;
    data.encrypted_text = (unsigned long) mCipher;
    data.text_len = mTextLen;
    data.text_buf_size = mCipherSize;
	data.char_widths = charWidths ? (unsigned long) charWidths : 0;
	data.char_widths_size = charWidthsSize;
    
    ret = ioctl(mFd, SCHROBUF_REGISTER, &data);
    
    return ret;
}

int SchrodPixelBuffer::unregisterSchrobuf(void) {
    int ret;	
    
    if (mFd < 0) {
        ALOGD("%s device file is not open (mFd = %d)\n", __func__, mFd);
        return 0;
    }
    
    ret = ioctl(mFd, SCHROBUF_UNREGISTER, NULL);
    
    return ret;
}

int hide_counter = 0;

int SchrodPixelBuffer::resolve(uint8_t *dst_addr, unsigned int textPos, unsigned int px, unsigned int fb_bytespp, bool conditional_char, bool trust_addr, bool last_res) {
    int ret;	
    struct schrobuf_resolve_ioctl data;
    
    if (mFd < 0) {
        ALOGD("%s device file is not open (mFd = %d)\n", __func__, mFd);
        return 0;
    }
    
    data.dst_addr = (unsigned long) dst_addr;
    data.text_pos = textPos;
	data.px = px;
	data.fb_bytespp = fb_bytespp;
    data.conditional_char = conditional_char;
	data.trust_addr = trust_addr;
	data.last_res = last_res;
    
    ret = ioctl(mFd, SCHROBUF_RESOLVE, &data);
    
    hide_counter++;
    return ret;
}

SchrodPixelBuffer::SchrodPixelBuffer(uint32_t size, unsigned int numBuffers,
				   const void *cipher, unsigned int textLen,
				   unsigned int cipherSize, int keyHandle) :
				   mCipher(cipher), mTextLen(textLen),
				   mCipherSize(cipherSize), mKeyHandle(keyHandle),
				   mNumBuffers(numBuffers), mBufferSize(size), mFd(-1) {
    unsigned int i;

    mBuffersMem = new uint8_t[numBuffers * mBufferSize];
    memset(mBuffersMem, 0x0, numBuffers * mBufferSize);
    mBuffers = new uint8_t *[numBuffers];
    
    for (i = 0; i < mNumBuffers; i++) {
        mBuffers[i] = &mBuffersMem[i * mBufferSize];
    }

    if (mFd < 0) {
        openSchrobuf();
    }

    if (mFd < 0) {
       ALOGE("%s Error: could not open schrobuf dev file\n", __func__);
       return;
    }
}

SchrodPixelBuffer::~SchrodPixelBuffer() {
    delete[] mBuffers;
    delete[] mBuffersMem;

    unregisterSchrobuf();
    closeSchrobuf();
}

uint8_t *SchrodPixelBuffer::getBuffer(unsigned int i) {

    if (i >= mNumBuffers) {
        ALOGE("%s Error: invalid index %d (mNumBuffers = %d)\n", __func__, i, mNumBuffers);
        return NULL;
    }

    return mBuffers[i];
}

int counter = 0;

int SchrodPixelBuffer::getKeyHandle() {
    return mKeyHandle;
}


}; // namespace uirenderer
}; // namespace android

