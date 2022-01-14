package com.c4_soft.starter.trashbins.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.c4_soft.starter.trashbins.domain.Household;
import com.c4_soft.starter.trashbins.domain.HouseholdType;

@Repository
public interface HouseholdRepo extends JpaRepository<Household, Long>, JpaSpecificationExecutor<Household> {
	String TAXPAYER = "taxpayer";
	Pattern POSITIVE_LONG_PATTERN = Pattern.compile("^\\d+$");

	static EntityNotFoundException notFound(Long id) {
		return new EntityNotFoundException("No household with id " + id);
	}

	static Specification<Household> searchSpec(String householdLabel, String taxpayerNameOrId, HouseholdType householdType) {
		if (!StringUtils.hasText(householdLabel) && !StringUtils.hasText(taxpayerNameOrId) && householdType == null) {
			return null;
		}

		final List<Specification<Household>> specs = new ArrayList<>(3);

		if (StringUtils.hasText(householdLabel)) {
			specs
					.add(
							(root, query, criteriaBuilder) -> criteriaBuilder
									.like(criteriaBuilder.lower(root.get("label")), "%" + householdLabel.toLowerCase().trim() + "%"));
		}

		if (StringUtils.hasText(taxpayerNameOrId)) {
			final var idMatcher = POSITIVE_LONG_PATTERN.matcher(taxpayerNameOrId);
			specs
					.add(
							(root, query, criteriaBuilder) -> idMatcher.matches()
									? criteriaBuilder.equal(root.get(TAXPAYER).get("id"), Long.parseLong(taxpayerNameOrId))
									: criteriaBuilder
											.like(criteriaBuilder.lower(root.get(TAXPAYER).get("name")), "%" + taxpayerNameOrId.toLowerCase().trim() + "%"));
		}

		if (householdType != null) {
			specs.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type").get("label"), householdType.getLabel()));
		}

		var spec = Specification.where(specs.get(0));
		for (var i = 1; i < specs.size(); ++i) {
			spec = spec.and(specs.get(i));
		}

		return spec;
	}
}
