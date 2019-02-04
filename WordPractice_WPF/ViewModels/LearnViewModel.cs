using Interfaces;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Data;
using System.Windows.Input;

namespace ViewModels
{
    public class LearnViewModel : ObservableObject, IPageViewModel
    {
        static private Random _random = new Random();
        #region Solid data members
        private IDAO _dao;
        private IUser _currentUser;
        private IEnumerable<IWordsDictionary> _dictionaries;
        private IEnumerable<WordConnectionBase> _wordConnections;
        private IEnumerable<IWordUserHistory> _wordUserHistories;
        #endregion
        #region Settings members
        private ICollectionView _dictionariesQuestion;
        private ICollectionView _dictionariesAnswer;
        private IWordsDictionary _selectedDictionaryQuestion;
        private IWordsDictionary _selectedDictionaryAnswer;
        private int _answersPerQuestion;
        private int _questionsPerSet;
        #endregion
        private RelayCommand _nextQuestionCommand;
        private RelayCommand _confirmAnswerCommand;
        private RelayCommand _generateQuestionSetCommand;

        #region Current question members
        private List<IWord> _availableQuestions;
        private List<IWord> _currentQuestionSet;
        private IEnumerable<IWord> _correctAnswers;
        private IWord _currentQuestionWord;
        private ObservableCollection<IWord> _currentAnswerWords;
        private IWord _selectedAnswerWord;
        private bool _answeredThisQuestion;
        private string _answerResultMessage;
        #endregion

        #region Score members
        private int _answeredCorrectly;
        private int _tries;
        private string _formattedScore;
        #endregion

        public LearnViewModel(IDAO dao, IUser user)
        {
            _dao = dao;
            _currentUser = user;
            _nextQuestionCommand = new RelayCommand(p => NextQuestion(), p => CanNextQuestion());
            _confirmAnswerCommand = new RelayCommand(p => ConfirmAnswer(), p => CanConfirmAnswer());
            _generateQuestionSetCommand = new RelayCommand(p => GenerateQuestionSet(), p => CanGenerateQuestionSet());
            _currentAnswerWords = new ObservableCollection<IWord>();
        }

        #region Properties
        public string Name
        {
            get
            {
                return "Learn";
            }
        }
        public ICollectionView DictionariesQuestion
        {
            get { return _dictionariesQuestion; }
        }
        public ICollectionView DictionariesAnswer
        {
            get { return _dictionariesAnswer; }
        }
        public IWordsDictionary SelectedDictionaryQuestion
        {
            get { return _selectedDictionaryQuestion; }
            set
            {
                _selectedDictionaryQuestion = value;
                if (value != null)
                {
                    UpdateAvailableQuestions();
                }
                RaisePropertyChanged("SelectedDictionaryQuestion");
            }
        }
        public IWordsDictionary SelectedDictionaryAnswer
        {
            get { return _selectedDictionaryAnswer; }
            set
            {
                _selectedDictionaryAnswer = value;
                if (value != null)
                {
                    UpdateAvailableQuestions();
                }
                RaisePropertyChanged("SelectedDictionaryAnswer");
            }
        }
        public int AnswersPerQuestion
        {
            get { return _answersPerQuestion; }
            set
            {
                _answersPerQuestion = value;
                RaisePropertyChanged("AnswersPerQuestion");
            }
        }
        public int QuestionsPerSet
        {
            get { return _questionsPerSet; }
            set
            {
                _questionsPerSet = value;
                RaisePropertyChanged("QuestionsPerSet");
            }
        }
        public ICommand NextQuestionCommand
        {
            get { return _nextQuestionCommand; }
        }
        public ICommand ConfirmAnswerCommand
        {
            get { return _confirmAnswerCommand; }
        }
        public ICommand GenerateQuestionSetCommand
        {
            get { return _generateQuestionSetCommand; }
        }
        public ObservableCollection<IWord> CurrentAnswerWords
        {
            get { return _currentAnswerWords; }
        }
        public IWord CurrentQuestionWord
        {
            get { return _currentQuestionWord; }
            set
            {
                _currentQuestionWord = value;
                RaisePropertyChanged("CurrentQuestionWord");
            }
        }
        public IWord SelectedAnswerWord
        {
            get { return _selectedAnswerWord; }
            set
            {
                _selectedAnswerWord = value;
                RaisePropertyChanged("SelectedAnswerWord");
            }
        }
        public string Score
        {
            get { return _formattedScore; }
            set
            {
                _formattedScore = value;
                RaisePropertyChanged("Score");
            }
        }
        private int Tries
        {
            set
            {
                _tries = value;
                Score = _answeredCorrectly.ToString() + " / " + _tries.ToString();
            }
        }
        private int CorrectAnswers
        {
            set
            {
                _answeredCorrectly = value;
                Score = _answeredCorrectly.ToString() + " / " + _tries.ToString();
            }
        }
        public string AnswerResultMessage
        {
            get { return _answerResultMessage; }
            set
            {
                _answerResultMessage = value;
                RaisePropertyChanged("AnswerResultMessage");
            }
        }
        #endregion
        
