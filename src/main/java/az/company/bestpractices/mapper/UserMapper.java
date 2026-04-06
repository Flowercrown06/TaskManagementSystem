package az.company.bestpractices.mapper;

import az.company.bestpractices.dto.response.LoginResponse;
import az.company.bestpractices.dto.response.UserResponse;
import az.company.bestpractices.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    UserResponse toResponse(User user);


    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "token", target = "token")
    LoginResponse toLoginResponse(User user, String token);

    java.util.List<UserResponse> toResponseList(java.util.List<User> users);
}