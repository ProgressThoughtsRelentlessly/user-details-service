package com.pthore.service.userdetails.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pthore.service.userdetails.entities.Follower;
import com.pthore.service.userdetails.entities.UserProfile;

public interface IFollowersRepository extends JpaRepository<Follower, Long>{

	void deleteByEmailAndUser(String email, UserProfile targetUser);

	void deleteByEmail(String email);

}
