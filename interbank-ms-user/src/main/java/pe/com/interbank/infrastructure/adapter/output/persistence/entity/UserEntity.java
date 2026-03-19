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
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TypeDocumentEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class UserEntity implements  Persistable<String>,Serializable {
    @Serial
    private static final long serialVersionUID = -3756090898223056680L;
    @Id
    private String document;
    TypeDocumentEnum typeDocument;
    private String firstname;
    private String lastname;
    private String address;
    private String email;
    private String phoneNumber;
    private Boolean enabled;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Transient
    @Builder.Default
    private boolean isNewEntry = true;

    @Override
    public @NotNull String getId() {
        return document;
    }

    @Override
    public boolean isNew() {
        return isNewEntry;
    }
}