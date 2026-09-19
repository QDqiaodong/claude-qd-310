# 动物园 · 笼舍与饲养巡查

园区的台账：**笼舍**、**动物**、**投喂**、**兽医巡查**。

业务重点：
- **统计由数据库算**：每个笼舍住了几只、平均几岁（`AnimalRepository.countGroupByEnclosure`）、
  每位饲养员喂了几次多少量（`FeedingRepository.summaryByKeeper`）、体检概况（正常/异常/今日巡查）
  都是 JPQL 聚合查询一次算完，前端拿到就画；
- 笼舍报修前要求先把动物迁走；笼舍住满不能再进；隔离中的动物不能直接投喂；
  巡查判成异常必须写备注。

## 技术栈

- 后端：Spring Boot 3.3 / Java 17、Spring Data JPA（**多个 `@Query` 聚合统计**）、MySQL 8、Redis 7
- 前端：Vue 3（Composition API，**表头排序是纯前端自己实现的**，没有引表格组件）+ Element Plus + Vite
- 一键起：`./start.sh`

## 业务模块

1. **笼舍**（`enclosure`）—— 编号名称、面积与可容纳、开放与维修
2. **动物**（`animal`）—— 编号名字物种、出生日期、所在笼舍、健康/观察/隔离
3. **投喂**（`feeding`）—— 单号、动物、时间、饲料与数量、饲养员
4. **兽医巡查**（`vet_check`）—— 单号、动物、日期、兽医、正常/异常与备注

## 本地跑起来

| | 地址 |
| --- | --- |
| 前端页面 | http://127.0.0.1:8240/ |
| 后端接口 | http://127.0.0.1:8340/api/enclosures |
| MySQL | 127.0.0.1:3540（库 `zoo_park`） |
| Redis | 127.0.0.1:6540 |

容器名统一是 `claude-qd-310-{mysql,redis,backend,frontend}`。

```bash
./start.sh              # 起容器
docker compose ps       # 看状态
docker compose down -v  # 停掉并清数据
```
