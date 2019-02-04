using Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAO
{
    public class CollectionDAO : IDAO
    {
        private static List<IUser> _users;
        private static List<IWordsDictionary> _dicts;
        private static List<Interfaces.WordConnectionBase> _connections;
        private static List<IWordUserHistory> _histories;

        public CollectionDAO()
        {
            if (_users == null)
            {
                _users = new List<IUser>()
                {
                    new User() {Id=1, Username="a", Password="a" },
                    new User() {Id=2, Username="Maciej", Password="Olszowy" }
                };
            }
            if (_dicts == null)
            {
                _dicts = new List<IWordsDictionary>()
                {
                    new WordsDictionary()
                    {
                        Language = "Polish",
                        Words = new List<IWord>()
                        {
                            new Word() {Language="Polish", Text="Pies" },
                            new Word() {Language="Polish", Text="Kot" },
                            new Word() {Language="Polish", Text="Drwal" }
                        }
                    },
                    new WordsDictionary()
                    {
                        Language = "English",
                        Words = new List<IWord>()
                        {
                            new Word() {Language = "English", Text="Lumberjack" },
                            new Word() {Language = "English", Text="Woodcutter" },
                            new Word() {Language = "English", Text="Dog" },
                            new Word() {Language = "English", Text="Cat" }
                        }
                    }
                };
            }
            if(_connections == null)
            {
                _connections = new List<WordConnectionBase>()
                {
                    new WordConnection()
                    {
                        Word1 = "Pies",
                        Word2 = "Dog",
                        Language1 = "Polish",
                        Language2 = "English"
                    }
                };
            }
            if(_histories == null)
            {
                _histories = new List<IWordUserHistory>()
                {
                    new WordUserHistory()
                    {
                        UserId=1,
                        Language = "English",
                        Word = "Dog",
                        TimesEncountered = 3,
                        TimesRight = 2
                    }
                };
            }
        }

        public void AddUser(IUser user)
        {
            _users.Add(user);
        }

        public IUser CreateNewUser()
        {
            return new User();
        }

        public IEnumerable<IWordsDictionary> GetAllDictionaries()
        {
            return new List<IWordsDictionary>(_dicts);
        }

        public IEnumerable<IUser> GetAllUsers()
        {
            return new List<IUser>(_users);
        }

        public IWordsDictionary CreateNewDictionary()
        {
            return new WordsDictionary();
        }

        public void SaveDictionary(IWordsDictionary dict)
        {
            var dic = _dicts.FirstOrDefault(x => x.Language == dict.Language);
            if (dic == null)
            {
                _dicts.Add(dict);
            }
            else
            {
                dic = dict;
            }
        }

        public IWord CreateNewWord()
        {
            return new Word();
        }

        public IEnumerable<WordConnectionBase> GetAllWordConnections()
        {
            return new List<WordConnectionBase>(_connections);
        }

        public WordConnectionBase CreateWordConnection()
        {
            return new WordConnection();
        }

        public void AddWordConnection(Interfaces.WordConnectionBase iwc)
        {
            _connections.Add(iwc);
        }
        public void RemoveWordConnection(Interfaces.WordConnectionBase iwc)
        {
            var toRemove = _connections.FirstOrDefault(x => x.Equals(iwc));
            if (toRemove != null)
            {
                _connections.Remove(iwc);
            }
        }
        public IEnumerable<IWordUserHistory> GetAllHistoriesForUser(IUser user)
        {
            return _histories.Where( x => x.UserId == user.Id);
        }

        public IWordUserHistory CreateWordUserHistory()
        {
            return new WordUserHistory();
        }
        public void SaveWordUserHistory(IWordUserHistory iwuh)
        {
            var hist = _histories.FirstOrDefault(x => x.Language == iwuh.Language && x.UserId == iwuh.UserId && x.Word == iwuh.Word);
            if (hist == null)
            {
                _histories.Add(iwuh);
            }
            else
            {
                hist = iwuh;
            }
        }
        public void RemoveWordUserHistory(IWordUserHistory iwuh)
        {
            var hist = _histories.FirstOrDefault(x => x.Language == iwuh.Language && x.UserId == iwuh.UserId && x.Word == iwuh.Word);
            if(hist!=null)
            {
                _histories.Remove(hist);
            }
        }
    }
}
