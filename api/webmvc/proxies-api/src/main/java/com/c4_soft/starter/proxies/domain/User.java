package com.c4_soft.starter.proxies.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	private String subject;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id")
	private Set<Proxy> proxies;

	public User(Collection<Proxy> proxies) {
		this.proxies = new HashSet<>(proxies);
	}

	public Set<Grant> getGrantsOn(String userSubject) {
		return proxies
				.stream()
				.filter(p -> p.getProxiedUser().getSubject().equals(userSubject))
				.map(Proxy::getGrants)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	public User setGrantsOn(User proxiedUser, Collection<Grant> grants) {
		final var proxy = proxies.stream().filter(p -> p.getProxiedUser().getSubject().equals(proxiedUser.getSubject())).findAny().orElseGet(() -> {
			final var p = new Proxy(null, proxiedUser, Collections.emptySet());
			proxies.add(p);
			return p;
		});
		if (grants.isEmpty()) {
			proxies.remove(proxy);
		} else {
			proxy.setGrants(new HashSet<>(grants));
		}
		return this;
	}

	public Map<String, Set<Grant>> getAllGrantsByUserSubject() {
		return proxies.stream().collect(Collectors.toMap(p -> p.getProxiedUser().getSubject(), Proxy::getGrants));
	}
}
