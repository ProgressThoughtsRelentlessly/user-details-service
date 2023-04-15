package com.pthore.service.userdetails.dto;

import java.util.List;

public class GroupOperationRequest {
	
	private String userEmail;
	private String otherUserEmail;
	private String groupOperation;
	private String jwt;
	private Long userId;
	private List<PublicUserProfileInfo> users;
	private List<String> groupJoinStatuses;
	
	
	
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getOtherUserEmail() {
		return otherUserEmail;
	}
	public void setOtherUserEmail(String otherUserEmail) {
		this.otherUserEmail = otherUserEmail;
	}
	public String getGroupOperation() {
		return groupOperation;
	}
	public void setGroupOperation(String groupOperation) {
		this.groupOperation = groupOperation;
	}
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public List<PublicUserProfileInfo> getUsers() {
		return users;
	}
	public void setUsers(List<PublicUserProfileInfo> users) {
		this.users = users;
	}
	public List<String> getGroupJoinStatuses() {
		return groupJoinStatuses;
	}
	public void setGroupJoinStatuses(List<String> groupJoinStatuses) {
		this.groupJoinStatuses = groupJoinStatuses;
	}

	
	
	
}
