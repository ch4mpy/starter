package com.c4_soft.starter.persistence.intervention;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.c4_soft.starter.domain.intervention.FaultAttachment;


@Repository
public interface FaultAttachmentRepo extends PagingAndSortingRepository<FaultAttachment, Long> {

    List<FaultAttachment> findByFaultId(long faultId);

    void deleteByFaultId(long faultId);
}
