package com.pthore.service.userdetails.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pthore.service.userdetails.entities.PrivateGroupJoinRequest;

public interface IPrivateGroupJoinRequestRepository extends JpaRepository<PrivateGroupJoinRequest, Long>{

}
