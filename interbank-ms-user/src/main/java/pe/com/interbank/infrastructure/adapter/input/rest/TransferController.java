package pe.com.interbank.infrastructure.adapter.input.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.interbank.application.port.input.DataMaskingServicePort;
import pe.com.interbank.application.port.input.TransferServicePort;
import pe.com.interbank.infrastructure.adapter.input.rest.mapper.TransferRestMapper;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.TransferRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.TransferResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transfer/api")
public class TransferController {

    private final TransferServicePort servicePort;
    private final TransferRestMapper restMapper;
    private final DataMaskingServicePort dataMaskingService;

    @GetMapping("")
    public Flux<TransferResponse> findAll() {
        return servicePort.findAll()
                .map(restMapper::toTransferResponse)
                .flatMap(dataMaskingService::maskTransferResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransferResponse>> findById(@PathVariable String id) {
        return servicePort.findById(id)
                .map(restMapper::toTransferResponse)
                .flatMap(dataMaskingService::maskTransferResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("")
    public Mono<ResponseEntity<TransferResponse>> register(@RequestBody TransferRequest request) {
        return servicePort.save(restMapper.toTransfer(request))
                .map(restMapper::toTransferResponse)
                .flatMap(dataMaskingService::maskTransferResponse)
                .map(response -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TransferResponse>> update(@PathVariable String id, @RequestBody TransferRequest request) {
        return servicePort.update(id, restMapper.toTransfer(request))
                .map(restMapper::toTransferResponse)
                .flatMap(dataMaskingService::maskTransferResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return servicePort.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
