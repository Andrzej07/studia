using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAO
{
    [Table("WordsDictionaries")]
    public class LanguageString
    {
        [Key]
        public string Language { get; set; }
    }
}
