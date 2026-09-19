package com.zoo.park.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 隔离加餐单。
 *
 * <p>只服务档案状态为「隔离」的动物，和普通投喂流水（feeding）彻底分开记；
 * 同一只隔离动物一个日历日只能落下一张，由库上的唯一约束兜底，
 * 两个人同时记也只有先成功的那张在。
 */
@Entity
@Table(name = "quarantine_feeding",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_qf_animal_day", columnNames = {"animal_id", "feed_date"}))
public class QuarantineFeeding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "feeding_code", nullable = false, length = 40, unique = true)
    public String feedingCode;

    @Column(name = "animal_id", nullable = false)
    public Long animalId;

    /** 加餐落在哪一个日历日。 */
    @Column(name = "feed_date", nullable = false)
    public LocalDate feedDate;

    /** 饲料名，必须能在该动物最近一张巡查备注里找到原文。 */
    @Column(name = "food_name", nullable = false, length = 40)
    public String foodName;

    /** 克数。 */
    @Column(name = "amount", nullable = false)
    public Integer amount;

    @Column(name = "keeper_name", nullable = false, length = 32)
    public String keeperName;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;
}
