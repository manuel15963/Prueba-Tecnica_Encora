package pe.com.interbank.infrastructure.adapter.input.rest.model.input;

import java.math.BigDecimal;

public record BalanceRequest(String phoneNumber, String originAccount, BigDecimal balanceAmount) {
}
