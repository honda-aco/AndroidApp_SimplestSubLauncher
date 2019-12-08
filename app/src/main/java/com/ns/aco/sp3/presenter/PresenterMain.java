package com.ns.aco.sp3.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ns.aco.sp.common.extent.MyArrayAdapterResolveInfo;
import com.ns.aco.sp.common.extent.MyListView;
import com.ns.aco.sp.common.util.UtilityService;
import com.ns.aco.sp.common.util.UtilityView;
import com.ns.aco.sp.common.view.ViewDragAndDrop;
import com.ns.aco.sp3.R;
import com.ns.aco.sp3.ad.AstWebView;
import com.ns.aco.sp3.dialog.SelectAppListDialog;
import com.ns.aco.sp3.entity.OperateDataBase;
import com.ns.aco.sp3.service.ServiceLauncher;
import com.ns.aco.sp3.view.ActivityMain;

import java.util.ArrayList;
import java.util.List;

public class PresenterMain implements PresenterIF {

    private static PresenterMain _presenterMain = null;
    private ActivityMain _activityMain = null;
    private OperateDataBase _operateDB = null;
    private final static String _extraName1 = "position";
    private final static String _extraName2 = "windowWidth";
    private final static String _extraName3 = "windowHeight";
    // 広告用WebView
    private AstWebView _astWebView = null;

    public ActivityMain getActivityMain(){
        return _activityMain;
    }

    public static String getExtraName1(){
        return _extraName1;
    }

    public static String getExtraName2(){
        return _extraName2;
    }

    public static String getExtraName3(){
        return _extraName3;
    }

    private PresenterMain(ActivityMain activityMain){
        _activityMain = activityMain;

        // DB操作クラスのインスタンスを生成しDBをオープンする
        _operateDB = new OperateDataBase(_activityMain.getApplicationContext());
        _operateDB.Open();
        try{
            _operateDB.createTable();
        }catch(Exception e){
        }
    }

    public static void create(ActivityMain activityMain){
        _presenterMain = new PresenterMain(activityMain);
    }

    public static PresenterMain newInstance(){
        return _presenterMain;
    }

    public void showSelectProcessDialog(List<ResolveInfo> selectedPackageList){
        _activityMain.getFloatingActionButton().setVisibility(View.INVISIBLE);
        SelectAppListDialog selectAppListDialog = new SelectAppListDialog();
        selectAppListDialog.setSelectedPackageList(selectedPackageList);
        selectAppListDialog.show(_activityMain.getFragmentManager(), null);
    }

    public void cancelSelectProcessDialog(){
        _activityMain.getFloatingActionButton().setVisibility(View.VISIBLE);
    }

    public void setApplicationList(List<ResolveInfo> resolveInfoList){
        PackageManager packageManager = _activityMain.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        MyArrayAdapterResolveInfo arrayAdapter = new MyArrayAdapterResolveInfo(
                _activityMain, com.ns.aco.sp.common.R.layout.listview_item, resolveInfoList, packageManager);
        arrayAdapter.setTextColor(Color.BLACK);
        arrayAdapter.setBackgroundColor(Color.WHITE);
        _activityMain.getListView().setAdapter(arrayAdapter);
        _activityMain.setPackageList(resolveInfoList);
        insertPACKAGENAME(resolveInfoList);

        _activityMain.getFloatingActionButton().setVisibility(View.VISIBLE);
    }

    public void insertPACKAGENAME(List<ResolveInfo> packageList){
        _operateDB.delete_PACKAGENAME();
        for (ResolveInfo packageName : packageList){
            _operateDB.insert_PACKAGENAME(packageName.activityInfo.name);
        }
    }

