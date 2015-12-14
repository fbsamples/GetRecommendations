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

import java.util.Arrays;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.MediaView;
import com.facebook.getrecommendations.dev.R;
import com.facebook.ads.NativeAd;

/**
 * A class to represent a single ad unit in the application
 */
public class AppUnit extends AbstractUnit {

    private LinearLayout mRoot;
    private MediaView mCover;
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mSubtitle;
    private TextView mContextSentence;
    private TextView mDescription;
    private TextView mCtaButton;

    /**
     * Constructs an AppUnit
     *
     * @param context the application context
     */
    public AppUnit(Context context) {
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        mRoot = (LinearLayout) layoutInflater.inflate(R.layout.app_unit, this);
        setBackground(new ColorDrawable(getResources().getColor(R.color.getrecommendations_background)));
        setOrientation(LinearLayout.VERTICAL);
        mCover = (MediaView) mRoot.findViewById(R.id.media);
        mIcon = (ImageView) mRoot.findViewById(R.id.icon);
        mTitle = (TextView) mRoot.findViewById(R.id.title);
        mSubtitle = (TextView) mRoot.findViewById(R.id.subtitle);
        mContextSentence = (TextView) mRoot.findViewById(R.id.context_sentence);
        mDescription = (TextView) mRoot.findViewById(R.id.description);
        mCtaButton = (TextView) mRoot.findViewById(R.id.cta_button);
    }

    @Override
    public void configure(NativeAd ad) {
        mCover.setNativeAd(ad);
        configureLayoutToAspectRatio(mCover);
        NativeAd.Image adIcon = ad.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, mIcon);
        mTitle.setText(ad.getAdTitle());
        String subtitle = ad.getAdSubtitle();
        mSubtitle.setVisibility(View.GONE);
        if (subtitle != null && !subtitle.isEmpty()) {
            mSubtitle.setText(subtitle);
            mSubtitle.setVisibility(View.VISIBLE);
        }
        mContextSentence.setText(ad.getAdSocialContext());
        mDescription.setText(ad.getAdBody());
        mCtaButton.setText(ad.getAdCallToAction());
        View[] clickEnabledViews = {mRoot};
        ad.registerViewForInteraction(mRoot, Arrays.asList(clickEnabledViews));
    }
}
