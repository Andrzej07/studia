using Interfaces;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAO
{
    public class Word : IWord
    {
        [Key]
        [Column(Order = 1)]
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
        [Key]
        [Column(Order = 2)]
        public string Text
        {
            get;
            set;
        }
    }
}
