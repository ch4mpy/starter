package com.c4_soft.starter.proxies.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c4_soft.starter.proxies.domain.Grant;

public interface GrantRepository extends JpaRepository<Grant, Long> {

}
