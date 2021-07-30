package com.c4_soft.starter.persistence.intervention;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.c4_soft.starter.domain.intervention.Fault;

@Repository
public interface FaultRepo extends PagingAndSortingRepository<Fault, Long> {

    Page<Fault> findByclosedAtIsNull(Pageable pageable);
    
}
