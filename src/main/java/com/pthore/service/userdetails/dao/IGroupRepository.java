package com.pthore.service.userdetails.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pthore.service.userdetails.entities.PthoreGroup;

public interface IGroupRepository extends JpaRepository<PthoreGroup, Long> {

}
