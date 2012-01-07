package com.piliguerra.android.hbpm;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.piliguerra.android.hbpm.anim.CustomCycleInterpolator;
import com.piliguerra.android.hbpm.db.DatabaseAdapter;

public class HeartBeatPerMusicActivity extends Activity {
	private static final String LOG_TAG = "HBPMActivity";
	private static final int REQUEST_ENABLE_BT = 1;
	
	private static final String NAME = "PolarHRMonitor";
	
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private Set<BluetoothDevice> pairedDevices = null;
	BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice polarBtDev;
	private static final Boolean ReadAsString = false;
	private DatabaseAdapter mDbHelper;
	private ImageView heart;
	public AnimationDrawable heartbeat;
	private ScaleAnimation hbAnim;
	private CustomCycleInterpolator ci;
	
	private final PolarCommsTask polarCommsTsk = new PolarCommsTask();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		AnimationSet rootSet = new AnimationSet(true);
		ci = new CustomCycleInterpolator(70.0f);
		hbAnim = new ScaleAnimation(0.9f,1.0f,0.9f,1.0f,0.9f,0.9f);
		hbAnim.setStartOffset(0);
		hbAnim.setDuration(60000);
		hbAnim.setRepeatCount(Animation.INFINITE);
		rootSet.addAnimation(hbAnim);
		hbAnim.setInterpolator(ci);
		
		ImageView heartImage = (ImageView) findViewById(R.id.heart);
		
		heartImage.startAnimation(hbAnim);
/*
        
        ImageView heartimg = (ImageView) findViewById(R.id.heart);
        heartimg.setBackgroundResource(R.drawable.hear);

       heartbeat = (AnimationDrawable) heartimg.getBackground();
*/
        /* heartbeat = new CustomAnimationDrawable(); 
        int n = filesNames.length; 
        Drawable frame; 
        for (int i = 0; i < n; i++) { 
        	frame = new BitmapDrawable(fileman.getImage(dir, filesNames[i])); 
        	animDrawable.addFrame(frame, 1000); 
        	} 
        myImageView.setBackgroundDrawable(animDrawable); 
        heartbeat.setOneShot(false); 
        heartbeat.setDuration(speed); 
        heartbeat.start(); */
        
        
        //heartbeat = (CustomAnimationDrawable) heart.getBackground();

        
        /*Intent analyseMusicIntent = new Intent(this, AnalyseMusicService.class);
        startService(analyseMusicIntent);
        
        mDbHelper = new DatabaseAdapter(this);*/
        
        if(enableBluetooth()){
        	Log.e(LOG_TAG, "Bluetooth on :)");
			pairWithDevice();
			polarCommsTsk.execute(NAME);	
        }else{
        	// Device does not support Bluetooth
       	 Toast.makeText(this, "Bluetooth is not available right now. Application will end.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
		txtHeartRate = (TextView) findViewById(R.id.txtHeartRate);
   }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		return true;
	}



