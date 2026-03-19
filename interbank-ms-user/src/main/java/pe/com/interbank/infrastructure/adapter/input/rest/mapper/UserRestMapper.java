package pe.com.interbank.infrastructure.adapter.input.rest.mapper;

import org.mapstruct.Mapper;
import pe.com.interbank.domain.model.User;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.RegisterRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.UpdateRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.UserRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.UserResponse;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    User toUser(UserRequest request);
    User toUser(RegisterRequest request);

    User toUser(UpdateRequest request);

    UserResponse toUserResponse(User user);

}
