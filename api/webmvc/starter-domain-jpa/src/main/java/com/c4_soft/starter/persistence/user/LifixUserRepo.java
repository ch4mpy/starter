package com.c4_soft.starter.persistence.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.c4_soft.starter.domain.user.LifixUser;

@Repository
public interface LifixUserRepo extends PagingAndSortingRepository<LifixUser, Long> {

}
