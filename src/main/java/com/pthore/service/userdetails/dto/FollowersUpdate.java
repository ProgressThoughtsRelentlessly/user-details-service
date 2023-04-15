package com.pthore.service.userdetails.dto;

public class FollowersUpdate {
	
	private String myEmail;
	private String targetEmail;
	private String operation; // add / remove.
	
	public FollowersUpdate() {
	}

	public String getMyEmail() {
		return myEmail;
	}

	public void setMyEmail(String myEmail) {
		this.myEmail = myEmail;
	}

	public String getTargetEmail() {
		return targetEmail;
	}

	public void setTargetEmail(String targetEmail) {
		this.targetEmail = targetEmail;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	
}
