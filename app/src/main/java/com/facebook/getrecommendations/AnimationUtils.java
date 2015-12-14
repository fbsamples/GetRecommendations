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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * A collection of util functions for animations
 */
public class AnimationUtils {

    private static final long SHORT_ANIMATION_DURATION = 400; // milliseconds

    /**
     * Animate a transition between two views
     *
     * @param viewToFadeOut
     * @param viewToFadeIn
     * @param viewToFadeOutVisibilityState
     */
    public static void crossFade(
            final View viewToFadeOut,
            final View viewToFadeIn,
            final int viewToFadeOutVisibilityState) {
        viewToFadeIn.setAlpha(0f);
        viewToFadeIn.setVisibility(View.VISIBLE);

        viewToFadeIn.animate()
                .alpha(1f)
                .setDuration(SHORT_ANIMATION_DURATION)
                .setListener(null);

        viewToFadeOut.animate()
                .alpha(0f)
                .setDuration(SHORT_ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewToFadeOut.setVisibility(viewToFadeOutVisibilityState);
                    }
                });
    }

    /**
     * Show a view
     *
     * @param fadedOutView the view to show
     */
    public static void crossFadeReset(final View fadedOutView) {
        fadedOutView.setAlpha(1f);
        fadedOutView.setVisibility(View.VISIBLE);
    }
}
