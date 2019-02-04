using Interfaces;
using System;
using System.Linq;
using System.Windows.Controls;
using System.Windows.Input;

namespace ViewModels
{
    public class LoginViewModel : ObservableObject, IPageViewModel
    {
        private string _message;
        private IUser _user;
        private RelayCommand _tryLoginCommand;
        private IDAO _dao;
        private ApplicationViewModel _applicationVM;

        public LoginViewModel(ApplicationViewModel appVM, IDAO dao)
        {
            _applicationVM = appVM;
            _dao = dao;
            _user = _dao.CreateNewUser();
            _tryLoginCommand = new RelayCommand(p => this.TryLogin(p), p => this.CanTryLogin(p));
        }

        #region Properties
        public string Name
        {
            get
            {
                return "Login";
            }
        }
        public string Username
        {
            get { return _user.Username; }
            set
            {
                _user.Username = value;
                RaisePropertyChanged("Username");
            }
        }
        public string Password
        {
            get { return _user.Password; }
            set
            {
                _user.Password = value;
                RaisePropertyChanged("Password");
            }
        }
        public string Message
        {
            get { return _message; }
            set
            {
                _message = value;
                RaisePropertyChanged("Message");
            }
        }
        public ICommand TryLoginCommand
        {
            get { return _tryLoginCommand; }
        }
        #endregion
        private void TryLogin(object parameter)
        {
            var users = _dao.GetAllUsers();
            var loggedUser = (from user in users
                            where (user.Username == Username
                            || user.Password == Password)
                            select user).ToList();
            if (loggedUser.Count > 0)
            {
                Message = "Login success.";
                _user = loggedUser.First();
                _applicationVM.HandleLogin(_user);
            }
            else
            {
                Message = "Login failed.";
            }
            //Username = first.Username;
            //Username = Password;
        }
        private bool CanTryLogin(object parameter)
        {
            Password = (parameter as PasswordBox).Password;
            if(String.IsNullOrEmpty(Username) || String.IsNullOrEmpty(Password))
            {
                return false;
            }
            if( Username.Any(x=>Char.IsWhiteSpace(x)) || Password.Any(x=>Char.IsWhiteSpace(x)) )
            {
                return false;
            }
            return true;
        }

        public void OnEntry()
        {
        }
    }
}
