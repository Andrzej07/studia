using Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAO
{
    public class DAO : IDAO
    {
        private AllmightyContext db = new AllmightyContext();

        public void AddUser(IUser user)
        {
            db.Users.Add((User)user);
            db.SaveChanges();
        }

        public void AddWordConnection(WordConnectionBase iwc)
        {
            db.WordConnections.Add((WordConnection)iwc);
            db.SaveChanges();
        }

        public IWordsDictionary CreateNewDictionary()
        {
            return new WordsDictionary();
        }

        public IUser CreateNewUser()
        {
            return new User();
        }

        public IWord CreateNewWord()
        {
            return new Word();
        }

        public WordConnectionBase CreateWordConnection()
        {
            return new WordConnection();
        }

        public IWordUserHistory CreateWordUserHistory()
        {
            return new WordUserHistory();
        }

        public IEnumerable<IWordsDictionary> GetAllDictionaries()
        {
            var returnList = new List<IWordsDictionary>();
            foreach(var lang in db.Languages)
            {
                var dict = new WordsDictionary()
                {
                    Language = lang.Language
                };
                dict.Words = new List<IWord>();
                var words = db.Words.Where(x => x.Language == lang.Language);
                foreach(var w in words)
                {
                    dict.Words.Add(w);
                }
                returnList.Add(dict);
            }
            return returnList;
        }

        public IEnumerable<IWordUserHistory> GetAllHistoriesForUser(IUser user)
        {
            return db.WordUserHistories.Where(x => x.UserId == user.Id);
        }

        public IEnumerable<IUser> GetAllUsers()
        {
            return db.Users;
        }

        public IEnumerable<WordConnectionBase> GetAllWordConnections()
        {
            return db.WordConnections;
        }

        public void RemoveWordConnection(WordConnectionBase iwc)
        {
            var toRemove = db.WordConnections.FirstOrDefault(x => x.Equals(iwc));
            if (toRemove != null)
            {
                db.WordConnections.Remove((WordConnection)iwc);
                db.SaveChanges();
            }
        }

        public void RemoveWordUserHistory(IWordUserHistory iwuh)
        {
            var hist = db.WordUserHistories.FirstOrDefault(x => x.Language == iwuh.Language && x.UserId == iwuh.UserId && x.Word == iwuh.Word);
            if (hist != null)
            {
                db.WordUserHistories.Remove(hist);
                db.SaveChanges();
            }
        }

        public void SaveDictionary(IWordsDictionary dict)
        {
            var dic = db.Languages.FirstOrDefault(x => x.Language == dict.Language);
            if (dic == null)
            {
                db.Languages.Add(new LanguageString() { Language = dict.Language });
                foreach(var w in dict.Words)
                {
                    db.Words.Add((Word)w);
                }
            }
            else
            {
                foreach (var w in dict.Words)
                {
                    var word = db.Words.FirstOrDefault(x => x.Text == w.Text);
                    if (word == null)
                    {
                        db.Words.Add((Word)w);
                    }
                    else
                    {
                        word.Picture = w.Picture;
                    }
                }
            }
            db.SaveChanges();
        }

        public void SaveWordUserHistory(IWordUserHistory iwuh)
        {
            var hist = db.WordUserHistories.FirstOrDefault(x => x.Language == iwuh.Language && x.UserId == iwuh.UserId && x.Word == iwuh.Word);
            if (hist == null)
            {
                db.WordUserHistories.Add((WordUserHistory)iwuh);
            }
            else
            {
                hist = (WordUserHistory)iwuh;
            }
                db.SaveChanges();
        }
    }
}