	private boolean enableBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			return false;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableIntent);
		}
		return true;
	}
	
	private void pairWithDevice() {
		pairedDevices = mBluetoothAdapter.getBondedDevices();

		for (final BluetoothDevice dev : pairedDevices) {
			Log.d(LOG_TAG, "BT device: [name :" + dev.getName() + "; addr:" + dev.getAddress() + "; BtClass:"
					+ dev.getBluetoothClass() + "; bonded:" + dev.getBondState());
			if (dev.getName().toLowerCase().contains("polar")) {
				polarBtDev = dev;
			}
		}

	}
    
	@Override
	protected void onStart() {
		super.onStart();
		if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
        	
        	startHBPM();
        }
	}

	private void startHBPM() {
	
	}
    
    
	private class PolarCommsTask extends AsyncTask<String, HeartRateRecord, Integer> {

		private Boolean continueRunning = true;
		private HeartRateRecord hrr = new HeartRateRecord();
		private BluetoothSocket mmSocket = null;

		@Override
		protected Integer doInBackground(final String... params) {
			try {
				// MY_UUID is the app’s UUID string, also used by the server
				// code
				// mmSocket =
				// polarBtDev.createRfcommSocketToServiceRecord(MY_UUID);

				final Method m = polarBtDev.getClass().getMethod("createRfcommSocket",
						new Class[] { int.class });
				mmSocket = (BluetoothSocket) m.invoke(polarBtDev, 1);
			} catch (final SecurityException e) {
				Log.d(LOG_TAG, "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			} catch (final NoSuchMethodException e) {
				Log.d(LOG_TAG, "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			} catch (final IllegalArgumentException e) {
				Log.d(LOG_TAG, "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				Log.d(LOG_TAG, "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			} catch (final InvocationTargetException e) {
				Log.d(LOG_TAG, "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			}

			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();

			} catch (final IOException connectException) {
				Log.d(LOG_TAG, "connectException : ", connectException);
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (final IOException closeException) {
					Log.d(LOG_TAG, "closeException : ", closeException);
				}
				mmSocket = null;
			}

			// If a connection was accepted
			if (mmSocket != null) {
				try {
					if (ReadAsString) {
					} else {
						final DataInputStream dataInputStream = new DataInputStream(mmSocket.getInputStream());

						final int[] byteRecord = new int[12];
						int dyn_data = 0;
						int curr_byte_nbr = 0;
						while ((dyn_data = dataInputStream.readUnsignedByte()) >= 0 && continueRunning) {
							if (dyn_data == 254 && byteRecord[0] != 0) {
								hrr = PolarMessageParser.parseBytes(byteRecord);

								publishProgress(hrr);

								for (int i = 0; i < curr_byte_nbr; i++) {
									byteRecord[i] = 0;
								}
								curr_byte_nbr = 0;
							}
							byteRecord[curr_byte_nbr++] = dyn_data;

						}
					}
					try {
						mmSocket.close();
					} catch (final IOException closeException) {
						Log.d(LOG_TAG, "closeException : ", closeException);
					}
					mmSocket = null;
				} catch (final IOException e) {
					Log.d(LOG_TAG, "Comms Exception : ", e);
				}
			}
			return 1;
		}
		
		@Override
		protected void onProgressUpdate(final HeartRateRecord... values) {
			super.onProgressUpdate(values);
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateHeartRate(values[0]);
				}
			});
		}

		public void setContinueRunning(final Boolean value) {
			continueRunning = value;
		}
	}
	

	private static final UUID MY_UUID = UUID.fromString("f2b90360-973f-11e0-aa80-0800200c9a66");
	private static final String PREF_CURRENT_AGE = "PREF_CURRENT_AGE";
	private static final String PREF_MAX_HR = "PREF_MAX_HR";
	private static final String PREF_REST_HR = "PREF_REST_HR";
	
	//private int restingHeartRate = 60;

	
	private int sixtyPercent = 0;
	private int seventyPercent = 0;
	private int eightyPercent = 0;
	private int ninetyPercent = 0;
	
/*	private TextView txtAge;*/

	private TextView txtHeartRate;
/*	private TextView txtRestingHeartRate;
	private TextView txtZone1From;

	private TextView txtZone1To;
	private TextView txtZone2From;
	private TextView txtZone2To;
	private TextView txtZone3From;
	private TextView txtZone3To;
	private TextView txtZone4From;
	private TextView txtZone4To;
	private TextView txtZone5From;

	private TextView txtZone5To;*/

	private void updateHeartRate(final HeartRateRecord hrr) {
		float hr = hrr.heartRate;
		txtHeartRate.setText("" + hrr.heartRate);
		ci.setCycles(hr);
		//heartbeat.setDuration((60/hrr.heartRate)*1000);
		if (hrr.heartRate < sixtyPercent) {
			txtHeartRate.setTextColor(getResources().getColor(R.color.zone1));
		} else if (hrr.heartRate < seventyPercent) {
			txtHeartRate.setTextColor(getResources().getColor(R.color.zone2));
		} else if (hrr.heartRate < eightyPercent) {
			txtHeartRate.setTextColor(getResources().getColor(R.color.zone3));
		} else if (hrr.heartRate < ninetyPercent) {
			txtHeartRate.setTextColor(getResources().getColor(R.color.zone4));
		} else {
			txtHeartRate.setTextColor(getResources().getColor(R.color.zone5));
		}
	}
	
	private final BroadcastReceiver btDevFoundReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					final String noDevices = getResources().getText(R.string.none_found).toString();
					mNewDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();		
		polarCommsTsk.setContinueRunning(false);
		polarCommsTsk.cancel(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
}