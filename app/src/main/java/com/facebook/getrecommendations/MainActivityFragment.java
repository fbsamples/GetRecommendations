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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.facebook.getrecommendations.dev.R;

/**
 * The main GetApps fragment
 */
public class MainActivityFragment extends Fragment implements NativeAdsManager.Listener {

    private static final String TAG = MainActivityFragment.class.getName();
    private static final int ADNW_APP_DISABLED = 1005;
    private static final int NUM_ADS = 10;

    private NativeAdsManager mAdsManager;
    private AppCardPager mAppPager;
    private AppPagerAdapter mAppPagerAdapter;
    private OnLogListener mLogCallback;

    /**
     * A logging interface for app events
     */
    public interface OnLogListener {

        /**
         * Logs a network error
         */
        public void onNetworkError();

        /**
         * Logs a no fill error
         */
        public void onNoFillError();

        /**
         * Logs an app scrolled event with the given parameters
         * @param parameters accompanying parameters for the event
         */
        public void onAppsScrolled(Bundle parameters);
    }

    private final ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                Bundle parameters = new Bundle();
                parameters.putInt("position", mAppPager.getCurrentItem());

                if (mLogCallback != null) {
                    mLogCallback.onAppsScrolled(parameters);
                }
            }
        }
    };

    private final View.OnClickListener mRetryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAppPagerAdapter.setLoadingState();

            configureAdsManager();
        }
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // If we have been backgrounded for long enough to have been gc'd, its worth
        // fetching a fresh set of ads
        configureAppPager();
        configureAdsManager();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnLogListener) {
            mLogCallback = (OnLogListener) activity;
        }
    }

    private void configureAdsManager() {
        mAdsManager = new NativeAdsManager(getActivity(), Utils.PLACEMENT_ID, NUM_ADS);
        mAdsManager.setListener(this);
        mAdsManager.loadAds();

        Log.i(TAG, String.format("Attempting load of %d ads in placement %s", NUM_ADS, Utils.PLACEMENT_ID));
    }

    private void configureAppPager() {
        mAppPagerAdapter = new AppPagerAdapter(getChildFragmentManager());
        mAppPagerAdapter.setLoadingState();
        mAppPagerAdapter.setRetryListener(mRetryListener);

        mAppPager = (AppCardPager) getView().findViewById(R.id.app_pager);
        mAppPager.setOnPageChangeListener(mOnPageChangeListener);
        mAppPager.setAdapter(mAppPagerAdapter);
        mAppPager.setPageMargin((int) getResources().getDimension(R.dimen.getrecs_blue_padding));
        mAppPager.setOffscreenPageLimit(AppCardPager.OFF_SCREEN_PAGE_LIMIT);
        mAppPager.setAppUnitState(AppUnitState.Loading);
    }

    @Override
    public void onAdsLoaded() {
        if (mAdsManager.getUniqueNativeAdCount() != 0) {
            Log.i(TAG, String.format("Loaded %d ads", mAdsManager.getUniqueNativeAdCount()));
            mAppPagerAdapter.setNativeAdsManager(mAdsManager);
            mAppPager.setAppUnitState(AppUnitState.Apps);
        } else {
            Log.w(TAG, "Successful ad request with 0 fill");
            if (mLogCallback != null) {
                mLogCallback.onNoFillError();
            }
            mAppPagerAdapter.setErrorState(true);
            mAppPager.setAppUnitState(AppUnitState.Error);
        }
    }

    @Override
    public void onAdError(AdError adError) {
        Log.w(TAG, "Encountered ad loading error '" + adError.getErrorMessage() + "'");
        if (mLogCallback != null) {
            mLogCallback.onNetworkError();
        }
        mAppPager.setAppUnitState(AppUnitState.Error);
        if (adError.getErrorCode() == ADNW_APP_DISABLED) {
            mAppPagerAdapter.setErrorState(false);
        } else {
            mAppPagerAdapter.setErrorState(true);
        }
    }

    @Override
    public void onDestroy() {
        mAdsManager.setListener(null);
        mAdsManager = null;
        mAppPager = null;
        mAppPagerAdapter = null;
        mLogCallback = null;
        super.onDestroy();
    }
}
