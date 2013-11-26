package android.pslab.jaehwan.object;

public class Feed {
	private String SNSName;
	private String FeedId;
	private String FeedDate;
	private String FeedImg;
	private String UserId;
	private String Text;
	
	public String getSNSName() {
		return SNSName;
	}
	public void setSNSName(String sNSName) {
		SNSName = sNSName;
	}
	public String getFeedId() {
		return FeedId;
	}
	public void setFeedId(String feedId) {
		FeedId = feedId;
	}
	public String getFeedDate() {
		return FeedDate;
	}
	public void setFeedDate(String feedDate) {
		FeedDate = feedDate;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getText() {
		return Text;
	}
	public void setText(String text) {
		Text = text;
	}


	public String getFeedImg() {
		return FeedImg;
	}

	public void setFeedImg(String feedImg) {
		FeedImg = feedImg;
	}
	
	
}
