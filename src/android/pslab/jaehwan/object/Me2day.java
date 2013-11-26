package android.pslab.jaehwan.object;

public class Me2day {
	private String userId;
	private String appKey;
	private String userKey;
	
	public Me2day(String userId, String appKey, String userKey)
	{
		this.userId=userId;
		this.appKey=appKey;
		this.userKey=userKey;
	}	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getUserKey() {
		return userKey;
	}
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}
	
}
