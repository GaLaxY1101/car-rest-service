package com.foxminded.korniichyk.car_rest_service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

@Data
@Entity
@Table(name = "models")
@EqualsAndHashCode
@ToString
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "model_id_seq")
    @SequenceGenerator(name = "model_id_seq", sequenceName = "model_id_sequence")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant startManufacturing;

    @Column(nullable = false)
    private Instant endManufacturing;

    @Column(nullable = false)
    private String generation;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "brand_id",
            foreignKey = @ForeignKey(name = "fk_model_brand_id")
    )
    private Brand brand;
    

}
