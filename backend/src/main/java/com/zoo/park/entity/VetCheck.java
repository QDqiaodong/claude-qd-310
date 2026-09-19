package com.zoo.park.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** 兽医巡查记录。 */
@Entity
@Table(name = "vet_check")
public class VetCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "check_code", nullable = false, length = 20, unique = true)
    public String checkCode;

    @Column(name = "animal_id", nullable = false)
    public Long animalId;

    @Column(name = "check_date")
    public LocalDate checkDate;

    @Column(name = "vet_name", nullable = false, length = 32)
    public String vetName;

    /** 正常 / 异常 */
    @Column(name = "check_result", nullable = false, length = 12)
    public String checkResult;

    @Column(name = "remark", length = 120)
    public String remark;
}
