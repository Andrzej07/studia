using Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAO
{
    public class WordsDictionary : IWordsDictionary
    {
        public string Language
        {
            get;
            set;
        }

        public ICollection<IWord> Words
        {
            get;
            set;
        }
    }
}
