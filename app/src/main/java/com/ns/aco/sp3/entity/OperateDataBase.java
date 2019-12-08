package com.ns.aco.sp3.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;

import com.ns.aco.sp.common.da.DatabaseAdapter;
import com.ns.aco.sp3.R;

public class OperateDataBase {
	private Context _context = null;
	private DatabaseAdapter _dbAdapter = null;
	private AssetManager _assetManager = null;
	private InputStream assetText = null;
	private final String _dbName;
	private final int _dbVersion;
	private final String _sqlFileCreateTbl = "50_Sql/01_CreateTable/01.txt";
	private final String _sqlFileDropTbl = "50_Sql/02_DropTable/01.txt";

	public OperateDataBase(Context context){
		_context = context;
		_dbName = _context.getString(R.string.db_name);
		_dbVersion = Integer.parseInt(context.getString(R.string.db_version));
		_dbAdapter = new DatabaseAdapter();

		// 頂点情報を格納したtextファイルを読込む
		_assetManager = context.getResources().getAssets();
	}

	public void Open(){
		_dbAdapter.open(_context, _dbName, _dbVersion);
	}

	public void Close(){
		_dbAdapter.close();
	}

	// テーブルの作成
	public boolean createTable() throws IOException{

		try {
			assetText = _assetManager.open(_sqlFileCreateTbl);
			BufferedReader bufText = new BufferedReader(new InputStreamReader(assetText));
			String[] sqlQuery = bufText.readLine().split(";");
			return _dbAdapter.MultiExecuteSql(sqlQuery);
		} catch (Exception e) {
			return false;
		} finally{
			assetText.close();
		}
	}

	// テーブルの削除
	public boolean dropTable() throws IOException{

		try {
			assetText = _assetManager.open(_sqlFileDropTbl);
			BufferedReader bufText = new BufferedReader(new InputStreamReader(assetText));
			String[] sqlQuery = bufText.readLine().split(";");
			_dbAdapter.MultiExecuteSql(sqlQuery);
			return true;
		} catch (Exception e) {
			return false;
		} finally{
			assetText.close();
		}
	}

	// PACKAGENAMEテーブルのデータを取得
	public ArrayList<String[]> get_PACKAGENAME(){
		// IDに対応するレコードを取得する
		return _dbAdapter.SelectSql(
				"SELECT PACKAGENAME FROM PACKAGENAME",
				new String[]{},
				1);
	}

	// PACKAGENAMEテーブルのデータ追加
	public boolean insert_PACKAGENAME(String packageName){
		return _dbAdapter.ExecuteSql(
				"INSERT INTO PACKAGENAME ( PACKAGENAME ) VALUES ( ? )",
				new Object[]{ packageName });
	}

	// PACKAGENAMEテーブルのデータ削除
	public boolean delete_PACKAGENAME(){
		return _dbAdapter.ExecuteSql(
				"DELETE FROM PACKAGENAME",
				new Object[]{});
	}
}