        public void OnEntry()
        {
            Tries = 0;
            CorrectAnswers = 0;
            AnswersPerQuestion = 4;
            QuestionsPerSet = 2;
            SelectedDictionaryAnswer = null;
            SelectedDictionaryQuestion = null;
            CurrentQuestionWord = null;
            CurrentAnswerWords.Clear();
            _dictionaries = _dao.GetAllDictionaries();
            _dictionariesQuestion = new CollectionViewSource() { Source = _dictionaries }.View;
            _dictionariesAnswer = new CollectionViewSource() { Source = _dictionaries }.View;
            _wordConnections = _dao.GetAllWordConnections();
            _wordUserHistories = _dao.GetAllHistoriesForUser(_currentUser);
            _availableQuestions = new List<IWord>();
            _currentQuestionSet = new List<IWord>();
        }

        private bool CanNextQuestion()
        {
            if(_selectedDictionaryAnswer == null || _selectedDictionaryQuestion==null)
            {
                return false;
            }
            if(_selectedDictionaryAnswer.Language == _selectedDictionaryQuestion.Language)
            {
                return false;
            }
            if(_selectedDictionaryAnswer.Words.Count < _answersPerQuestion)
            {
                return false;
            }
            if(_currentQuestionSet.Count == 0)
            {
                return false;
            }
            if(CurrentQuestionWord != null && !_answeredThisQuestion)
            {
                return false;
            }
            return true;
        }
        private void NextQuestion()
        {
            AnswerResultMessage = "";
            CurrentQuestionWord = _currentQuestionSet.First();
            _currentQuestionSet.Remove(CurrentQuestionWord);
            _correctAnswers = SelectedDictionaryAnswer.Words.Where(x => AreWordsConnected(x, SelectedDictionaryAnswer.Language, CurrentQuestionWord, SelectedDictionaryQuestion.Language));
            CurrentAnswerWords.Clear();
            int n = _answersPerQuestion;
            bool insertedCorrectAnswer = false;
            while(n > 0)
            {
                if((!insertedCorrectAnswer && _random.Next(_answersPerQuestion) == 0)
                    || (!insertedCorrectAnswer && n == 1))
                {
                    CurrentAnswerWords.Add(_correctAnswers.ElementAt(_random.Next(_correctAnswers.Count())));
                    insertedCorrectAnswer = true;
                    n--;
                }
                else
                {
                    IWord word;
                    do
                    {
                        word = SelectedDictionaryAnswer.Words.ElementAt(_random.Next(SelectedDictionaryAnswer.Words.Count));
                    } while (CurrentAnswerWords.Contains(word));
                    if(_correctAnswers.Contains(word))
                    {
                        insertedCorrectAnswer = true;
                    }
                    CurrentAnswerWords.Add(word);
                    n--;
                }
            }
            SelectedAnswerWord = null;
            _answeredThisQuestion = false;
        }
        private bool CanConfirmAnswer()
        {
            if(_currentAnswerWords == null
                || SelectedAnswerWord == null)
            {
                return false;
            }
            if(_answeredThisQuestion)
            {
                return false;
            }           
            return true;
        }
        private void ConfirmAnswer()
        {
            var history = _wordUserHistories.FirstOrDefault(x => x.Language == SelectedDictionaryQuestion.Language
           && x.Word == CurrentQuestionWord.Text && x.UserId == _currentUser.Id);
            if(history == null)
            {
                history = _dao.CreateWordUserHistory();
                history.Language = SelectedDictionaryQuestion.Language;
                history.Word = CurrentQuestionWord.Text;
                history.UserId = _currentUser.Id;
                history.TimesEncountered = 0;
                history.TimesRight = 0;
            }
            if(_correctAnswers.Contains(SelectedAnswerWord))
            {
                CorrectAnswers = _answeredCorrectly + 1;
                AnswerResultMessage = "Correct!\n";
                history.TimesRight++;
            }
            else
            {
                AnswerResultMessage = "Wrong!\n";
                foreach(var word in _correctAnswers)
                {
                    if(CurrentAnswerWords.Contains(word))
                    {
                        AnswerResultMessage += "Correct answer: '" + word.Text + "'.\n";
                        break;
                    }
                }
            }
            if(_currentQuestionSet.Count == 0)
            {
                AnswerResultMessage += "No more questions!";
            }
            Tries = _tries + 1;
            history.TimesEncountered++;
            _dao.SaveWordUserHistory(history);
            _answeredThisQuestion = true;
        }
        private bool HasAnyConnections(IWord word, string queriedWordLanguage, string foreignLanguage)
        {
            if (_wordConnections.Any(x => x.Language1 == queriedWordLanguage && x.Language2 == foreignLanguage && x.Word1 == word.Text)
                || _wordConnections.Any(x => x.Language2 == queriedWordLanguage && x.Language1 == foreignLanguage && x.Word2 == word.Text))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        private bool AreWordsConnected(IWord word1, string language1, IWord word2, string language2)
        {
            var conn = _dao.CreateWordConnection();
            conn.Language1 = language1;
            conn.Language2 = language2;
            conn.Word1 = word1.Text;
            conn.Word2 = word2.Text;
            if (_wordConnections.Any(x => x.Equals(conn)))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        private void UpdateAvailableQuestions()
        {
            Tries = 0;
            CorrectAnswers = 0;
            _availableQuestions.Clear();
            if(SelectedDictionaryQuestion == null
                || SelectedDictionaryAnswer == null)
            {
                return;
            }
            _availableQuestions = (from word in SelectedDictionaryQuestion.Words
                                   where (HasAnyConnections(word, SelectedDictionaryQuestion.Language, SelectedDictionaryAnswer.Language))
                                   select word).ToList();
            if (_availableQuestions.Count > 0)
            {
                _availableQuestions.Shuffle();               
            }
        }
        private bool CanGenerateQuestionSet()
        {
            if(_availableQuestions == null)
            {
                return false;
            }
            if(QuestionsPerSet < 1)
            {
                return false;
            }
            if(_availableQuestions.Count < QuestionsPerSet)
            {
                return false;
            }
            if (CurrentQuestionWord != null && !_answeredThisQuestion)
            {
                return false;
            }
            if( _currentQuestionSet.Count > 0)
            {
                return false;
            }
            return true;
        }
        private void GenerateQuestionSet()
        {
            if(QuestionsPerSet == _availableQuestions.Count)
            {
                foreach(var w in _availableQuestions)
                {
                    _currentQuestionSet.Add(w);
                }
                NextQuestion();
                return;
            }
            var newWords = _availableQuestions.Where( x => 
                    !_wordUserHistories.Any( wuh => 
                    wuh.Word == x.Text && wuh.UserId == _currentUser.Id && wuh.Language == x.Language));
            var knownWords = _availableQuestions.Where(x =>
                   _wordUserHistories.Any(wuh =>
                  wuh.Word == x.Text && wuh.UserId == _currentUser.Id && wuh.Language == x.Language && IsWellKnown(wuh.TimesEncountered, wuh.TimesRight)));
            var usuallyCorrectWords = _availableQuestions.Where(x =>
                                    _wordUserHistories.Any(wuh =>
                                    wuh.Word == x.Text && wuh.UserId == _currentUser.Id && wuh.Language == x.Language && IsUsuallyCorrect(wuh.TimesEncountered, wuh.TimesRight)));
            var usuallyIncorrectWords = _availableQuestions.Where(x =>
                                    _wordUserHistories.Any(wuh =>
                                    wuh.Word == x.Text && wuh.UserId == _currentUser.Id && wuh.Language == x.Language && IsUsuallyIncorrect(wuh.TimesEncountered, wuh.TimesRight)));
            int questionsLeft = QuestionsPerSet;
            int selectedKnownWords = 0;
            int selectedNewWords = 0;
            int selectedUsuallyCorrectWords = 0;
            int selectedUsuallyIncorrectWords = 0;
            while(questionsLeft > 0)
            {
                int roll = _random.Next(100);
                IEnumerable<IWord> chosenSet;
                if(roll < 30 && selectedNewWords++ < newWords.Count())
                {
                    chosenSet = newWords;
                }
                else if(roll < 50 && selectedKnownWords++ < knownWords.Count())
                {
                    chosenSet = knownWords;
                }
                else if(roll < 70 && selectedUsuallyCorrectWords++ < usuallyCorrectWords.Count())
                {
                    chosenSet = usuallyCorrectWords;
                }
                else if(selectedUsuallyIncorrectWords++ < usuallyIncorrectWords.Count())
                {
                    chosenSet = usuallyIncorrectWords;
                }
                else
                {
                    continue;
                }
                IWord chosenWord;
                do
                {
                    chosenWord = chosenSet.ElementAt(_random.Next(chosenSet.Count()));
                } while (_currentQuestionSet.Contains(chosenWord));
                _currentQuestionSet.Add(chosenWord);
                questionsLeft--;
            }
            NextQuestion();
        }
        private bool IsWellKnown(int encountered, int timesRight)
        {
            if(encountered == 0)
            {
                return false;
            }
            var percCorrect = (float)timesRight / (float)encountered;
            return percCorrect > 80;
        }
        private bool IsUsuallyCorrect(int encountered, int timesRight)
        {
            if (encountered == 0)
            {
                return false;
            }
            var percCorrect = (float)timesRight / (float)encountered;
            return percCorrect >= 50 && percCorrect <= 80;
        }
        private bool IsUsuallyIncorrect(int encountered, int timesRight)
        {
            if (encountered == 0)
            {
                return false;
            }
            var percCorrect = (float)timesRight / (float)encountered;
            return percCorrect < 50;
        }
    }
}
