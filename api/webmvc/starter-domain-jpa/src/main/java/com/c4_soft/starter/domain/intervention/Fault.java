package com.c4_soft.starter.domain.intervention;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.c4_soft.starter.domain.user.LifixUser;

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

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false, name = "opened_by")
    final LifixUser openedBy;

    @Column(nullable = false, updatable = false, name = "opened_on")
    final Date openedAt;

    @ManyToOne
    @JoinColumn(name = "closed_by")
    LifixUser closedBy;

    @Column(name = "closed_on")
    Date closedAt;

    @OneToMany(orphanRemoval = true, mappedBy = "fault")
    final List<FaultAttachment> attachments;

    public Fault(LifixUser opener, String description) {
        this(null, description, opener, new Date(), null, null, new ArrayList<>());
    }

    Fault() {
        this(null, null);
    }

}
