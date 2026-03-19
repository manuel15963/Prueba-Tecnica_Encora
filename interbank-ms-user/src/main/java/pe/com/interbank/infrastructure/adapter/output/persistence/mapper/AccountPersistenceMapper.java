package pe.com.interbank.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import pe.com.interbank.domain.model.Account;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.AccountEntity;

@Mapper(componentModel = "spring")
public interface AccountPersistenceMapper {

    AccountEntity toAccountEntity(Account account);
    Account toAccount(AccountEntity account);
}
