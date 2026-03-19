package pe.com.interbank.infrastructure.adapter.input.rest.model.input;

import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.CurrencyEnum;

import java.math.BigDecimal;

public record AccountRequest(String phoneNumber, String document, String accountNumber,
                             String bankingEntity, String deviceSerial, BigDecimal dailyLimit,
                             BigDecimal operationLimit, String username, String password,
                             CurrencyEnum currency) {
}
