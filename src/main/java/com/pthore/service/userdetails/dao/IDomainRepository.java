package com.pthore.service.userdetails.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pthore.service.userdetails.entities.Domain;

public interface IDomainRepository extends JpaRepository<Domain, String> {
	public Domain findByDomainName(String domainName);
	public Domain deleteDomainByName(String domainName);
}
