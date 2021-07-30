package com.c4_soft.starter.domain.intervention;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fault_attachment")
public class FaultAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, name = "fault_id")
    Long faultId;

    @Column(nullable = false)
    String extension;

    public FaultAttachment(Long faultId, String extension) {
        this(null, faultId, extension);
    }

}
