/* 
 * SchrodinText - Strong Protection of Sensitive Textual Content of Mobile Applications
 * File: SchrodinTextView.java
 * Description: Start of SchrodinText secure monitor.
 *
 * Copyright (c) 2016-2019 University of California - Irvine, Irvine, USA
 * All rights reserved.
 *
 * Authors: Ardalan Amiri Sani
 *          Nicholas Wei
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer,
 *    without modification.
 * 2. Redistributions in binary form must reproduce at minimum a disclaimer
 *    substantially similar to the "NO WARRANTY" disclaimer below
 *    ("Disclaimer") and any redistribution must be conditioned upon
 *    including a substantially similar Disclaimer requirement for further
 *    binary redistribution.
 * 3. Neither the names of the above-listed copyright holders nor the names
 *    of any contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * Alternatively, this software may be distributed under the terms of the
 * GNU General Public License ("GPL") version 2 as published by the Free
 * Software Foundation.
 *
 * NO WARRANTY
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 */

package android.widget;

import com.android.internal.R;

import android.annotation.Nullable;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.io.*;
import android.util.Log;

/** @hide */
public class SchrodinTextView extends TextView {

	/** @hide */
    static final String LOG_TAG = "SchrodinTextView";
	private String textMode = "STANDARD";	// see setLayoutMode()
	private byte[] lastCiphertext;
	private int lastCiphertextSize;
	private int lastTextLen;
	private int lastKeyHandle;

    public SchrodinTextView(Context context) {
        this(context, null);
    }

    public SchrodinTextView(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.textViewStyle);
    }

    public SchrodinTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressWarnings("deprecation")
    public SchrodinTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCiphertext(byte[] ciphertext, int ciphertextSize, int textLen, int keyHandle) {
		int i;
		lastCiphertext = ciphertext;
		lastCiphertextSize = ciphertextSize;
		lastTextLen = textLen;
		lastKeyHandle = keyHandle;

		String dummyText = "";
		setEncryptedMode(true);
		//Log.d(LOG_TAG, "System Wall Clock Time (ms) = " + System.currentTimeMillis()); 	// Uncomment to measure latency
		setCipher(ciphertext);
		setKeyHandle(keyHandle);
		setEncryptedTextLength(textLen);
		for (i = 0; i < textLen; i++)
			dummyText += "X";
		setText(dummyText);
		
		mEncryptedView = true;
    }

	public void redrawEncryptedText() {
		if (lastCiphertext == null)
			return;
		this.setCiphertext(lastCiphertext, lastCiphertextSize, lastTextLen, lastKeyHandle);
	}
    
    public void clearCiphertext() {
    	clearEncryptedText();
    }

	public void setLayoutMode(String mode) {
		if (mode.equalsIgnoreCase("LESS") || mode.equalsIgnoreCase("MORE") || || mode.equalsIgnoreCase("MAX")) {
			textMode = mode;
		}
		else {
			textMode = "STANDARD";
		}
		setEncryptedLayoutMode(textMode);
	}
}


