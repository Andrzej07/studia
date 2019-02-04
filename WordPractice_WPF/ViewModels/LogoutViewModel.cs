using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ViewModels
{
    public class LogoutViewModel : IPageViewModel
    {
        public string Name
        {
            get
            {
                return "Logout";
            }
        }

        public void OnEntry()
        {
        }
    }
}
