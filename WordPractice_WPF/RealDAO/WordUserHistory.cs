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
    [Table("WordUserHistories")]
    public class WordUserHistory : IWordUserHistory
    {
        [Key]
        [Column(Order =1)]
        public int UserId { get; set; }
        [Key]
        [Column(Order = 2)]
        public string Language { get; set; }
        [Key]
        [Column(Order = 3)]
        public string Word { get; set; }
        [Required]
        public int TimesEncountered { get; set; }
        [Required]
        public int TimesRight { get; set; }
    }
}
