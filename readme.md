# SimpleSearch

### SimpleSearch is in memory search engine based on trie data structure

It provides lightning-fast search and suggestion on datasets that contains about 1 million entries
---

## Features:

- ### fuzzy prefix matching
- ### fast fuzzy search
- ### easy to configure

---

## Performance

All benchmarks performed on a pc with 8cores/16threads 4.3 Ghz CPU and 32gb of RAM. For benchmarks was used dataset
containing 1 Million random generated words containing ascii characters
<br>
Indexed dataset took approximately 850mb of RAM for one shard(trie data structure)
<br>
Was used OpenJdk17
<br>
As load testing tool was used Apache JMeter
<br>

The average time spent on queries is approximately 120ms for 1 million words dataset

```
{
    "indexedTotal": 1000000,
    "cpu": "16",
    "threads": 19,
    "memory": "846mb",
    "shardsInfo": [
        {
            "name": "shard-0",
            "indexed": 1000000
        }
    ]
}
```

---
<h4 align="center">
    Fuzzy Lookup for word with fuzzy distance 1
</h4>

![Fuzzy Lookup for word with fuzzy distance 1](benchmarks/1.png)

---
<h4 align="center">
    Fuzzy Lookup for word with fuzzy distance 2
</h4>

![Fuzzy Lookup for word with fuzzy distance 2](benchmarks/2.png)

---


<h4 align="center">
    Fuzzy Lookup for word with fuzzy distance 3
</h4>

![Fuzzy Lookup for word with fuzzy distance 3](benchmarks/3.png)

## Api usage:


#### Create indexes on file:
#### Request: POST http://host:port/simplesearch/api/v1/save
#### Body:
```json
{
  "path": "/path/to/local/file.extension",
  "mask": [], // mask to filter files by extension/regex [if specified path is directory] (not required)
  "separators": [] // separators to split file content (not required)
}
```
#### Response:
```json
{
  "session": "session_uuid",
  "indexingTime": 4,
  "documentsIndexed": 1,
  "entitiesIndexed": 250
}
```

---

#### Fuzzy search:
#### Request: POST http://host:port/simplesearch/api/v1/search
#### Body:
```json
{
  "toSearch": "text-to-search",
  "count": 100, // expected amount of founded matches
  "distance": 1, // fuzzy distance
  "sort": true, // sort result by similarity
  "fuzziness": true, // evaluate fuzzy distance based on search word length
  "operator": "ALL"
}
```
#### Response:
```json
{
  "header": {
    "normalizingTime": null,
    "sorted": true,
    "founded": 1,
    "shardsUsed": 1,
    "Qtime": 5
  },
  "resultList": [
    {
      "key": "too",
      "metadata": [
        {
          "path": "path/to/indexed/file",
          "position": 129, // position in file
          "type": "PLAIN_TEXT", // document type
          "content": null
        }
      ]
    }
  ]
}
```

---


## Quick start:

## Run SimpleSearch locally

#### For running SimpleSearch you need to have Java Runtime Environment 17 or higher

- Check java version

```
java -version
```

- Clone the SimpleSearch repository:

```
git clone git@github.com:serfom256/SimpleSearch.git
```

- Build executable jar file from source:

```
cd SimpleSearch && gradle build jar
```

- Run SimpleSearch

```
java -jar build/libs/SimpleSearch.jar
```

---

## Run SimpleSearch in docker

Here's an example how to run SimpleSearch in docker container

- Clone the SimpleSearch repository:

```
git clone git@github.com:serfom256/SimpleSearch.git
```

- Run mysql

```
cd SimpleSearch

docker-compose -f deployments/docker/docker-compose.yaml up
```

- Build docker image of the SimpleSearch:

```
docker build -t simplesearch:latest -f deployments/docker/Dockerfile .
```

- Run SimpleSearch on Docker:

```
docker run simplesearch
```

