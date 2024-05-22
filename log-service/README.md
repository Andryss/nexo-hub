# Сервис для ведения логов

## Способ подключения

Для отправки логов необходимо добавить в проект зависимости:

```kotlin
dependencies {
    implementation("org.vivlaniv.nexohub:log-service:1.0-SNAPSHOT")
    implementation("org.redisson:redisson:3.21.3")
}
```

А также подключить appender в файле конфигурации:

```xml
<appender name="LOG-SERVICE" class="org.vivlaniv.nexohub.LogServiceAppender">
    <redisUrl><!-- redis url (default: redis://redis:6379) --></redisUrl>
    <logsTopic><!-- logs topic to write (default: logs) --></logsTopic>
    <serviceName><!-- service name or id (default: unnamed) --></serviceName>
</appender>
```
