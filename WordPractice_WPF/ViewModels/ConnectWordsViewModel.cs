using Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using System.Windows.Data;
using System.ComponentModel;

namespace ViewModels
{
    public class ConnectWordsViewModel : ObservableObject, IPageViewModel
    {
        private IDAO _dao;
        private IWordsDictionary _selectedDictionaryLeft;
        private IWordsDictionary _selectedDictionaryRight;
        private IWord _selectedWordLeft;
        private IWord _selectedWordRight;
        private RelayCommand _connectWordsCommand;
        private RelayCommand _disconnectWordsCommand;
        private IEnumerable<IWordsDictionary> _dictionaries;
        private ICollectionView _dictionariesLeft;
        private ICollectionView _dictionariesRight;
        private string _message;
        private IEnumerable<WordConnectionBase> _wordConnections;

        private string _leftFilter;
        private string _rightFilter;

        public ConnectWordsViewModel(IDAO dao)
        {
            _dao = dao;
            _connectWordsCommand = new RelayCommand(p => ConnectWords(), p => CanConnectWords());
            _disconnectWordsCommand = new RelayCommand(p => DisconnectWords(), p => CanDisconnectWords());
        }
        #region Properties
        public string Name
        {
            get
            {
                return "Connect words";
            }
        }
        public string LeftFilter
        {
            get { return _leftFilter; }
            set
            {
                _leftFilter = value;
                DoFilter(SelectedDictionaryLeft, value);
                RaisePropertyChanged("LeftFilter");
            }
        }
        public string RightFilter
        {
            get { return _rightFilter; }
            set
            {
                _rightFilter = value;
                DoFilter(SelectedDictionaryRight, value);
                RaisePropertyChanged("RightFilter");
            }
        }
        public ICollectionView DictionariesLeft
        {
            get { return _dictionariesLeft; }
        }
        public ICollectionView DictionariesRight
        {
            get { return _dictionariesRight; }
        }
        public IWord SelectedWordLeft
        {
            get { return _selectedWordLeft; }
            set
            {
                _selectedWordLeft = value;
                ConnectedMessage = "";
                RaisePropertyChanged("SelectedWordLeft");
            }
        }
        public IWord SelectedWordRight
        {
            get { return _selectedWordRight; }
            set
            {
                _selectedWordRight = value;
                ConnectedMessage = "";
                RaisePropertyChanged("SelectedWordRight");
            }
        }
        public IWordsDictionary SelectedDictionaryLeft
        {
            get { return _selectedDictionaryLeft; }
            set
            {
                _selectedDictionaryLeft = value;
                RaisePropertyChanged("SelectedDictionaryLeft");
            }
        }
        public IWordsDictionary SelectedDictionaryRight
        {
            get { return _selectedDictionaryRight; }
            set
            {
                _selectedDictionaryRight = value;
                RaisePropertyChanged("SelectedDictionaryRight");
            }
        }
        public ICommand ConnectWordsCommand
        {
            get { return _connectWordsCommand; }
        }
        public ICommand DisconnectWordsCommand
        {
            get { return _disconnectWordsCommand; }
        }
        public IEnumerable<IWordsDictionary> Dictionaries
        {
            get { return _dictionaries; }
        }
        public string ConnectedMessage
        {
            get { return _message; }
            set
            {
                _message = value;
                RaisePropertyChanged("ConnectedMessage");
            }
        }

        #endregion
        private bool IsStuffNullOrEmpty()
        {
            return (SelectedWordRight == null
                || SelectedWordLeft == null
                || SelectedDictionaryRight == null
                || SelectedDictionaryLeft == null
                || String.IsNullOrEmpty(SelectedDictionaryLeft.Language)
                || String.IsNullOrEmpty(SelectedDictionaryRight.Language)
                || String.IsNullOrEmpty(SelectedWordLeft.Text)
                || String.IsNullOrEmpty(SelectedWordRight.Text));
        }
        private bool AreWordsConnected()
        {
            var conn = _dao.CreateWordConnection();
            conn.Language1 = SelectedDictionaryLeft.Language;
            conn.Language2 = SelectedDictionaryRight.Language;
            conn.Word1 = SelectedWordLeft.Text;
            conn.Word2 = SelectedWordRight.Text;
            return _wordConnections.Any(x => x.Equals(conn));
        }
        private bool CanConnectWords()
        {
            if(IsStuffNullOrEmpty())
            {
                return false;
            }
            if (SelectedDictionaryLeft == SelectedDictionaryRight)
            {
                return false;
            }
            if(AreWordsConnected())
            {
                ConnectedMessage = "Words are connected";
                return false;
            }

            return true;
        }
        private void ConnectWords()
        {
            WordConnectionBase iwc = _dao.CreateWordConnection();
            iwc.Language1 = SelectedDictionaryLeft.Language;
            iwc.Language2 = SelectedDictionaryRight.Language;
            iwc.Word1 = SelectedWordLeft.Text;
            iwc.Word2 = SelectedWordRight.Text;
            _dao.AddWordConnection(iwc);
            _wordConnections = _dao.GetAllWordConnections();
        }
        private bool CanDisconnectWords()
        {
            if (IsStuffNullOrEmpty())
            {
                return false;
            }
            if (SelectedDictionaryLeft == SelectedDictionaryRight)
            {
                return false;
            }
            if(!AreWordsConnected())
            {
                ConnectedMessage = "Words are not connected";
                return false;
            }
            return true;
        }
        private void DisconnectWords()
        {
            WordConnectionBase iwc = _dao.CreateWordConnection();
            iwc.Language1 = SelectedDictionaryLeft.Language;
            iwc.Language2 = SelectedDictionaryRight.Language;
            iwc.Word1 = SelectedWordLeft.Text;
            iwc.Word2 = SelectedWordRight.Text;
            _dao.RemoveWordConnection(iwc);
            _wordConnections = _dao.GetAllWordConnections();
        }

        private void DoFilter(IWordsDictionary dict, string filter)
        {
            if(dict == null)
            {
                return;
            }
            if(dict.Words == null)
            {
                return;
            }
            var view = CollectionViewSource.GetDefaultView(dict.Words);
            if (filter.Length > 0)
            {
                view.Filter = (w) => ((IWord)w).Text.Contains(filter, StringComparison.OrdinalIgnoreCase);
            }
            else
            {
                view.Filter = null;
            }
        }

        public void OnEntry()
        {
            _dictionaries = _dao.GetAllDictionaries();
            _wordConnections = _dao.GetAllWordConnections();
            _dictionariesLeft = new CollectionViewSource() { Source = _dictionaries }.View;
            _dictionariesRight = new CollectionViewSource() { Source = _dictionaries }.View;
            SelectedDictionaryLeft = null;
            SelectedDictionaryRight = null;
            ConnectedMessage = "";
        }
    }
}
