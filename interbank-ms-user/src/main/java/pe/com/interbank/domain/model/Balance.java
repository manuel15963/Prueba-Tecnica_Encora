package pe.com.interbank.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balance implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    private String phoneNumber;
    private String originAccount;
    private BigDecimal balanceAmount;
    private LocalDateTime lastUpdate;
    @Transient
    @JsonIgnore
    private Boolean isNewEntry;
}
