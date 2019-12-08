package com.ns.aco.sp3.presenter;

public interface PresenterIF {

    abstract public void onResume();
    abstract public void onPause();
    abstract public void onWindowFocusChanged();
    abstract public void onDestroy();

}
