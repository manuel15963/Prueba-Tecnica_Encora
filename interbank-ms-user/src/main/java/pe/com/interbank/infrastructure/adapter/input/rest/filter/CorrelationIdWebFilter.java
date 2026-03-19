package pe.com.interbank.infrastructure.adapter.input.rest.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pe.com.interbank.utils.Constants;
import pe.com.interbank.utils.StructuredLogUtil;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class CorrelationIdWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String correlationId = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(Constants.CORRELATION_ID_HEADER))
                .filter(value -> !value.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());

        exchange.getAttributes().put(Constants.CORRELATION_ID_ATTR, correlationId);
        exchange.getAttributes().put(Constants.REQUEST_START_TIME_ATTR, System.currentTimeMillis());
        exchange.getResponse().getHeaders().set(Constants.CORRELATION_ID_HEADER, correlationId);

        return chain.filter(exchange)
                .doOnError(error -> exchange.getAttributes().put(Constants.REQUEST_ERROR_ATTR, error.getClass().getSimpleName()))
                .doFinally(signalType -> logRequestCompleted(exchange, correlationId));
    }

    private void logRequestCompleted(ServerWebExchange exchange, String correlationId) {
        long start = (long) exchange.getAttributeOrDefault(Constants.REQUEST_START_TIME_ATTR, System.currentTimeMillis());
        long durationMs = Math.max(System.currentTimeMillis() - start, 0L);

        HttpStatusCode status = exchange.getResponse().getStatusCode();
        int statusValue = status != null ? status.value() : 200;
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("correlationId", correlationId);
        fields.put("method", exchange.getRequest().getMethod() != null ? exchange.getRequest().getMethod().name() : null);
        fields.put("path", exchange.getRequest().getPath().value());
        fields.put("status", statusValue);
        fields.put("durationMs", durationMs);
        fields.put("error", exchange.getAttribute(Constants.REQUEST_ERROR_ATTR));

        StructuredLogUtil.info(log, "http.request.completed", fields);
    }
}


