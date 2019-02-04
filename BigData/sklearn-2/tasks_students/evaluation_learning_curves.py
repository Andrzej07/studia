'''
Author: Kalina Jasinska
'''

from plot_learning_curve import evaluate_accuracy_and_time
from sklearn.learning_curve import learning_curve
from sklearn.naive_bayes import GaussianNB
from sklearn.neighbors import KNeighborsClassifier
from sklearn.linear_model import LogisticRegression

from classifiers_students.naive_bayes import NaiveBayesNumNom
from utils.evaluate import scorer_squared_error, scorer_01loss
from utils.load import read_datasets
import time
import numpy as np
# Implement plotting of a learning curve using sklearn
# Remember that the evaluation metrics to plot are 0/1 loss and squared error


datasets = [('data/badges2-train.csv', 'data/badges2-test.csv',  "Badges2"),
            ('data/credit-a-train.csv','data/credit-a-test.csv', "Credit-a"),
            ('data/credit-a-mod-train.csv','data/credit-a-mod-test.csv', "Credit-a-mod"),
            ('data/spambase-train.csv', 'data/spambase-test.csv', "Spambase")
           ]


def make_learning_curves():
    raise NotImplementedError


def evaluate_classifer():
    for fn, fn_test, ds_name in datasets:
        print("Dataset {0}".format(ds_name))
        X_train, y_train, X_test, y_test, is_categorical = read_datasets(fn, fn_test)
        for classifier in [NaiveBayesNumNom(is_categorical), LogisticRegression()]:
            #classifier = LogisticRegression()
            #classifier = NaiveBayesNumNom(is_categorical)
            #print(X_train)
            #print(X_train[0:1])
            #for i in range(1, 15):
            evaluate_accuracy_and_time(classifier, X_train, y_train, X_test, y_test, ds_name)


if __name__ == "__main__":
    evaluate_classifer()
    #make_learning_curves()