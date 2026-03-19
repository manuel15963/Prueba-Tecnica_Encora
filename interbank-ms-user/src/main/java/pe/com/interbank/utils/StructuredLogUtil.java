package pe.com.interbank.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public final class StructuredLogUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .findAndRegisterModules();

    private StructuredLogUtil() {
    }

    public static void info(Logger logger, String event, Map<String, Object> fields) {
        logger.info(serialize(event, fields));
    }

    public static void warn(Logger logger, String event, Map<String, Object> fields) {
        logger.warn(serialize(event, fields));
    }

    public static void error(Logger logger, String event, Map<String, Object> fields) {
        logger.error(serialize(event, fields));
    }

    private static String serialize(String event, Map<String, Object> fields) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("event", event);
        payload.put("timestamp", Instant.now().toString());
        payload.putAll(fields);

        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            return "{\"event\":\"log.serialization.error\",\"message\":\"" + ex.getMessage() + "\"}";
        }
    }
}

