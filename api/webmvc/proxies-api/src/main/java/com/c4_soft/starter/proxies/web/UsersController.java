package com.c4_soft.starter.proxies.web;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c4_soft.commons.web.ResourceNotFoundException;
import com.c4_soft.starter.proxies.domain.Grant;
import com.c4_soft.starter.proxies.domain.User;
import com.c4_soft.starter.proxies.jpa.GrantRepository;
import com.c4_soft.starter.proxies.jpa.UserRepository;
import com.c4_soft.starter.proxies.security.CustomOidcToken;
import com.c4_soft.starter.proxies.web.dto.ProxyDto;
import com.c4_soft.starter.proxies.web.dto.UserProxiesDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/users", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
@RequiredArgsConstructor
@Transactional
@PreAuthorize("isAuthenticated()")
public class UsersController {
	private final UserRepository userRepo;
	private final GrantRepository grantRepo;

	@GetMapping("/{userSubject}/proxies")
	public UserProxiesDto getUserProxies(@PathVariable(name = "userSubject") String userSubject) {
		return userRepo.findById(userSubject).map(u -> {
			final Set<ProxyDto> proxies = u.getProxies().stream().map(p -> {
				final Set<Long> grantIds = p.getGrants().stream().map(Grant::getId).collect(Collectors.toSet());
				return new ProxyDto(p.getProxiedUser().getSubject(), grantIds);
			}).collect(Collectors.toSet());
			return new UserProxiesDto(proxies);
		}).orElseThrow(() -> new ResourceNotFoundException(String.format("No user with subject %s", userSubject)));
	}

	@PutMapping("/{proxiedUserSubject}/proxies/{grantedUserSubject}")
	@PreAuthorize("#token.getSubject() == #proxiedUserSubject")
	public ResponseEntity<?> editUserProxy(
			@PathVariable(name = "proxiedUserSubject") @NotEmpty String proxiedUserSubject,
			@PathVariable(name = "grantedUserSubject") @NotEmpty String grantedUserSubject,
			@RequestBody Collection<Long> grantIds,
			@AuthenticationPrincipal CustomOidcToken token) {

		final var proxiedUser = getOrCreateUser(proxiedUserSubject);
		final var grantedUser = getOrCreateUser(grantedUserSubject);
		final var grants = grantRepo.findAllById(grantIds);
		grantedUser.setGrantsOn(proxiedUser, grants);
		userRepo.save(grantedUser);

		return ResponseEntity.accepted().build();
	}

	@DeleteMapping("/{proxiedUserSubject}/proxies/{grantedUserSubject}")
	@PreAuthorize("#token.getSubject() == #proxiedUserSubject")
	public ResponseEntity<?> deleteUserProxy(
			@PathVariable(name = "proxiedUserSubject") String proxiedUserSubject,
			@PathVariable(name = "grantedUserSubject") String grantedUserSubject,
			@AuthenticationPrincipal CustomOidcToken token) {

		final var proxiedUser = getOrCreateUser(proxiedUserSubject);
		final var grantedUser = getOrCreateUser(grantedUserSubject);
		grantedUser.setGrantsOn(proxiedUser, Collections.emptySet());
		userRepo.save(grantedUser);

		return ResponseEntity.accepted().build();
	}

	private User getOrCreateUser(String subject) {
		return userRepo.findById(subject).orElseGet(() -> userRepo.save(new User(subject, Collections.emptySet())));
	}

}
