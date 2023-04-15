package com.pthore.service.userdetails.entities;


import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.CreationTimestamp;



@Entity
public class Follower {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator="FollowerSequenceGenerator")
	@SequenceGenerator(
			name = "FollowerSequenceGenerator", 
			sequenceName = "follower_sequence",
			initialValue = 1,
			allocationSize = 1
		)	
	private Long id;
	
	private String email;
	
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY)
	private UserProfile user;
	
	@CreationTimestamp
	private LocalDate followedDate;
	
	public Follower() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserProfile getUser() {
		return user;
	}

	public void setUser(UserProfile user) {
		this.user = user;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getFollowedDate() {
		return followedDate;
	}

	public void setFollowedDate(LocalDate followedDate) {
		this.followedDate = followedDate;
	}
	
	
	
	

}
