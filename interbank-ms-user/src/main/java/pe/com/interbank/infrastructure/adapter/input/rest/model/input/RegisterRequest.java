package pe.com.interbank.infrastructure.adapter.input.rest.model.input;

import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.CurrencyEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TypeDocumentEnum;

import java.math.BigDecimal;

public record RegisterRequest(String document, TypeDocumentEnum typeDocument, String firstname,
                              String lastname, String address, String email,
                              String phoneNumber, String username, String password,
                              String accountNumber, String bankingEntity, String deviceSerial,
                              BigDecimal dailyLimit , BigDecimal operationLimit, CurrencyEnum currency){
}