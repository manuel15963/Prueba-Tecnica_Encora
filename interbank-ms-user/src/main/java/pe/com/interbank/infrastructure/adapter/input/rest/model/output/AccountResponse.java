package pe.com.interbank.infrastructure.adapter.input.rest.model.output;

import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.CurrencyEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.RoleEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(String phoneNumber, String document, String accountNumber,
                              String bankingEntity, String deviceSerial, BigDecimal dailyLimit,
                              BigDecimal operationLimit, String username, String password,
                              RoleEnum role, CurrencyEnum currency, LocalDateTime createdDate,
                              LocalDateTime updatedDate) {




}
