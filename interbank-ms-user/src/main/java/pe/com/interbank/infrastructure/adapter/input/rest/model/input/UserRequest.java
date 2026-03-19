package pe.com.interbank.infrastructure.adapter.input.rest.model.input;

import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TypeDocumentEnum;

public record UserRequest(String document, TypeDocumentEnum typeDocument, String firstname,
                          String lastname, String address, String email, String phoneNumber) {
}
