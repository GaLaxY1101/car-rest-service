package com.foxminded.korniichyk.car_rest_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(
        name = "engines",
        uniqueConstraints = @UniqueConstraint(name = "uq_engine_name", columnNames = "name")
)
@EqualsAndHashCode
@ToString
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "engines_id_seq")
    @SequenceGenerator(name = "engines_id_seq", sequenceName = "engines_id_sequence")
    private Long id;

    private String name;

    @Column(nullable = false)
    private Double capacity;


    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type{
        PETROL,
        DIESEL,
        ELECTRIC
    }


}
