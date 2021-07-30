package com.c4_soft.starter.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@Entity
@Table(name = "lifixuser")
public class LifixUser {
    /**
     * User OpenID subject
     */
    @Id
    final String sub;

    @Column(nullable = false)
    String email;

    @Column(nullable = false, name = "username")
    String userName;
    
    LifixUser() {
        this(null, null, null);
    }
}