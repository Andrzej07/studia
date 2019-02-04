using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces
{
    public abstract class WordConnectionBase
    {
        public abstract string Word1 { get; set; }
        public abstract string Word2 { get; set; }
        public abstract string Language1 { get; set; }
        public abstract string Language2 { get; set; }

        public override bool Equals(object obj)
        {
            if(obj == null)
            {
                return false;
            }
            WordConnectionBase b = obj as WordConnectionBase;
            if(b == null)
            {
                return false;
            }
            return Equals(b);
        }
        public bool Equals(WordConnectionBase b)
        {
            if((object)b == null)
            {
                return false;
            }
            return (Language1==b.Language1 && Language2==b.Language2 && Word1==b.Word1 && Word2==b.Word2)
                || (Language2 == b.Language1 && Language1 == b.Language2 && Word2 == b.Word1 && Word1 == b.Word2);
        }
        public override int GetHashCode()
        {
            return base.GetHashCode();
        }
    }
}
