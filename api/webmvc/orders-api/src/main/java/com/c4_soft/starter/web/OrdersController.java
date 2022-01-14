package com.c4_soft.starter.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c4_soft.commons.web.ResourceNotFoundException;
import com.c4_soft.springaddons.security.oauth2.oidc.OidcAuthentication;
import com.c4_soft.starter.cafeskifo.domain.Order;
import com.c4_soft.starter.cafeskifo.persistence.UnsecuredOrderRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class OrdersController {

	private final UnsecuredOrderRepository orderRepo;

	private final OrderMapper orderMapper;

	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getAll() {
		final var orders = orderRepo.findAll();
		final var dtos = StreamSupport.stream(orders.spliterator(), true).map(orderMapper::toDto).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderResponseDto> getById(@PathVariable("id") long id) {
		final var order = getOrderById(id);
		return ResponseEntity.ok(orderMapper.toDto(order));
	}

	@PostMapping
	@Transactional
	public ResponseEntity<Long> placeOrder(@Valid @RequestBody OrderCreationRequestDto dto, OidcAuthentication auth) {
		final var order = orderRepo.save(Order.builder().userSubject(auth.getToken().getSubject()).drink(dto.getDrink()).table(dto.getTable()).build());

		return ResponseEntity.created(linkTo(methodOn(OrdersController.class).getById(order.getId())).withSelfRel().toUri()).body(order.getId());
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<?> deleteById(@PathVariable("id") long id) {
		final var order = getOrderById(id);
		orderRepo.delete(order);
		return ResponseEntity.noContent().build();
	}

	private Order getOrderById(long id) {
		return orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No order with id: " + id));
	}
}
