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
  data: 
    "v1" : "c1"
    "v2" : "c2"

flow:
  - uflow-debug-processor

```



## Dev workflow

```
 cd uflow-app
 ../gradlew bootRun
```

http://localhost:8080/

http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

Example flow files
```yaml

config:
  data: 
    "v1" : "c1"
    "v2" : "c2"

flow:
  - uflow-debug-processor

```
