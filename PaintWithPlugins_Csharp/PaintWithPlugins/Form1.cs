using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Interfaces;

namespace PaintWithPlugins
{
    public partial class Form1 : Form
    {
        private List<IAction> _actions;
        private IAction _activeAction;
        private List<IImageModification> _modifications = new List<IImageModification>();
        private List<IImageModification> _undoneModifications = new List<IImageModification>();
        private List<Button> _buttons = new List<Button>();
        private Dictionary<string, string> _globalProperties = new Dictionary<string, string>();

        private bool _hardLock = false;

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_load(object sender, EventArgs e)
        {
            PluginsLoader pl = new PluginsLoader();
            _actions = pl.LoadActions(@"./Plugins");
            Bitmap bitmap = new Bitmap(pictureBox1.Width, pictureBox1.Height);
            using (Graphics g = Graphics.FromImage(bitmap))
            {
                g.Clear(Color.White);
            }
            pictureBox1.Image = bitmap;

            foreach (IAction action in _actions)
            {
                action.PictureBox = pictureBox1;
                action.Modifications = _modifications;
                action.GlobalProperties = _globalProperties;

                Button button = new Button {Text = action.GetButtonCaption()};
                _buttons.Add(button);
                button.MouseClick += new MouseEventHandler((o, ee) =>
                {
                    action.OnButtonClick(o, ee);
                    if (action.IsSelectable())
                    {
                        _activeAction = action;
                        foreach (Button butt in _buttons)
                        {
                            if (butt.Text != _activeAction.GetButtonCaption())
                            {
                                butt.BackColor = Color.FromKnownColor(KnownColor.Control);
                            }
                            else
                            {
                                butt.BackColor = Color.LightSkyBlue;
                            }
                        }
                    }
                });
                
                flowLayoutPanel1.Controls.Add(button);
            }

            if (_actions.Count > 0)
            {
                _activeAction = _actions[0];
                _buttons[0].BackColor = Color.LightSkyBlue;
            }

            CheckUndoRedo();
        }

        private void pictureBox1_MouseDown(object sender, MouseEventArgs e)
        {
            if(_activeAction != null)
            _activeAction.OnMouseDown(sender, e);
            CheckUndoRedo();
        }

        private void pictureBox1_MouseMove(object sender, MouseEventArgs e)
        {
            if (_activeAction != null)
                _activeAction.OnMouseMove(sender, e);
            CheckUndoRedo();
        }

        private void pictureBox1_MouseUp(object sender, MouseEventArgs e)
        {
            if (_activeAction != null)
                _activeAction.OnMouseUp(sender, e);
            CheckUndoRedo();
        }

        // undo
        private void button1_Click(object sender, EventArgs e)
        {
            button1.Enabled = false;
            button2.Enabled = false;
            _hardLock = true;
            _undoneModifications.Add(_modifications[_modifications.Count - 1]);
            _modifications.RemoveAt(_modifications.Count - 1);

            List<IImageModification> modifications = new List<IImageModification>(_modifications);

            Task.Factory.StartNew(() =>
            {
                Bitmap bitmap = new Bitmap(pictureBox1.Width, pictureBox1.Height);
                using (Graphics g = Graphics.FromImage(bitmap))
                {
                    g.Clear(Color.White);
                }


                foreach (IImageModification im in modifications)
                {
                    im.ModifyImage(bitmap);
                }

                pictureBox1.Invoke((MethodInvoker) delegate() { pictureBox1.Image = bitmap; pictureBox1.Invalidate();});

                //Console.WriteLine(pictureBox1.InvokeRequired);
                _hardLock = false;
                CheckUndoRedo();
            });
        }

        // redo
        private void button2_Click(object sender, EventArgs e)
        {
            button1.Enabled = false;
            button2.Enabled = false;
            _hardLock = true;
            _modifications.Add(_undoneModifications[_undoneModifications.Count - 1]);
            _undoneModifications.RemoveAt(_undoneModifications.Count - 1);

            _modifications[_modifications.Count - 1].ModifyImage(pictureBox1.Image);
            pictureBox1.Invalidate();
            _hardLock = false;
            CheckUndoRedo();
        }

        private void CheckUndoRedo()
        {
            button1.Invoke((MethodInvoker) delegate() { button1.Enabled = !_hardLock && _modifications.Count > 0; });
            button2.Invoke((MethodInvoker) delegate () { button2.Enabled = !_hardLock && _undoneModifications.Count > 0; });
        }
    }
}
