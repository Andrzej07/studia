using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Timers;
using System.Windows.Threading;

namespace ViewModels
{
    public class HomeViewModel : ObservableObject, IPageViewModel
    {
        private string _tableFlipText;
        DispatcherTimer _timer;
        private int _currentFrame;
        private int _increment;
        /*static private string[] _frames = 
            { @"(\°-°)\ ┬─┬",
            @"(\°-°)\                                     ]",
            @"(╯°□°)╯       [", 
        }; */
        static private string[] _frames =
        {
            "(\\\u00B0-\u00B0)\\ \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\ \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\ \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\ \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\ \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\ \u252C\u2500\u252C",
            "(\\\u00B0\u25A1\u00B0)\\  \u252C\u2500\u252C",
            "(-\u00B0\u25A1\u00B0)-  \u252C\u2500\u252C",
            "(\u256F\u00B0\u25A1\u00B0)\u256F    ]",
            "(\u256F\u00B0\u25A1\u00B0)\u256F    ]",
            "(\u256F\u00B0\u25A1\u00B0)\u256F  \uFE35  \u253B\u2501\u253B",
            "(\u256F\u00B0\u25A1\u00B0)\u256F  \uFE35  \u253B\u2501\u253B",
            "(\u256F\u00B0\u25A1\u00B0)\u256F       [",
            "(\u256F\u00B0\u25A1\u00B0)\u256F       [",
            "(\u256F\u00B0\u25A1\u00B0)\u256F       \uFE35  \u252C\u2500\u252C",
            "(\u256F\u00B0\u25A1\u00B0)\u256F       \uFE35  \u252C\u2500\u252C",
            "(\u256F\u00B0\u25A1\u00B0)\u256F                 ]",
            "(\u256F\u00B0\u25A1\u00B0)\u256F                 ]",
            "(\u256F\u00B0\u25A1\u00B0)\u256F               \uFE35  \u253B\u2501\u253B",
            "(\u256F\u00B0\u25A1\u00B0)\u256F               \uFE35  \u253B\u2501\u253B",
            "(\u256F\u00B0\u25A1\u00B0)\u256F                         [",
            "(\u256F\u00B0\u25A1\u00B0)\u256F                         [",                     
           /* "(-\u00B0\u25A1\u00B0)-  \u252C\u2500\u252C",
            "(\\\u00B0\u25A1\u00B0)\\  \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\                            \uFE35  \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\                            \uFE35  \u252C\u2500\u252C", */
            "(-\u00B0\u25A1\u00B0)-                           \uFE35  \u252C\u2500\u252C",
            "(\\\u00B0\u25A1\u00B0)\\                            \uFE35  \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\                                     ]",
            "(\\\u00B0-\u00B0)\\                                     ]",
            "(\\\u00B0-\u00B0)\\                                     \uFE35 \u253B\u2501\u253B",
            "(\\\u00B0-\u00B0)\\                                     \uFE35 \u253B\u2501\u253B",
            "(\\\u00B0-\u00B0)\\                                               [",
            "(\\\u00B0-\u00B0)\\                                               [",
            "(\\\u00B0-\u00B0)\\                                              \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\                                              \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\                                              \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\                                              \u252C\u2500\u252C",
            "(\\\u00B0-\u00B0)\\                                              \u252C\u2500\u252C"
        };
        


        public HomeViewModel()
        {
            _timer = new DispatcherTimer();
            _timer.Tick += new EventHandler(TimerTick);
            _timer.Interval = new TimeSpan(750000);
        }

        public string Name
        {
            get
            {
                return "Home";
            }
        }
        public string TableFlipText
        {
            get { return _tableFlipText; }
            set
            {
                _tableFlipText = value;
                RaisePropertyChanged("TableFlipText");
            }
        }
        public void OnEntry()
        {
            _currentFrame = 0;
            _increment = 1;
            _timer.Start();
        }
        public void OnExit()
        {
            _timer.Stop();
        }

        private void TimerTick(object sender, EventArgs e)
        {
            TableFlipText = _frames[_currentFrame];
            
            if((_currentFrame == _frames.Count()-1 && _increment > 0) || (_currentFrame == 0 && _increment<0))
            {
                _increment = -_increment;
            }
            _currentFrame += _increment;
        }
    }
}
