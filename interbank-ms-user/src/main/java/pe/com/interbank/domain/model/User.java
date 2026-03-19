package pe.com.interbank.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TypeDocumentEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable{
    @Serial
    private static final long serialVersionUID = -3756090898223056680L;
    private String document;
    TypeDocumentEnum typeDocument;
    private String firstname;
    private String lastname;
    private String address;
    private String email;
    private String phoneNumber;
    @Transient
    private Boolean enabled;
    @Transient
    private LocalDateTime createdDate;
    @Transient
    private LocalDateTime updatedDate;
    @Transient
    @JsonIgnore
    private Boolean isNewEntry;
}