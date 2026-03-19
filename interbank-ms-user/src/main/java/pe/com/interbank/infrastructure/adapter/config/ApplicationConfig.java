package pe.com.interbank.infrastructure.adapter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.AccountRepository;
import reactor.core.publisher.Mono;

import static pe.com.interbank.utils.ErrorCatalog.USER_NOT_FOUND;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AccountRepository accountRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> accountRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(USER_NOT_FOUND.getTitle())))
                .map(user -> User.builder()
                        .password(user.getUsername())
                        .username( user.getPassword())
                        .authorities(user.getAuthorities())
                        .build()
                );
    }

}
