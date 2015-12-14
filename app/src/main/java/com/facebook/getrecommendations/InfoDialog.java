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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.getrecommendations.dev.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * A dialog to display information to the user
 */
public class InfoDialog {

    private static AlertDialog.Builder alert;
    private static final float DIALOG_FILL_RATIO = 0.9f;

    /**
     * Show a license dialog in the given context with the given resource
     *
     * @param context         the application context
     * @param bodyResourceId  the resource to display as the body
     * @param titleResourceId the resource to display as the title
     */
    public static void showDialog(final Context context, int bodyResourceId, int titleResourceId) {
        InputStream is = context.getResources().openRawResource(bodyResourceId);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);

            new AlertDialog.Builder(context)
                    .setMessage(new String(b))
                    .setTitle(titleResourceId)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showWebDialog(final Context context, int urlId) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        FrameLayout dialogViewContents = (FrameLayout) layoutInflater.inflate(
                R.layout.web_dialog,
                null);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        final WebView wv = (WebView) dialogViewContents.findViewById(R.id.wv);
        wv.getLayoutParams().width = (int) (size.x * DIALOG_FILL_RATIO);
        final ProgressBar progressBar = (ProgressBar) dialogViewContents.findViewById(R.id.progress_bar);
        progressBar.getLayoutParams().width = (int) (size.x * DIALOG_FILL_RATIO);

        final String initialUrl = context.getResources().getString(urlId);
        wv.loadUrl(initialUrl);
        TextView errorText =
                (TextView) dialogViewContents.findViewById(R.id.web_dialog_error);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogViewContents)
                .setNegativeButton(
                        R.string.action_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();

        CustomWebViewClient client =
                new CustomWebViewClient(initialUrl, progressBar, context, errorText);
        wv.setWebViewClient(client);
    }
}
