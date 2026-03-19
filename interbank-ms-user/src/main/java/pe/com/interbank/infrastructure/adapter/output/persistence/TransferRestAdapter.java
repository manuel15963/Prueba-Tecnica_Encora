package pe.com.interbank.infrastructure.adapter.output.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.com.interbank.application.port.output.TransferPersistencePort;
import pe.com.interbank.domain.exception.NotFoundException;
import pe.com.interbank.domain.model.Transfer;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.TransferEntity;
import pe.com.interbank.infrastructure.adapter.output.persistence.mapper.TransferPersistenceMapper;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.TransferRepository;
import pe.com.interbank.utils.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

import static pe.com.interbank.utils.Constants.TRANSFER;


@Component
@Slf4j
@RequiredArgsConstructor
public class TransferRestAdapter implements TransferPersistencePort {

    private final TransferRepository transferRepository;
    private final TransferPersistenceMapper mapper;

    @Override
    public Mono<Transfer> findById(String id) {
        return transferRepository.findById(id)
                .map(mapper::toTransfer);
    }

    @Override
    public Flux<Transfer> findAll() {
        return transferRepository.findAll()
                .map(mapper::toTransfer);
    }

    @Override
    public Mono<Transfer> save(Transfer transfer) {
        return Mono.defer(() -> {
            TransferEntity entity = createTransfer(transfer);
            return transferRepository.save(entity)
                    .map(mapper::toTransfer)
                    .doOnError(error -> log.error(Constants.ERROR_SAVING, TRANSFER, error.getMessage(), error));
        });
    }

    @Override
    public Mono<Transfer> update(String id, Transfer transfer) {
        return Mono.defer(() -> transferRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Transfer not found with id: " + id)))
                .flatMap(existingTransfer -> {
                    updateTransfer(existingTransfer, transfer);
                    return transferRepository.save(existingTransfer);
                })
                .map(mapper::toTransfer)
                .doOnError(error -> log.error(Constants.ERROR_SAVING, Constants.TRANSFER, error.getMessage(), error))
        );
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return transferRepository.deleteById(id)
                .doOnError(error -> log.error(Constants.ERROR_DELETED, Constants.BALANCE, error.getMessage(), error));
    }

    private TransferEntity createTransfer(Transfer transfer) {
        TransferEntity entity = mapper.toTransferEntity(transfer);
        transfer.setCreatedDate(Constants.convertToLocalTimeZone(LocalDateTime.now()));
        transfer.setIsNewEntry(true);
        return entity;
    }


    private void updateTransfer(TransferEntity existingTransfer, Transfer transfer) {
        Optional.ofNullable(transfer.getOriginAccount()).ifPresent(existingTransfer::setOriginAccount);
        Optional.ofNullable(transfer.getTargetNumber()).ifPresent(existingTransfer::setTargetNumber);
        Optional.ofNullable(transfer.getTargetAccount()).ifPresent(existingTransfer::setTargetAccount);
        Optional.ofNullable(transfer.getAmount()).ifPresent(existingTransfer::setAmount);
        Optional.ofNullable(transfer.getTransferType()).ifPresent(existingTransfer::setTransferType);
        Optional.ofNullable(transfer.getTransferStatus()).ifPresent(existingTransfer::setTransferStatus);
        existingTransfer.setUpdatedDate(Constants.convertToLocalTimeZone(LocalDateTime.now()));
        existingTransfer.setNewEntry(false);
    }
}