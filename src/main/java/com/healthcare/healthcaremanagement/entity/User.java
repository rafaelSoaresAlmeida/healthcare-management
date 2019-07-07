package com.healthcare.healthcaremanagement.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "User")
@Table(name = "User")
@AllArgsConstructor
public class User implements Serializable {

    @Id
    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String password;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "cnpj", nullable = false)
    private Institution institution;
}
