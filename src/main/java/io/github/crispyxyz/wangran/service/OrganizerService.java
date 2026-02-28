package io.github.crispyxyz.wangran.service;

import io.github.crispyxyz.wangran.model.Organizer;
import io.github.crispyxyz.wangran.request.UpdateOrganizerRequest;

/**
 *
 * 针对表【organizer】的数据库操作Service
 *
 */
public interface OrganizerService extends EntityService<Organizer> {
    Organizer create(String name, String phone, String address);

    Organizer partialUpdate(int id, UpdateOrganizerRequest request);
}
