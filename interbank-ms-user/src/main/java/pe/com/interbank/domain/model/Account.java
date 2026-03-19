package pe.com.interbank.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
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
public class Account implements Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = -3756090898223056680L;
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
    @Transient
    private LocalDateTime createdDate;
    @Transient
    private LocalDateTime updatedDate;
    @Transient
    @JsonIgnore
    private Boolean isNewEntry;
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(("ROLE_" + role.name())));
    }

}
