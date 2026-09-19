-- 动物园 · 笼舍与饲养巡查
SET NAMES utf8mb4;

DROP TABLE IF EXISTS isolation_meal;
DROP TABLE IF EXISTS vet_check;
DROP TABLE IF EXISTS feeding;
DROP TABLE IF EXISTS animal;
DROP TABLE IF EXISTS enclosure;

CREATE TABLE enclosure (
  id              BIGINT      NOT NULL AUTO_INCREMENT,
  enclosure_code  VARCHAR(20) NOT NULL,
  enclosure_name  VARCHAR(60) NOT NULL,
  zone_area       INT         NULL,
  capacity        INT         NULL,
  enclosure_state VARCHAR(12) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_enclosure_code (enclosure_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE animal (
  id           BIGINT      NOT NULL AUTO_INCREMENT,
  animal_code  VARCHAR(20) NOT NULL,
  animal_name  VARCHAR(40) NOT NULL,
  species      VARCHAR(40) NOT NULL,
  enclosure_id BIGINT      NULL,
  birth_date   DATE        NULL,
  animal_state VARCHAR(12) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_animal_code (animal_code),
  KEY idx_animal_enclosure (enclosure_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE feeding (
  id           BIGINT      NOT NULL AUTO_INCREMENT,
  feeding_code VARCHAR(20) NOT NULL,
  animal_id    BIGINT      NOT NULL,
  feed_time    VARCHAR(8)  NULL,
  food_name    VARCHAR(40) NOT NULL,
  amount       INT         NOT NULL,
  keeper_name  VARCHAR(32) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_feeding_code (feeding_code),
  KEY idx_feeding_animal (animal_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE vet_check (
  id           BIGINT      NOT NULL AUTO_INCREMENT,
  check_code   VARCHAR(20) NOT NULL,
  animal_id    BIGINT      NOT NULL,
  check_date   DATE        NULL,
  vet_name     VARCHAR(32) NOT NULL,
  check_result VARCHAR(12) NOT NULL,
  remark       VARCHAR(120) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_check_code (check_code),
  KEY idx_check_animal (animal_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 隔离加餐台：只收隔离动物的加餐单，同一动物同一个日历日只能落下一张
CREATE TABLE isolation_meal (
  id          BIGINT      NOT NULL AUTO_INCREMENT,
  meal_code   VARCHAR(20) NOT NULL,
  animal_id   BIGINT      NOT NULL,
  meal_date   DATE        NOT NULL,
  food_name   VARCHAR(40) NOT NULL,
  grams       INT         NOT NULL,
  keeper_name VARCHAR(32) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_meal_code (meal_code),
  UNIQUE KEY uk_meal_animal_day (animal_id, meal_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO enclosure (enclosure_code, enclosure_name, zone_area, capacity, enclosure_state) VALUES
('EN-01', '猛兽馆 1 号', 260, 3, '开放'),
('EN-02', '灵长馆 2 号', 180, 6, '开放'),
('EN-03', '水禽湖东区', 400, 30, '开放'),
('EN-04', '两爬馆 3 号', 120, 8, '维修');

INSERT INTO animal (animal_code, animal_name, species, enclosure_id, birth_date, animal_state) VALUES
('AN-01', '大毛', '东北虎', 1, '2019-05-12', '健康'),
('AN-02', '二毛', '东北虎', 1, '2021-03-08', '观察'),
('AN-03', '灵灵', '金丝猴', 2, '2020-07-21', '健康'),
('AN-04', '白白', '天鹅', 3, '2022-04-02', '健康'),
('AN-05', '青青', '绿孔雀', 3, '2023-01-15', '隔离');

INSERT INTO feeding (feeding_code, animal_id, feed_time, food_name, amount, keeper_name) VALUES
('FD-01', 1, '08:30', '牛肉块', 8, '老张'),
('FD-02', 2, '08:30', '牛肉块', 6, '老张'),
('FD-03', 3, '09:00', '水果拼盘', 4, '小李'),
('FD-04', 4, '09:30', '谷物饲料', 3, '小李'),
('FD-05', 4, '15:30', '谷物饲料', 2, '小李'),
('FD-06', 1, '16:00', '牛肉块', 7, '老张'),
('FD-07', 3, '16:00', '水果拼盘', 5, '小王');

INSERT INTO vet_check (check_code, animal_id, check_date, vet_name, check_result, remark) VALUES
('VC-01', 1, '2026-09-15', '孙兽医', '正常', '体重稳定'),
('VC-02', 2, '2026-09-15', '孙兽医', '异常', '食欲下降，继续观察'),
('VC-03', 3, '2026-09-16', '钱兽医', '正常', '精神状态良好'),
('VC-04', 4, '2026-09-17', '钱兽医', '异常', '羽毛轻微脱落'),
('VC-05', 1, '2026-09-18', '孙兽医', '正常', '无异常');
