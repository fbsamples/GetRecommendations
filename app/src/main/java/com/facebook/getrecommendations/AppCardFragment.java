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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.ads.NativeAd;

public class AppCardFragment extends Fragment {

    private AbstractUnit mAppUnit;
    private NativeAd mNativeAd;
    private AbstractAppUnitFactory mAbstractAppUnitFactory;
    private int mPosition = -1;

    public interface AbstractAppUnitFactory {
        AbstractUnit createUnit(Context context, int position);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        if (mAbstractAppUnitFactory == null || mPosition < 0) {
            return new AppUnitLoading(getActivity());
        }

        mAppUnit = mAbstractAppUnitFactory.createUnit(getActivity(), mPosition);
        mAppUnit.configure(mNativeAd);
        return mAppUnit;
    }

    public void setNativeAd(NativeAd ad) {
        mNativeAd = ad;
    }

    public void setAbstractAppUnitFactory(AbstractAppUnitFactory abstractAppUnitFactory) {
        mAbstractAppUnitFactory = abstractAppUnitFactory;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    /**
     * @return the {@link AbstractUnit} associated with this fragment
     */
    public AbstractUnit getAppUnit() {
        return mAppUnit;
    }
}
