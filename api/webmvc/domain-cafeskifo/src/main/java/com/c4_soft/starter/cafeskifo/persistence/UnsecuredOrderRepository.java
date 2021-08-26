package com.c4_soft.starter.cafeskifo.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import com.c4_soft.starter.cafeskifo.domain.Order;

@Repository
interface UnsecuredOrderRepository extends CrudRepository<Order, Long>, RevisionRepository<Order, Long, Long> {
}