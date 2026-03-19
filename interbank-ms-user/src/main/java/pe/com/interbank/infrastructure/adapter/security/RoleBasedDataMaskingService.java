package pe.com.interbank.infrastructure.adapter.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import pe.com.interbank.application.port.input.DataMaskingServicePort;
import pe.com.interbank.domain.model.Account;
import pe.com.interbank.domain.model.User;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AccountResponse;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AuthResponse;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.BalanceResponse;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.TransferResponse;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.UserResponse;
import pe.com.interbank.utils.Constants;
import reactor.core.publisher.Mono;

@Service
public class RoleBasedDataMaskingService implements DataMaskingServicePort {

    @Override
    public Mono<UserResponse> maskUserResponse(UserResponse response) {
        return isAdmin().map(isAdmin -> isAdmin ? response : new UserResponse(
                maskKeepLast(response.document(), Constants.MASK_VISIBLE_LAST_4),
                response.typeDocument(),
                maskName(response.firstname()),
                maskName(response.lastname()),
                response.address(),
                maskEmail(response.email()),
                maskKeepLast(response.phoneNumber(), Constants.MASK_VISIBLE_LAST_3),
                response.enabled(),
                response.createdDate(),
                response.updatedDate()
        ));
    }

    @Override
    public Mono<AccountResponse> maskAccountResponse(AccountResponse response) {
        return isAdmin().map(isAdmin -> {
            String maskedPassword = maskAll(response.password());
            if (isAdmin) {
                return new AccountResponse(
                        response.phoneNumber(),
                        response.document(),
                        response.accountNumber(),
                        response.bankingEntity(),
                        response.deviceSerial(),
                        response.dailyLimit(),
                        response.operationLimit(),
                        response.username(),
                        maskedPassword,
                        response.role(),
                        response.currency(),
                        response.createdDate(),
                        response.updatedDate()
                );
            }

            return new AccountResponse(
                    maskKeepLast(response.phoneNumber(), Constants.MASK_VISIBLE_LAST_3),
                    maskKeepLast(response.document(), Constants.MASK_VISIBLE_LAST_4),
                    maskKeepLast(response.accountNumber(), Constants.MASK_VISIBLE_LAST_4),
                    response.bankingEntity(),
                    maskKeepLast(response.deviceSerial(), Constants.MASK_VISIBLE_LAST_4),
                    response.dailyLimit(),
                    response.operationLimit(),
                    maskKeepLast(response.username(), Constants.MASK_VISIBLE_LAST_3),
                    maskedPassword,
                    response.role(),
                    response.currency(),
                    response.createdDate(),
                    response.updatedDate()
            );
        });
    }

    @Override
    public Mono<BalanceResponse> maskBalanceResponse(BalanceResponse response) {
        return isAdmin().map(isAdmin -> isAdmin ? response : new BalanceResponse(
                maskKeepLast(response.phoneNumber(), Constants.MASK_VISIBLE_LAST_3),
                maskKeepLast(response.originAccount(), Constants.MASK_VISIBLE_LAST_4),
                response.balanceAmount(),
                response.lastUpdate()
        ));
    }

    @Override
    public Mono<TransferResponse> maskTransferResponse(TransferResponse response) {
        return isAdmin().map(isAdmin -> isAdmin ? response : new TransferResponse(
                maskKeepLast(response.originNumber(), Constants.MASK_VISIBLE_LAST_3),
                maskKeepLast(response.originAccount(), Constants.MASK_VISIBLE_LAST_4),
                maskKeepLast(response.targetNumber(), Constants.MASK_VISIBLE_LAST_3),
                maskKeepLast(response.targetAccount(), Constants.MASK_VISIBLE_LAST_4),
                response.amount(),
                response.transferType(),
                response.transferStatus(),
                response.createdDate(),
                response.updatedDate()
        ));
    }

