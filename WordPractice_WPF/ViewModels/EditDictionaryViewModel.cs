using Interfaces;
using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Drawing;
using System.Windows.Input;
using System.Globalization;

namespace ViewModels
{
    public class EditDictionaryViewModel : ObservableObject, IPageViewModel
    {
        private IDAO _dao;
        private ObservableCollection<IWordsDictionary> _dictionaries;
        private IEnumerable<IWordUserHistory> _histories;
        private IEnumerable<WordConnectionBase> _connections;
        private IUser _currentUser;
        private IWordsDictionary _selectedDictionary;
        private IWord _selectedWord;
        private string _newDictionaryName;
        private string _newWordText;
        private RelayCommand _addNewDictionaryCommand;
        private RelayCommand _saveCurrentDictionaryCommand;
        private RelayCommand _addNewWordCommand;
        private RelayCommand _removeWordWommand;
        private RelayCommand _uploadImageCommand;
        private string _historyText;
        private string _savedMessage;
        private ObservableCollection<IWord> _selectedDictionaryWords;

        public EditDictionaryViewModel(IDAO dao, IUser user)
        {
            _dao = dao;
            _currentUser = user;
            _addNewDictionaryCommand = new RelayCommand(p => AddNewDictionary(), p => CanAddNewDictionary());
            _saveCurrentDictionaryCommand = new RelayCommand(p => SaveSelectedDictionary());
            _addNewWordCommand = new RelayCommand(p => AddNewWord(), p=>CanAddNewWord());
            _removeWordWommand = new RelayCommand(p => RemoveWord(), p => CanRemoveWord());
            _uploadImageCommand = new RelayCommand(p => UploadImage(), p => CanUploadImage());
        }
        private void GetAllWords()
        {
            SelectedDictionaryWords = new ObservableCollection<IWord>();
            foreach (var word in _selectedDictionary.Words)
            {
                SelectedDictionaryWords.Add(word);
            }
        }
        #region Properties
        public string Name
        {
            get
            {
                return "Edit dictionaries";
            }
        }
        public ObservableCollection<IWordsDictionary> Dictionaries
        {
            get { return _dictionaries; }
        }
        public IWordsDictionary SelectedDictionary
        {
            get { return _selectedDictionary; }
            set
            {
                _selectedDictionary = value;
                if (SelectedDictionary != null)
                {
                    GetAllWords();
                }
                HistoryText = "";
                SavedMessage = "";
                RaisePropertyChanged("SelectedDictionary");
            }
        }
        public IWord SelectedWord
        {
            get { return _selectedWord; }
            set
            {
                _selectedWord = value;
                if (_selectedWord != null)
                {
                    var hist = _histories.FirstOrDefault(x => x.Language == SelectedDictionary.Language && x.Word == _selectedWord.Text);
                    if (hist == null)
                    {
                        HistoryText = "0 / 0";
                    }
                    else
                    {
                        HistoryText = hist.TimesRight.ToString() + " / " + hist.TimesEncountered.ToString();
                    }
                }
                RaisePropertyChanged("SelectedWord");
            }
        }
        public string NewDictionaryName
        {
            get { return _newDictionaryName; }
            set
            {
                _newDictionaryName = value;
                RaisePropertyChanged("NewDictionaryName");
            }
        }
        public string NewWordText
        {
            get { return _newWordText; }
            set
            {
                _newWordText = value;
                RaisePropertyChanged("NewWordText");
            }
        }
        public ICommand AddNewDictionaryCommand
        {
            get { return _addNewDictionaryCommand; }
        }
        public ICommand SaveSelectedDictionaryCommand
        {
            get { return _saveCurrentDictionaryCommand; }
        }
        public ICommand AddNewWordCommand
        {
            get { return _addNewWordCommand; }
        }
        public ICommand RemoveWordCommand
        {
            get { return _removeWordWommand; }
        }
        public ICommand UploadImageCommand
        {
            get { return _uploadImageCommand; }
        }
        public string HistoryText
        {
            get { return _historyText; }
            set
            {
                _historyText = value;
                if (value != "")
                {
                    _historyText = "History ";
                    _historyText += value;
                }               
                RaisePropertyChanged("HistoryText");
            }
        }
        public string SavedMessage
        {
            get { return _savedMessage; }
            set
            {
                _savedMessage = value;
                RaisePropertyChanged("SavedMessage");
            }
        }
        public ObservableCollection<IWord> SelectedDictionaryWords
        {
            get { return _selectedDictionaryWords; }
            set
            {
                _selectedDictionaryWords = value;
                RaisePropertyChanged("SelectedDictionaryWords");
            }
        }
        #endregion
        private void GetAllDictionaries()
        {
            foreach(var d in _dao.GetAllDictionaries())
            {
                _dictionaries.Add(d);
            }
        }

