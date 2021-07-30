package com.c4_soft.starter.domain.intervention;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "fault_attachment")
public class FaultAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fault_id", nullable = false)
    final Fault fault;

    @Column(nullable = false)
    String extension;

    public FaultAttachment(Fault fault, String extension) {
        this(null, fault, extension);
    }

    FaultAttachment() {
        this(null, null, null);
    }

}
