package venuproject.uwf.edu;

public class SessionInfo {

	private String authorizationToken;
	private long sessionTime;
	
	public String getAuthorizationToken() {
		return authorizationToken;
	}
	public void setAuthorizationToken(String authorizationToken) {
		this.authorizationToken = authorizationToken;
	}
	public long getSessionTime() {
		return sessionTime;
	}
	public void setSessionTime(long sessionTime) {
		this.sessionTime = sessionTime;
	};
	
}
