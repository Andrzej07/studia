from sklearn.base import BaseEstimator
import numpy as np
import math


class NaiveBayesNominal:
    counts = {"outcomes": {}, "evidence": {}}
    probs = {}

    def __init__(self):
        self.classes_ = None
        self.model = dict()
        self.y_prior = []

    def fit(self, X, y):
        possibleValues = {}
        rowCount = len(y)
        for outcome in y:
            if outcome not in self.counts["outcomes"]:
                self.counts["outcomes"][outcome] = 0
            self.counts["outcomes"][outcome] += 1

        for outcome in self.counts["outcomes"].keys():
            self.probs[outcome] = self.counts["outcomes"][outcome] / float(rowCount)

        for (i,row) in enumerate(X):
            for (j, value) in enumerate(row):
                outcome = y[i]
                key = self.getCountName(value, outcome)
                if j not in self.counts["evidence"]:
                    self.counts["evidence"][j] = {}
                if key not in self.counts["evidence"][j]:
                    self.counts["evidence"][j][key] = 0
                self.counts["evidence"][j][key] += 1
                if j not in possibleValues:
                    possibleValues[j] = []
                if value not in possibleValues[j]:
                    possibleValues[j].append(value)

        for outcome in self.counts["outcomes"].keys():
            for key, evidence in self.counts["evidence"].items():
                for value in possibleValues[key]:
                    probkey = self.getProbName(key, value, outcome)
                    countkey = self.getCountName(value, outcome)
                    evidenceCount = 0
                    if countkey in evidence:
                        evidenceCount = evidence[countkey]
                    self.probs[probkey] = evidenceCount / float(self.counts["outcomes"][outcome])
        # print "Final probs:"
        print(self.probs)

    def getCountName(self, value, outcome):
        return str(value)+"|"+str(outcome)

    def getProbName(self, evidence_name, value, outcome):
        return str(evidence_name)+"="+self.getCountName(value, outcome)

    def predict_proba(self, X):
        raise NotImplementedError

    def predict(self, X):
        result = []
        for row in X:
            maxProb = -1
            bestClass = 0
            for outcome in self.counts["outcomes"].keys():
                currProb = self.probs[outcome]
                for j in range(row.shape[0]):
                    value = row[j]
                    currProb *= self.probs[self.getProbName(j, value, outcome)]
                if(currProb > maxProb):
                    maxProb = currProb
                    bestClass = outcome
            result.append(bestClass)
        return result

class NaiveBayesGaussian:
    means = {}
    vars = {}
    y_counts = {}
    probs = {}

    def fit(self, X, y):
        lists = {}

        for el in y:
            if el not in self.means:
                self.means[el] = {}
                self.vars[el] = {}
                self.y_counts[el] = 0
                lists[el] = {}
            self.y_counts[el] += 1

        for(i, row) in enumerate(X):
            for(j, value) in enumerate(row):
                if j not in lists[y[i]]:
                    lists[y[i]][j] = []
                lists[y[i]][j].append(value)

        print(lists)

        for outcome, attrs in lists.items():
            for attr, list in attrs.items():
                self.means[outcome][attr] = np.mean(list)
                self.vars[outcome][attr] = np.var(list)
        print(self.means)
        print(self.vars)

    def predict_proba(self, X):
        raise NotImplementedError

    def predict(self, X):
        result = []
        for row in X:
            maxProb = -1
            bestClass = 0
            for outcome in self.y_counts.keys():
                currProb = self.y_counts[outcome] #not really a prob
                for (j, value) in enumerate(row):
                    currProb *= self.calculate(self.means[outcome][j], self.vars[outcome][j], value)
                if(currProb > maxProb):
                    maxProb = currProb
                    bestClass = outcome
            result.append(bestClass)
        return result

    def calculate(self, mean, var, value):
        return (1 / math.sqrt(2 * math.pi * var)) * math.exp(-(value - mean)**2/(2*var))

