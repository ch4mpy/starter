package com.c4_soft.starter.proxies.web;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c4_soft.starter.proxies.jpa.GrantRepository;
import com.c4_soft.starter.proxies.web.dto.GrantDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/grants", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
@RequiredArgsConstructor
@Transactional
public class GrantsController {

	private final GrantRepository grantRepo;

	@GetMapping()
	public List<GrantDto> getAll() {
		return grantRepo.findAll().stream().map(g -> new GrantDto(g.getId(), g.getLabel())).collect(Collectors.toList());
	}

}
