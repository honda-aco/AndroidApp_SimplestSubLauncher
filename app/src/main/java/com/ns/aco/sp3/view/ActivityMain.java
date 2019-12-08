package com.ns.aco.sp3.view;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.ns.aco.sp3.R;
import com.ns.aco.sp3.presenter.PresenterMain;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PresenterMain _presenterMain = null;
    private CoordinatorLayout _coordinatorLayout = null;
    private Spinner _spinnerPosition = null;
    private Button _buttonStart = null;
    private Button _buttonStop = null;
    private FloatingActionButton _floatingActionButton = null;
    private ListView _listView = null;
    private List<ResolveInfo> _packageList = null;
    private final int ACTION_MANAGE_OVERLAY_PERMISSION = 0;

    public int getWindowWidth(){
        return _coordinatorLayout.getWidth();
    }

    public int getWindowHeight(){
        return _coordinatorLayout.getHeight();
    }

    public FloatingActionButton getFloatingActionButton(){
        return _floatingActionButton;
    }

    public Spinner getSpinnerPosition(){
        return _spinnerPosition;
    }

    public Button getButtonStart(){
        return _buttonStart;
    }

    public Button getButtonStop(){
        return _buttonStop;
    }

    public ListView getListView(){
        return _listView;
    }

    public void setPackageList(List<ResolveInfo> packageList){
        _packageList = packageList;
    }

    public List<ResolveInfo> getPackageList(){
        return _packageList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PresenterMain.create(ActivityMain.this);
        _presenterMain = PresenterMain.newInstance();

        setContentView(R.layout.coordinator_main);

        _coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Toolbar右のサブメニューボタンの有効化
        setSupportActionBar(toolbar);

        _spinnerPosition = (Spinner) findViewById(R.id.spinnerPosition);

        _buttonStart = (Button) findViewById(R.id.buttonStart);
        _buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _presenterMain.checkPermission(ACTION_MANAGE_OVERLAY_PERMISSION);
            }
        });

        _buttonStop = (Button) findViewById(R.id.buttonStop);
        _buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _presenterMain.stopLauncher();
            }
        });

        _listView = (ListView) findViewById(R.id.listApplication);

        _packageList = new ArrayList<>();

        _floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        _floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //         .setAction("Action", null).show();
                _presenterMain.showSelectProcessDialog(_packageList);
            }
        });

        _presenterMain.initListView();

        // アスタ広告の設定
        _presenterMain.createAstAd();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                _presenterMain.startLauncher();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // ...その他の処理...
        _presenterMain.onResume();
    }

    @Override
    public void onPause() {
        _presenterMain.onPause();
        // ...その他の処理...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Activity終了時 破棄
        _presenterMain.onDestroy();
        // ...その他の処理...
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
          super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_initialize) {
            _presenterMain.clearApplicationList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
