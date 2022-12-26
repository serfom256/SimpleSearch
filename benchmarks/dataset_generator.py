import random
import string

#  generates a data set with 1_000_000 random words of length from 3 to 15

RESULTS_COUNT = 1_000_000


def generate_seq(start, end):
    return ''.join(random.choice(string.ascii_uppercase) for _ in range(random.randint(start, end)))


if __name__ == "__main__":
    data = set()
    with open("dataset_1M.txt", mode="w+") as file:
        for i in range(RESULTS_COUNT):
            s = generate_seq(3, 15)
            while s in data:
                s = generate_seq(3, 15)
            data.add(s)
            file.write(s + "\n")
        print("Dataset generated")
