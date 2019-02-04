using Interfaces;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace ViewModels
{
    public class ApplicationViewModel : ObservableObject
    {
        private ICommand _changePageCommand;
        private IPageViewModel _currentPageViewModel;
        private ObservableCollection<IPageViewModel> _pageViewModels;
        private List<IPageViewModel> _removeAfterLogin;
        private IDAO _dao;
        private IUser _currentUser;

        public ApplicationViewModel(string dataSourceName)
        {
            #region Create DAO
            try
            {
                Assembly dataSource = Assembly.UnsafeLoadFrom(dataSourceName);
                var types = dataSource.GetTypes().Where(type => type.GetInterfaces().Contains(typeof(IDAO))).ToList();
                _dao = (IDAO)Activator.CreateInstance(types.First(), null);
            }
            catch (Exception e)
            {
                System.Console.WriteLine(e.Data.ToString());
                System.Environment.Exit(404);
            }
            #endregion
            #region Set up pages
            _pageViewModels = new ObservableCollection<IPageViewModel>();
            _removeAfterLogin = new List<IPageViewModel>();
            SetUpPages();
            #endregion
          
        }
        private void SetUpPages()
        {
            _pageViewModels.Add(new HomeViewModel());
            IPageViewModel login = new LoginViewModel(this, _dao);
            _pageViewModels.Add(login);
            _removeAfterLogin.Add(login);
            IPageViewModel register = new RegisterViewModel(_dao);
            _pageViewModels.Add(register);
            _removeAfterLogin.Add(register);
            CurrentPageViewModel = PageViewModels[0];
            CurrentPageViewModel.OnEntry();
        }
        #region Properties
        public ICommand ChangePageCommand
        {
            get
            {
                if(_changePageCommand == null)
                {
                    _changePageCommand = new RelayCommand( p => ChangeViewModel((IPageViewModel)p), p=>p is IPageViewModel);
                }
                return _changePageCommand;
            }
        }
        public ObservableCollection<IPageViewModel> PageViewModels
        {
            get
            {
                return _pageViewModels;
            }
            set
            {
                _pageViewModels = value;
                RaisePropertyChanged("PageViewModels");
            }
        }
        public IPageViewModel CurrentPageViewModel
        {
            get
            {
                return _currentPageViewModel;
            }
            set
            {
                if(_currentPageViewModel!=value)
                {
                    _currentPageViewModel = value;
                    RaisePropertyChanged("CurrentPageViewModel");
                }
            }
        }
        #endregion
        private void ChangeViewModel(IPageViewModel viewModel)
        {
            if(!PageViewModels.Contains(viewModel))
            {
                PageViewModels.Add(viewModel);
            }
            if(CurrentPageViewModel.Name.Equals("Home"))
            {
                ((HomeViewModel)CurrentPageViewModel).OnExit();
            }
            CurrentPageViewModel = PageViewModels.FirstOrDefault(vm => vm == viewModel);
            CurrentPageViewModel.OnEntry();
            if (CurrentPageViewModel.Name.Equals("Logout"))
            {
                HandleLogout();
            }
        }
        public void HandleLogin(IUser user)
        {
            _currentUser = user;
            foreach(var page in _removeAfterLogin)
            {
                _pageViewModels.Remove(page);
            }
            CurrentPageViewModel = _pageViewModels.First();
            CurrentPageViewModel.OnEntry();
            _pageViewModels.Add(new LogoutViewModel());
            _pageViewModels.Add(new EditDictionaryViewModel(_dao, _currentUser));
            _pageViewModels.Add(new ConnectWordsViewModel(_dao));
            _pageViewModels.Add(new LearnViewModel(_dao, _currentUser)); 
        }
        public void HandleLogout()
        {
            _pageViewModels.Clear();
            SetUpPages();
            _currentUser = null;
        }
    }
}
