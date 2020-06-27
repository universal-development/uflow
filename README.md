# uflow
Micro framework for distribuited messages processing.


## Workflow

* Flow file is prepared with processing steps and used queues
* Flow file is sent to queue for processing 

```
 | Initial Queue | -> | Processing Step A -> | Qeueue | ->  Processing Step B ->... 
```

## Flow file sections

`config`
 Section storing generic configurations 


`flow`
 List of processors where to send message


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



