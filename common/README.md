# Common module

Module containing common components for all services

## Content

Main package: *org.vivlaniv.nexohub.common*

[task](./src/main/kotlin/org/vivlaniv/nexohub/common/task) - package with task messages services can exchange

[util](./src/main/kotlin/org/vivlaniv/nexohub/common/util) - package with util methods for message brokers and common utils

## Usage

Include common module with gradle:

```gradle
dependencies {
    implementation(project(":common"))
}
```