    @Override
    public Mono<AuthResponse> maskAuthResponse(AuthResponse response) {
        if (response == null) {
            return Mono.empty();
        }
        return isAdmin().map(isAdmin -> {
            User user = response.user();
            Account account = response.account();

            if (user == null || account == null) {
                return response;
            }

            User maskedUser = User.builder()
                    .document(isAdmin ? user.getDocument() : maskKeepLast(user.getDocument(), Constants.MASK_VISIBLE_LAST_4))
                    .typeDocument(user.getTypeDocument())
                    .firstname(isAdmin ? user.getFirstname() : maskName(user.getFirstname()))
                    .lastname(isAdmin ? user.getLastname() : maskName(user.getLastname()))
                    .address(user.getAddress())
                    .email(isAdmin ? user.getEmail() : maskEmail(user.getEmail()))
                    .phoneNumber(isAdmin ? user.getPhoneNumber() : maskKeepLast(user.getPhoneNumber(), Constants.MASK_VISIBLE_LAST_3))
                    .enabled(user.getEnabled())
                    .createdDate(user.getCreatedDate())
                    .updatedDate(user.getUpdatedDate())
                    .isNewEntry(user.getIsNewEntry())
                    .build();

            Account maskedAccount = Account.builder()
                    .phoneNumber(isAdmin ? account.getPhoneNumber() : maskKeepLast(account.getPhoneNumber(), Constants.MASK_VISIBLE_LAST_3))
                    .document(isAdmin ? account.getDocument() : maskKeepLast(account.getDocument(), Constants.MASK_VISIBLE_LAST_4))
                    .accountNumber(isAdmin ? account.getAccountNumber() : maskKeepLast(account.getAccountNumber(), Constants.MASK_VISIBLE_LAST_4))
                    .bankingEntity(account.getBankingEntity())
                    .deviceSerial(isAdmin ? account.getDeviceSerial() : maskKeepLast(account.getDeviceSerial(), Constants.MASK_VISIBLE_LAST_4))
                    .dailyLimit(account.getDailyLimit())
                    .operationLimit(account.getOperationLimit())
                    .username(isAdmin ? account.getUsername() : maskKeepLast(account.getUsername(), Constants.MASK_VISIBLE_LAST_3))
                    .password(maskAll(account.getPassword()))
                    .role(account.getRole())
                    .currency(account.getCurrency())
                    .createdDate(account.getCreatedDate())
                    .updatedDate(account.getUpdatedDate())
                    .isNewEntry(account.getIsNewEntry())
                    .build();

            return new AuthResponse(response.token(), maskedUser, maskedAccount);
        });
    }

    private Mono<Boolean> isAdmin() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(authentication -> authentication.getAuthorities().stream()
                        .anyMatch(authority -> Constants.ROLE_ADMIN.equals(authority.getAuthority())))
                .defaultIfEmpty(false);
    }

    private String maskAll(String value) {
        if (isBlank(value)) {
            return value;
        }
        return Constants.MASK_HIDDEN;
    }

    private String maskKeepLast(String value, int visibleChars) {
        if (isBlank(value)) {
            return value;
        }
        if (value.length() <= visibleChars) {
            return Constants.MASK_HIDDEN;
        }
        int hiddenChars = value.length() - visibleChars;
        return Constants.MASK_CHAR.repeat(hiddenChars) + value.substring(hiddenChars);
    }

    private String maskEmail(String email) {
        if (isBlank(email) || !email.contains("@")) {
            return maskAll(email);
        }
        String[] parts = email.split("@", 2);
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 1) {
            return Constants.MASK_HIDDEN + "@" + domain;
        }
        return localPart.charAt(0) + Constants.MASK_CHAR.repeat(Math.max(localPart.length() - 1, 1)) + "@" + domain;
    }

    private String maskName(String name) {
        if (isBlank(name)) {
            return name;
        }
        if (name.length() <= 1) {
            return Constants.MASK_HIDDEN;
        }
        return name.charAt(0) + Constants.MASK_CHAR.repeat(name.length() - 1);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}


