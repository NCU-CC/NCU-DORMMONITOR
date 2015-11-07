package com.da.ncudormmoitor.andr.client.activity;

import java.util.ArrayList;

import com.da.ncudormmoitor.andr.client.config.Config;
import com.da.ncudormmoitor.andr.client.net.NetService;

import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
//定期抓資料監控整體狀態
public class MonitorService extends Service {

	boolean isConnected = true;
	Context mContext;
	Button mButton;
	MonitorRunnable monitorRunnable;
	Thread sendThread;
	ArrayList<String> mFlowList = null;
	private long mUpload;

	private String mIP;
	private boolean mIsLock;
	private boolean mNotificationTurnOn;
	private long mUpperBoundary;
	private int mInterval;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		monitorRunnable = new MonitorRunnable();
		sendThread = new Thread(monitorRunnable);
		sendThread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("MonitorService", "onStart()");
		if (!sendThread.isAlive()) {
			sendThread = new Thread(monitorRunnable);
			sendThread.start();
		}
		Log.d("MonitorService sendThread.isAlive()", sendThread.isAlive() + "");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class MonitorRunnable implements Runnable {

		public MonitorRunnable() {
			NetService.init();
		}

		@Override
		public void run() {
			do {
				updateSettings();
                // 這裡可以實行抓資料
				try {
					NetService.getInstance().getTotalFlow(Config.DORM_URL, mIP); // 抓總量
					mFlowList = NetService.getInstance().getDetailFlow(
							Config.DORM_URL, mIP); // 抓詳細流量
					broadcast();
					sendNotification(1);
					Thread.sleep(mInterval);
				} catch (Exception e) {
					Intent intent = new Intent();
					intent.putExtra(Config.INTENT_SAFETY, "Not connect");
					sendNotification(2);
					// isConnected = false;
					Log.e(e.getMessage(), e.getMessage());
					e.printStackTrace();
					try {
						Thread.sleep(mInterval);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			} while (isConnected);

		}
	};

	@Override
	public void onDestroy() {
		// sendThread.destroy();
		super.onDestroy();
	}

	private void updateSettings() {
		SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, 0);
		mIP = settings.getString(Config.PREF_IP, "");
		mNotificationTurnOn = settings.getBoolean(
				Config.PREF_NotificationTurnOn, true);
		mUpperBoundary = settings.getLong(Config.PREF_UpperBoundary,2147483648L);//(float) 2.0 @edited, long integer in java ended with L
		mInterval = settings.getInt(Config.PREF_Interval, 10) * 60 * 1000;
	}

	private void broadcast() {
		// Broadcast to main

		String tempUpload = NetService.getInstance().getUpLoadFlow();
		//mUpload = Float.parseFloat(tempUpload.substring(
				//tempUpload.indexOf("(") + 1, tempUpload.indexOf("G") - 1));
        mUpload = Long.parseLong(tempUpload);
		Log.d("upload from MointorService", mUpload + "");

		Intent intent = new Intent();
		try {
			mIsLock = NetService.getInstance().isLock(Config.DORM_URL, mIP);
			if (mIsLock) {
				intent.putExtra(Config.INTENT_SAFETY, Config.INTENT_STATE_LOCK);
				if (NetService.getInstance().getLockMsg() != null) {
					intent.putExtra(Config.INTENT_LOCK_MSG, NetService
							.getInstance().getLockMsg());
					sendNotification(3);
				}
			} else if (mUpload >= mUpperBoundary) {
				intent.putExtra(Config.INTENT_SAFETY,
						Config.INTENT_STATE_DANGEROUS);
			} else {
				intent.putExtra(Config.INTENT_SAFETY, Config.INTENT_STATE_SAFE);
			}

			intent.putExtra(Config.INTENT_DOWNLOAD_ALL, NetService
					.getInstance().getDownLoadFlowAll());
			intent.putExtra(Config.INTENT_DOWNLOAD, NetService.getInstance()
					.getDownLoadFlow());
			intent.putExtra(Config.INTENT_UPLOAD_ALL, NetService.getInstance()
					.getUpLoadFlowAll());
			intent.putExtra(Config.INTENT_UPLOAD, NetService.getInstance()
					.getUpLoadFlow());

		} catch (IndexOutOfBoundsException e) {
			// IP有錯或是網路錯誤造成取不到總量,總量的arrayList是空的
			intent.putExtra(Config.INTENT_SAFETY, "Not connect");
			// isConnected = false;
			Log.e("IP有錯或是網路錯誤造成取不到總量", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		intent.setAction(Config.FILTER_MAIN);
		sendBroadcast(intent);
		// broadcast to detail
		intent = new Intent();
		intent.putExtra(Config.INTENT_FLOW, mFlowList);
		intent.setAction(Config.FILTER_DETAIL);
		sendBroadcast(intent);
	}

	@SuppressWarnings("deprecation")
	private void sendNotification(int flag) {
		if (flag == 1) { // 超量通知
			if (mNotificationTurnOn && mUpload >= mUpperBoundary && !mIsLock) {
				Intent notificationIntent = new Intent(this, MainActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this,
						0, notificationIntent, 0);
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification noti = new Notification.Builder(mContext)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker(getString(R.string.noti_tickerText))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentTitle(getString(R.string.noti_title))
                        .setContentText(getString(R.string.noti_content))
                        .setWhen(System.currentTimeMillis())
                        .build();
				notificationManager.notify(0, noti);
			}
		} else if (flag == 2) { // IP或網路錯誤通知
			if (mNotificationTurnOn) {
				Intent notificationIntent = new Intent(this, MainActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this,
						0, notificationIntent, 0);
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification noti = new Notification.Builder(mContext)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker(getString(R.string.noti_tickerText2))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle(getString(R.string.noti_title2))
                        .setContentText(getString(R.string.noti_content2))
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .build();
				notificationManager.notify(0, noti);
			}
		} else if (flag == 3) { //
			if (mIsLock) {
				Intent notificationIntent = new Intent(this, MainActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this,
						0, notificationIntent, 0);
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                          Notification noti = new Notification.Builder(mContext)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker(getString(R.string.noti_tickerText3))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle(getString(R.string.noti_title3))
                        .setContentText(getString(R.string.noti_content3))
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .build();
				notificationManager.notify(0, noti);
			}
		}
	}
}