PREGUNTAS:
1) ¿Qué arquitectura se está usando?
Se está usando una arquitectura hexagonal con capas:
domain: reglas de negocio y modelos.
application: casos de uso y puertos (port.input, port.output).
infrastructure: adaptadores técnicos (REST, seguridad, persistencia).
 
2) ¿Qué patrones identificas?
Builder: en modelos de dominio (User, Account, Transfer, Balance) con @Builder de Lombok para construir objetos complejos.
Adapter: los mapeadores REST (UserRestMapper, AccountRestMapper, etc.) actúan como adaptadores entre modelo de dominio y DTOs.
Repository: repositorios de persistencia encapsulan acceso a datos (...Repository), desacoplando lógica de negocio del detalle de BD.
Strategy (ligero): con la ofuscación por rol, el comportamiento cambia según rol (ADMIN/USER), que conceptualmente aplica una estrategia de enmascaramiento.
Dependency Injection (IoC): Spring inyecta dependencias en controladores/servicios (@RequiredArgsConstructor, @Service, @RestController).
DTO (patrón de transferencia): UserResponse, AccountResponse, TransferResponse, etc., para exponer datos sin acoplar entidades internas.
 
3) ¿Por qué usamos R2DBC y no JPA?
Porque el proyecto es reactivo .
R2DBC: acceso a BD no bloqueante .
JPA/Hibernate: tradicionalmente bloqueante.
 
4) ¿Qué diferencia hay entre Flux y Mono?
Mono<T>: 0 o 1 elemento.
Flux<T>: 0..N elementos.
Ejemplo:
“uno” -> Mono
“muchos” -> Flux
 
5) ¿Qué hace Flyway? ¿Qué problema resuelve?
Flyway gestiona migraciones de base de datos versionadas.
Resuelve principalmente:
    Desorden de cambios SQL entre ambientes (dev/qa/prod).
    Falta de trazabilidad de quién cambió qué.
    Inconsistencias por ejecutar scripts manuales en distinto orden.
 
6) Explicar cómo evitar duplicidad bajo múltiples requests
Tenemos lo siguiente: dos requests casi simultáneos crean el mismo dato de usuario, cuenta, transferencia,etc.
Podemos implementar lo siguiente:
Crea índices/constraints únicos (ej. username, document, accountNumber, o idempotency_key).
Idempotencia por operación de escritura
    Usa Idempotency-Key por request de creación/pago/transferencia.
    Debes capturar violación de constraint único y mapear a error de negocio.
Transaccionalidad
    Mantén operaciones críticas dentro de transacción.