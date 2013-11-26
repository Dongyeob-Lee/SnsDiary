
package com.pslab.snsdiary.dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.pslab.snsdiary.note.Note;

public class Dropbox_controler {
	private DropboxAPI<AndroidAuthSession> mDBApi;
	final static String APP_KEY = "ytpfmlv3hnbbump";
	final static String APP_SECRET = "t55tyra0j9hd59c";
	final static AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private SharedPreferences prefs;
	public Handler handler;
	Context mContext;

	public static boolean isValid(Context context){
		SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(context);
		String dropbox_key = preference.getString("dropbox_key", "");
		String dropbox_secret = preference.getString("dropbox_secret", "");
		if (dropbox_key.length() > 0 && dropbox_secret.length() > 0){
			return true;
		}
		return false;
	}
	public static boolean unlinkSession(Context context){
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		p = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = p.edit();
		editor.clear();
		editor.commit();
		return false;
	}
	public Dropbox_controler(Context context) {
		super();
		this.mContext = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

		String dropbox_key = prefs.getString("dropbox_key", "");
		String dropbox_secret = prefs.getString("dropbox_secret", "");
		
		System.out.println("key, secret:"+dropbox_key+","+dropbox_secret);
		
		if (dropbox_key.length() > 0 && dropbox_secret.length() > 0) {
			AccessTokenPair access = new AccessTokenPair(dropbox_key,
					dropbox_secret);
			AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
			AndroidAuthSession session = new AndroidAuthSession(appKeys,
					ACCESS_TYPE);
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
			mDBApi.getSession().setAccessTokenPair(access);
//			if(mDBApi==null){
//				Log.d("diary", "mDBApi null...");
//				mDBApi = new DropboxAPI<AndroidAuthSession>(session);
//					
//				mDBApi.getSession().startAuthentication(mContext);
//				if(mDBApi==null){
//					Log.d("diary", "mDBApi null...½à!");
//				}
//			}
		}else{
			Log.d("diary", "mDBApi null...");
			 AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
			 AndroidAuthSession session = new AndroidAuthSession(appKeys,
			 ACCESS_TYPE);
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
				
			mDBApi.getSession().startAuthentication(mContext);
		}
	}

	public Dropbox_controler(Context context, Handler handler) {
		// TODO Auto-generated constructor stub
		super();
		this.mContext = context;
		this.handler = handler;
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

		String dropbox_key = prefs.getString("dropbox_key", "");
		String dropbox_secret = prefs.getString("dropbox_secret", "");
		
		System.out.println("key, secret:"+dropbox_key+","+dropbox_secret);
		
		if (dropbox_key.length() > 0 && dropbox_secret.length() > 0) {
			AccessTokenPair access = new AccessTokenPair(dropbox_key,
					dropbox_secret);
			AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
			AndroidAuthSession session = new AndroidAuthSession(appKeys,
					ACCESS_TYPE);
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
			mDBApi.getSession().setAccessTokenPair(access);
			Log.d("dropbox", "session valid");
//			if(mDBApi==null){
//				Log.d("diary", "mDBApi null...");
//				mDBApi = new DropboxAPI<AndroidAuthSession>(session);
//					
//				mDBApi.getSession().startAuthentication(mContext);
//				if(mDBApi==null){
//					Log.d("diary", "mDBApi null...½à!");
//				}
//			}
		}else{
			Log.d("dropbox", "mDBApi null...");
			 AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
			 AndroidAuthSession session = new AndroidAuthSession(appKeys,
			 ACCESS_TYPE);
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
				
			mDBApi.getSession().startAuthentication(mContext);
		}
	}

