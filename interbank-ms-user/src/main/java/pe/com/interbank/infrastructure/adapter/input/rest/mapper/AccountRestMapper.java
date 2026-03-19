package pe.com.interbank.infrastructure.adapter.input.rest.mapper;

import org.mapstruct.Mapper;
import pe.com.interbank.domain.model.Account;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.AccountRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.RegisterRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.UpdateRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AccountResponse;

@Mapper(componentModel = "spring")
public interface AccountRestMapper {

    Account toAccount(RegisterRequest request);

    Account toAccount(AccountRequest request);

    Account toAccount(UpdateRequest request);

    AccountResponse toAccountResponse(Account account);

}
