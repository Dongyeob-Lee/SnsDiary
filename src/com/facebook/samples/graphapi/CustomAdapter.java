package com.facebook.samples.graphapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import com.facebook.Session;
import com.pslab.snsdiary.R;
import com.pslab.snsdiary.daily.Daily;
import com.pslab.snsdiary.note.Note;
import com.pslab.snsdiary.note.NoteActivity;

import dalvik.bytecode.Opcodes;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Message;
import android.pslab.jaehwan.api.SAPI;
import android.pslab.jaehwan.object.Comment;
import android.pslab.jaehwan.object.User;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends ArrayAdapter<String>{
	private Vector items;
	private Activity activity;
	private int RsrcId;
	SAPI sapi = new SAPI();
	private ArrayList<String> feedIdList = new ArrayList<String>();
	int index;
	Session session;
	Context context;
	private Bitmap profile;
	ArrayList<User> userlist;
	String myid;
	
	public CustomAdapter(Context context, int RsrcId, Vector items, Activity activity){
		super(context, RsrcId, items);
		this.items = items;
		this.activity = activity;
		this.RsrcId = RsrcId;
		this.session = Session.getActiveSession();
		this.index = index;
		this.context= context;
	}
	
	public CData getItems(int position){
		return (CData)items.get(position);
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		if(v == null){
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(RsrcId, null);
		}
		return searchRow(position,v);
	}
	
	public View searchRow(int position, View v){
		final CData p = (CData)items.get(position);
		sapi.setFacebookAccessToken(session);
		
		if(p!=null){
			TextView text1 = (TextView)v.findViewById(R.id.feed_list_user_id);
			TextView text2 = (TextView)v.findViewById(R.id.feed_list_content);
			TextView text3 = (TextView)v.findViewById(R.id.feed_list_item_time);
			ImageView userimage = (ImageView)v.findViewById(R.id.feed_list_user_pic);
			ImageView feedimage = (ImageView)v.findViewById(R.id.feed_list_content_img);
			
			text1.setText(p.getS_name());
			text2.setText(p.getS_message());
			text3.setText(p.getS_dadte());
			if (p.getS_profile() != null)
				userimage.setImageBitmap(p.getS_profile());
			if (p.getS_photo() != null) {
				feedimage.setImageBitmap(p.getS_photo());
			}
			
	
			Button intomessagebtn = (Button)v.findViewById(R.id.feed_list_write_into_messgae);
	        intomessagebtn.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {

					final FeedIntoMsgAdapter ca = setAdapter(p.getS_msgNum());
			    	  
			    	AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			    	builder.setTitle("댓글목록")
			    	.setCancelable(true)
			    	
			    	.setAdapter(ca, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					builder.create().show();
					
			
				}
	        });
	        
	        final Button likedislikebtn = (Button)v.findViewById(R.id.feed_list_like_dislike);
//	        userlist = sapi.getlikeuser("facebook", p.getS_msgNum());
//			for(int i=0 ; i<userlist.size();i++){
//				if(sapi.getfaceUserid().equals(userlist.get(i).getUserId()))
//				{
//					likedislikebtn.setBackgroundResource(R.drawable.dislike);
//					
//				}
//				
//				
//			}

	        likedislikebtn.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO like_dislike_btn
					
						userlist = sapi.getlikeuser("facebook", p.getS_msgNum());
						
						for(int i=0 ; i<userlist.size();i++){
							if(sapi.getfaceUserid().equals(userlist.get(i).getUserId()))
							{
								sapi.deleteFeed("facebook", p.getS_msgNum());//지금 dislike가안먹힘..이건재환이에게물어보자.
								likedislikebtn.setBackgroundResource(R.drawable.like);
								
							}
							else{
								sapi.setLike("facebook", p.getS_msgNum());
								likedislikebtn.setBackgroundResource(R.drawable.dislike);
							}
						}
					
					Log.d("ddd",p.getS_msgNum());
					Log.d("ddd","오나마나");
				}
			});
	        
	        Button selectfeedbtn = (Button)v.findViewById(R.id.feed_list_get_feed);
	        selectfeedbtn.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					//요것은 글의번호임.. 이걸 xml로 저장하면됨.
//					AlertDialog.Builder builder = new AlertDialog.Builder(context);
//					builder.setTitle("Feed 불러오기");
//					builder.setMessage("Feed를 불러오시겠습니까?");
//					builder.setPositiveButton("예", new Dialog.OnClickListener() {
//						
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
							feedIdList.add(p.getS_msgNum());
							Toast.makeText(getContext(), "Feed 불러오기 완료", Toast.LENGTH_SHORT).show();
//						}
//					});
//					builder.setNegativeButton("아니오", null);
//					builder.show();
				}
			});
			
		}
		return v;
	}
	
	public ArrayList<String> getFeedIdList() {
		return feedIdList;
	}

	public void setFeedIdList(ArrayList<String> feedIdList) {
		this.feedIdList = feedIdList;
	}

	private FeedIntoMsgAdapter setAdapter(String msgNum){
    	Vector<CData> vector = new Vector<CData>();

        sapi.setFacebookAccessToken(session);

        ArrayList<Comment> comment =sapi.getComments("facebook",msgNum);
		for(int i=0 ; i<comment.size();i++){
			
				vector.add(new CData(sapi.getUser("facebook", comment.get(i).getUserId()).getName(),comment.get(i).getText(),downloadBitmap(sapi.getUser("facebook",
						comment.get(i).getUserId()).getUserProfileUrl()),null,comment.get(i).getCommentDate(),null));	
		}

    	return new FeedIntoMsgAdapter(getContext(),R.layout.intomessage, vector, activity);
    }
	
	
	
	public Bitmap downloadBitmap(String urlname){
		Log.d("URl", "url "+ urlname);
		URL url = null;
		Bitmap Result = null;
		try {
			url = new URL(urlname);
			Result = BitmapFactory.decodeStream(url.openStream());
			Bitmap.createScaledBitmap(Result, 10, 10, false);
			Log.d("URL","herecome?" + Result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Result = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Result = null;
		}
		return Result;
	}
}
 


