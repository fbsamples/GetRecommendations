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
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * An {@link ViewPager}
 */
public class AppCardPager extends ViewPager {

    public static final int OFF_SCREEN_PAGE_LIMIT = 3;

    private int mTouchSlop;
    private float mStartX;
    private float mStartY;

    private AppUnitState mAppUnitState;

    /**
     * Construct an AppCardPager
     *
     * @param context the application context
     */
    public AppCardPager(Context context) {
        super(context, null);
    }

    /**
     * Construct an AppCardPager with the given context and attributes
     *
     * @param context the application context
     * @param attrs   a set of initial attributes
     */
    public AppCardPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration) / 2;
    }

    public void setAppUnitState(AppUnitState appUnitState) {
        mAppUnitState = appUnitState;
    }

    /**
     * Determines if this class should handle the given event
     *
     * @param event the event to handle
     * @return false to always handle events in this class, true otherwise
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mAppUnitState == AppUnitState.Apps && getChildCount() > 1) {
            return handleTouch(event);
        }
        return false;
    }

    /**
     * Handle touch events to smooth out scrolling and distinguish between distinct tap events
     *
     * @param event
     * @return
     */
    private boolean handleTouch(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mStartX = event.getX();
            mStartY = event.getY();
        } else if (action == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();

            if (Math.abs(mStartY - y) > (Math.abs(mStartX - x) + mTouchSlop)) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAppUnitState == AppUnitState.Apps && getChildCount() > 1) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) {
                height = h;
            }
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
