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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.os.Parcelable;

import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

public class AppPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = AppPagerAdapter.class.getName();

    private final HashMap<Integer, WeakReference<AppCardFragment>> mLoadedFragments = new HashMap<>();
    private AppUnitState mAppUnitState;

    private final AppCardFragment.AbstractAppUnitFactory mLoadingOrErrorUnitFactory = new AppCardFragment.AbstractAppUnitFactory() {
        @Override
        public AbstractUnit createUnit(Context context, int position) {
            AppUnitLoading unitLoading = new AppUnitLoading(context);
            configureLoadingOrErrorCard(unitLoading, false, position);
            return unitLoading;
        }
    };

    private final AppCardFragment.AbstractAppUnitFactory mAppUnitFactory = new AppCardFragment.AbstractAppUnitFactory() {
        @Override
        public AbstractUnit createUnit(Context context, int position) {
            return new AppUnit(context);
        }
    };

    private List<NativeAd> mAds;
    private int mAdCount;

    private boolean mRetry;
    private View.OnClickListener mRetryListener;

    public AppPagerAdapter(FragmentManager fm) {
        super(fm);
        mAds = new LinkedList<>();
        mAdCount = 0;
        mAppUnitState = AppUnitState.Loading;
        mRetry = false;
    }

    /**
     * Setup the ads for the application
     *
     * @param adsManager the {@link NativeAdsManager} to use for initialization
     */
    public void setNativeAdsManager(NativeAdsManager adsManager) {
        mAppUnitState = AppUnitState.Apps;

        mAds.clear();
        mAdCount = adsManager.getUniqueNativeAdCount();
        for (int i = 0; i < mAdCount; i++) {
            mAds.add(adsManager.nextNativeAd());
        }
        notifyDataSetChanged();
    }

    /**
     * Initialze the error state of the app
     *
     * @param retry whether or not to retry an ads fetch
     */
    public void setErrorState(boolean retry) {
        AppUnitState previousState = mAppUnitState;
        mAppUnitState = AppUnitState.Error;
        mRetry = retry;
        if (previousState == AppUnitState.Loading) {
            rebindFragments();
        } else {
            mAdCount = 2;
            notifyDataSetChanged();
        }
    }

    /**
     * Initializes the loading state of the app
     */
    public void setLoadingState() {
        AppUnitState previousState = mAppUnitState;
        mAppUnitState = AppUnitState.Loading;

        if (previousState == AppUnitState.Error) {
            rebindFragments();
        } else {
            mAdCount = 2;
            notifyDataSetChanged();
        }
    }

    public void setRetryListener(View.OnClickListener retryListener) {
        mRetryListener = retryListener;
    }

    @Override
    public float getPageWidth(int position) {
        return AppUnit.SCREEN_TO_WIDTH_RATIO;
    }

    @Override
    public int getCount() {
        return mAdCount;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        AppCardFragment appCardFragment = new AppCardFragment();
        appCardFragment.setPosition(position);

        if (mAppUnitState != AppUnitState.Apps) {
            appCardFragment.setAbstractAppUnitFactory(mLoadingOrErrorUnitFactory);
        } else {
            appCardFragment.setAbstractAppUnitFactory(mAppUnitFactory);
            appCardFragment.setNativeAd(mAds.get(position));
        }

        return appCardFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        AppCardFragment cardFragment = (AppCardFragment) super.instantiateItem(container, position);
        mLoadedFragments.put(position, new WeakReference<AppCardFragment>(cardFragment));
        return cardFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.e(TAG, position + "\t" + mAdCount);
        if (position < mLoadedFragments.size() && position < mAds.size()) {
            mLoadedFragments.remove(position);
            if (mAppUnitState == AppUnitState.Apps) {
                mAds.get(position).unregisterView();
            }
        }

        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        // This is a hack to force a reload of all cards when notifyDataSetChanged() is changed.
        return POSITION_NONE;
    }

    private void rebindFragments() {
        for (Map.Entry<Integer, WeakReference<AppCardFragment>> entry : mLoadedFragments.entrySet()) {
            AppCardFragment fragment = entry.getValue().get();
            if (fragment != null) {
                if (fragment.getAppUnit() instanceof AppUnitLoading) {
                    configureLoadingOrErrorCard((AppUnitLoading) fragment.getAppUnit(), true, entry.getKey());
                }
            }
        }
    }

    private void configureLoadingOrErrorCard(AppUnitLoading unit, boolean animated, int position) {
        if (mAppUnitState == AppUnitState.Loading) {
            unit.setState(AppUnitState.Loading, animated, mRetry);
        } else if (mAppUnitState == AppUnitState.Error) {
            if (position == 0) {
                unit.setState(AppUnitState.Error, animated, mRetry);
                unit.setRetryClickListener(mRetryListener);
            } else {
                unit.setState(AppUnitState.Loading, animated, mRetry);
                unit.setShowLoading(false);
            }
        }
    }

    /**
     * This is a hack to remove caching from the adapter
     * If the app has been backgrounded long enough to be garbage collected
     * refetching the ads is a reasonable action to take rather than
     * restoring from old ads
     *
     * @param state
     * @param loader
     */
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
