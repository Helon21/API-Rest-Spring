package com.practice.trainingapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserEntity {

    @Id
    private Long id;
    private String name;
    private String email;

    public UserEntity() {

    }

    public UserEntity(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
