package com.piliguerra.android.hbpm.music;

import android.app.IntentService;
import android.content.Intent;

import com.piliguerra.android.hbpm.db.DatabaseAdapter;

public class PlayMusicService extends IntentService {

	DatabaseAdapter dba;
	
	public PlayMusicService() {
		super("PlayMusicService");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		
	}

	
}
