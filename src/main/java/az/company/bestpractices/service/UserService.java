package az.company.bestpractices.service;

import az.company.bestpractices.dto.request.UpdateUserRoleRequest;
import az.company.bestpractices.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> findAllActiveUsers();
    UserResponse updateUserRole(Long id, UpdateUserRoleRequest request);
    void deactivateUser(Long id);
}