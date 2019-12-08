package com.ns.aco.sp3.dialog;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ResolveInfo;
import android.widget.ListView;
import com.ns.aco.sp.common.dialog.SelectApplicationDialog;
import com.ns.aco.sp.common.extent.MyArrayAdapterResolveInfo;
import com.ns.aco.sp3.presenter.PresenterMain;

public class SelectAppListDialog extends SelectApplicationDialog {

	private PresenterMain _presenterMain = null;

	public SelectAppListDialog(){
		super();
		_presenterMain = PresenterMain.newInstance();
	}

	@Override
	protected String dialogTitle() {
		return null;
	}

	@Override
	protected void okClick(ListView listView) {
//		List<ResolveInfo> resolveInfoList = new ArrayList<>();
		MyArrayAdapterResolveInfo arrayAdapter = (MyArrayAdapterResolveInfo) listView.getAdapter();

//		for (Integer position : arrayAdapter.getPositionList()){
//			resolveInfoList.add(arrayAdapter.getResolveInfo(position));
//		}
		List<ResolveInfo> resolveInfoList = arrayAdapter.get_CheckedResolveInfoList();
		_presenterMain.setApplicationList(resolveInfoList);
		this.dismiss();
	}

	@Override
	protected void cancelClick(){
		_presenterMain.cancelSelectProcessDialog();
		this.dismiss();
	}
};