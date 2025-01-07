package org.example.backend.Usermanagement;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository     // spring bean
public interface UserManagementRepository extends CrudRepository<User,Integer> {

    User findByUserId(String userId);

}
