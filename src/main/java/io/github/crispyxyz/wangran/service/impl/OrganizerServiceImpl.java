package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.crispyxyz.wangran.exception.ResourceConflictException;
import io.github.crispyxyz.wangran.mapper.OrganizerMapper;
import io.github.crispyxyz.wangran.model.Organizer;
import io.github.crispyxyz.wangran.request.UpdateOrganizerRequest;
import io.github.crispyxyz.wangran.service.OrganizerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * 针对表【organizer】的数据库操作Service实现
 *
 */
@Service
@Slf4j
public class OrganizerServiceImpl extends BaseEntityService<OrganizerMapper, Organizer> implements OrganizerService {

    @Override
    protected SFunction<Organizer, ?> getIdField() {
        return Organizer::getId;
    }

    @Transactional
    @Override
    public Organizer create(String name, String phone, String address) {
        if (isFieldConflict(Organizer::getName, name, null)) {
            log.warn("创建主办方失败，名称已存在：{}", name);
            throw new ResourceConflictException("该名称已存在");
        }
        Organizer organizer = new Organizer();
        organizer.setName(name);
        organizer.setAddress(address);
        organizer.setPhoneNumber(phone);
        save(organizer);
        log.info("创建主办方成功，ID：{}，名称：{}，电话：{}", organizer.getId(), name, phone);
        return organizer;
    }

    @Transactional
    @Override
    public Organizer partialUpdate(int id, UpdateOrganizerRequest request) {
        Organizer updated = updateBuilder(id).set(Organizer::getName, request.getName())
                                             .set(Organizer::getPhoneNumber, request.getPhoneNumber())
                                             .set(Organizer::getAddress, request.getAddress())
                                             .execute();
        log.info("更新主办方信息，ID：{}，新名称：{}，新电话：{}", id, request.getName(), request.getPhoneNumber());
        return updated;
    }
}