    public void initListView(){
        PackageManager packageManager =_activityMain.getPackageManager();
        // ランチャーから起動出来るアプリケーションの一覧
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

        ArrayList<String[]> packageNameList = _operateDB.get_PACKAGENAME();
        List<ResolveInfo> selectedResolveInfoList = new ArrayList<ResolveInfo>();

        for (ResolveInfo resolveInfo : resolveInfoList){
            for (String[] packageName : packageNameList){
                if (resolveInfo.activityInfo.name.equals(packageName[0])){
                    selectedResolveInfoList.add(resolveInfo);
                }
            }
        }

        MyArrayAdapterResolveInfo arrayAdapter = new MyArrayAdapterResolveInfo(
                _activityMain, com.ns.aco.sp.common.R.layout.listview_item, selectedResolveInfoList, packageManager);
        arrayAdapter.setTextColor(Color.BLACK);
        arrayAdapter.setBackgroundColor(Color.WHITE);
        _activityMain.getListView().setAdapter(arrayAdapter);
        _activityMain.setPackageList(selectedResolveInfoList);

        // 前回起動したサービスが存在する場合の処理
        if (UtilityService.existsService(_activityMain, _activityMain.getString(R.string.service_name))){
            _activityMain.getButtonStart().setLayoutParams(
                    new LinearLayout.LayoutParams(0, 0)
            );
        }else{
            _activityMain.getButtonStop().setLayoutParams(
                    new LinearLayout.LayoutParams(0, 0)
            );
        }
    }

    public void clearApplicationList(){
        _activityMain.getListView().setAdapter(null);
        _activityMain.getPackageList().clear();
        _operateDB.delete_PACKAGENAME();
    }

