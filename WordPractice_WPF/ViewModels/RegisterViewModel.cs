using Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Windows.Input;

namespace ViewModels
{
    public class RegisterViewModel : ObservableObject, IPageViewModel
    {
        private IDAO _dao;
        private IUser _user;
        private RelayCommand _tryRegisterCommand;
        private string _message;

        public RegisterViewModel(IDAO dao)
        {
            _dao = dao;
            _user = dao.CreateNewUser();
            _tryRegisterCommand = new RelayCommand(p => this.TryRegister(p), p => this.CanTryRegister(p));
        }
        #region Properties
        public string Name
        {
            get
            {
                return "Register";
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
        public string Username
        {
            get { return _user.Username; }
            set
            {
                _user.Username = value;
                RaisePropertyChanged("Username");
            }
        }
        public ICommand TryRegisterCommand
        {
            get { return _tryRegisterCommand; }
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
        #endregion
        private void TryRegister(object parameter)
        {
            var users = _dao.GetAllUsers();
            if (users.Any(x => x.Username == _user.Username))
            {
                Message = "Username already taken.";
            }
            else
            {
                Message = "Registration successful.";
                _user.Id = users.Count() + 1;
                _dao.AddUser(_user);
                _user = _dao.CreateNewUser();
            }
        }
        private bool CanTryRegister(object parameter)
        {
            Password = (parameter as PasswordBox).Password;
            if (String.IsNullOrEmpty(Username) || String.IsNullOrEmpty(Password))
            {
                return false;
            }
            if (Username.Any(x => Char.IsWhiteSpace(x)) || Password.Any(x => Char.IsWhiteSpace(x)))
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
