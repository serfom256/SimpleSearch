import requests, time

url = 'http://localhost:8050/api/v1/search'
payload = {
    "toSearch": "search",
    "count": 100,
    "distance": 2,
    "sort": True,
    "fuzziness": True
}
start = time.time()
for i in range(1000):
    if i % 100 == 0:
        print(i, "requests done")
    requests.post(url, json=payload)
print(requests.post(url, json=payload).text)
print("Time spent for 1000 requests: ", time.time() - start)
