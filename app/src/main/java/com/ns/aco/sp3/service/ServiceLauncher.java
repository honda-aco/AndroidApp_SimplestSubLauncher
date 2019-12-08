package com.ns.aco.sp3.service;

import com.ns.aco.sp.common.extent.MyListView;
import com.ns.aco.sp.common.listener.OnDropListenerIF;
import com.ns.aco.sp.common.util.UtilityImageView;
import com.ns.aco.sp.common.util.UtilityView;
import com.ns.aco.sp.common.view.ViewDragAndDrop;
import com.ns.aco.sp3.R;
import com.ns.aco.sp3.presenter.PresenterMain;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ServiceLauncher extends Service {

	private String TAG = "LocalService";
	private PresenterMain _presenterMain = null;
	private WindowManager _windowManager = null;
	private WindowManager.LayoutParams _windowManagerLP = null;
	// サブランチャーの位置
	private String _position = null;
	private int _windowWidth = 0;
	private int _windowHeight = 0;
	private int _viewSize = 0;
	private int _paddingWidth = 0;
	// ApplicationListオープン時の
	private LinearLayout _linearLayoutOpen = null;
	private View _spaceView = null;
	private ImageView _imageLauncherOpen = null;
	private MyListView _listApplication = null;
	private ImageView _imageLauncherClose = null;
	private View.DragShadowBuilder _shadowImageLauncherClose = null;
	private ViewDragAndDrop _viewDragAndDrop = null;

	public WindowManager get_windowManager() {
		return _windowManager;
	}

	public WindowManager.LayoutParams get_windowManagerLP() {
		return _windowManagerLP;
	}

	public String get_position() {
		return _position;
	}

	public void set_position(String _position) {
		this._position = _position;
	}

	public int get_windowWidth() {
		return _windowWidth;
	}

	public int get_windowHeight() {
		return _windowHeight;
	}

	public int get_viewSize() {
		return _viewSize;
	}

	public int get_paddingWidth() {
		return _paddingWidth;
	}

	public LinearLayout get_linearLayoutOpen() {
		return _linearLayoutOpen;
	}

	public View get_spaceView() {
		return _spaceView;
	}

	public ImageView get_imageLauncherOpen() {
		return _imageLauncherOpen;
	}

	public ListView get_listApplication() {
		return _listApplication;
	}

	public ImageView get_imageLauncherClose() {
		return _imageLauncherClose;
	}

	public View.DragShadowBuilder get_shadowImageLauncherClose() {
		return _shadowImageLauncherClose;
	}

	public ViewDragAndDrop get_viewDragAndDrop() {
		return _viewDragAndDrop;
	}

	@Override
    public void onCreate() {
    	super.onCreate();
        Log.i(TAG, "onCreate");

        // 通知なくサービスをForegroundとして登録する
        startForeground(1, new Notification());
    }

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	Log.i(TAG, "onStartCommand Received start id " + startId + ": " + intent);

		_presenterMain = PresenterMain.newInstance();

		_position = intent.getStringExtra(PresenterMain.getExtraName1());
		_windowWidth = intent.getIntExtra(PresenterMain.getExtraName2(), 0);
		_windowHeight = intent.getIntExtra(PresenterMain.getExtraName3(), 0);

		UtilityView utilityView = new UtilityView(getBaseContext());
		_viewSize = (int) utilityView.convertDpToPixel(50.0f);
		_paddingWidth = (int) utilityView.convertDpToPixel(10.0f);

		// OpenGL描画用Viewに被せるための画像のViewをサービスに追加
		Bitmap bitmapFrontClose = UtilityImageView.getBitmapSize(
				getBaseContext(),
				R.drawable.launcher_open,
				_viewSize,
				_viewSize
		);

		// Launcherを閉じた状態のViewの設定
		_imageLauncherClose = new ImageView(this);
		_imageLauncherClose.setScaleType(ImageView.ScaleType.FIT_XY);
		_imageLauncherClose.setImageBitmap(bitmapFrontClose);
		_imageLauncherClose.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						_presenterMain.showApplicationList(ServiceLauncher.this);
					}
				}
		);
		_imageLauncherClose.setOnLongClickListener(
				new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						return _presenterMain.dragImageLauncherClose(ServiceLauncher.this, v);
					}
				}
		);

		// Drag&Drop用のフレームレイアウトにImageViewを追加
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(_viewSize, _viewSize);
		if (_position.equals(getString(R.string.position_top_left))) {
			layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		} else if (_position.equals(getString(R.string.position_top_right))) {
			layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
		} else if (_position.equals(getString(R.string.position_bottom_left))) {
			layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
		} else {
			layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		}
		_viewDragAndDrop = new ViewDragAndDrop(
				getBaseContext(),
				new OnDropListenerIF.OnDropListener() {
					@Override
					public void drop(View view) {
						_presenterMain.dropImageLauncherClose(ServiceLauncher.this, view);
					}
					@Override
					public void dragEnded() {
						_presenterMain.updateImageLauncherClose(ServiceLauncher.this);
					}
				});
		// ImageViewをFrameLayoutに追加
		_viewDragAndDrop.get_frameDragDrop().addView(_imageLauncherClose, layoutParams);
		_viewDragAndDrop.get_frameDragDrop().setPadding(_paddingWidth, 0, _paddingWidth, 0);
		// Drag用にShadowオブジェクトを生成
		_shadowImageLauncherClose = new View.DragShadowBuilder(_imageLauncherClose);

		// Launcherを開いた状態のViewの設定
		// サブランチャーを下に持っていった場合に上部のスペースを確保するためのView
		_spaceView = new View(this);
		LinearLayout.LayoutParams spaceLayoutParams = new LinearLayout.LayoutParams(0, 0);
		spaceLayoutParams.weight = 1;
		_spaceView.setLayoutParams(spaceLayoutParams);

		// サブランチャーイメージの設定
		Bitmap bitmapFrontOpen = UtilityImageView.getBitmapSize(
				getBaseContext(),
				R.drawable.launcher_close,
				_viewSize,
				_viewSize
		);

		_imageLauncherOpen = new ImageView(this);
		_imageLauncherOpen.setScaleType(ImageView.ScaleType.FIT_XY);
		_imageLauncherOpen.setImageBitmap(bitmapFrontOpen);
		_imageLauncherOpen.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						_presenterMain.closeApplicationList(ServiceLauncher.this);
					}
				});

		// ListViewの設定
		View layoutApplicationList = View.inflate(this, R.layout.application_list, null);
		_linearLayoutOpen = (LinearLayout) layoutApplicationList.findViewById(R.id.linearApplicationList);
		_linearLayoutOpen.setPadding(_paddingWidth, 0, _paddingWidth, 0);

		_listApplication = (MyListView) layoutApplicationList.findViewById(R.id.listApplication);
		_listApplication.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						try{
							_presenterMain.startApplication(ServiceLauncher.this, parent, position);
						}catch(Exception e){
							Toast.makeText(getBaseContext(), getString(R.string.error_message1), Toast.LENGTH_SHORT).show();
						}
					}
				}
		);
		_presenterMain.setPackageListAtListView(_listApplication);

		// ListViewのサイズが親のLinearLayoutより大きいとサブランチャーが画面外に追いやられるため高さを制御する
		int listItemHeight = (int) utilityView.convertDpToPixel(50.0f);
		int listViewHeight = listItemHeight * _listApplication.getAdapter().getCount();
		if ((listViewHeight + _viewSize) > _windowHeight){
			int height = 0;
			// ListViewの高さはListItemの高さ * Xでないと（端数が出ると）表示が崩れるため
			for (int i = 1; (listItemHeight * i) < (_windowHeight - _viewSize); i++){
				height = listItemHeight * i;
			}
			_listApplication.setLayoutParams(
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							height
					));
		}

		// Viewのサイズを設定する
		_windowManagerLP = new WindowManager.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSPARENT
		);

		// WindowManagerの位置と中のViewを設定
		_windowManager = (WindowManager)getBaseContext().getSystemService(Context.WINDOW_SERVICE);
		_presenterMain.switchWindowManagerPosition(this);
		_presenterMain.closeApplicationList(this);

		//明示的にサービスの起動、停止が決められる場合の返り値
		// return START_STICKY; 			// IntentがNULLの可能性がある
		return START_REDELIVER_INTENT; // IntentがNULLにならないが再起動までの待機時間が長い
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
        Log.i(TAG, "onDestroy");
        // foregroundを解除し、サービスを終了できる状態にする
        stopForeground(true);

		try{
			_windowManager.removeView(_viewDragAndDrop.get_frameDragDrop());
		}catch(Exception e){
			Log.d("ServiceLauncher","_windowManager.removeView(_linearLayoutClose)で例外");
		}

		try{
			_windowManager.removeView(_linearLayoutOpen);
		}catch(Exception e){
			Log.d("ServiceLauncher","_windowManager.removeView(_linearLayoutOpen)で例外");
		}
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
