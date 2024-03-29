package com.healthcare.healthcaremanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity(name = "Institution")
@Table(name = "Institution")
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("PMD.ImmutableField")
public class Institution implements Serializable {

    @Id
    @Column
    private String cnpj;

    @Column
    private String name;

    @Column
    private Integer pixeon;

    @JsonIgnore
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL)
    private List<Exam> exams;

    @JsonIgnore
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL)
    private List<User> users;

    public Institution(final String cnpj, final String name) {
        this.cnpj = cnpj;
        this.name = name;
        this.pixeon = 20;
    }
}
