using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces
{
    public interface IWordUserHistory
    {
        int UserId { get; set; }
        string Language { get; set; }
        string Word { get; set; }
        int TimesEncountered { get; set; }
        int TimesRight { get; set; }
    }
}
