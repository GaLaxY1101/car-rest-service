package com.foxminded.korniichyk.car_rest_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

@Data
@Entity
@Table(
        name = "cars",
        uniqueConstraints = @UniqueConstraint(name = "uq_car_serial_number", columnNames = "serialNumber")

)
@EqualsAndHashCode
@ToString
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "car_id_seq", sequenceName = "car_id_sequence")
    private Long id;

    @Column(nullable = false)
    private String color;

    private String serialNumber;

    @Version
    @Column(nullable = false)
    private Integer version;

    @ManyToOne
    @JoinColumn(
            name = "category_id",
            foreignKey = @ForeignKey(name = "fk_cars_category_id"))
    private Category category;

    @ManyToOne
    @JoinColumn(
            name = "model_id",
            foreignKey = @ForeignKey(name = "fk_cars_model_id")
    )
    private Model model;

    @Column(nullable = false)
    private Instant manufacturingDate;

    @ManyToOne
    @JoinColumn(
            name = "engine_id",
            foreignKey = @ForeignKey(name = "fk_cars_engine_id")
    )
    private Engine engine;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Drive drive;

    public enum Drive {
        ALL,
        FRONT,
        BACK,
    }

}
