using Interfaces;
using System.ComponentModel.DataAnnotations;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAO
{
    public class User : IUser
    {
        [Key]
        public int Id
        {
            get;
            set;
        }

        public string Password
        {
            get;
            set;
        }

        public string Username
        {
            get;
            set;
        }
    }
}
