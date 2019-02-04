using Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAO
{
    public class WordConnection : Interfaces.WordConnectionBase
    {
        public override string Language1
        {
            get;
            set;
        }

        public override string Language2
        {
            get;
            set;
        }

        public override string Word1
        {
            get;
            set;
        }

        public override string Word2
        {
            get;
            set;
        }
    }
}
