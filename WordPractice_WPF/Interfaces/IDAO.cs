using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces
{
    public interface IDAO
    {
        IEnumerable<IWordsDictionary> GetAllDictionaries();
        IEnumerable<IUser> GetAllUsers();
        IEnumerable<WordConnectionBase> GetAllWordConnections();
        IEnumerable<IWordUserHistory> GetAllHistoriesForUser(IUser user);

        IUser CreateNewUser();
        void AddUser(IUser user);

        WordConnectionBase CreateWordConnection();
        void AddWordConnection(WordConnectionBase iwc);
        void RemoveWordConnection(WordConnectionBase iwc);

        IWord CreateNewWord();

        IWordUserHistory CreateWordUserHistory();
        void SaveWordUserHistory(IWordUserHistory iwuh);
        void RemoveWordUserHistory(IWordUserHistory iwuh);

        IWordsDictionary CreateNewDictionary();
        void SaveDictionary(IWordsDictionary dict);
    }
}
