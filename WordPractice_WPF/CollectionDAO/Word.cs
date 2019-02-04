using Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAO
{
    public class Word : IWord
    {
        public string Language
        {
            get;
            set;
        }

        public byte[] Picture
        {
            get;
            set;
        }

        public string Text
        {
            get;
            set;
        }
    }
}
