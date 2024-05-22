# Swagger Mosquitto(mqtt)

[ConnectionTask](../common/src/main/kotlin/org/vivlaniv/nexohub/ConnectionTask.kt)

## $user/signin

### /in AuthUserTask

```json lines
{
  "id": String,
  "username": String,
  "password": String
}
```

### /out AuthUserTaskResult

```json lines
{
  "id": String,
  "code": Int,
  "errorMessage": String?,
  "token": String
}
```

## $user/signup

### /in RegisterUserTask

```json lines
{
  "id": String,
  "username": String,
  "password": String
}
```

### /out RegisterUserTaskResult

```json lines
{
  "id": String,
  "code": Int,
  "errorMessage": String?
}
```

## $user/search

### /in SearchDevicesTask

```json lines
{
  "id": String
}
```

### /out SearchDevicesTaskResult

```json lines
{
  "tid": String,
  "devices": List<DeviceInfo>
}
```

## $user/save

### /in SaveDeviceTask

```json lines
{
  "id": String,
  "device": String,
  "room": String?,
  "alias": String?
}
```

### /out SaveDeviceTaskResult

```json lines
{
  "tid": String,
  "code": Int,
  "errorMessage": String?
}
```

## $user/fetch/devices

### /in FetchSavedDevicesTask

```json lines
{
  "id": String
}
```

### /out FetchSavedDevicesTaskResult

```json lines
{
  "tid": String,
  "code": Int,
  "errorMessage": String?,
  "devices": List<SavedDevice>?
}
```

## $user/fetch/props

### /in FetchDevicesPropertiesTask

```json lines
{
  "id": String,
  "include": List<String>?,
}
```

### /out FetchDevicesPropertiesTaskResult

```json lines
{
  "tid": String,
  "code": Int,
  "errorMessage": String?,
  "properties": Map<String, List<PropertyInfo>>?
}
```

## $user/property

### /in PutDevicePropertyTask

```json lines
{
  "id": String,
  "device": String,
  "property": String,
  "value": Int
}
```

### /out PutDevicePropertyTaskResult

```json lines
{
  "tid": String,
  "code": Int,
  "errorMessage": String?,
  "device": String,
}
```
