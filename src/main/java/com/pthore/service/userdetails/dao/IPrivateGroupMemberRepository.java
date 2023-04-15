package com.pthore.service.userdetails.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pthore.service.userdetails.entities.PrivateGroupMember;

public interface IPrivateGroupMemberRepository extends JpaRepository<PrivateGroupMember, Long> {

}
