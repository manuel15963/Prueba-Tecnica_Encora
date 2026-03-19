package pe.com.interbank.infrastructure.adapter.input.rest.model.output;

import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TransferStatusEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TransferTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(String originNumber, String originAccount, String targetNumber,
                               String targetAccount, BigDecimal amount, TransferTypeEnum transferType,
        TransferStatusEnum transferStatus, LocalDateTime createdDate, LocalDateTime updatedDate) {
}