	public void download_xml() {
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
			
				
				try {
					
					DropboxFileInfo fileinfo;
					String path = mContext.getFilesDir().getAbsolutePath();
					// String filename ="main.xml";
					int n = 1;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Entry entries = mDBApi.metadata("/", 10000, null, true, null);
					for(Entry e : entries.contents){
						Log.d("diary", e.fileName());
						if(e.isDir){
							System.out.println("isdir");
							if(e.fileName().equals("image")){
								Entry imgEntry = mDBApi.metadata("/image/", 10000, null, true, null);
								System.out.println("image");
								for(Entry img : imgEntry.contents){
									System.out.println("imgcnt");
									File imgFile = new File(path+"/",img.fileName());
									FileOutputStream imgFileOutputStream = new FileOutputStream(imgFile);
									mDBApi.getFile("/image/"+img.fileName(), null, imgFileOutputStream, null);
								}
							}
						}else{
							File file = new File(path,e.fileName());
							FileOutputStream fileOutputStream = new FileOutputStream(file);
							mDBApi.getFile("/"+e.fileName(), null, fileOutputStream, null);
						}
						
					}
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (DropboxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * ByteArrayOutputStream outputstream = null; int n=0;
				 * for(n=0;n<10;n++){ try { outputstream = new
				 * ByteArrayOutputStream(); DropboxFileInfo info =
				 * mDBApi.getFile("/" + n + ".xml", null, outputstream, null); }
				 * 
				 * catch (Exception e) { e.printStackTrace(); } }
				 */
				
				handler.sendEmptyMessage(0);
			}
		});
		
		thread.start();
	}
	public void download_select_xml(final String name){
		Thread thread = new Thread(new Runnable() {
			public void run() {
			
				
				try {
					
					DropboxFileInfo fileinfo;
					String path = mContext.getFilesDir().getAbsolutePath();
					// String filename ="main.xml";
					int n = 1;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Entry entries = mDBApi.metadata("/", 10000, null, true, null);
					for (Entry e : entries.contents) {
						if(e.fileName().equals(name)){
							File file = new File(path, e.fileName());
							FileOutputStream fileOutputStream = new FileOutputStream(
									file);
							mDBApi.getFile("/" + e.fileName(), null,
									fileOutputStream, null);
						}
						
					}
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (DropboxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * ByteArrayOutputStream outputstream = null; int n=0;
				 * for(n=0;n<10;n++){ try { outputstream = new
				 * ByteArrayOutputStream(); DropboxFileInfo info =
				 * mDBApi.getFile("/" + n + ".xml", null, outputstream, null); }
				 * 
				 * catch (Exception e) { e.printStackTrace(); } }
				 */
				
				handler.sendEmptyMessage(0);
			}
		});
		
		thread.start();
	}
	public void upload_png(final Note note){
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					DropboxFileInfo fileinfo;
					String path = mContext.getFilesDir().getAbsolutePath()+"/";
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					for (int i = 0; i < note.getNoteDiaries().size(); i++){
						String imagename = note.getNoteDiaries().get(i).getSCanvasFileName();
						File file = new File(path, imagename);
						FileInputStream in = new FileInputStream(file);
						Log.d("diary", "imgpath:" + path + ",imagename:"
								+ imagename);
						while (!file.exists()) {
							Log.d("check", "dndjdjdj");
						}
						if (file.exists()) {
							Log.d("diary", "imgfile exist");
							mDBApi.putFileOverwrite("/image/" + imagename, in,
									file.length(), null);
						}

					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (DropboxException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	public void delete_xml(final String filename, final Note note){
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					DropboxFileInfo fileinfo;
					String path = mContext.getFilesDir().getAbsolutePath();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					mDBApi.delete("/"+filename);
					for(int i=0; i<note.getNoteDiaries().size(); i++){
						mDBApi.delete("/image/"+note.getNoteDiaries().get(i).getSCanvasFileName());
					}

				} catch (DropboxException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	public void upload_xml(final String filename) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					DropboxFileInfo fileinfo;
					String path = mContext.getFilesDir().getAbsolutePath();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					File file = new File(path, filename);
					FileInputStream in = new FileInputStream(file);
					// fileinfo = mDBApi.getFile("/"+".xml", null,
					// outputStream, null);
					if(!file.exists()){
						Log.d("diary", "no upload file");
					}
					if(mDBApi==null){
						Log.d("diary", "mdbapi is null");
					}
					Log.d("Diary", "uploadXML");
					mDBApi.putFileOverwrite("/" + filename, in, file.length(),
							null);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (DropboxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public void resume(){
		if (mDBApi != null && mDBApi.getSession().authenticationSuccessful()) {
			Log.d("diary", "resume");
			try {
				mDBApi.getSession().finishAuthentication();
				AccessTokenPair tokens = mDBApi.getSession()
						.getAccessTokenPair();
				Editor editor = prefs.edit();
				editor.putString("dropbox_key", tokens.key);
				editor.putString("dropbox_secret", tokens.secret);
				editor.commit();
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}
	}
	
}