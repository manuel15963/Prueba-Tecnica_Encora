package pe.com.interbank.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TransferStatusEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TransferTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transfer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String originNumber;
    private String originAccount;
    private String targetNumber;
    private String targetAccount;
    private BigDecimal amount;
    private TransferTypeEnum transferType;
    private TransferStatusEnum transferStatus;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    @Transient
    @JsonIgnore
    private Boolean isNewEntry;
}
