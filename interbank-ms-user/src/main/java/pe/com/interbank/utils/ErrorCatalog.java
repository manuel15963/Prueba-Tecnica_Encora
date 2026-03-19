package pe.com.interbank.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCatalog {

    USER_NOT_FOUND("ERR_USER_001", "User not found.","Usuario no encontrado."),
    INVALID_USER("ERR_USER_002", "Invalid Crendentials.","Credenciales inválidas."),
    USER_DUPLICATE("ERR_USER_003", "Duplicate user is not allowed.","No se admite usuario duplicado."),
    INVALID_TOKEN("ERR_INVALID_TOKEN_004", "Invalid token.","Token invalido."),
    NOT_FOUND("ERR_NOT_FOUND_001", "Elemento not found.","Elemento no encontrado."),
    GENERIC_ERROR("ERR_GEN_001", "An unexpected error occurred.","Se produjo un error inesperado.");


    private final String code;
    private final String title;
    private final String description;
}