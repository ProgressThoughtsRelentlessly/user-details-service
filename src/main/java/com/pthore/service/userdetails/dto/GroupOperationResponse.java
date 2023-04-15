package com.pthore.service.userdetails.dto;

import java.util.List;

public class GroupOperationResponse {
	
	private List<PublicUserProfileInfo> users;
	private String groupJoinStatus;
	
	public List<PublicUserProfileInfo> getUsers() {
		return users;
	}
	public void setUsers(List<PublicUserProfileInfo> users) {
		this.users = users;
	}
	public String getGroupJoinStatus() {
		return groupJoinStatus;
	}
	public void setGroupJoinStatus(String groupJoinStatus) {
		this.groupJoinStatus = groupJoinStatus;
	}
	
	
	
	@Override
	public String toString() {
		return "GroupOperationResponse [users=" + users + ", groupJoinStatus=" + groupJoinStatus + "]";
	}
	
	
	
}
