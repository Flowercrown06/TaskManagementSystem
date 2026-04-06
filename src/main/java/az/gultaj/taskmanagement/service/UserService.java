package az.gultaj.taskmanagement.service;

import az.gultaj.taskmanagement.dto.request.UpdateUserRoleRequest;
import az.gultaj.taskmanagement.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> findAllActiveUsers();
    UserResponse updateUserRole(Long id, UpdateUserRoleRequest request);
    void deactivateUser(Long id);
}