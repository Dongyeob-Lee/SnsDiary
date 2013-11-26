package android.pslab.jaehwan.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.os.Bundle;
import android.pslab.jaehwan.object.Comment;
import android.pslab.jaehwan.object.Feed;
import android.pslab.jaehwan.object.Me2day;
import android.pslab.jaehwan.object.User;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;


public class SAPI {
	Twitter mTwitter;
	Me2day mMe2day;
	Session mFacebook;
	boolean showLog;
	GraphObject graphObject;
	ArrayList<Feed> result;
	ArrayList<Comment> resultComment;
	ArrayList<String> resultString;
	User resultUser;
	String param,param2,param3,param4;
	
	String c_until, c_since;
	
	
	private void log4SAPI(String log)
	{
		if(showLog) Log.d("SAPI",log);
	}
	
	//TODO setAccessToken Method
	public boolean setFacebookAccessToken(Session session)
	{		
		mFacebook=session;
		return true;
	}
	
	public boolean setTwitterAccessToken(String token, String secretToken, String consumerKey, String consumerSecretKey)
	{
		AccessToken mTwitterAccessToken=new AccessToken(token,secretToken);
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true);
	    cb.setOAuthConsumerKey(consumerKey);
	    cb.setOAuthConsumerSecret(consumerSecretKey);
	    TwitterFactory factory = new TwitterFactory(cb.build());
	    mTwitter = factory.getInstance();
		mTwitter.setOAuthAccessToken(mTwitterAccessToken);			
		
