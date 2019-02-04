using Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAO
{
    public class WordUserHistory : IWordUserHistory
    {
        public int UserId { get; set; }
        public string Language { get; set; }
        public string Word { get; set; }
        public int TimesEncountered { get; set; }
        public int TimesRight { get; set; }
    }
}
