package com.c4soft.starter.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.util.StringUtils;

import com.c4soft.starter.domain.Household;
import com.c4soft.starter.domain.HouseholdType;

import lombok.val;

public interface HouseholdRepo extends PagingAndSortingRepository<Household, Long>, JpaSpecificationExecutor<Household> {
    static final String TAXPAYER = "taxpayer";
    static final Pattern POSITIVE_LONG_PATTERN = Pattern.compile("^\\d+$");

    public static EntityNotFoundException notFound(Long id) {
        return new EntityNotFoundException("No household with id " + id);
    }

    public static Specification<Household> searchSpec(String householdLabel, String taxpayerNameOrId, HouseholdType householdType) {
        if (!StringUtils.hasText(householdLabel) && !StringUtils.hasText(taxpayerNameOrId) && householdType == null) {
            return null;
        }

        final List<Specification<Household>> specs = new ArrayList<>(3);

        if (StringUtils.hasText(householdLabel)) {
            specs.add(
                    (root, query, criteriaBuilder) -> criteriaBuilder
                            .like(criteriaBuilder.lower(root.get("label")), "%" + householdLabel.toLowerCase().trim() + "%"));
        }

        if (StringUtils.hasText(taxpayerNameOrId)) {
            val idMatcher = POSITIVE_LONG_PATTERN.matcher(taxpayerNameOrId);
            specs.add(
                    (root, query, criteriaBuilder) -> idMatcher.matches()
                            ? criteriaBuilder.equal(root.get(TAXPAYER).get("id"), Long.parseLong(taxpayerNameOrId))
                            : criteriaBuilder.like(criteriaBuilder.lower(root.get(TAXPAYER).get("name")), "%" + taxpayerNameOrId.toLowerCase().trim() + "%"));
        }

        if (householdType != null) {
            specs.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type").get("label"), householdType.getLabel()));
        }

        var spec = Specification.where(specs.get(0));
        for (int i = 1; i < specs.size(); ++i) {
            spec = spec.and(specs.get(i));
        }

        return spec;
    }
}
