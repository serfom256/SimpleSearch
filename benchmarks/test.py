import requests
import time
import random
import string

url = 'http://localhost:8050/simplesearch/api/v1/search'


def test_1000_req_random():
    print("Doing 1000 request with random words to search")
    payload = {
        "toSearch": None,
        "count": 1,
        "distance": 2,
        "sort": True,
        "fuzziness": True
    }
    start = time.time()
    for i in range(1000):
        if i % 100 == 0:
            print(i, "requests done")
        body = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(random.randint(3, 15)))
        payload["toSearch"] = body
        requests.post(url, json=payload)
    print(requests.post(url, json=payload).text)
    print("Time spent for 1000 requests with random content: ", time.time() - start)


def test_1000_req_static():
    print("Doing 1000 request with static word to search")
    payload = {
        "toSearch": "_java_",
        "count": 10,
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


if __name__ == "__main__":
    test_1000_req_random()
    test_1000_req_static()
