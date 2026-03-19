package pe.com.interbank.infrastructure.adapter.input.rest.model.output;

import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TypeDocumentEnum;

import java.time.LocalDateTime;

public record UserResponse  (String document, TypeDocumentEnum typeDocument, String firstname,
                             String lastname, String address, String email, String phoneNumber,
                             Boolean enabled, LocalDateTime createdDate, LocalDateTime updatedDate) {

}
