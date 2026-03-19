package pe.com.interbank.infrastructure.adapter.input.rest.model.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalanceResponse (String phoneNumber, String originAccount, BigDecimal balanceAmount,
                               LocalDateTime lastUpdate) {

}
