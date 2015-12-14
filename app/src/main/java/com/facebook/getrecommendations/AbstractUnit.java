/**
 * Copyright (c) 2015-present, Facebook, Inc. All rights reserved.
 * <p>
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 * <p>
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.facebook.getrecommendations;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.facebook.ads.NativeAd;

/**
 * An AbstractUnit represents a responsive layout that adjusts its size relative to the device
 */
public abstract class AbstractUnit extends LinearLayout {

    public static final float SCREEN_TO_WIDTH_RATIO = 0.8f;
    private static final double WIDTH_TO_HEIGHT_RATIO = 2.11;

    /**
     * Constructs an AbstractUnit
     *
     * @param context the application context
     */
    public AbstractUnit(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    /**
     * Calculate and return the scaled size of the unit
     *
     * @param context the application context
     * @return scaled size of the unit
     */
    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (int) (size.x * SCREEN_TO_WIDTH_RATIO);
    }

    /**
     * Scale the layout of the unit
     *
     * @param view
     */
    protected void configureLayoutToAspectRatio(View view) {
        view.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        (int) (getWidth(getContext()) / WIDTH_TO_HEIGHT_RATIO)));
    }

    /**
     * Configure the {@link NativeAd} for the UI
     *
     * @param ad
     */
    public void configure(NativeAd ad) {

    }

    /**
     * Set the state of the application
     *
     * @param state    the state to apply
     * @param animated true for animation, false otherwise
     * @param retry    whether or not to retry an ads fetch
     */
    public void setState(AppUnitState state, boolean animated, boolean retry) {

    }
}
