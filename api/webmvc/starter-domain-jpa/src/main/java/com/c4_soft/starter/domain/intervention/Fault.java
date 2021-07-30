package com.c4_soft.starter.domain.intervention;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
@Table(name = "fault")
public class Fault {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String description;

    @Column(nullable = false, updatable = false, name="opened_by")
    final String openedBy;

    @Column(nullable = false, updatable = false, name="opened_on")
    final Date openedAt;

    @Column(name="closed_by")
    String closedBy;

    @Column(name="closed_on")
    Date closedAt;

    public Fault(String openerSubject, String description) {
        this(null, description, openerSubject, new Date(), null, null);
    }

    Fault() {
        this(null, null);
    }

}
