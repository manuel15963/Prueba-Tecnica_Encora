package pe.com.interbank.infrastructure.adapter.input.rest.model.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -693193246632907730L;

    private String code;
    private String message;
    private List<String> details;
    private String timestamp;
}
