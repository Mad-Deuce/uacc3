package dms.service;



import dms.entity.UserEntity;

import java.util.List;

/**
 * Service interface for class {@link UserEntity}.
 *
 * @author Eugene Suleimanov
 * @version 1.0
 */

public interface UserService {

    UserEntity register(UserEntity user);

    List<UserEntity> getAll();

    UserEntity findByLogin(String username);

    UserEntity findById(Long id);

    void delete(Long id);
}
