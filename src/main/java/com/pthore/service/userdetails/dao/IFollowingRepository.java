package com.pthore.service.userdetails.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pthore.service.userdetails.entities.Following;

@Repository
public interface IFollowingRepository extends  JpaRepository<Following, Long> {

}
