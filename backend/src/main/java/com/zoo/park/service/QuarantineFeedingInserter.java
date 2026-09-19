package com.zoo.park.service;

import com.zoo.park.entity.QuarantineFeeding;
import com.zoo.park.repository.QuarantineFeedingRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 真正往库里写加餐单的那一下，单独开一个事务。
 *
 * <p>两个人几乎同时给同一只隔离动物记加餐时，事务前的“今天有没有单”都可能看到没有，
 * 最后靠库上 uk_qf_animal_day 唯一约束把后到的那张挡下，这里 saveAndFlush 立刻落库，
 * 约束违例由 {@link QuarantineFeedingService} 接住，再回查先成功的是谁。
 */
@Component
public class QuarantineFeedingInserter {

    private final QuarantineFeedingRepository quarantineFeedings;

    public QuarantineFeedingInserter(QuarantineFeedingRepository quarantineFeedings) {
        this.quarantineFeedings = quarantineFeedings;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public QuarantineFeeding insert(QuarantineFeeding order) {
        return quarantineFeedings.saveAndFlush(order);
    }
}
