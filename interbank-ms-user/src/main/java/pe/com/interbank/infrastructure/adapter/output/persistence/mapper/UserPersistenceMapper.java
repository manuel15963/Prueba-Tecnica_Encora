package pe.com.interbank.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import pe.com.interbank.domain.model.User;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    UserEntity toUserEntity(User user);
    User toUser(UserEntity user);

}