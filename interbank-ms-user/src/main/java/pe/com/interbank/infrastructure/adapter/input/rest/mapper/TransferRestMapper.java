package pe.com.interbank.infrastructure.adapter.input.rest.mapper;

import org.mapstruct.Mapper;
import pe.com.interbank.domain.model.Transfer;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.TransferRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.TransferResponse;

@Mapper(componentModel = "spring")
public interface TransferRestMapper {

    Transfer toTransfer(TransferRequest request);

    TransferResponse toTransferResponse(Transfer transfer);
}
