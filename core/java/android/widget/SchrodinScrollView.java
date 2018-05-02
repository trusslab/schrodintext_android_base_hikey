/* 
 * SchrodinText - Strong Protection of Sensitive Textual Content of Mobile Applications
 * File: SchrodinScrollView.java
 * Description: ScrollView with additional functionality to detect scrolling start and stop.
 * 				Modified from: https://stackoverflow.com/a/38933585
 *
 * Copyright (c) 2016-2019 University of California - Irvine, Irvine, USA
 * All rights reserved.
 *
 * Authors: Ardalan Amiri Sani
 *			Nicholas Wei
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
import android.util.Log;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/** @hide */
public class SchrodinScrollView extends ScrollView {

	// Event listeners
    public interface OnScrollListener {
        public void onScrollStart();
        public void onScrollEnd();
    }

	private static final String LOG_TAG = "SchrodinScrollView";
    private long lastScrollUpdate = -1;
    private int scrollTaskInterval = 100;	// check every 100ms
    private Runnable mScrollingRunnable;
    private OnScrollListener mOnScrollListener;

    public SchrodinScrollView(Context context) {
        this(context, null, 0);
		init(context);
    }

    public SchrodinScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
		init(context);
    }

    public SchrodinScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
		init(context);
    }

    private void init(Context context) {
        // check for scrolling every scrollTaskInterval milliseconds
        mScrollingRunnable = new Runnable() {
            public void run() {
                if ((System.currentTimeMillis() - lastScrollUpdate) > scrollTaskInterval) {
                    // scrolling has stopped.
                    lastScrollUpdate = -1;
                    mOnScrollListener.onScrollEnd();
                } else {
                    // still scrolling - check again in scrollTaskInterval milliseconds
                    postDelayed(this, scrollTaskInterval);
                }
            }
        };
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollListener != null) {
            if (lastScrollUpdate == -1) {
                mOnScrollListener.onScrollStart();
                postDelayed(mScrollingRunnable, scrollTaskInterval);
            }

            lastScrollUpdate = System.currentTimeMillis();
        }
    }

    public OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    public void setOnScrollListener(OnScrollListener mOnEndScrollListener) {
        this.mOnScrollListener = mOnEndScrollListener;
    }
}
