# Log service

Service collecting other services logs

## Content

Main package: *org.vivlaniv.nexohub.log*

## Usage

Include log service with gradle:

```kotlin
dependencies {
    implementation("org.vivlaniv.nexohub:log-service:1.0-SNAPSHOT")
    implementation("org.redisson:redisson:3.21.3")
}
```

Add appender in logback.xml configuration file:

```xml
<appender name="LOG-SERVICE" class="org.vivlaniv.nexohub.log.LogServiceAppender">
    <redisUrl><!-- redis url (default: redis://redis:6379) --></redisUrl>
    <logsTopic><!-- logs topic to write (default: logs) --></logsTopic>
    <serviceName><!-- service name or id (default: unnamed) --></serviceName>
</appender>
```
