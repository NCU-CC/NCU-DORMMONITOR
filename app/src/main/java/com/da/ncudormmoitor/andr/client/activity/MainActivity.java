package com.da.ncudormmoitor.andr.client.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.da.ncudormmoitor.andr.client.config.Config;
import com.da.ncudormmoitor.andr.client.net.NetService;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

	ProgressDialog mProgressDialog;
	ArrayList<String> mFlowDetailList;

	ImageButton settingButton;
	ImageButton displayButton;
	ImageButton reConnectButton;
	TextView IPView;
	TextView uploadMessege;
	TextView uploadView;
	TextView downloadMessege;
	TextView downloadView;
	TextView uploadAllMessege;
	TextView uploadAllView;
	TextView downloadAllMessege;
	TextView downloadAllView;
    TextView uploadViewInByte;
    TextView downloadViewInByte;
    TextView uploadAllViewInByte;
    TextView downloadAllViewInByte;
    TextView stateView;

	BroadcastReceiver mReceiver;

	SharedPreferences settings;

	private boolean mIsFresh = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("MainActivity", "OnCreate()");
		// 設定Activity的View
		viewSetup();
		// 設定receiver接收MoniterService來的資料
		receiverSetup();
		// 剛create時就建立service去run
		Intent intent = new Intent(MainActivity.this, MonitorService.class);
		startService(intent);
	}

	@Override
	protected void onStart() {
		Log.d("MainActivty", "OnStart()" + mIsFresh);
		viewChange();
		if (isOutOfTime() || mIsFresh) {
			Log.d("Main", "new RefreshTask().execute()");
			new RefreshTask().execute();
		}
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {

		if (arg0.getId() == R.id.imageButton1) {
			Intent intent = new Intent(MainActivity.this, SettingActivity.class);
			startActivity(intent);
		} else if (arg0.getId() == R.id.imageButton2) {
			Intent intent = new Intent(MainActivity.this, DetailActivity.class);
			intent.putExtra(Config.INTENT_MAIN_TO_DETAIL, mFlowDetailList);
			startActivity(intent);
		} else if (arg0.getId() == R.id.connetButton) {
			new RefreshTask().execute();
			//reConnectButton.setVisibility(View.INVISIBLE);
		}

	}

	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		super.onPause();
		saveData();
		mIsFresh = false;
	}

	private void viewSetup() {
		settings = getSharedPreferences(Config.PREFS_NAME, 0);
		settingButton = (ImageButton) findViewById(R.id.imageButton1);
		displayButton = (ImageButton) findViewById(R.id.imageButton2);
		reConnectButton = (ImageButton) findViewById(R.id.connetButton);
		IPView = (TextView) findViewById(R.id.IPtextView);
		uploadMessege = (TextView) findViewById(R.id.textView1);
		downloadMessege = (TextView) findViewById(R.id.textView2);
		uploadAllMessege = (TextView) findViewById(R.id.textView3);
		downloadAllMessege = (TextView) findViewById(R.id.textView4);
		uploadView = (TextView) findViewById(R.id.textView5);
		downloadView = (TextView) findViewById(R.id.textView6);
		uploadAllView = (TextView) findViewById(R.id.textView7);
		downloadAllView = (TextView) findViewById(R.id.textView8);
        uploadViewInByte = (TextView) findViewById(R.id.textView9);
        downloadViewInByte = (TextView) findViewById(R.id.textView10);
        uploadAllViewInByte = (TextView) findViewById(R.id.textView11);
        downloadAllViewInByte = (TextView) findViewById(R.id.textView12);
		stateView = (TextView) findViewById(R.id.textView13);
		reConnectButton.setOnClickListener(this);
		settingButton.setOnClickListener(this);
		displayButton.setOnClickListener(this);
		stateView.setText("尚未取得流量資訊");
		stateView.setTextColor(Color.BLACK);
	}

	@SuppressLint("NewApi")
	private void viewChange() {
		settings = getSharedPreferences(Config.PREFS_NAME, 0);//第一個要改的地方-sharedPreferences
		IPView.setText("當前 IP: "
				+ settings.getString(Config.PREF_IP, "當前 IP: 140.115."));
		Set<String> flowSet = settings.getStringSet(Config.PREF_MAIN_FLOWS,
				null);
		Set<String> flowDetailSet = settings.getStringSet(Config.PREF_MAIN_FLOWS_DATAIL,
				null);
		String state = settings.getString(Config.PREF_MAIN_STATE, null);
		int state_color = settings.getInt(Config.PREF_MAIN_STATE_COLOR,
				Color.BLACK);

		if (flowSet != null) {
			try {
				List<String> flowList = new ArrayList<String>(flowSet);
				mFlowDetailList = new ArrayList<String>(flowDetailSet);

                downloadView.setText(byteToProperUnit(flowList.get(0)));
                downloadAllView.setText(byteToProperUnit(flowList.get(1)));
                uploadAllView.setText(byteToProperUnit(flowList.get(2)));
                uploadView.setText(byteToProperUnit(flowList.get(3)));

                downloadViewInByte.setText(flowList.get(0));
                downloadAllViewInByte.setText(flowList.get(1));
                uploadAllViewInByte.setText(flowList.get(2));
                uploadViewInByte.setText(flowList.get(3));

                /*
                Integer upload = Integer.valueOf(flowList.get(0))/1000000;
			    Integer download = Integer.valueOf(flowList.get(1))/1000000;
                Integer uploadAll = Integer.valueOf(flowList.get(2))/1000000;
                Integer downloadAll = Integer.valueOf(flowList.get(3))/1000000;
                uploadView.setText(upload.toString()+"MB");
                downloadView.setText(download.toString()+"MB");
                uploadAllView.setText(uploadAll.toString()+"MB");
                downloadAllView.setText(downloadAll.toString()+"MB");
                */
			    /*
				uploadView.setText(flowList.get(0));
				downloadView.setText(flowList.get(1));
				uploadAllView.setText(flowList.get(2));
				downloadAllView.setText(flowList.get(3));
                */
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (state != null) {
			stateView.setText(state);
			stateView.setTextColor(state_color);
		} else {
			stateView.setText("尚未取得流量資訊");
			stateView.setTextColor(Color.BLACK);
		}
		//reConnectButton.setVisibility(View.INVISIBLE);
	}

	private void receiverSetup() {
		mReceiver = new BroadcastReceiver() {//接收從MonitorService來的Broadcast
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("MainActivity", "OnReceive");
				// 處理接到的內容
				if (intent.getFlags() == SettingActivity.INTENT_FLAG) {
					Log.d("Setting按下確定",
							intent.getBooleanExtra(
									Config.INTENT_SETTINGS_TO_MAIN, false) + "");
					mIsFresh = intent.getBooleanExtra(
							Config.INTENT_SETTINGS_TO_MAIN, false);
				} else {
					// 四個總流量(用大單位表示)
					downloadView.setText(byteToProperUnit(intent
                            .getStringExtra(Config.INTENT_DOWNLOAD)));
					downloadAllView.setText(byteToProperUnit(intent
							.getStringExtra(Config.INTENT_DOWNLOAD_ALL)));
					uploadAllView.setText(byteToProperUnit(intent
							.getStringExtra(Config.INTENT_UPLOAD_ALL)));
					uploadView.setText(byteToProperUnit(intent
							.getStringExtra(Config.INTENT_UPLOAD)));
                    // 四個總流量(用Byte表示)
                    downloadViewInByte.setText(intent
                            .getStringExtra(Config.INTENT_DOWNLOAD));
                    downloadAllViewInByte.setText(intent
                            .getStringExtra(Config.INTENT_DOWNLOAD_ALL));
                    uploadAllViewInByte.setText(intent
                            .getStringExtra(Config.INTENT_UPLOAD_ALL));
                    uploadViewInByte.setText(intent
                            .getStringExtra(Config.INTENT_UPLOAD));

					if (intent.getStringExtra(Config.INTENT_SAFETY).equals(
							Config.INTENT_STATE_SAFE)) { // 安全的情況
						stateView.setText(R.string.main_state_safe);
						stateView.setTextColor(Color.GREEN);
                        uploadView.setBackgroundColor(Color.rgb(255, 255, 187));
                        uploadViewInByte.setBackgroundColor(Color.rgb(255, 255, 187));
						//reConnectButton.setVisibility(View.INVISIBLE);
					} else if (intent.getStringExtra(Config.INTENT_SAFETY)
							.equals(Config.INTENT_STATE_DANGEROUS)) { // 有危險的情況
						stateView.setText(R.string.main_state_dangerous);
                        long upperBoundary = settings.getLong(Config.PREF_UpperBoundary, 2147483648L);
                        if(upperBoundary>1048576000){
                            long upperBoundaryText = upperBoundary/1048576000;//GB
                            stateView.append(upperBoundaryText+"GB");
                        }
                        else{
                            long upperBoundaryText = upperBoundary/1048576;//MB
                            stateView.append(upperBoundaryText+"MB");
                        }
                        //stateView.append(String.format("%.1f", settings.getFloat(Config.PREF_UpperBoundary, 2))+"G.");//@edited
						stateView.setTextColor(Color.rgb(255, 69, 0));
						uploadView.setBackgroundColor(Color.rgb(255, 69, 0));
                        uploadViewInByte.setBackgroundColor(Color.rgb(255, 69, 0));
					} else if (intent.getStringExtra(Config.INTENT_SAFETY)
							.equals("Not connect")) {
						stateView.setText(R.string.main_state_error);
						stateView.setTextColor(Color.GRAY);
                        uploadView.setBackgroundColor(Color.rgb(255, 255, 187));
                        uploadViewInByte.setBackgroundColor(Color.rgb(255, 255, 187));
						reConnectButton.setVisibility(View.VISIBLE);
						Intent stopIntent = new Intent(MainActivity.this,
								MonitorService.class);
						stopService(stopIntent);
					} else { // 鎖網的情況
						stateView.setText(intent
								.getStringExtra(Config.INTENT_LOCK_MSG));
						stateView.setTextColor(Color.RED);
						uploadView.setBackgroundColor(Color.RED);
                        uploadViewInByte.setBackgroundColor(Color.RED);
					}
					stateView.append("(" + getDateTime() + ")");
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Config.FILTER_MAIN);
		registerReceiver(mReceiver, filter);
	}
    //用在receiverSetup()中，設定downloadView、downloadAllView、uploadAllView、uploadView的文字時
    //把MonitorService送來單位為byte的流量字串轉成單位為MB的流量字串。
    public String byteToProperUnit(String stringToBeCoverted){
        if(stringToBeCoverted!=null){
            Long FlowInByte = Long.parseLong(stringToBeCoverted);
            if(FlowInByte > 1073741824L){
                FlowInByte = FlowInByte/1073741824L;
                return FlowInByte.toString()+" GB";
            }
            else{
                FlowInByte = FlowInByte/1048576L;
                return FlowInByte.toString()+" MB";
            }
        }
        else{
            return "";
        }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent(MainActivity.this, MonitorService.class);
		stopService(intent);
		unregisterReceiver(mReceiver);
	}

	private class RefreshTask extends AsyncTask<Void, Void, Void> {
		private boolean isLock;
		private String lockMsg;
		private String ip;
		private long upload;
		private long upperBoundary;
		private ArrayList<String> totalList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			NetService.init();
			updateSettings();
			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setTitle("抓取資料中...");
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				//
				mFlowDetailList = NetService.getInstance().getDetailFlow(
						Config.DORM_URL, ip);
				//
				isLock = NetService.getInstance().isLock(Config.DORM_URL, ip);
				lockMsg = NetService.getInstance().getLockMsg();
				totalList = NetService.getInstance().getTotalFlow(
						Config.DORM_URL, ip);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// Set title into TextView
			try {
				String tempUpload = totalList.get(NetService.UPLOAD_OUT_LONG_INDEX);//UPLOAD_OUT_INDEX@edited
				upload = Long.parseLong(tempUpload);
				Log.d("MainActivity.RefreshTask.upload", upload + "");

                Log.e("debug", String.valueOf(isLock));

				if (isLock) { // 鎖網的情況
					stateView.setText(lockMsg);
					stateView.setTextColor(Color.RED);
					uploadView.setBackgroundColor(Color.RED);
                    uploadViewInByte.setBackgroundColor(Color.RED);
				} else if (upload >= upperBoundary) { // 有危險的情況
					stateView.setText(R.string.main_state_dangerous);
                    if(upperBoundary>1048576000){
                        long upperBoundaryText = upperBoundary/1048576000;//GB
                        stateView.append(upperBoundaryText+"GB");
                    }
                    else{
                        long upperBoundaryText = upperBoundary/1048576;//MB
                        stateView.append(upperBoundaryText+"MB");
                    }
					//stateView.append(String.format("%.1f", settings.getFloat(Config.PREF_UpperBoundary, 2))+"G.");//@edited
					stateView.setTextColor(Color.rgb(255, 69, 0));
					uploadView.setBackgroundColor(Color.rgb(255, 69, 0));
                    uploadViewInByte.setBackgroundColor(Color.rgb(255, 69, 0));
				} else { // 安全的情況
					stateView.setText(R.string.main_state_safe);
					stateView.setTextColor(Color.GREEN);
                    uploadView.setBackgroundColor(Color.rgb(255, 255, 187));
                    uploadViewInByte.setBackgroundColor(Color.rgb(255, 255, 187));
					//reConnectButton.setVisibility(View.INVISIBLE);
				}
                try{
                    Integer upload = Integer.valueOf(totalList.get(NetService.UPLOAD_OUT_LONG_INDEX));
                    Integer uploadAll = Integer.valueOf(totalList.get(NetService.UPLOAD_ALL_LONG_INDEX));
                    Integer download = Integer.valueOf(totalList.get(NetService.DOWNLOAD_OUT_LONG_INDEX));
                    Integer downloadAll = Integer.valueOf(totalList.get(NetService.DOWNOAD_ALL_LONG_INDEX));

                    uploadView.setText(byteToProperUnit(upload.toString()));
                    uploadAllView.setText(byteToProperUnit(uploadAll.toString()));
                    downloadView.setText(byteToProperUnit(download.toString()));
                    downloadAllView.setText(byteToProperUnit(downloadAll.toString()));

                    /*
                    Integer upload = Integer.valueOf(totalList.get(NetService.UPLOAD_OUT_LONG_INDEX))/1048576;
                    Integer uploadAll = Integer.valueOf(totalList.get(NetService.UPLOAD_ALL_LONG_INDEX))/1048576;
                    Integer download = Integer.valueOf(totalList.get(NetService.DOWNLOAD_OUT_LONG_INDEX))/1048576;
                    Integer downloadAll = Integer.valueOf(totalList.get(NetService.DOWNOAD_ALL_LONG_INDEX))/1048576;

                    if(upload > 1024){
                        upload/=1024;
                        uploadView.setText(upload.toString() + "GB");
                    }else{
                        uploadView.setText(upload.toString() + "MB");
                    }


                    if(uploadAll > 1024){
                        uploadAll/=1024;
                        uploadAllView.setText(uploadAll.toString() + "GB");
                    }else{
                        uploadAllView.setText(uploadAll.toString() + "MB");
                    }

                    if(download > 1024){
                        download/=1024;
                        downloadView.setText(download.toString() + "GB");
                    }else{
                        downloadView.setText(download.toString() + "MB");
                    }

                    if(downloadAll > 1024){
                        downloadAll/=1024;
                        downloadAllView.setText(downloadAll.toString() + "GB");
                    }else{
                        downloadAllView.setText(downloadAll.toString() + "MB");
                    }*/
                    //第三個修改的地方
                    uploadViewInByte.setText(totalList.get(NetService.UPLOAD_OUT_LONG_INDEX));
                    uploadAllViewInByte.setText(totalList
                            .get(NetService.UPLOAD_ALL_LONG_INDEX));
                    downloadViewInByte.setText(totalList
                            .get(NetService.DOWNLOAD_OUT_LONG_INDEX));
                    downloadAllViewInByte.setText(totalList
                            .get(NetService.DOWNOAD_ALL_LONG_INDEX));

                }catch (Exception e){
                    e.printStackTrace();
                    uploadView.setText(totalList.get(NetService.UPLOAD_OUT_INDEX));
                    uploadAllView.setText(totalList
                            .get(NetService.UPLOAD_ALL_INDEX));
                    downloadView.setText(totalList
                            .get(NetService.DOWNLOAD_OUT_INDEX));
                    downloadAllView.setText(totalList
                            .get(NetService.DOWNOAD_ALL_INDEX));
                    uploadViewInByte.setText(totalList.get(NetService.UPLOAD_OUT_LONG_INDEX));
                    uploadAllViewInByte.setText(totalList
                            .get(NetService.UPLOAD_ALL_LONG_INDEX));
                    downloadViewInByte.setText(totalList
                            .get(NetService.DOWNLOAD_OUT_LONG_INDEX));
                    downloadAllViewInByte.setText(totalList
                            .get(NetService.DOWNOAD_ALL_LONG_INDEX));
                }


                /*
				uploadView.setText(totalList.get(NetService.UPLOAD_OUT_LONG_INDEX));
				uploadAllView.setText(totalList
						.get(NetService.UPLOAD_ALL_LONG_INDEX));
				downloadView.setText(totalList
						.get(NetService.DOWNLOAD_OUT_LONG_INDEX));
				downloadAllView.setText(totalList
						.get(NetService.DOWNOAD_ALL_LONG_INDEX));
				*/
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				Log.e("IP有錯或是網路錯誤造成取不到總量", e.getMessage());
				stateView.setText(R.string.main_state_error);
				stateView.setTextColor(Color.GRAY);
				reConnectButton.setVisibility(View.VISIBLE);
			}
			stateView.append("(" + getDateTime() + ")");
			mProgressDialog.dismiss();
		}

		private void updateSettings() {
			SharedPreferences settings = getSharedPreferences(
					Config.PREFS_NAME, 0);
			ip = settings.getString(Config.PREF_IP, "");
			upperBoundary = settings.getLong(Config.PREF_UpperBoundary,
                    2147483648L);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String getDateTime() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		System.out.println(strDate);
		return strDate;
	}

	private boolean isOutOfTime() {
		long currentTime = System.currentTimeMillis();
		Date date = new Date(settings.getLong(Config.PREF_LAST_UPDATE_TIME, 0));
		long prefTime = date.getTime();
		if (currentTime - prefTime >= Config.UPDATE_DELAY) {
			Log.d("isOutOfTime()", "上次更新時間:" + date + " 已在10分鐘前 ");
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong(Config.PREF_LAST_UPDATE_TIME, currentTime);
			editor.commit();
			return true;
		} else {
			Log.d("isOutOfTime()", "" + "上次更新時間:" + date + " 距離上次刷新還不到10分鐘");
			return false;
		}
	}

	@SuppressLint("NewApi")
	private void saveData() {
		settings = getSharedPreferences(Config.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		// save flows
		Set<String> flowSet = new HashSet<String>();
		List<String> flowList = new ArrayList<String>();
		flowList.add(uploadView.getText() + "");
		flowList.add(downloadView.getText() + "");
		flowList.add(uploadAllView.getText() + "");
		flowList.add(downloadAllView.getText() + "");
		flowSet.addAll(flowList);
		editor.putStringSet(Config.PREF_MAIN_FLOWS, flowSet);
		// save state
		editor.putString(Config.PREF_MAIN_STATE, stateView.getText() + "");
		editor.putInt(Config.PREF_MAIN_STATE_COLOR,
				stateView.getCurrentTextColor());
		editor.commit();
	}

}