package com.zoo.park.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** 笼舍。 */
@Entity
@Table(name = "enclosure")
public class Enclosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "enclosure_code", nullable = false, length = 20, unique = true)
    public String enclosureCode;

    @Column(name = "enclosure_name", nullable = false, length = 60)
    public String enclosureName;

    @Column(name = "zone_area")
    public Integer zoneArea;

    @Column(name = "capacity")
    public Integer capacity;

    /** 开放 / 维修 */
    @Column(name = "enclosure_state", nullable = false, length = 12)
    public String enclosureState;
}
