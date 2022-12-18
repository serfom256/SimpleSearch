import random
import string

#  generates a data set with 10_000_000 random words of length from 3 to 13
with open("dataset.txt", mode="w+") as file:
    for i in range(10_000_000):
        s = ''.join(random.choice(string.ascii_uppercase + string.digits)
                    for _ in range(random.randint(3, 13)))
        file.write(s + "\n")
