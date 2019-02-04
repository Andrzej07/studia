using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Interfaces;

namespace PaintWithPlugins
{
    class PluginsLoader
    {
        public List<IAction> LoadActions(string pluginDirectory)
        {
            Console.WriteLine("Starting loading...");
            List<IAction> actions = new List<IAction>();
            string[] files = Directory.GetFiles(pluginDirectory);
            foreach (string file in files)
            {
                Console.WriteLine(file);
                if (file.EndsWith(".dll") && file != "Interfaces.dll")
                {
                    Console.WriteLine(@"Loading plugin: " + file);
                    Assembly ass = Assembly.LoadFrom(file);
                    foreach (Type type in ass.GetTypes())
                    {
                        if (type.IsClass && type.IsPublic && typeof(IAction).IsAssignableFrom(type))
                        {
                            actions.Add((IAction) Activator.CreateInstance(type));
                            Console.WriteLine(@"Loaded class: " + type.Name);
                        }
                    }
                }

            }
            return actions;
        }

    }
}
