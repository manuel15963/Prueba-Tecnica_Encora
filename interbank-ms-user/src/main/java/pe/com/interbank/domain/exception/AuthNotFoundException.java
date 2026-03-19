package pe.com.interbank.domain.exception;

public class AuthNotFoundException extends RuntimeException {

    public AuthNotFoundException(String msg) {
        super(msg);
    }
}
