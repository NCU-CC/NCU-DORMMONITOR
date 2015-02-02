package com.da.ncudormmoitor.andr.client.activity;

import com.da.ncudormmoitor.andr.client.config.Config;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;

import java.math.BigInteger;

public class SettingActivity extends Activity {

	final static public int INTENT_FLAG = 255;
	String mIP;
	private long mUpperBoundary;
	private int mInterval;
	private boolean mNotificationTurnOn;

	private EditText IPEditText;
	private CheckedTextView TurnOnCheckView;

    private EditText mUpperBoundaryEditText;

	private Spinner spinner1;
	private Spinner spinner2;
	private String[] list1 = {"MB","GB" };
	private String[] list2 = {"5 分鐘", "10 分鐘", "30 分鐘", "60分鐘"};
    private int currentSpinnerItem = 0;

	private ArrayAdapter<String> listAdapter1;
	private ArrayAdapter<String> listAdapter2;

	private Button saveButton;
	private Button cancelButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, 0);
		mIP = settings.getString(Config.PREF_IP, "140.115.");
		mUpperBoundary = settings.getLong(Config.PREF_UpperBoundary,
				 2147483648L);
		mInterval = settings.getInt(Config.PREF_Interval, 10);
		mNotificationTurnOn = settings.getBoolean(
				Config.PREF_NotificationTurnOn, true);

		IPEditText = (EditText) findViewById(R.id.editText1);
		IPEditText.setText(mIP);

		TurnOnCheckView = (CheckedTextView) findViewById(R.id.check1);
		TurnOnCheckView.setChecked(mNotificationTurnOn);
		TurnOnCheckView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				((CheckedTextView) v).toggle();
				mNotificationTurnOn = ((CheckedTextView) v).isChecked();
			}
		});

        // 流量設定的edit text及spinner***********************************************************************
        mUpperBoundaryEditText = (EditText) findViewById(R.id.editText2);
        mUpperBoundaryEditText.setTextColor(Color.WHITE);
        spinner1 = (Spinner) findViewById(R.id.spinner1);

        listAdapter1 = new ArrayAdapter<String>(this, R.layout.ub_spinner,
                list1);
        listAdapter1
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(listAdapter1);

        long textOfmUpperBoundaryEditText = mUpperBoundary;//根據存下來的mUpperBoundary來決定要顯示GB還是MB
        if(mUpperBoundary>1048576000){
            textOfmUpperBoundaryEditText = textOfmUpperBoundaryEditText/1048576000;//GB
            spinner1.setSelection(1);
        }
        else{
            textOfmUpperBoundaryEditText = textOfmUpperBoundaryEditText/1048576;//MB
            spinner1.setSelection(0);
        }
        mUpperBoundaryEditText.setText(textOfmUpperBoundaryEditText+"");

        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try{
                    currentSpinnerItem = position;
                    Long upperBoundary = Long.valueOf(String.valueOf(mUpperBoundaryEditText.getText()));
                    if(upperBoundary == null){
                        upperBoundary = 2147483648L;
                    }
                    switch (position){
                        case 0://MB
                            mUpperBoundary = upperBoundary*1048576;
                            break;
                        case 1://GB
                            mUpperBoundary = upperBoundary*1073741824;
                            break;
                        default:
                            mUpperBoundary = upperBoundary;
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner1.setSelection(3);
            }
        });

        /*
		for (int i = 0; i < list1.length; i++) {
			if ((Float.toString(mUpperBoundary) + " G").equals(list1[i]))
				spinner1.setSelection();

		}


		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				mUpperBoundary = (float) ((position + 1) * 0.5);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});*/
        // 流量設定的spinner***********************************************************************


        // 間隔設定的spinner***********************************************************************
				spinner2 = (Spinner) findViewById(R.id.spinner2);

				listAdapter2 = new ArrayAdapter<String>(this, R.layout.ub_spinner,
						list2);
				listAdapter2
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner2.setAdapter(listAdapter2);

				for (int i = 0; i < list2.length; i++) {
					if ((Integer.toString(mInterval) + " 分鐘").equals(list2[i]))
						spinner2.setSelection(i);

				}

				spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						if(position==0)
							mInterval = 5;
						else if(position==1)
							mInterval = 10;
						else if(position==2)
							mInterval = 30;
						else
							mInterval = 60;
						

					}
				

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});
				// ���j�]�w��spinner***********************************************************************

		cancelButton = (Button) findViewById(R.id.button1);
		cancelButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		saveButton = (Button) findViewById(R.id.button2);
		saveButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(
						Config.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				mIP = IPEditText.getText().toString();
				editor.putBoolean(Config.PREF_NotificationTurnOn,
						mNotificationTurnOn);
				editor.putLong(Config.PREF_UpperBoundary, getmUpperBoundary());
				editor.putInt(Config.PREF_Interval, mInterval);
				editor.putString(Config.PREF_IP, mIP);
				editor.commit();
				Intent intent = new Intent(SettingActivity.this,
						MonitorService.class);
				startService(intent);
				intent = new Intent();
				intent.putExtra(Config.INTENT_SETTINGS_TO_MAIN, true);
				intent.setAction(Config.FILTER_MAIN);
				intent.setFlags(INTENT_FLAG);
				sendBroadcast(intent);
				finish();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Config.PREF_NotificationTurnOn, mNotificationTurnOn);
		editor.putLong(Config.PREF_UpperBoundary, mUpperBoundary);
		editor.putString(Config.PREF_IP, mIP);
		editor.commit();
	}

    private long getmUpperBoundary() {
        Long upperBoundary = Long.valueOf(String.valueOf(mUpperBoundaryEditText.getText()));
        if (upperBoundary == null) {
            upperBoundary = 2L;//下面會把它轉成byte
        }
        switch (currentSpinnerItem) {
            case 0://MB
                mUpperBoundary = upperBoundary * 1048576;
                break;
            case 1://GB
                mUpperBoundary = upperBoundary * 1073741824;
                break;
            default:
                mUpperBoundary = upperBoundary;
                break;
        }
        return mUpperBoundary;
    }
}
