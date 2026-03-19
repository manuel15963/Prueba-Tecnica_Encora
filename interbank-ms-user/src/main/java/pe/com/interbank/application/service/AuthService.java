package pe.com.interbank.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.interbank.application.port.input.AuthServicePort;
import pe.com.interbank.application.port.output.AccountPersistencePort;
import pe.com.interbank.application.port.output.UserPersistencePort;
import pe.com.interbank.domain.exception.InvalidCredentialException;
import pe.com.interbank.domain.model.Account;
import pe.com.interbank.domain.model.Login;
import pe.com.interbank.domain.model.User;
import pe.com.interbank.infrastructure.adapter.input.rest.mapper.AccountRestMapper;
import pe.com.interbank.infrastructure.adapter.input.rest.mapper.UserRestMapper;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.CurrencyEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.RoleEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.LoginRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.RegisterRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AuthResponse;
import pe.com.interbank.infrastructure.adapter.security.TokenProvider;
import pe.com.interbank.utils.Constants;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static pe.com.interbank.utils.ErrorCatalog.INVALID_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthServicePort {

    private final UserPersistencePort userPersistencePort;
    private final AccountPersistencePort accountPersistencePort;
    private final AccountRestMapper accountRestMapper;
    private final UserRestMapper userRestMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Override
    public Mono<AuthResponse> login(LoginRequest request) {
        return accountPersistencePort.findByUsername(request.username())
                .filter(accountExists -> passwordEncoder.matches(request.password(), accountExists.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidCredentialException(INVALID_USER.getTitle())))
                .flatMap(account -> userPersistencePort.findById(account.getDocument())
                        .map(user -> new AuthResponse(tokenProvider.generateToken(account), user, account))
                );
    }

    @Override
    @Transactional
    public Mono<AuthResponse> register(RegisterRequest request) {
        return createUserAndSave(request)
                .flatMap(user -> createAccountAndSave(request)
                        .map(account -> new AuthResponse(tokenProvider.generateToken(account), user, account))
                );
    }

    @Override
    public Mono<String> logout(Login login) {
        // implement logout logic or throw an exception
        throw new UnsupportedOperationException("Logout not implemented");
    }

    @Override
    public Mono<String> recoverPassword(Login login) {
        // implement recover password logic or throw an exception
        throw new UnsupportedOperationException("Recover password not implemented");
    }

    // Crear y guardar usuario
    private Mono<User> createUserAndSave(RegisterRequest request) {
        return Mono.defer(() -> {
            User newUser = createUser(request);
            return userPersistencePort.save(newUser);
        });
    }

    // Crear y guardar cuenta
    private Mono<Account> createAccountAndSave(RegisterRequest request) {
        return Mono.defer(() -> {
            Account newAccount = createAccount(request);
            return accountPersistencePort.save(newAccount);
        });
    }

    private User createUser(RegisterRequest request) {
        User user = userRestMapper.toUser(request);
        user.setEnabled(Constants.ENABLED);
        user.setCreatedDate(Constants.convertToLocalTimeZone(LocalDateTime.now()));
        return user;
    }

    private Account createAccount(RegisterRequest request) {
        Account account = accountRestMapper.toAccount(request);
        account.setPassword(encodePassword(request.password()));
        account.setRole(RoleEnum.USER);
        account.setCurrency(CurrencyEnum.PEN);
        return account;
    }

    // Método auxiliar para encriptar contraseña
    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

}
