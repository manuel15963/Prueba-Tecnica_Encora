package pe.com.interbank.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Login implements Serializable {
    @Serial
    private static final long serialVersionUID = -2842162447504194856L;
    private String username;
    private String password;
}
