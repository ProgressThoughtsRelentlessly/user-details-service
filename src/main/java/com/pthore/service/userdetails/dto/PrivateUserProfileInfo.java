package com.pthore.service.userdetails.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pthore.service.userdetails.entities.Domain;
import com.pthore.service.userdetails.entities.Follower;
import com.pthore.service.userdetails.entities.Following;

public class PrivateUserProfileInfo {
	
	private String email;
	private String profilePicture;
	private String description;
	private Long followersCount;
	private Map<String, String> socialMediaLinks;
	private Map<String, Integer> monthlyPostCount;	
	private Set<Follower> followers;
	private Set<Following> usersYouFollow;
	private Set<Following> groupsYouFollow;
	private Set<Domain> domainsYouFollow;
	private Long learningStreak;
	
	
	
	public Long getLearningStreak() {
		return learningStreak;
	}
	public void setLearningStreak(Long learningStreak) {
		this.learningStreak = learningStreak;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(Long followersCount) {
		this.followersCount = followersCount;
	}
	public Map<String, String> getSocialMediaLinks() {
		return socialMediaLinks;
	}
	public void setSocialMediaLinks(Map<String, String> socialMediaLinks) {
		this.socialMediaLinks = socialMediaLinks;
	}
	public Map<String, Integer> getMonthlyPostCount() {
		return monthlyPostCount;
	}
	public void setMonthlyPostCount(Map<String, Integer> monthlyPostCount) {
		this.monthlyPostCount = monthlyPostCount;
	}

	public Set<Follower> getFollowers() {
		return followers;
	}
	public void setFollowers(Set<Follower> followers) {
		this.followers = followers;
	}
	public Set<Following> getUsersYouFollow() {
		return usersYouFollow;
	}
	public void setUsersYouFollow(Set<Following> usersYouFollow) {
		this.usersYouFollow = usersYouFollow;
	}
	public Set<Following> getGroupsYouFollow() {
		return groupsYouFollow;
	}
	public void setGroupsYouFollow(Set<Following> groupsYouFollow) {
		this.groupsYouFollow = groupsYouFollow;
	}
	public Set<Domain> getDomainsYouFollow() {
		return domainsYouFollow;
	}
	public void setDomainsYouFollow(Set<Domain> domainsYouFollow) {
		this.domainsYouFollow = domainsYouFollow;
	}
	
	
	
}
