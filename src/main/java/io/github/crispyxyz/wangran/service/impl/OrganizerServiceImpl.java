package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.crispyxyz.wangran.exception.ResourceConflictException;
import io.github.crispyxyz.wangran.mapper.OrganizerMapper;
import io.github.crispyxyz.wangran.model.Organizer;
import io.github.crispyxyz.wangran.request.UpdateOrganizerRequest;
import io.github.crispyxyz.wangran.service.OrganizerService;
import org.springframework.stereotype.Service;

/**
 *
 * 针对表【organizer】的数据库操作Service实现
 *
 */
@Service
public class OrganizerServiceImpl extends BaseEntityService<OrganizerMapper, Organizer> implements OrganizerService {

    @Override
    protected SFunction<Organizer, ?> getIdField() {
        return Organizer::getId;
    }

    @Override
    public Organizer create(String name, String phone, String address) {
        if (isFieldConflict(Organizer::getName, name, null)) {
            throw new ResourceConflictException("该名称已存在");
        }
        Organizer organizer = new Organizer();
        organizer.setName(name);
        organizer.setAddress(address);
        organizer.setPhoneNumber(phone);
        save(organizer);
        return organizer;
    }

    @Override
    public Organizer partialUpdate(int id, UpdateOrganizerRequest request) {
        return updateBuilder(id).set(Organizer::getName, request.getName())
                                .set(Organizer::getPhoneNumber, request.getPhoneNumber())
                                .set(Organizer::getAddress, request.getAddress())
                                .execute();
    }
}
