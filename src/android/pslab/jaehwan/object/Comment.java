package android.pslab.jaehwan.object;

public class Comment {
	private String SNSName;
	private String FeedId;
	private String CommentId;
	private String CommentDate;
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
	public String getCommentId() {
		return CommentId;
	}
	public void setCommentId(String commentId) {
		CommentId = commentId;
	}
	public String getCommentDate() {
		return CommentDate;
	}
	public void setCommentDate(String commentDate) {
		CommentDate = commentDate;
	}

	
	
}
