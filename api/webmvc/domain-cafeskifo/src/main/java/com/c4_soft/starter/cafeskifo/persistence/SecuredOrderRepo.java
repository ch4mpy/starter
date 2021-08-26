package com.c4_soft.starter.cafeskifo.persistence;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import com.c4_soft.starter.cafeskifo.domain.Order;

@Repository
public class SecuredOrderRepo {

	private UnsecuredOrderRepository delegate;

	@Autowired
	public SecuredOrderRepo(UnsecuredOrderRepository delegate) {
		this.delegate = delegate;
	}

	@PostFilter("hasAuthority('BARMAN') or hasAuthority('WAITER') or filterObject.userSubject == authentication.token.subject")
	public Iterable<Order> findAll() {
		return this.delegate.findAll();
	}

	@PostAuthorize("hasAuthority('BARMAN') or hasAuthority('WAITER') or returnObject.orElse(null)?.userSubject == authentication.token.subject")
	public Optional<Order> findById(Long id) {
		return this.delegate.findById(id);
	}

	@PreAuthorize("hasAuthority('BARMAN') or hasAuthority('WAITER') or #entity.userSubject == authentication.token.subject")
	public void delete(Order entity) {
		this.delegate.delete(entity);
	}

	@PreAuthorize("hasAuthority('BARMAN') or hasAuthority('WAITER') or #entity.userSubject == authentication.token.subject")
	public Order save(Order entity) {
		return this.delegate.save(entity);
	}
}
