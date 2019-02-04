import numpy as np
from sklearn.metrics import confusion_matrix
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import cross_val_predict
from sklearn.pipeline import Pipeline
from sklearn.feature_selection import SelectFromModel, SelectKBest


def create_random_data(n_instances, n_features):
    X = 2*np.random.rand(n_instances, n_features) - 1
    scores =  2*np.random.rand(n_instances) - 1
    where_positive = scores > 0
    where_negative = scores <= 0
    scores[where_positive] = 1
    scores[where_negative] = 0
    y = np.array(scores.ravel().tolist(), dtype=np.int32)
    print(sum(y)/len(y))
    return X, y


def print_evaluation(y, predicted):
    #print("Confusion matrix")
    conf_m = confusion_matrix(y, predicted)
    #print(conf_m)
    print("Accuracy {}".format(str(sum(conf_m[i][i] for i in range(len(conf_m)))/len(y))))


class SelectFromModelKBest:
    def __init__(self, clf, k):
        self.clf = clf
        self.k = k
        self.eps = 0.0001
        self.smf = None

    def fit(self, X, y):
        thr_up = 1.0
        thr_down = 0.0

        while True:
            thr = (thr_up +thr_down)/2
            smf = SelectFromModel(self.clf, threshold = thr)
            smf.fit(X, y)
            Xselected = smf.transform(X)

            if Xselected.shape[1] == self.k:
                self.thr = thr
                self.smf = smf
                break
            if  Xselected.shape[1] > self.k:
                thr_down = thr
            else:
                thr_up = thr
            if abs(thr_up - thr_down) < self.eps:
                self.smf = smf
                break

    def fit_transform(self, X, y):
        self.fit(X, y)
        return self.transform(X)

    def transform(self, X):
        if self.smf is not None:
            return self.smf.transform(X)
        else:
            return None


def compare_cv(X, y, folds, n_top_features):
    # Scenario 1
    print("Scenario 1 : Selection + CV(LR)")
    sfm = SelectFromModel(LogisticRegression())
    # sfm = SelectKBest(k=n_top_features)
    sfm = SelectFromModelKBest(clf=LogisticRegression(), k=n_top_features)
    sfm.fit(X, y)
    X_selected = sfm.transform(X)

    predicted = cross_val_predict(LogisticRegression(), X_selected, y, cv=folds)
    print_evaluation(y, predicted)

    # Scenario 2
    print("Scenario 2: CV(Selection, LR)")
    clf = Pipeline([
        #('feature_selection', SelectFromModel(LogisticRegression())),
        # ('feature_selection', SelectKBest(k=n_top_features)),
        ('feature_selection', SelectFromModelKBest(clf=LogisticRegression(), k=n_top_features)),
        ('classification', LogisticRegression())
    ])
    predicted = cross_val_predict(clf, X, y, cv=folds)
    print_evaluation(y, predicted)


def main():
    n_instances = 100
    n_features = 100
    folds = 10
    n_top_features = 5

    X, y = create_random_data(n_instances, n_features)
    compare_cv(X, y, folds, n_top_features)


if __name__ == "__main__":
    main()
