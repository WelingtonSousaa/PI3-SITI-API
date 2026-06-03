package com.siti.sitiapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String description;
    private String status;
}