class NaiveBayesNumNom(BaseEstimator):

    def __init__(self, is_cat=None, m=0.0):
        #print(is_cat)
        self.is_cat = is_cat # true - nominal, false - numerical

    def fit(self, X, y):
        if isinstance(X, np.matrix):
            X = X.A
        self.means = {}
        self.vars = {}
        self.y_counts = {}
        self.probs = {}

        lists = {}
        x_counts = {}
        #print(X)
        #print(y)
        for el in y:
            if el not in self.means:
                self.probs[el] = {}
                self.means[el] = {}
                self.vars[el] = {}
                self.y_counts[el] = 0
                x_counts[el] = {}
                lists[el] = {}
            self.y_counts[el] += 1
        for(i, row) in enumerate(X):
            for(j, value) in enumerate(row):
                outcome = y[i]
                if self.is_cat[j]:
                    # nominal
                    if j not in x_counts[outcome]:
                        x_counts[outcome][j] = {value: 0}
                        self.probs[outcome][j] = {}
                    if value not in x_counts[outcome][j]:
                        x_counts[outcome][j][value] = 0
                    x_counts[outcome][j][value] += 1
                else:
                    # numerical
                    if j not in lists[outcome]:
                        lists[outcome][j] = []
                    lists[outcome][j].append(value)

        #print(lists)
        #numerical
        for outcome, attrs in lists.items():
            for attr, list in attrs.items():
                self.means[outcome][attr] = np.mean(list)
                self.vars[outcome][attr] = np.var(list)
        #print(self.means)
        #print(self.vars)
        #nominal
        for outcome in self.y_counts.keys():
            for attr, values in x_counts[outcome].items():
                for value, count in x_counts[outcome][attr].items():
                    self.probs[outcome][attr][value] = count / float(self.y_counts[outcome])

        #print(self.probs)

    def predict_proba(self, X):
        if isinstance(X, np.matrix):
            X = X.A
        result = []
        for row in X:
            resultRow = []
            maxProb = -1
            bestClass = 0
            for outcome in self.y_counts.keys():
                currProb = self.y_counts[outcome] #not really a prob
                for (j, value) in enumerate(row):
                    if self.is_cat[j]:
                        #nominal
                        if value in self.probs[outcome][j]:
                            currProb *= self.probs[outcome][j][value]
                        else:
                            currProb *= 1e-6
                    else:
                        #numerical
                        if j not in self.vars[outcome]:
                            currProb *= 1e-6
                        else:
                            currProb *= self.calculate(self.means[outcome][j], self.vars[outcome][j], value)
                resultRow.append(currProb)
            probsSum = np.sum(resultRow)
            #if(probsSum == 0):
            #    probsSum = 1
            resultRow = [x / probsSum for x in resultRow]
            #print(resultRow)
            result.append(resultRow)
        return result

    def predict(self, X):
        if isinstance(X, np.matrix):
            X = X.A
        result = []
        for row in X:
            maxProb = -1
            bestClass = 0
            for outcome in self.y_counts.keys():
                currProb = self.y_counts[outcome] #not really a prob
                for (j, value) in enumerate(row):
                    if self.is_cat[j]:
                        #nominal
                        if value in self.probs[outcome][j]:
                            currProb *= self.probs[outcome][j][value]
                        else:
                            currProb *= 1e-6
                    else:
                        #numerical
                        if j not in self.vars[outcome]:
                            currProb *= 1e-6
                        else:
                            currProb *= self.calculate(self.means[outcome][j], self.vars[outcome][j], value)
                if(currProb > maxProb):
                    maxProb = currProb
                    bestClass = outcome
            result.append(bestClass)
        return result

    def calculate(self, mean, var, value):
        if(var == 0):
            var = 1e-6
        return (1 / math.sqrt(2 * math.pi * var)) * math.exp(-(value - mean)**2/(2*var))