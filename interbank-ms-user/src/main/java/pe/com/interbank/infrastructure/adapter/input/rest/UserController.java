package pe.com.interbank.infrastructure.adapter.input.rest;

import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.interbank.application.port.input.DataMaskingServicePort;
import pe.com.interbank.application.port.input.UserServicePort;
import pe.com.interbank.infrastructure.adapter.input.rest.mapper.UserRestMapper;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.UpdateRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.UserRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/api")
@Validated
public class UserController {

    private final UserServicePort servicePort;
    private final UserRestMapper restMapper;
    private final DataMaskingServicePort dataMaskingService;

    @GetMapping("")
    public Flux<UserResponse> findAll() {
        return servicePort.findAll()
                .map(restMapper::toUserResponse)
                .flatMap(dataMaskingService::maskUserResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> findById(
            @PathVariable
            @NotBlank(message = "id no puede ser vacio")
            @Size(min = 8, max = 8, message = "id debe tener exactamente 8 caracteres")
            @Pattern(regexp = "^[A-Za-z0-9]{8}$", message = "id debe ser alfanumerico de 8 caracteres")
            String id) {
        return servicePort.findById(id)
                .map(restMapper::toUserResponse)
                .flatMap(dataMaskingService::maskUserResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("")
    public Mono<ResponseEntity<UserResponse>> register(@RequestBody UserRequest request) {
        return servicePort.save(restMapper.toUser(request))
                .map(restMapper::toUserResponse)
                .flatMap(dataMaskingService::maskUserResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(response));
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> update(
            @PathVariable
            @NotBlank(message = "id no puede ser vacio")
            @Size(min = 8, max = 8, message = "id debe tener exactamente 8 caracteres")
            @Pattern(regexp = "^[A-Za-z0-9]{8}$", message = "id debe ser alfanumerico de 8 caracteres")
            String id,
            @RequestBody UpdateRequest request) {
        return servicePort.update(id, restMapper.toUser(request))
                .map(restMapper::toUserResponse)
                .flatMap(dataMaskingService::maskUserResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(
            @PathVariable
            @NotBlank(message = "id no puede ser vacio")
            @Size(min = 8, max = 8, message = "id debe tener exactamente 8 caracteres")
            @Pattern(regexp = "^[A-Za-z0-9]{8}$", message = "id debe ser alfanumerico de 8 caracteres")
            String id) {
        return servicePort.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}