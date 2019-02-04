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
    public class WordConnection : Interfaces.WordConnectionBase
    {
        [Key]
        [Column(Order = 1)]
        public override string Language1
        {
            get;
            set;
        }
        [Key]
        [Column(Order = 2)]
        public override string Language2
        {
            get;
            set;
        }
        [Key]
        [Column(Order = 3)]
        public override string Word1
        {
            get;
            set;
        }
        [Key]
        [Column(Order = 4)]
        public override string Word2
        {
            get;
            set;
        }
    }
}
