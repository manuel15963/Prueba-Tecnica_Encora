package pe.com.interbank.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import pe.com.interbank.domain.model.Transfer;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.TransferEntity;

@Mapper(componentModel = "spring")
public interface TransferPersistenceMapper {

    TransferEntity toTransferEntity(Transfer transfer);
    Transfer toTransfer(TransferEntity transfer);
}
