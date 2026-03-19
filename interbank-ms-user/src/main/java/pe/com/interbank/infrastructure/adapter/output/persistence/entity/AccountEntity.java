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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.CurrencyEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.RoleEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("account")
public class AccountEntity implements Persistable<String>, Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String phoneNumber;
    private String document;
    private String accountNumber;
    private String bankingEntity;
    private String deviceSerial;
    private BigDecimal dailyLimit;
    private BigDecimal operationLimit;
    private String username;
    private String password;
    RoleEnum role;
    private CurrencyEnum currency;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(("ROLE_" + role.name())));
    }

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
