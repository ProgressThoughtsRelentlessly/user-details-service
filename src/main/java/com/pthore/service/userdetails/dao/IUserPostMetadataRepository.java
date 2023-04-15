package com.pthore.service.userdetails.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pthore.service.userdetails.entities.UserPostMetadata;
import com.pthore.service.userdetails.entities.UserProfile;

public interface IUserPostMetadataRepository extends JpaRepository<UserPostMetadata, Long> {

	public List<UserPostMetadata> findByPostIdIn(List<String> postIds);

	public List<UserPostMetadata> findByUserProfile(UserProfile userProfile);

}
