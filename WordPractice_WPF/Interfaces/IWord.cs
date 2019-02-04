using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces
{
    public interface IWord
    {
        string Language { get; set; }
        string Text { get; set; }
        byte[] Picture { get; set; }
    }
}
