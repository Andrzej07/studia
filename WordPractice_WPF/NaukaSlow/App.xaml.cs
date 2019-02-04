using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;
using ViewModels;

namespace NaukaSlow
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);
            MainWindow wind = new NaukaSlow.MainWindow();
            ApplicationViewModel context = new ApplicationViewModel(NaukaSlow.Properties.Settings.Default.dataSource);
            wind.DataContext = context;
            wind.Show();
        }
    }
}
