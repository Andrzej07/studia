import numpy as np
import matplotlib.pyplot as plt
from sklearn.learning_curve import learning_curve

from utils.load import convert_to_onehot
from utils.evaluate import scorer_squared_error, scorer_01loss, scorer
from utils.load import read_and_convert_pandas_files
import time


def evaluate_accuracy_and_time(classifier, X_train, y_train, X_test, y_test, ds_name):
    #start_time = time.time()
    #classifier.fit(X_train, y_train)
    #training_time = time.time() - start_time
    #print("Training time = {0}".format(training_time))

    #scorers = [(scorer_01loss, "0/1 loss"), (scorer_squared_error, "squared error")]

    fig = plt.figure(1, figsize=(10,5))
    plt.subplot(121)
    plt.title("Size/score")
    plt.xlabel("Size")
    plt.ylabel("Score")
    train_sizes, train_scores, test_scores = learning_curve(classifier, X_train, y_train, scoring=scorer, train_sizes=np.linspace(0.1, 1.0, 20))
    train_scores_mean = np.mean(train_scores, axis=1)
    train_scores_std = np.std(train_scores, axis=1)
    test_scores_mean = np.mean(test_scores, axis=1)
    test_scores_std = np.std(test_scores, axis=1)
    plt.grid()

    plt.fill_between(train_sizes, train_scores_mean - train_scores_std,
                     train_scores_mean + train_scores_std, alpha=0.1,
                     color="r")
    plt.fill_between(train_sizes, test_scores_mean - test_scores_std,
                     test_scores_mean + test_scores_std, alpha=0.1, color="g")
    plt.plot(train_sizes, train_scores_mean, 'o-', color="r",
             label="Training score")
    plt.plot(train_sizes, test_scores_mean, 'o-', color="g",
             label="Cross-validation score")

    plt.legend(loc="best")

    times = []
    for e in train_sizes:
        start_time = time.time()
        classifier.fit(X_train[0:e], y_train[0:e])
        times.append(time.time() - start_time)
    plt.subplot(122)
    plt.title("Size/time")
    plt.xlabel("Size")
    plt.ylabel("Time [s]")
    plt.grid()
    plt.plot(train_sizes, times, 'o-')
    #plt.show()
    plt.savefig("plots/"+classifier.__class__.__name__+ds_name+".png")
    fig.clear()

    #for scorer, scorer_name in scorers:
    #    train_sizes, train_scores, test_scores = learning_curve(classifier, X_train, y_train, scoring=scorer)
    #    print(train_sizes)
     #   print(train_scores)
     #   print(test_scores)
        #print("Train {0} = {1}".format(scorer_name, scorer(classifier, X_train, y_train)))
        #print("Test {0} = {1}".format(scorer_name, scorer(classifier, X_test, y_test)))

