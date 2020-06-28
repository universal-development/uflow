# uflow
Micro framework for distribuited messages processing.

## Workflow

* Flow file is prepared with processing steps and used queues
* Flow file is sent to queue for processing 

```
 | Initial Queue | -> | Processing Step A -> | Queue | ->  Processing Step B ->... 
```

## Flow model sections

`config`
Section storing generic configurations 

`flow`
Ordered list of queues where to send message

Example (yaml representation):

```yaml
config:
  key1: value1
  key1: 
    - value2
    - value3

flow:
  - queue1
  - queue2
  - queue3
  - queue4

```



## Dev workflow

```
 cd uflow-app
 ../gradlew bootRun
```

http://localhost:8080/

http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config