using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces
{
    public interface IWordsDictionary
    {
        // primary key
        string Language { get; set; }
        ICollection<IWord> Words { get; set; }
    }
}
