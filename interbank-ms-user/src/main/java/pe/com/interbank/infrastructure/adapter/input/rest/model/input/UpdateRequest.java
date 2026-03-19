package pe.com.interbank.infrastructure.adapter.input.rest.model.input;

public record UpdateRequest(String firstname,String lastname,String address,
                            String email,String typeDocument,String document,
                            String phoneNumber,String username, String password) {
}
