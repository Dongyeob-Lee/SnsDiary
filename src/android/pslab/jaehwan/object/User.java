package android.pslab.jaehwan.object;

public class User {
	private String SNSname;
	private String name;
	private String userId;
	private String userProfileUrl;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserProfileUrl() {
		return userProfileUrl;
	}
	public void setUserProfileUrl(String userProfileUrl) {
		this.userProfileUrl = userProfileUrl;
	}
	public String getSNSname() {
		return SNSname;
	}
	public void setSNSname(String sNSname) {
		SNSname = sNSname;
	}
	
}
