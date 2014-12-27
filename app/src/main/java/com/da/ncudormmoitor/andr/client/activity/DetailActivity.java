package com.da.ncudormmoitor.andr.client.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.da.ncudormmoitor.andr.client.config.Config;
import com.da.ncudormmoitor.andr.client.net.NetService;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class DetailActivity extends Activity {

	Button mShowDiagramButton;
	NetService netService;
	ListView mDataListView;
	ListView mTimeListView;
	ArrayAdapter mDataAdapter;
	ArrayAdapter adapter;
	ArrayList<String> mData;
	ArrayList<String> diagramData;
	Context mContext;

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("DetailActivity", "OnReceiver");

			try {
				@SuppressWarnings("unchecked")
				ArrayList<String> flow = (ArrayList<String>) getIntent()
						.getSerializableExtra(Config.INTENT_MAIN_TO_DETAIL);
				// 處理接到的內容
				Log.d("DetailActivity從Service收到的第一筆資料", flow.get(0));
				diagramData = flow;
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				Date mDate = new Date();
				long mTime = mDate.getTime();
				if (mTime % (24 * 60 * 6) != 0)
					mTime = (mTime / (1000 * 60 * 10)) * (1000 * 60 * 10);
				String mTimeStr = sdf.format(mDate);

				mData.clear();
				for (int i = 0; i < 144; i++) {
					Date date1 = new Date(mTime);
					Date date2 = new Date(mTime + 10 * 60 * 1000);
					String flowdata = flow.get(i);
					while (flowdata.length() < 10)
						flowdata = "0" + flowdata;
					mData.add(sdf.format(date1) + "-" + sdf.format(date2)
							+ "\t\t|\t\t" + flowdata + "  byte");
					mTime -= 10 * 60 * 1000;
				}

				mDataAdapter.notifyDataSetChanged();
				mShowDiagramButton.setEnabled(true);
			} catch (IndexOutOfBoundsException e) {
				Toast.makeText(mContext, R.string.detail_toast_short,
						Toast.LENGTH_LONG).show();
			} catch (NullPointerException e) {
				Toast.makeText(mContext, R.string.detail_toast_short,
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		mContext = this;
		mData = new ArrayList<String>();
		mData.add("資料連線中...");
		initView();
		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> flow = (ArrayList<String>) getIntent()
					.getSerializableExtra(Config.INTENT_MAIN_TO_DETAIL);
			// 處理接到的內容
			Log.d("DetailActivity從Service收到的第一筆資料", flow.get(0));
			diagramData = flow;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			Date mDate = new Date();
			long mTime = mDate.getTime();
			if (mTime % (24 * 60 * 6) != 0)
				mTime = (mTime / (1000 * 60 * 10)) * (1000 * 60 * 10);
			String mTimeStr = sdf.format(mDate);

			mData.clear();
			for (int i = 0; i < 144; i++) {
				Date date1 = new Date(mTime);
				Date date2 = new Date(mTime + 10 * 60 * 1000);
				String flowdata = flow.get(i);
				while (flowdata.length() < 10)
					flowdata = "0" + flowdata;
				mData.add(sdf.format(date1) + "-" + sdf.format(date2)
						+ "\t\t|\t\t" + flowdata + "  byte");
				mTime -= 10 * 60 * 1000;
			}

			mDataAdapter.notifyDataSetChanged();
			mShowDiagramButton.setEnabled(true);
		} catch (IndexOutOfBoundsException e) {
			Toast.makeText(mContext, R.string.detail_toast_short,
					Toast.LENGTH_LONG).show();
		} catch (NullPointerException e) {
			Toast.makeText(mContext, R.string.detail_toast_short,
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Config.FILTER_DETAIL);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		Log.d("DetailActivity", "onPause()");
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	private void initView() {
		mDataListView = (ListView) findViewById(R.id.listView1);
		mShowDiagramButton = (Button) findViewById(R.id.button1);
		mShowDiagramButton.setEnabled(false);
		mDataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mData);
		mDataListView.setAdapter(mDataAdapter);
		mShowDiagramButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DetailActivity.this,
						DiagramActivity.class);
				intent.putExtra("diagram_data", diagramData);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
