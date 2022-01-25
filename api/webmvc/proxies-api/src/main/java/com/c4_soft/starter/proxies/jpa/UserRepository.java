package com.c4_soft.starter.proxies.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c4_soft.starter.proxies.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

}
