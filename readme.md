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
Was used OpenJdk14
<br>
As load testing tool was used Apache JMeter
<br>

The average time spent on queries is approximately 70ms for 1 million words dataset

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

## Quick start:

Here's an example how to run SimpleSearch in a docker container

- Clone the SimpleSearch repository:

```
git clone git@github.com:serfom256/SimpleSearch.git
```

- Run mysql and redis

```
cd SimpleSearch

docker-compose -f deployments/docker/docker-compose.yaml up
```

- Build docker image of the SimpleSearch:

```
cd SimpleSearch

docker build -t simplesearch:latest -f deployments/docker/Dockerfile .
```

- Run SimpleSearch on Docker:

```
docker run simplesearch
```

