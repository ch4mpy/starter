package com.c4_soft.starter.cafeskifo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import com.c4_soft.starter.cafeskifo.domain.Order;

@Repository
public interface UnsecuredOrderRepository extends JpaRepository<Order, Long>, RevisionRepository<Order, Long, Long> {
}