        private bool CanAddNewDictionary()
        {
            if (String.IsNullOrEmpty(NewDictionaryName))
            {
                return false;
            }
            if (_dictionaries.Any(x => x.Language.Equals(NewDictionaryName, StringComparison.OrdinalIgnoreCase)))
            {
                return false;
            }
            if (NewDictionaryName.Any(x => !Char.IsLetter(x)))
            {
                return false;
            }
            return true;
        }
        private void AddNewDictionary()
        {
            NewDictionaryName = CultureInfo.CurrentCulture.TextInfo.ToTitleCase(NewDictionaryName.ToLower());
            IWordsDictionary dict = _dao.CreateNewDictionary();
            dict.Language = NewDictionaryName;
            dict.Words = new List<IWord>();
            _dictionaries.Add(dict);
            SelectedDictionary = dict;
            SelectedWord = null;
        }
        private void SaveSelectedDictionary()
        {
            _dao.SaveDictionary(SelectedDictionary);
            SavedMessage = "Dictionary saved.";
        }

        private bool CanAddNewWord()
        {
            if(SelectedDictionary == null || SelectedDictionaryWords == null)
            {
                return false;
            }
            if (String.IsNullOrEmpty(NewWordText))
            {
                return false;
            }
            if (SelectedDictionary.Words.Any(x => x.Text.Equals(NewWordText, StringComparison.OrdinalIgnoreCase)))
            {
                return false;
            }
            if(NewWordText.Any( x => !Char.IsLetter(x)))
            {
                return false;
            }
            return true;
        }
        private void AddNewWord()
        {
            NewWordText = CultureInfo.CurrentCulture.TextInfo.ToTitleCase(NewWordText.ToLower());
            IWord word = _dao.CreateNewWord();
            word.Text = NewWordText;
            word.Language = SelectedDictionary.Language;
            SelectedDictionary.Words.Add(word);
            SelectedDictionaryWords.Add(word);
            SavedMessage = "";
            SelectedWord = word;
        }
        private bool CanRemoveWord()
        {
            if(SelectedDictionary == null || SelectedWord == null)
            {
                return false;
            }
            return true;
        }
        private void RemoveWord()
        {
            SavedMessage = "";
            SelectedDictionary.Words.Remove(SelectedWord);
            SelectedDictionaryWords.Remove(SelectedWord);
            foreach(var iwuh in _histories)
            {
                if(iwuh.Language == SelectedDictionary.Language && iwuh.Word == SelectedWord.Text && iwuh.UserId==_currentUser.Id)
                {
                    _dao.RemoveWordUserHistory(iwuh);
                }
            }
            foreach(var conn in _connections)
            {
                if( (conn.Language1 == SelectedDictionary.Language && conn.Word1 == SelectedWord.Text)
                    || (conn.Language2 == SelectedDictionary.Language && conn.Word2 == SelectedWord.Text))
                {
                    _dao.RemoveWordConnection(conn);
                }
            }
            _histories = _dao.GetAllHistoriesForUser(_currentUser);
            _connections = _dao.GetAllWordConnections();
            //SelectedWord = null;
        }
        private bool CanUploadImage()
        {
            if(SelectedWord == null)
            {
                return false;
            }
            return true;
        }
        private void UploadImage()
        {
            var fdialog = new OpenFileDialog();

            if(fdialog.ShowDialog() == true)
            {
                SelectedWord.Picture = File.ReadAllBytes(fdialog.FileName);
                try
                {
                    using (MemoryStream ms = new MemoryStream(SelectedWord.Picture))
                    {
                        var img = Image.FromStream(ms);
                    }
                    SavedMessage = "";
                }
                catch (Exception)
                {
                    MessageBox.Show("Invalid file.");
                    SelectedWord.Picture = null;
                }
                RaisePropertyChanged("SelectedWord");
            }
        }
        public void OnEntry()
        {
            _dictionaries = new ObservableCollection<IWordsDictionary>();
            GetAllDictionaries();
            _histories = _dao.GetAllHistoriesForUser(_currentUser);
            _connections = _dao.GetAllWordConnections();
            SelectedDictionary = null;
            SelectedDictionaryWords = null;
            SelectedWord = null;
            SavedMessage = "";           
        }
    }
}
