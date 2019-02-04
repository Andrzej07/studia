import math
import numpy as np
import random
import matplotlib.pyplot as plt
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier

from classifiers_students.naive_bayes import NaiveBayesNumNom

w1 = -1
w2 = 0.1
w0 = 0.3
numPoints = 1500

transparent_colors_map = {-1: (0, 0, 1, 0.1), 1: (1, 0, 0, 0.1)}
colors_map = {-1: 'blue', 1: 'red'}



def get_next_rand():
    return random.uniform(-1, 1)

def class_gen1(pair):
    return np.sign(w1*pair[0]+w2*pair[1]+w0)

def class_gen2(pair):
    return np.sign((pair[0]-w1)**2 + (pair[1]-w2)**2 - w0)


X = []
y = []

for i in range(numPoints):
    X.append([get_next_rand(), get_next_rand()])
    y.append(class_gen1(X[-1]))

transposed = np.transpose(X)
print(transposed)
colors = [colors_map[yi] for yi in y]

axes = plt.gca()
axes.set_xlim([-1,1])
axes.set_ylim([-1,1])
plt.scatter(transposed[0], transposed[1], color=colors, edgecolors='none')

classifier = NaiveBayesNumNom(is_cat=[False, False])
#classifier = DecisionTreeClassifier()
#classifier = LogisticRegression()
classifier.fit(X, y)

X_test = []
num_points = 100

for i in np.linspace(-1, 1, num_points):
    for j in np.linspace(-1, 1, num_points):
        X_test.append([i, j])

y_test = classifier.predict(X_test)

colors_test = [transparent_colors_map[yi] for yi in y_test]
transposed2 = np.transpose(X_test)
plt.scatter(transposed2[0], transposed2[1], color=colors_test, edgecolors='none')

plt.show()
print(X)
print(y)
