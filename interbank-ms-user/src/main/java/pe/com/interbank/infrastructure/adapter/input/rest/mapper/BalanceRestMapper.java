package pe.com.interbank.infrastructure.adapter.input.rest.mapper;

import org.mapstruct.Mapper;
import pe.com.interbank.domain.model.Balance;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.BalanceRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.UpdateRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.BalanceResponse;

@Mapper(componentModel = "spring")
public interface BalanceRestMapper {

    Balance toBalance(BalanceRequest request);

    Balance toBalance(UpdateRequest request);

    BalanceResponse toBalanceResponse(Balance balance);
}