		return true;	
	}
	
	public boolean setMe2dayAccessToken(String userKey, String userId, String appKey)
	{
		mMe2day = new Me2day(userKey,userId,appKey);
		return true;
	}
	
	//TODO getFeed(String type, String feedId)
	public Feed getFeed(String type, String feedId)
	{
		if(type == null || feedId == null)
		{
			log4SAPI("getFeed 留ㅺ�蹂�� ���");
			return null;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			graphObject=null;
			Feed feed = new Feed();
			Request request;
			String url = feedId;
			request = new Request(mFacebook,url,null,null,new Request.Callback() {				
				public void onCompleted(Response response) {		
					graphObject = response.getGraphObject();
					
				}
			});
			request.executeAndWait();
			
			
			
			feed.setFeedId(feedId);
			feed.setSNSName(type);
			feed.setText(graphObject.getProperty("message").toString());
			feed.setFeedImg(graphObject.getProperty("picture").toString());
			feed.setFeedDate(graphObject.getProperty("created_time").toString());
			try {
				feed.setUserId(graphObject.getInnerJSONObject().getJSONObject("from").getString("id").toString());
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			return feed;
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			return null; //吏��������
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			param=feedId;
			result=null;
			
			new Thread(){
				public void run(){	
					String url="http://me2day.net/api/get_posts.xml?post_id="+param;
					result = parseMe2dayPosts(url);					
				}
			}.start();
			
			while(result==null);
			
			return result.get(0);
		}
		else
		{
			log4SAPI("getFeed 留ㅺ�蹂��(type) ���");
			return null;
		}
		
	}
	
	//TODO getFriendTimeline(String type, String friendId)
	public ArrayList<Feed> getFriendTimeline(String type, String userId)
	{
		if(type == null || userId == null)
		{
			log4SAPI("getFriendTimeline 留ㅺ�蹂�� ���");
			return null;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			ArrayList<Feed> flist = new ArrayList<Feed>();
			try {
			Bundle b= new Bundle();
			//b.putCharSequence("since", 媛�
			Request request;
			String url = userId+"/feed";
			request = new Request(mFacebook,url,null,null,new Request.Callback() {				
				public void onCompleted(Response response) {		
					graphObject = response.getGraphObject();
					
				}
			});
			request.executeAndWait();
			
			JSONArray data = graphObject.getInnerJSONObject().getJSONArray("data");
			for(int i=0;i<data.length();i++)
			{
				
				Log.d("test","�ㅻ�");
				Feed feed = new Feed();
				if(data.getJSONObject(i).has("id"))
					feed.setFeedId(data.getJSONObject(i).getString("id").toString());
					feed.setSNSName(type);
					if(data.getJSONObject(i).has("message"))
					feed.setText(data.getJSONObject(i).getString("message").toString());
//					else if(data.getJSONObject(i).has("story"))
//						feed.setText(data.getJSONObject(i).getString("story").toString());
//						
					if(data.getJSONObject(i).has("picture") && data.getJSONObject(i).has("object_id"))
					{
						
						String objectId=data.getJSONObject(i).getString("object_id");
						request = new Request(mFacebook,url,null,null,new Request.Callback() {				
							public void onCompleted(Response response) {		
								graphObject = response.getGraphObject();
							}
						});
						
						JSONArray imgarr = graphObject.getInnerJSONObject().getJSONArray("images");
						
						String pictureUrl=null;
						
						for(int j=0;j<imgarr.length();j++)
						{
							int x= Integer.parseInt(imgarr.getJSONObject(j).get("width").toString());
							int y= Integer.parseInt(imgarr.getJSONObject(j).get("height").toString());
							if(x<=500 && y<=500){
								pictureUrl = imgarr.getJSONObject(j).get("source").toString();
								break;
							}
						}
						
						//String pictureUrl = graphObject.getProperty("source").toString();
						Log.d("url","url"+ pictureUrl);
						feed.setFeedImg(pictureUrl);
						
						
						
					}
					feed.setFeedImg(data.getJSONObject(i).getString("picture").toString());
					
					
					
					if(data.getJSONObject(i).has("created_time"))
					feed.setFeedDate(data.getJSONObject(i).getString("created_time").toString());
					if(data.getJSONObject(i).has("from"))
					feed.setUserId(data.getJSONObject(i).getJSONObject("from").getString("id"));
					flist.add(feed);
			}
				
			} catch (Throwable e) {			e.printStackTrace();	}
			return flist;
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			
				result = null;
				param = userId;
				
				new Thread(){
					
					public void run(){
						List<Status> feeds = null;
						try {
							feeds = mTwitter.getUserTimeline(Long.parseLong(param));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (TwitterException e) {
							e.printStackTrace();
						}
							
						ArrayList<Feed> temp = new ArrayList<Feed>();
						for(Status status : feeds)
						{
							Feed fd = new Feed();
							fd.setFeedDate(setTwitterDateFormat(status.getCreatedAt().toString()));
							fd.setFeedId(String.valueOf(status.getId()));
							fd.setFeedImg(null);
							fd.setSNSName("Twitter");
							fd.setText(status.getText());
							twitter4j.User user = status.getUser();
							fd.setUserId(String.valueOf(user.getId()));
							temp.add(fd);
						}						
						result=temp;						
					}
					
				}.start();
				
				while(result==null);
				
				return result;
			
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			try {			
				result = null;
				param = userId;
				new Thread(){
					
					public void run(){
						String url="http://me2day.net/api/get_posts/"+param+".xml";
						result = parseMe2dayPosts(url);
					}
				}.start();
				while(result==null);
			   return result;
			}catch (Throwable e) {	log4SAPI("getFriendTimeline Me2day ���"); return null; }
		}
		else
		{
			log4SAPI("getFriendTimeline 留ㅺ�蹂��(type) ���");
			return null;
		}
	}
	
	//TODO getUserLikesTimeline(String type)
	public ArrayList<Feed> getUserLikesTimeline(String type)
	{
		if(type == null)
		{
			log4SAPI("getFriendTimeline 留ㅺ�蹂�� ���");
			return null;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			return null; //���
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			
			result = null;
			new Thread(){
				
				public void run(){
					List<Status> feeds = null;
					try {
						feeds = mTwitter.getFavorites();
					} catch (TwitterException e) {
						e.printStackTrace();
					}
					ArrayList<Feed> temp = new ArrayList<Feed>();	
					
					for(Status status : feeds)
					{
						Feed fd = new Feed();
						fd.setFeedDate(setTwitterDateFormat(status.getCreatedAt().toString()));
						fd.setFeedId(String.valueOf(status.getId()));
						fd.setFeedImg(null);
						fd.setSNSName("Twitter");
						fd.setText(status.getText());
						twitter4j.User user = status.getUser();
						fd.setUserId(String.valueOf(user.getId()));
						temp.add(fd);
					}
					
					result = temp;
				}
			}.start();
			
			while(result==null);
			
			return result;
		
		}
		else if(type.equalsIgnoreCase("me2day"))
		{			
			result=null;
			
			new Thread(){
				public void run(){	
			
				String url="http://me2day.net/api/get_posts/"+mMe2day.getUserId()+".xml?scope=metoo";
				result = parseMe2dayPosts(url);
				}
			}.start();
			while(result==null);			
			   return result;
		}
		else
		{
			log4SAPI("getFriendTimeline 留ㅺ�蹂��(type) ���");
			return null;
		}
	}

	//TODO  getUserTimeline(String type)
	public ArrayList<Feed> getUserTimeline(String type)
	{
		if(type == null)
		{
			log4SAPI("getUserTimeline 留ㅺ�蹂�� ���");
			return null;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			ArrayList<Feed> flist = new ArrayList<Feed>();
			try {
				
				
				String url = "me/home";
				Request request1;
				request1 = new Request(mFacebook,url,null,null,new Request.Callback() {				
					public void onCompleted(Response response) {		
						graphObject = response.getGraphObject();
						Log.d("erorrrrrr", "error :"+ response.getError());
						Log.d("result","graph:"+graphObject.toString());

					}
				});
				request1.executeAndWait();
				
				
//				JSONObject paging = graphObject.getInnerJSONObject().getJSONObject("paging");
//				c_until = paging.getString("next").toString().substring(paging.getString("next").toString().indexOf("until=")+6);
//				c_since = paging.getString("previous").toString().substring(paging.getString("previous").toString().indexOf("since=")+6);
//
//				Log.d("c1","???"+ c_until);
//				Log.d("C1","???"+ c_since);
//				
				
				
				
			Request request;
		//	String url = "me/feed";
			request = new Request(mFacebook,url,null,null,new Request.Callback() {				
				public void onCompleted(Response response) {		
					graphObject = response.getGraphObject();
					Log.d("erorrrrrr", "error :"+ response.getError());
					Log.d("result","graph:"+graphObject.toString());

				}
			});			
			request.executeAndWait();
			
			JSONObject paging = graphObject.getInnerJSONObject().getJSONObject("paging");
			c_until = paging.getString("next").toString().substring(paging.getString("next").toString().indexOf("until=")+6);
			c_since = paging.getString("previous").toString().substring(paging.getString("previous").toString().indexOf("since=")+6);

			Log.d("c1","???"+ c_until);
			Log.d("C1","???"+ c_since);
			

			
			
			JSONArray data = graphObject.getInnerJSONObject().getJSONArray("data");
		for(int i=0;i<data.length();i++)
			{
			//Log.d("result","id:"+data.getJSONObject(i).getString("id"));
				Feed feed = new Feed();
				if(data.getJSONObject(i).has("id"))
				feed.setFeedId(data.getJSONObject(i).getString("id").toString());
				feed.setSNSName(type);
				if(data.getJSONObject(i).has("message"))
				feed.setText(data.getJSONObject(i).getString("message").toString());
//				else if(data.getJSONObject(i).has("story"))
//					feed.setText(data.getJSONObject(i).getString("story").toString());
//					
				
				
				
				
				
				
				if(data.getJSONObject(i).has("picture") && data.getJSONObject(i).has("object_id"))
				{
//					Log.d("result","if");
//					String objectId=data.getJSONObject(i).getString("object_id");
//					request1 = new Request(mFacebook,objectId,null,null,new Request.Callback() {				
//						public void onCompleted(Response response) {		
//							graphObject = response.getGraphObject();
//						}
//					});
//					request1.executeAndWait();
//					Log.d("result","prictureurl");
//					
//					//���二쇱�泥�━��
//					JSONArray imgarr = graphObject.getInnerJSONObject().getJSONArray("images");
//					
//					String pictureUrl=null;
//					
//					for(int j=0;j<imgarr.length();j++)
//					{
//						int x= Integer.parseInt(imgarr.getJSONObject(j).get("width").toString());
//						int y= Integer.parseInt(imgarr.getJSONObject(j).get("height").toString());
//						if(x<=500 && y<=500){
//							pictureUrl = imgarr.getJSONObject(j).get("source").toString();
//							break;
//						}
//					}
//					
					
//					String pictureUrl = graphObject.getProperty("picture").toString();
//					feed.setFeedImg(pictureUrl);
//					Log.d("result","prictureurl1"+ pictureUrl);
					
				}				
//				
				if(data.getJSONObject(i).has("picture"))
				feed.setFeedImg(data.getJSONObject(i).getString("picture").toString());
				if(data.getJSONObject(i).has("created_time"))
				feed.setFeedDate(data.getJSONObject(i).getString("created_time").toString());
				if(data.getJSONObject(i).has("from"))
				feed.setUserId(data.getJSONObject(i).getJSONObject("from").getString("id"));
				flist.add(feed);
			}
		
//		request = new Request(mFacebook,url,null,null,new Request.Callback() {				
//			public void onCompleted(Response response) {		
//				graphObject = response.getGraphObject();
//				Log.d("erorrrrrr", "error :"+ response.getError());
//				Log.d("result","graph:"+graphObject.toString());
//
//			}
//		});
//		request.executeAndWait();
		
		
				
			} catch (Throwable e) {			e.printStackTrace();	
						}
			return flist;
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
				result = null;
				new Thread(){
					public void run(){
				List<Status> feeds = null;
				try {
					feeds = mTwitter.getHomeTimeline();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				ArrayList<Feed> temp = new ArrayList<Feed>();	
				
				for(Status status : feeds)
				{
					Feed fd = new Feed();
					fd.setFeedDate(setTwitterDateFormat(status.getCreatedAt().toString()));
					fd.setFeedId(String.valueOf(status.getId()));
					fd.setFeedImg(null);
					fd.setSNSName("Twitter");
					fd.setText(status.getText());
					twitter4j.User user = status.getUser();
					fd.setUserId(String.valueOf(user.getId()));
					temp.add(fd);
				}
				result = temp;
					}
				}.start();
				while(result==null);
				return result;
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			result=null;
			
			new Thread(){
				public void run(){			
				String url="http://me2day.net/api/get_posts/"+mMe2day.getUserId()+".xml";
				result = parseMe2dayPosts(url);
				}
			}.start();
			
			while(result==null);
			
				return result;
			
		}
		else
		{
			log4SAPI("getUserTimeline 留ㅺ�蹂��(type) ���");
			return null;
		}
	}
	
	//TODO  getUserTimeline(String type)
	public ArrayList<Feed> NextTimeline(String type)
	{
		if(type == null)
		{
			log4SAPI("getUserTimeline 留ㅺ�蹂�� ���");
			return null;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			ArrayList<Feed> flist = new ArrayList<Feed>();
			try {
	
				Log.d("c1", "test "+ c_until);
		
			Bundle b1;
			b1 = new Bundle();
			b1.putCharSequence("until",c_until);
			
			String url = "me/home";
			Request request;
			//	String url = "me/feed";
				request = new Request(mFacebook,url,b1,null,new Request.Callback() {				
					public void onCompleted(Response response) {		
						graphObject = response.getGraphObject();
						Log.d("erorrrrrr", "error :"+ response.getError());
						Log.d("result","graph:"+graphObject.toString());

					}
				});
				request.executeAndWait();
				
				
				JSONArray data = graphObject.getInnerJSONObject().getJSONArray("data");
			for(int i=0;i<data.length();i++)
				{
				//Log.d("result","id:"+data.getJSONObject(i).getString("id"));
					Feed feed = new Feed();
					if(data.getJSONObject(i).has("id"))
					feed.setFeedId(data.getJSONObject(i).getString("id").toString());
					feed.setSNSName(type);
					if(data.getJSONObject(i).has("message"))
					feed.setText(data.getJSONObject(i).getString("message").toString());
//					else if(data.getJSONObject(i).has("story"))
//						feed.setText(data.getJSONObject(i).getString("story").toString());
//						
					
					
					
					
					
					
//					if(data.getJSONObject(i).has("picture") && data.getJSONObject(i).has("object_id"))
//					{
//						Log.d("result","if");
//						String objectId=data.getJSONObject(i).getString("object_id");
//						request = new Request(mFacebook,objectId,null,null,new Request.Callback() {				
//							public void onCompleted(Response response) {		
//								graphObject = response.getGraphObject();
//							}
//						});
//						request.executeAndWait();
//						Log.d("result","prictureurl");
//						JSONArray imgarr = graphObject.getInnerJSONObject().getJSONArray("images");
//						
//						String pictureUrl=null;
//						
//						for(int j=0;j<imgarr.length();j++)
//						{
//							int x= Integer.parseInt(imgarr.getJSONObject(j).get("width").toString());
//							int y= Integer.parseInt(imgarr.getJSONObject(j).get("height").toString());
//							if(x<=500 && y<=500){
//								pictureUrl = imgarr.getJSONObject(j).get("source").toString();
//								break;
//							}
//						}
//						
//						
////						String pictureUrl = graphObject.getProperty("picture").toString();
//						feed.setFeedImg(pictureUrl);
//						Log.d("result","prictureurl1"+ pictureUrl);
//						
//					}
					
//					
					if(data.getJSONObject(i).has("picture"))
					feed.setFeedImg(data.getJSONObject(i).getString("picture").toString());
					if(data.getJSONObject(i).has("created_time"))
					feed.setFeedDate(data.getJSONObject(i).getString("created_time").toString());
					if(data.getJSONObject(i).has("from"))
					feed.setUserId(data.getJSONObject(i).getJSONObject("from").getString("id"));
					flist.add(feed);
				
				
				
//					Request request1;
//					request1 = new Request(mFacebook,url,b1,null,new Request.Callback() {				
//						public void onCompleted(Response response) {		
//							graphObject = response.getGraphObject();
//							Log.d("erorrrrrr", "error :"+ response.getError());
//							Log.d("result","graph:"+graphObject.toString());
//
//						}
//					});
					//request1.executeAndWait();
				
				
				JSONObject paging = graphObject.getInnerJSONObject().getJSONObject("paging");
				c_until = paging.getString("next").toString().substring(paging.getString("next").toString().indexOf("until=")+6);
				c_since = paging.getString("previous").toString().substring(paging.getString("next").toString().indexOf("since=")+6);
//				
				Log.d("c1","??? " +c_until);
			}
				
			} catch (Throwable e) {			e.printStackTrace();	
						}
			return flist;
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
				result = null;
				new Thread(){
					public void run(){
				List<Status> feeds = null;
				try {
					feeds = mTwitter.getHomeTimeline();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				ArrayList<Feed> temp = new ArrayList<Feed>();	
				
				for(Status status : feeds)
				{
					Feed fd = new Feed();
					fd.setFeedDate(setTwitterDateFormat(status.getCreatedAt().toString()));
					fd.setFeedId(String.valueOf(status.getId()));
					fd.setFeedImg(null);
					fd.setSNSName("Twitter");
					fd.setText(status.getText());
					twitter4j.User user = status.getUser();
					fd.setUserId(String.valueOf(user.getId()));
					temp.add(fd);
				}
				result = temp;
					}
				}.start();
				while(result==null);
				return result;
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			result=null;
			
			new Thread(){
				public void run(){			
				String url="http://me2day.net/api/get_posts/"+mMe2day.getUserId()+".xml";
				result = parseMe2dayPosts(url);
				}
			}.start();
			
			while(result==null);
			
				return result;
			
		}
		else
		{
			log4SAPI("getUserTimeline 留ㅺ�蹂��(type) ���");
			return null;
		}
	}
	
	
	
	//TODO postFeed(String type, String message)
	public boolean postFeed(String type, String message)
	{
		if(type == null || message == null)
		{
			log4SAPI("postFeed 留ㅺ�蹂�� ���");
			return false;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			Request request = Request.newStatusUpdateRequest(mFacebook, message, new Request.Callback() {
				
				public void onCompleted(Response response) {					
					graphObject = response.getGraphObject();
				}
			});
			request.executeAndWait();
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			param = message;
			new Thread(){
				public void run(){
					try {
						mTwitter.updateStatus(param);
					} catch (TwitterException e) {
						e.printStackTrace();
					}
				}
			}.start();
			return true;
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			param = message;
			new Thread(){
				public void run(){
					try {
						param = java.net.URLEncoder.encode(param, "UTF-8");
						//param = java.net.URLEncoder.encode(param, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}			
					String url="http://me2day.net/api/create_post.xml?post[body]="+param;									
				    parseMe2dayPosts(setUrlFormat(url));
				 }
			}.start();
		}
		else
		{
			log4SAPI("postFeed 留ㅺ�蹂��(type) ���");
			return false;
		}
		return false;
	}
	
	//TODO postFeedToFriend(String type, String message, String friendId)
	public boolean postFeedToFriend(String type, String message, String friendId)
	{
		if(type == null || message == null || friendId == null)
		{
			log4SAPI("postFeedToFriend 留ㅺ�蹂�� ���");
			return false;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
				Bundle bundle = new Bundle();
				bundle.putString("message", message);			
				Request request = new Request(mFacebook, friendId+"/feed", bundle, HttpMethod.valueOf("POST"), new Request.Callback() {
					public void onCompleted(Response response) {						
						graphObject = response.getGraphObject();
					}
				});
			request.executeAndWait();
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			param = message;
			param2 = friendId;
			new Thread(){
				public void run(){	
		
				try {
					mTwitter.updateStatus("@"+param2+" "+param);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				
				}
			}.start();
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			return this.postFeed("me2day", "/"+friendId+"/"+param);
		}
		else
		{
			log4SAPI("postFeedToFriend 留ㅺ�蹂��(type) ���");
			return false;
		}
		return false;
	}
	
	//TODO postPhoto(String type, String message, String filePath, String fileName, String userId)
	public boolean postPhoto(String type, String message, String filePath, String fileName, String userId)
	{
		if(type == null || filePath == null || fileName == null)
		{
			log4SAPI("postPhoto 留ㅺ�蹂�� ���");
			return false;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			Bundle bundle = new Bundle();
			bundle.putString("message", message);			
			File file = new File(filePath,fileName);
			Request request = null;
			try {
				request = Request.newUploadPhotoRequest(mFacebook, file, new Request.Callback() {					
					public void onCompleted(Response response) {
						graphObject = response.getGraphObject();
					}
				});
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			request.executeAndWait();
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			param = filePath;
			param2 = fileName;
			param3 = message;
			new Thread(){
				public void run(){
			File file = new File(param+param2);
			InputStream file_body = null;
			try {
				file_body = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			StatusUpdate item = new StatusUpdate(param3);
			item.setMedia(param2, file_body);
			try {
				mTwitter.updateStatus(item);
			} catch (TwitterException e) {
				e.printStackTrace();
			}			
				}
			}.start();
			return true;
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			param = userId;
			param2 = filePath;
			param3 = fileName;
			param4 = message;
			
			
			new Thread(){
				public void run(){
			HttpClient httpClient = new DefaultHttpClient();
			try{
			if(param==null)
				param=mMe2day.getUserId();
			HttpPost request = new HttpPost("http://me2day.net/api/create_post/"+param+".xml");
			MultipartEntity entity = new MultipartEntity();
			File f = new File(param2+param3);
			entity.addPart("akey",new StringBody(mMe2day.getAppKey()));			
			entity.addPart("post[body]",new StringBody(param4) );
			entity.addPart("uid",new StringBody(mMe2day.getUserId()) );
			entity.addPart("ukey",new StringBody(setUserKey(mMe2day.getUserKey())) );
			entity.addPart("attachment",new FileBody(f) );
			request.setEntity(entity);
			HttpResponse response = httpClient.execute(request);
			InputStream is = response.getEntity().getContent();
			int ch;
			StringBuffer b = new StringBuffer();
			while((ch=is.read())!=-1){
				b.append((char) ch);
			}
			Log.e("Me2dayAPI","RESULT="+b.toString());
			}catch(Throwable e){}
				}
			}.start();
			return true;
			
		}
		else
		{
			log4SAPI("postPhoto 留ㅺ�蹂��(type) ���");
			return false;
		}
		return false;
	}
	
	//TODO setLike(String type, String feedId)
	public boolean setLike(String type, String feedId)
	{
		if(type == null || feedId == null)
		{
			log4SAPI("setLike 留ㅺ�蹂�� ���");
			return false;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			Request request;
			request = new Request(mFacebook, feedId+"/likes", null, HttpMethod.valueOf("POST"),new Request.Callback() {				
				public void onCompleted(Response response) {
					graphObject = response.getGraphObject();		
				}
			});
			request.executeAndWait();
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			param = feedId;
			new Thread(){
				public void run(){
				try {
					mTwitter.createFavorite(Long.parseLong(param));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				}
			}.start();
				return true;
				
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			param = feedId;
			new Thread(){
				public void run(){
			try {						
				String url="http://me2day.net/api/metoo.xml?post_id="+param;				
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true); 
				XmlPullParser xpp = factory.newPullParser();
				InputStream stream = getInputStream(url);
				xpp.setInput(stream, "UTF-8");
			}catch (Throwable e) {	log4SAPI("setLike Me2day ���"); }
				}
			}.start();
		}
		else
		{
			log4SAPI("setLike 留ㅺ�蹂��(type) ���");
			return false;
		}
		return false;
	}
	
	
	
	//TODO deleteFeed(String type, String feedId)
	public boolean deleteFeed(String type, String feedId)
	{
		if(type == null || feedId == null)
		{
			log4SAPI("deleteFeed 留ㅺ�蹂�� ���");
			return false;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			Request request;
			request = new Request(mFacebook, feedId+"/likes", null, HttpMethod.valueOf("DELETE"),new Request.Callback() {				
				public void onCompleted(Response response) {
					graphObject = response.getGraphObject();		
				}
			});
			request.executeAndWait();
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			param = feedId;
			new Thread(){
				public void run(){
				try {
					mTwitter.destroyStatus(Long.parseLong(param));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				}
			}.start();
				return true;
				
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			param = feedId;
			new Thread(){
				public void run(){
				String url="http://me2day.net/api/delete_post.xml?post_id="+param;
			    parseMe2dayPosts(url);
				}
			}.start();
			   return true;
		}
		else
		{
			log4SAPI("deleteFeed 留ㅺ�蹂��(type) ���");
			return false;
		}
		return false;
	}
	
	public ArrayList<User> getlikeuser(String type, String feedId)
	{
		if(type == null || feedId == null)
		{
			log4SAPI("getComments 留ㅺ�蹂�� ���");
			return null;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			ArrayList<User> ulist = new ArrayList<User>();
			try {
			
			Request request;
			String url = feedId+"/likes";
			request = new Request(mFacebook,url,null,null,new Request.Callback() {				
				public void onCompleted(Response response) {		
					graphObject = response.getGraphObject();
					
				}
			});
			request.executeAndWait();
			JSONArray data = graphObject.getInnerJSONObject().getJSONArray("data");
			for(int i=0;i<data.length();i++)
			{
				User user = new User();
				if(data.getJSONObject(i).has("id"))
				user.setUserId(data.getJSONObject(i).getString("id").toString());
				if(data.getJSONObject(i).has("name"))
				user.setName(data.getJSONObject(i).getString("name").toString());
				
				ulist.add(user);
			}
				
			} catch (Throwable e) {			e.printStackTrace();	}
			return ulist;
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			
			
			return null;
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			
			   return null;
		}
		else
		{
			log4SAPI("getComments 留ㅺ�蹂��(type) ���");
			return null;
		}
	}
	
	public String getfaceUserid(){
		String id = null;
		
		try {
			
			
			String url = "/me/";
			Request request;
			request = new Request(mFacebook,url,null,null,new Request.Callback() {				
				public void onCompleted(Response response) {		
					graphObject = response.getGraphObject();
					
				}
			});
			request.executeAndWait();
			
			//JSONArray data = graphObject.getInnerJSONObject().getJSONArray("id");
			
			id = graphObject.getProperty("id").toString();
			//Log.d("test", ">?" + graphObject.getProperty("id").toString());
				
			} catch (Throwable e) {			e.printStackTrace();	}
			
		return id;
	}
		
	
	

	
	//TODO getComments(String type, String feedId)
	public ArrayList<Comment> getComments(String type, String feedId)
	{
		if(type == null || feedId == null)
		{
			log4SAPI("getComments 留ㅺ�蹂�� ���");
			return null;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			ArrayList<Comment> clist = new ArrayList<Comment>();
			try {
			
			Request request;
			String url = feedId+"/comments";
			request = new Request(mFacebook,url,null,null,new Request.Callback() {				
				public void onCompleted(Response response) {		
					graphObject = response.getGraphObject();
					
				}
			});
			request.executeAndWait();
			JSONArray data = graphObject.getInnerJSONObject().getJSONArray("data");
			for(int i=0;i<data.length();i++)
			{
				Comment comment = new Comment();
				if(data.getJSONObject(i).has("id"))
				comment.setCommentId(data.getJSONObject(i).getString("id").toString());
				comment.setSNSName(type);
				if(data.getJSONObject(i).has("message"))
				comment.setText(data.getJSONObject(i).getString("message").toString());
				comment.setFeedId(feedId);
				if(data.getJSONObject(i).has("created_time"))
				comment.setCommentDate(data.getJSONObject(i).getString("created_time").toString());
				if(data.getJSONObject(i).has("from"))
				comment.setUserId(data.getJSONObject(i).getJSONObject("from").getString("id"));
				clist.add(comment);
			}
				
			} catch (Throwable e) {			e.printStackTrace();	}
			return clist;
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			param = type;
			param2 = feedId;
			resultComment = null;
			
			new Thread(){
				public void run(){
			try{
			List<Status> statuses = mTwitter.getRetweets(Long.parseLong(param2));
			ArrayList<Comment> temp = new ArrayList<Comment>();
			for(Status status : statuses)
			{				
				Comment comment = new Comment();
				comment.setSNSName(param);
				comment.setCommentDate(setTwitterDateFormat(status.getCreatedAt().toString()));
				comment.setCommentId(""+status.getId());
				comment.setFeedId(param2);
				comment.setText(status.getText());
				twitter4j.User user = status.getUser();
				comment.setUserId(""+user.getId());
				temp.add(comment);
			}
			resultComment = temp;
			}catch(Throwable e){}
				}
			}.start();
			
			while(resultComment==null);
			
			return resultComment;
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			resultComment = null;
			param = feedId;
			new Thread(){
				public void run(){
			try {						
				String url="http://me2day.net/api/get_comments.xml?post_id="+param;
				
				resultComment = parseMe2dayComments(url,param);
			
			}catch (Throwable e) {	log4SAPI("getComments Me2day ���");  }
				}
			}.start();
			
			while(resultComment==null);
			
			   return resultComment;
		}
		else
		{
			log4SAPI("getComments 留ㅺ�蹂��(type) ���");
			return null;
		}
	}
	
	//TODO postComment(String type, String message, String feedId)
	public boolean postComment(String type, String message, String feedId)
	{
		if(type == null || feedId == null || message == null)
		{
			log4SAPI("postComment 留ㅺ�蹂�� ���");
			return false;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			Request request;
			Bundle bundle = new Bundle();
			bundle.putString("message", message);
			request = new Request(mFacebook, feedId+"/comments", bundle, HttpMethod.valueOf("POST"),new Request.Callback() {				
				public void onCompleted(Response response) {
					graphObject = response.getGraphObject();		
				}
			});
			request.executeAndWait();
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			try{
				return false; //吏����� ���
				
			}catch(Throwable e){return false;}
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			param=feedId;
			param2 = message;
			
			new Thread(){
				public void run(){
			
			try {						
				String url="http://me2day.net/api/create_comment.xml?post_id="+param;
				param2 = java.net.URLEncoder.encode(param2,"UTF-8");	
				url+="&body="+param2;
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true); 
				XmlPullParser xpp = factory.newPullParser();
				InputStream stream = getInputStream(url);
				xpp.setInput(stream, "UTF-8");
			   
			}catch (Throwable e) {	log4SAPI("postComment Me2day ���"); }
				}
			}.start();
			return true;
		}
		else
		{
			log4SAPI("postComment 留ㅺ�蹂��(type) ���");
			return false;
		}
		return false;
	}
	
	//TODO deleteComment(String type, String commentId)
	public boolean deleteComment(String type, String commentId)
	{
		if(type == null || commentId == null)
		{
			log4SAPI("deleteComment 留ㅺ�蹂�� ���");
			return false;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			Request request;
			request = new Request(mFacebook, commentId,null, HttpMethod.valueOf("DELETE"),new Request.Callback() {				
				public void onCompleted(Response response) {
					graphObject = response.getGraphObject();		
				}
			});
			request.executeAndWait();
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			param = commentId;
			new Thread(){
				public void run(){
				try {
					mTwitter.destroyStatus(Long.parseLong(param));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				
				}
			}.start();
				return true;
				
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			param = commentId;
			new Thread(){
				public void run(){
			try {						
				String url="http://me2day.net/api/delete_comment.xml?comment_id="+param;
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true); 
				XmlPullParser xpp = factory.newPullParser();
				InputStream stream = getInputStream(url);
				xpp.setInput(stream, "UTF-8");
			  
			}catch (Throwable e) {	log4SAPI("deleteComment Me2day ���"); }
				}
			}.start();
			 return true;
		}
		else
		{
			log4SAPI("deleteComment 留ㅺ�蹂��(type) ���");
			return false;
		}
		return false;
	}
	
	//TODO getUser(String type, String userId
	public User getUser(String type, String userId)
	{
		if(type == null || userId == null)
		{
			log4SAPI("getUser 留ㅺ�蹂�� ���");
			return null;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			try{
			User user = new User();
			Request request;
			String url = userId;
			request = new Request(mFacebook,url,null,null,new Request.Callback() {				
				public void onCompleted(Response response) {		
					graphObject = response.getGraphObject();
					
				}
			});
			request.executeAndWait();
			user.setUserId(userId);
			user.setName(graphObject.getProperty("name").toString());
			user.setSNSname(type);
									
			user.setUserProfileUrl("http://graph.facebook.com/"+user.getUserId()+"/picture");
			
			return user;
			}catch(Throwable e){return null;}
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			resultUser = null;
			param = userId;
			new Thread(){
				public void run(){
			try{
				twitter4j.User user = mTwitter.showUser(Long.parseLong(param));
				User temp = new User();
				temp.setUserId(""+user.getId());
				temp.setSNSname("twitter");
				temp.setName(user.getName());
				temp.setUserProfileUrl(user.getProfileImageURL().toString());
				resultUser = temp;
			}catch(Throwable e){}
				}
			}.start();
			
			while(resultUser==null);
			
			return resultUser;
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			param = userId;
			new Thread(){
				public void run(){
			try {						
				String url = "http://me2day.net/api/get_person/"+param+".xml";
				resultUser = parseMe2dayPerson(url).get(0);
			
			}catch (Throwable e) {	log4SAPI("getUser Me2day ���"); }
				}
			}.start();
			return resultUser;
		}
		else
		{
			log4SAPI("getUser 留ㅺ�蹂��(type) ���");
			return null;
		}
	}
	
	//TODO getFriends(String type)
	public ArrayList<String> getFriends(String type)
	{
		if(type == null)
		{
			log4SAPI("getFriendsId 留ㅺ�蹂�� ���");
			return null;
		}
		
		if(type.equalsIgnoreCase("facebook"))
		{
			try {
				ArrayList<String> result = new ArrayList<String>();
				Request request;
				String url = "me/friends";
				request = new Request(mFacebook,url,null,null,new Request.Callback() {				
					public void onCompleted(Response response) {		
						graphObject = response.getGraphObject();
						
					}
				});
				request.executeAndWait();
				JSONArray data = graphObject.getInnerJSONObject().getJSONArray("data");
				for(int i=0;i<data.length();i++)
				{
					if(data.getJSONObject(i).has("id"))
					result.add(data.getJSONObject(i).getString("id").toString());
				}
				return result;
				} catch (Throwable e) {		return null;	}
				
		}
		else if(type.equalsIgnoreCase("twitter"))
		{
			resultString = null;
			new Thread(){
				public void run(){
			ArrayList<String> temp = new ArrayList<String>();
			
				try {
					boolean inUser = false;
					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();
					InputStream stream = getInputStream("http://api.twitter.com/1/statuses/friends/"
							+ mTwitter.getId() + ".xml");
					xpp.setInput(stream, "UTF-8");
					int eventType = xpp.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						switch (eventType) {
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.END_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:						
							if (xpp.getName().equals("id") && inUser) {
								temp.add(xpp.nextText());
							}
							break;
						case XmlPullParser.END_TAG:
							
							break;
						case XmlPullParser.TEXT:
							break;
						}
						eventType = xpp.next();
					}
					resultString = temp;
				} catch (Exception e) {
				}
				}
			}.start();
			while(resultString==null);
				return resultString;
			
		}
		else if(type.equalsIgnoreCase("me2day"))
		{
			new Thread(){
				public void run(){
			resultString =null;
			try {						
				String url = "http://me2day.net/api/get_friends/"+mMe2day.getUserId()+".xml?scope=all";				
				ArrayList<User> users = parseMe2dayPerson(url);
				ArrayList<String> temp = new ArrayList<String>();
				for(User user : users)
				{
					temp.add(user.getUserId());
				}
				resultString = temp;
			}catch (Throwable e) {	log4SAPI("getFriends Me2day ���"); }
				}
			}.start();
			while(resultString==null);
			return resultString;
		}
		else
		{
			log4SAPI("getFriends 留ㅺ�蹂��(type) ���");
			return null;
		}
	}
	
//	//TODO test(String type)
//	public boolean test(String type)
//	{
//		if(type == null)
//		{
//			log4SAPI("test 留ㅺ�蹂�� ���");
//			return false;
//		}
//		
//		if(type.equalsIgnoreCase("facebook"))
//		{
//			Request request;
//			request = new Request(mFacebook, "me/permissions", null, HttpMethod.valueOf("GET"),new Request.Callback() {				
//				@Override
//				public void onCompleted(Response response) {
//					graphObject = response.getGraphObject();		
//				}
//			});
//			
//			request.executeAndWait();
//			if(graphObject.getInnerJSONObject().has("data"))
//				return true;
//			return false;
//		}
//		else if(type.equalsIgnoreCase("twitter"))
//		{
//			new Thread(){
//				public void run(){
//			try{
//				return mTwitter.test();
//			}catch(Throwable e){}
//				}
//			}.start();
//		}
//		else if(type.equalsIgnoreCase("me2day"))
//		{
//			String result_code = null;
//			String result_message=null;
//			try{
//				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//				factory.setNamespaceAware(true); 
//				XmlPullParser xpp = factory.newPullParser();
//				InputStream stream = getInputStream(setUrlFormat("http://me2day.net/api/noop.xml"));
//				xpp.setInput(stream, "UTF-8");
//				int eventType = xpp.getEventType(); 
//				   while (eventType != XmlPullParser.END_DOCUMENT) {
//				    switch(eventType){
//				    case XmlPullParser.START_DOCUMENT:     break;
//				    case XmlPullParser.END_DOCUMENT:     break;
//				    case XmlPullParser.START_TAG:	
//				    	if(xpp.getName().equals("code")){
//				    		result_code=xpp.nextText();
//				    	}
//				    	if(xpp.getName().equals("message")){
//				    		result_message=xpp.nextText();
//				    	}			    	
//				    	
//				     break;
//				    case XmlPullParser.END_TAG:	  	break;			    
//				    case XmlPullParser.TEXT:     break; 
//				    }
//				    eventType = xpp.next();
//				   }
//				   if(result_code.equals("0") && result_message.equals("�깃났������."))
//					   return true;
//				   else
//					   return false;
//				
//			}catch (Throwable e) {	Log.e("Me2dayAPI","test ���"); return false; }
//		}
//		else
//		{
//			log4SAPI("getFriendTimeline 留ㅺ�蹂��(type) ���");
//			return false;
//		}
//	}
	
	//TODO API Utility
	
	//TODO parseMe2dayPosts(String url)
	private ArrayList<Feed> parseMe2dayPosts(String url)
	{
		try{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser();
			InputStream stream = getInputStream(url);
			xpp.setInput(stream, "UTF-8");
			int eventType = xpp.getEventType(); 
			   boolean inAuthor=false,inMedia=false;
			   ArrayList<Feed> feedList = new ArrayList<Feed>();
			   Feed feed = null;
			   while (eventType != XmlPullParser.END_DOCUMENT) {
			    switch(eventType){
			    case XmlPullParser.START_DOCUMENT:     break;
			    case XmlPullParser.END_DOCUMENT:     break;
			    case XmlPullParser.START_TAG:	
			    	if(xpp.getName().equals("post")){
			    		feed= new Feed();
			    		feed.setSNSName("Me2day");
			    	}
			    	if(xpp.getName().equals("post_id")){
			    		feed.setFeedId(xpp.nextText());
			    	}
			    	if(xpp.getName().equals("textBody")){			    		
			    		feed.setText(xpp.nextText());	
			    	}
			    	if(xpp.getName().equals("pubDate")){			    		
			    		feed.setFeedDate(xpp.nextText());
			    	}
			    	if(xpp.getName().equals("author")){		
			    		inAuthor=true;
			    	}
			    	if(xpp.getName().equals("name") && inAuthor){		
			    		feed.setUserId(xpp.nextText());	
			    	}
			    	if(xpp.getName().equals("media")){		
			    		inMedia=true;
			    	}
			    	if((xpp.getName().equals("photoUrl") || xpp.getName().equals("source")) && inMedia){		
			    		feed.setFeedImg(xpp.nextText());
			    	}			    	
			    	
			     break;
			    case XmlPullParser.END_TAG:	   
			    	if(xpp.getName().equals("media")  && inMedia){		
			    		inMedia=false;
			    	}
			    	if(xpp.getName().equals("post")){
			    		feedList.add(feed);		
			    	}
			    	if(xpp.getName().equals("author") && inAuthor){		
			    		inAuthor=false;
			    	}
			    	break;
			    
			    case XmlPullParser.TEXT:     break; 
			    }
			    eventType = xpp.next();
			   }
			   return feedList;
			
		}catch (Throwable e) {	return null; }
	}
	
	//TODO parseMe2dayComments
	private ArrayList<Comment> parseMe2dayComments(String url,String feedId)
	{
		try{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser();
			InputStream stream = getInputStream(url);
			xpp.setInput(stream, "UTF-8");
			int eventType = xpp.getEventType(); 
			   ArrayList<Comment> comments = new ArrayList<Comment>();
			   Comment comment=null;
			   while (eventType != XmlPullParser.END_DOCUMENT) {
			    switch(eventType){
			    case XmlPullParser.START_DOCUMENT:     break;
			    case XmlPullParser.END_DOCUMENT:     break;
			    case XmlPullParser.START_TAG:	
			    	if(xpp.getName().equals("comment")){
			    		comment = new Comment();
			    		comment.setSNSName("me2day");
			    		comment.setFeedId(feedId);
			    	}
			    	if(xpp.getName().equals("commentId")){
			    		comment.setCommentId(xpp.nextText());
			    	}
			    	if(xpp.getName().equals("textBody")){
			    		comment.setText(xpp.nextText());
			    	}
			    	if(xpp.getName().equals("pubDate")){
			    		comment.setCommentDate(xpp.nextText());
			    	}
			    	if(xpp.getName().equals("id")){
			    		comment.setUserId(xpp.nextText());
			    	}
			    	
			    	
			     break;
			    case XmlPullParser.END_TAG:	   
			    	if(xpp.getName().equals("comment") ){	
			    		comments.add(comment);			    		
			    	}
			    	
			    	break;
			    
			    case XmlPullParser.TEXT:     break; 
			    }
			    eventType = xpp.next();
			   }
			   return comments;
			
		}catch (Throwable e) {	Log.e("Me2dayAPI","parseComments ���"); return null; }
	}
	
	//TODO parseMe2dayPerson
	private ArrayList<User> parseMe2dayPerson(String url)
	{
		try{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser();
			InputStream stream = getInputStream(url);
			xpp.setInput(stream, "UTF-8");
			int eventType = xpp.getEventType(); 
			   ArrayList<User> persons = new ArrayList<User>();
			   User person=null;
			   while (eventType != XmlPullParser.END_DOCUMENT) {
			    switch(eventType){
			    case XmlPullParser.START_DOCUMENT:     break;
			    case XmlPullParser.END_DOCUMENT:     break;
			    case XmlPullParser.START_TAG:	
			    	if(xpp.getName().equals("person")){
			    		person = new User();
			    		person.setSNSname("me2day");
			    	}
			    	if(xpp.getName().equals("id")){
			    		person.setUserId(xpp.nextText());
			    	}
			    	if(xpp.getName().equals("nickname")){
			    		person.setName(xpp.nextText());
			    	}
			    	if(xpp.getName().equals("face")){
			    		person.setUserProfileUrl(xpp.nextText());
			    	}
			    		
			    	
			     break;
			    case XmlPullParser.END_TAG:	   
			    	if(xpp.getName().equals("person") ){	
			    		persons.add(person);			    		
			    	}
			    	
			    	break;
			    
			    case XmlPullParser.TEXT:     break; 
			    }
			    eventType = xpp.next();
			   }
			   return persons;
			
		}catch (Throwable e) { return null; }
	}
	
	
	
	
	
	//TODO Connection Utility 
	
	//TODO Me2day urlFormat
	private String setUrlFormat(String url)
	{		
		try {
			
			String ukey = setUserKey(mMe2day.getUserKey());
			if(url.indexOf("?", 0)>0)
			{
				url+="&uid="+mMe2day.getUserId()+"&ukey="+ukey+"&akey="+mMe2day.getAppKey();
			}
			else
			{
				url+="?uid="+mMe2day.getUserId()+"&ukey="+ukey+"&akey="+mMe2day.getAppKey();
			}
			
			
		} catch (Throwable e) { return null; }
		
		
		return url;
	}
	
	//TODO Twitter DateFormat(String date)
	private String setTwitterDateFormat(String date)
	{
		String fulldate=date;
		
		int yidx=fulldate.indexOf(".", 0);
		String y=fulldate.substring(0,yidx);
		int midx=fulldate.indexOf(".", yidx+1);
		String m=fulldate.substring(yidx+2,midx);
		int didx=fulldate.indexOf(".",midx+1);
		String d=fulldate.substring(midx+2,didx);
		int apidx=fulldate.indexOf(" ",didx+2);
		String ap=fulldate.substring(didx+2,apidx);
		String fulltime=fulldate.substring(apidx+1);
		String time;
		if(ap.equals("�ㅼ�"))
		{
		if(Integer.parseInt(fulltime.substring(0,fulltime.indexOf(":", 0)))<10)
			{
				fulltime="0"+fulltime;
			}
			
		}
		if(ap.equals("�ㅽ�"))
		{						
			if(Integer.parseInt(fulltime.substring(0,fulltime.indexOf(":", 0)))==12)
				time=fulltime;
			else
				time=String.valueOf(Integer.parseInt(fulltime.substring(0,fulltime.indexOf(":", 0)))+12)+fulltime.substring(fulltime.indexOf(":", 0));
		}
		else
			time=fulltime;
		String month;
		if(Integer.parseInt(m)<10)	{month="0"+m;}
		else		{month=""+m;}
		String day;
		if(Integer.parseInt(d)<10)	{day="0"+d;}
		else		{day=""+d;}				
		String Date = y+"-"+month+"-"+day+"T"+time;
		return Date;
	}
	
	//TODO Me2day setUserKey(MD5)
	private String setUserKey(String user_key)
	{		
		try {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		String input_pass = mMe2day.getUserKey();
		input_pass = "12345678" + input_pass;
		digest.update(input_pass.getBytes(), 0, input_pass.length());
		user_key = new BigInteger(1, digest.digest()).toString(16);
		String ukey="12345678"+user_key;
		return ukey;
		}catch (Throwable e) {return null; }
	}
	
	//TODO getInputStream(String para_url)
	private InputStream getInputStream(String para_url) {
		int count=10;
		while (count>0) {
			try {
				
				URL url = new URL(para_url);
				URLConnection con = url.openConnection();
				InputStream is = con.getInputStream();
				return is;
			} catch (Exception e) {
				e.getMessage();
				count--;
			}
		}
		return null;
	}
	
}
