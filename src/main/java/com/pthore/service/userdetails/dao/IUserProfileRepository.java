package com.pthore.service.userdetails.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pthore.service.userdetails.entities.UserProfile;

public interface IUserProfileRepository extends JpaRepository<UserProfile, Long> {
	
	public UserProfile findByEmail(String email);
	
	public List<UserProfile> findByFirstnameOrLastnameOrEmail(String firstname, String lastname, String email);

	public List<UserProfile> findByEmailIn(Set<String> acceptedToGroupUsersEmail);

	public List<UserProfile> findByIsGroupEquals(boolean b);
}