    public void checkPermission(int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(_activityMain.getBaseContext())) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + _activityMain.getPackageName()));
                _activityMain.startActivityForResult(intent, requestCode);
            }else {
                _presenterMain.startLauncher();
            }
        }else{
            _presenterMain.startLauncher();
        }
    }

    public void startLauncher(){
        if (_activityMain.getPackageList().size() == 0){
            Toast.makeText(_activityMain, _activityMain.getString(R.string.warn_message1), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(_activityMain, ServiceLauncher.class);
        intent.putExtra(_extraName1, (String) _activityMain.getSpinnerPosition().getSelectedItem());
        intent.putExtra(_extraName2, _activityMain.getWindowWidth());
        intent.putExtra(_extraName3, _activityMain.getWindowHeight());
        _activityMain.startService(intent);
        // ボタンをStop用に切り替える
        _activityMain.getButtonStop().setLayoutParams(
                new LinearLayout.LayoutParams(
                        _activityMain.getButtonStart().getWidth(),
                        _activityMain.getButtonStart().getHeight()));
        _activityMain.getButtonStart().setLayoutParams(
                new LinearLayout.LayoutParams(0, 0)
        );
    }

    public void stopLauncher(){
        _activityMain.stopService(new Intent(_activityMain, ServiceLauncher.class));
        // ボタンをStart用に切り替える
        _activityMain.getButtonStart().setLayoutParams(
                new LinearLayout.LayoutParams(
                        _activityMain.getButtonStop().getWidth(),
                        _activityMain.getButtonStop().getHeight()));
        _activityMain.getButtonStop().setLayoutParams(
                new LinearLayout.LayoutParams(0, 0));
    };

    public void startApplication(ServiceLauncher serviceLauncher, View view, int position){
        MyArrayAdapterResolveInfo arrayAdapter = (MyArrayAdapterResolveInfo) ((ListView)view).getAdapter();
        ResolveInfo resolveInfo = arrayAdapter.getResolveInfo(position);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        serviceLauncher.startActivity(intent);
        closeApplicationList(serviceLauncher);
    }

    public void setPackageListAtListView(MyListView listView){
        PackageManager packageManager =_activityMain.getPackageManager();
//        ArrayAdapter arrayAdapter = new ArrayAdapter(
//                _activityMain, com.ns.aco.sp.common.R.layout.listview_item);
        MyArrayAdapterResolveInfo arrayAdapter = new MyArrayAdapterResolveInfo(
                _activityMain, com.ns.aco.sp.common.R.layout.listview_item, _activityMain.getPackageList(), packageManager);
        arrayAdapter.setTextColor(Color.WHITE);
        arrayAdapter.setBackgroundColor(Color.BLACK);
        listView.setAdapter(arrayAdapter);
        listView.setAlpha(0.9f);
    }

    // WindowManagerの位置と中のViewの配置を変更する
    public void switchWindowManagerPosition(ServiceLauncher serviceLauncher) {
        String position = serviceLauncher.get_position();
        Context context = serviceLauncher.getBaseContext();
        WindowManager.LayoutParams windowManagerLP = serviceLauncher.get_windowManagerLP();
        int windowWidth = serviceLauncher.get_windowWidth();
        int windowHeight = serviceLauncher.get_windowHeight();
        int viewSize = serviceLauncher.get_viewSize();
        LinearLayout linearLayoutOpen = serviceLauncher.get_linearLayoutOpen();
        View spaceView = serviceLauncher.get_spaceView();
        ImageView imageLauncherOpen = serviceLauncher.get_imageLauncherOpen();
        ListView listApplication = serviceLauncher.get_listApplication();

        // ImageViewのサイズを指定するためのLayoutParams
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(viewSize, viewSize);
        // LinearLayoutの子を全部削除する
        linearLayoutOpen.removeAllViews();

        // Viewの初期表示位置を取得
        if (position.equals(context.getString(R.string.position_top_left))) {
            windowManagerLP.x = UtilityView.getCoordinateX_topLeft(windowWidth, viewSize);
            windowManagerLP.y = UtilityView.getCoordinateY_topLeft(windowHeight, viewSize);
            // Open時のView位置の設定
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            linearLayoutOpen.addView(imageLauncherOpen, layoutParams);
            linearLayoutOpen.addView(listApplication);

        } else if (position.equals(context.getString(R.string.position_top_right))) {
            windowManagerLP.x = UtilityView.getCoordinateX_topRight(windowWidth, viewSize);
            windowManagerLP.y = UtilityView.getCoordinateY_topRight(windowHeight, viewSize);
            // Open時のView位置の設定
            layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
            linearLayoutOpen.addView(imageLauncherOpen, layoutParams);
            linearLayoutOpen.addView(listApplication);

        } else if (position.equals(context.getString(R.string.position_bottom_left))) {
            windowManagerLP.x = UtilityView.getCoordinateX_bottomLeft(windowWidth, viewSize);
            windowManagerLP.y = UtilityView.getCoordinateY_bottomLeft(windowHeight, viewSize);
            // Open時のView位置の設定
            linearLayoutOpen.addView(spaceView);
            linearLayoutOpen.addView(listApplication);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
            linearLayoutOpen.addView(imageLauncherOpen, layoutParams);

        } else {
            windowManagerLP.x = UtilityView.getCoordinateX_bottomRight(windowWidth, viewSize);
            windowManagerLP.y = UtilityView.getCoordinateY_bottomRight(windowHeight, viewSize);
            // Open時のView位置の設定
            linearLayoutOpen.addView(spaceView);
            linearLayoutOpen.addView(listApplication);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            linearLayoutOpen.addView(imageLauncherOpen, layoutParams);
        }
    }

    public void showApplicationList(ServiceLauncher serviceLauncher){
        try{
            serviceLauncher.get_windowManager().removeView(serviceLauncher.get_viewDragAndDrop().get_frameDragDrop());
        }catch (Exception ex){
        }
        serviceLauncher.get_windowManagerLP().width = ViewGroup.LayoutParams.MATCH_PARENT;
        serviceLauncher.get_windowManagerLP().height = ViewGroup.LayoutParams.MATCH_PARENT;
        serviceLauncher.get_windowManager().addView(
                serviceLauncher.get_linearLayoutOpen(),
                serviceLauncher.get_windowManagerLP()
        );
    }

    public void closeApplicationList(ServiceLauncher serviceLauncher){
        try{
            serviceLauncher.get_windowManager().removeView(serviceLauncher.get_linearLayoutOpen());
        }catch (Exception ex){
        }
        serviceLauncher.get_windowManagerLP().width = serviceLauncher.get_viewSize() + (serviceLauncher.get_paddingWidth() * 2);
        serviceLauncher.get_windowManagerLP().height = serviceLauncher.get_viewSize();
        serviceLauncher.get_windowManager().addView(
                serviceLauncher.get_viewDragAndDrop().get_frameDragDrop(),
                serviceLauncher.get_windowManagerLP()
        );
        serviceLauncher.get_viewDragAndDrop().get_imgTopLeft().setVisibility(View.INVISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgTopRight().setVisibility(View.INVISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgBottomLeft().setVisibility(View.INVISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgBottomRight().setVisibility(View.INVISIBLE);
    }

    public void displayImageLauncherClose(ServiceLauncher serviceLauncher){
        try{
            serviceLauncher.get_windowManager().removeView(serviceLauncher.get_viewDragAndDrop().get_frameDragDrop());
        }catch (Exception ex){
        }
        serviceLauncher.get_windowManagerLP().width = serviceLauncher.get_viewSize() + (serviceLauncher.get_paddingWidth() * 2);
        serviceLauncher.get_windowManagerLP().height = serviceLauncher.get_viewSize();
        serviceLauncher.get_windowManager().addView(
                serviceLauncher.get_viewDragAndDrop().get_frameDragDrop(),
                serviceLauncher.get_windowManagerLP()
        );
        serviceLauncher.get_viewDragAndDrop().get_imgTopLeft().setVisibility(View.INVISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgTopRight().setVisibility(View.INVISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgBottomLeft().setVisibility(View.INVISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgBottomRight().setVisibility(View.INVISIBLE);
    }
    
    public void updateImageLauncherClose(ServiceLauncher serviceLauncher){
        serviceLauncher.get_windowManagerLP().width = serviceLauncher.get_viewSize() + (serviceLauncher.get_paddingWidth() * 2);
        serviceLauncher.get_windowManagerLP().height = serviceLauncher.get_viewSize();
        serviceLauncher.get_windowManager().updateViewLayout(
                serviceLauncher.get_viewDragAndDrop().get_frameDragDrop(),
                serviceLauncher.get_windowManagerLP()
        );
        serviceLauncher.get_viewDragAndDrop().get_imgTopLeft().setVisibility(View.INVISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgTopRight().setVisibility(View.INVISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgBottomLeft().setVisibility(View.INVISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgBottomRight().setVisibility(View.INVISIBLE);
    }
        
    public boolean dragImageLauncherClose(ServiceLauncher serviceLauncher, View view){
//        view.startDrag(null, new View.DragShadowBuilder(view), view, 0);
        // Drop領域の表示
        serviceLauncher.get_viewDragAndDrop().get_imgTopLeft().setVisibility(View.VISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgTopRight().setVisibility(View.VISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgBottomLeft().setVisibility(View.VISIBLE);
        serviceLauncher.get_viewDragAndDrop().get_imgBottomRight().setVisibility(View.VISIBLE);
        // 位置によるDrop領域とView位置の設定
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                serviceLauncher.get_viewSize(),
                serviceLauncher.get_viewSize());

        String position = serviceLauncher.get_position();
        if (position.equals(serviceLauncher.getString(R.string.position_top_left))) {
            serviceLauncher.get_viewDragAndDrop().get_imgTopLeft().setVisibility(View.INVISIBLE);
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        } else if (position.equals(serviceLauncher.getString(R.string.position_top_right))) {
            serviceLauncher.get_viewDragAndDrop().get_imgTopRight().setVisibility(View.INVISIBLE);
            layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        } else if (position.equals(serviceLauncher.getString(R.string.position_bottom_left))) {
            serviceLauncher.get_viewDragAndDrop().get_imgBottomLeft().setVisibility(View.INVISIBLE);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        } else {
            serviceLauncher.get_viewDragAndDrop().get_imgBottomRight().setVisibility(View.INVISIBLE);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        }
        // FrameLayoutの更新
        serviceLauncher.get_viewDragAndDrop().get_frameDragDrop().updateViewLayout(
                serviceLauncher.get_imageLauncherClose(),
                layoutParams);
        // WindowManagerの更新
        serviceLauncher.get_windowManagerLP().width = ViewGroup.LayoutParams.MATCH_PARENT;
        serviceLauncher.get_windowManagerLP().height = ViewGroup.LayoutParams.MATCH_PARENT;
        serviceLauncher.get_windowManager().updateViewLayout(
                serviceLauncher.get_viewDragAndDrop().get_frameDragDrop(),
                serviceLauncher.get_windowManagerLP()
        );
        view.startDrag(null, serviceLauncher.get_shadowImageLauncherClose(), view, 0);
        // ドラッグ&ドロップ処理を開始する
        return false;
    }

    public void dropImageLauncherClose(ServiceLauncher serviceLauncher, View view){
        Drawable drawable = null;
        ViewDragAndDrop viewDragAndDrop = serviceLauncher.get_viewDragAndDrop();
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = serviceLauncher.getDrawable(R.drawable.drop_area);
        }else {
            drawable = serviceLauncher.getResources().getDrawable(R.drawable.drop_area);
        }
        
        if (view.getId() == viewDragAndDrop.get_imgTopLeft().getId()){
            serviceLauncher.set_position(serviceLauncher.getString(R.string.position_top_left));
            viewDragAndDrop.get_imgTopLeft().setImageDrawable(drawable);
        }else if (view.getId() == viewDragAndDrop.get_imgTopRight().getId()){
            serviceLauncher.set_position(serviceLauncher.getString(R.string.position_top_right));
            viewDragAndDrop.get_imgTopRight().setImageDrawable(drawable);
        }else if (view.getId() == viewDragAndDrop.get_imgBottomLeft().getId()){
            serviceLauncher.set_position(serviceLauncher.getString(R.string.position_bottom_left));
            viewDragAndDrop.get_imgBottomLeft().setImageDrawable(drawable);
        }else{
            serviceLauncher.set_position(serviceLauncher.getString(R.string.position_bottom_right));
            viewDragAndDrop.get_imgBottomRight().setImageDrawable(drawable);
        }

        switchWindowManagerPosition(serviceLauncher);
        updateImageLauncherClose(serviceLauncher);
    }

    @Override
    public void onResume() {
        _astWebView.resumeTimers();
    }

    @Override
    public void onPause() {
        _astWebView.pauseTimers();
    }

    @Override
    public void onWindowFocusChanged() {}

    @Override
    public void onDestroy() {
        deallocateAstAd();
        _operateDB.Close();
    }

    // 広告用WebView生成
    public void createAstAd(){

        deallocateAstAd();

        FrameLayout adBase = new FrameLayout(_activityMain);
        RelativeLayout adRelative = new RelativeLayout(_activityMain);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        adRelative.setLayoutParams(params);
        adBase.addView(adRelative);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        _activityMain.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics); // (1)
        float density = displaymetrics.density;

        // WebViewを利用して広告バナーを作成
        _astWebView = new AstWebView(_activityMain.getApplicationContext());
        _astWebView.getSettings().setJavaScriptEnabled(true); // javascriptを有効化する
        _astWebView.setVerticalScrollbarOverlay(true); // スクロールバー消去

        _astWebView.setWebViewClient(new WebViewClient() { // (2)
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.indexOf("http://public.astrsk.net") == 0
                        && url.indexOf("click.cgi") > 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    _activityMain.startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        RelativeLayout.LayoutParams bannerParam = new RelativeLayout.LayoutParams((int) (320 * density), (int) (50 * density));
        // 配置先については状況に応じ、変更してください
        bannerParam.setMargins((int)(displaymetrics.widthPixels / 2 - 160 * density), 0, 0, 0); // (4)
        bannerParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        adRelative.addView(_astWebView, bannerParam);
        _activityMain.addContentView(adBase, new ViewGroup.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        adRelative.bringToFront();
        _astWebView.loadUrl("file:///android_asset/astAd.html");
    }

    // 広告用WebView破棄
    private void deallocateAstAd() {
        if (_astWebView != null) {
            _astWebView.stopLoading();
            _astWebView.getSettings().setJavaScriptEnabled(false);
            _astWebView.setWebViewClient(null);
            _astWebView.destroy();
            _astWebView = null;
        }
    }
}
