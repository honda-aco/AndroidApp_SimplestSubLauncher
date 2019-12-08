package com.ns.aco.sp3.ad;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView;

public class AstWebView extends WebView {

    public AstWebView(Context context) {
        super(context);
    }

    @Override
    public boolean onCheckIsTextEditor () {
        return false;
    }

    @Override
    public InputConnection onCreateInputConnection (EditorInfo outAttrs) {
        return null;
    }
}
