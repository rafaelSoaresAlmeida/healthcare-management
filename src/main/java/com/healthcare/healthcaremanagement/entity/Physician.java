package com.healthcare.healthcaremanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name = "Physician")
@Table(name = "Physician")
@AllArgsConstructor
@NoArgsConstructor
public class Physician {

    @Id
    @Column
    private String crm;

    @Column
    private String name;
}
