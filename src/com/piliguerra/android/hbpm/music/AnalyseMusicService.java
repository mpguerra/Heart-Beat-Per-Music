package com.piliguerra.android.hbpm.music;

import java.io.File;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import com.piliguerra.android.hbpm.db.DatabaseAdapter;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class AnalyseMusicService extends IntentService {

	DatabaseAdapter dba;
	
	public AnalyseMusicService() {
		super("AnalyseMusicService");
	}
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		//initialise db
		dba = new DatabaseAdapter(this.getApplicationContext());
		//look through media library
		ContentResolver contentResolver = getContentResolver();
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor == null) {
		    // query failed, handle error.
		} else if (!cursor.moveToFirst()) {
		    // no media on the device
		} else {
		    /*int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
		    int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);*/
		    do {
			    String path = uri.getEncodedPath();
			    File file = new File(path);
			    MP3File mp3file = new MP3File(file);
				//check id3tags
			    try {
			    	ID3Tag[] tags = mp3file.getTags();
					for (ID3Tag tag: tags){
						  if (tag instanceof ID3V1_0Tag)
				            {
				               break;
				            }
				            else if (tag instanceof ID3V2_3_0Tag)
				            {
				                ID3V2_3_0Tag oID3V2_3_0Tag = (ID3V2_3_0Tag)tag;
				                // check if this v2.3.0 frame contains a bpm frame
				                // if bpm populated, add song to db
				                if (oID3V2_3_0Tag.getTBPMTextInformationFrame() != null)
				                {
				                 int bpm = oID3V2_3_0Tag.getTBPMTextInformationFrame().getBeatsPerMinute();
				                 long id = dba.createEntry(path,bpm);
				                 break;
				                }else{
				                	break;
				                }
				            }
					}
				        

				} catch (ID3Exception e) {
					e.printStackTrace();
				}
		       
		       //MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		       
		       // ...process entry...
		    } while (cursor.moveToNext());
		}
	}

}
