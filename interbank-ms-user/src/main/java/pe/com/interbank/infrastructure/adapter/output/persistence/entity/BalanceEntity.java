package pe.com.interbank.infrastructure.adapter.output.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("balance")
public class BalanceEntity implements Persistable<String>,Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String phoneNumber;
    private String originAccount;
    private BigDecimal balanceAmount;
    private LocalDateTime lastUpdate;

    @Transient
    @Builder.Default
    private boolean isNewEntry = true;

    @Override
    public @NotNull String getId() {
        return phoneNumber;
    }

    @Override
    public boolean isNew() {
        return isNewEntry;
    }
}
