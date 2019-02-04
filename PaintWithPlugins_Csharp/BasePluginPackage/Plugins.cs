using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Interfaces;

namespace BasePluginPackage
{
    public class Pencil : IAction
    {
        private Point _previousPoint;
        private DrawLineModification _currentModification;

        public PictureBox PictureBox { get; set; }
        public List<IImageModification> Modifications { get; set; }
        public Dictionary<string, string> GlobalProperties { get; set; }

        public void OnMouseDown(object sender, MouseEventArgs e)
        {
            _previousPoint = e.Location;
            _currentModification = new DrawLineModification(e.Location);
        }

        public void OnMouseMove(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                using (Graphics g = Graphics.FromImage(PictureBox.Image))
                {
                    Pen pen = new Pen(GlobalProperties.ContainsKey("Color") ? ColorTranslator.FromHtml(GlobalProperties["Color"]) : Color.Black);
                    _currentModification.AddPoint(e.Location, pen);
                    g.DrawLine(pen, _previousPoint, e.Location);
                }
                _previousPoint = e.Location;
                
                PictureBox.Invalidate();
            }
        }

        public void OnMouseUp(object sender, MouseEventArgs e)
        {
            if (_currentModification != null && !_currentModification.IsEmpty())
            {
                Modifications.Add(_currentModification);
                _currentModification = null;
            }
        }

        public void OnButtonClick(object sender, EventArgs e){}

        public string GetButtonCaption()
        {
            return "Pencil";
        }

        public bool IsSelectable()
        {
            return true;
        }
    }

    public class DrawLine : IAction
    {
        private Pen _pen;
        private Point _startPoint;
        private DrawLineModification _currentModification;
        private Bitmap _baseBitmap;

        public PictureBox PictureBox { get; set; }
        public List<IImageModification> Modifications { get; set; }
        public Dictionary<string, string> GlobalProperties { get; set; }

        public void OnMouseDown(object sender, MouseEventArgs e)
        {
            _currentModification = new DrawLineModification(e.Location);
            _startPoint = e.Location;
            _baseBitmap = (Bitmap) PictureBox.Image.Clone();
            _pen = new Pen(GlobalProperties.ContainsKey("Color") ? ColorTranslator.FromHtml(GlobalProperties["Color"]) : Color.Black);
        }

        public void OnMouseMove(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                /*using (Graphics g = Graphics.FromImage(PictureBox.Image))
                {
                    g.Clear(Color.White);
                }

                foreach (IImageModification im in Modifications)
                {
                    im.ModifyImage(PictureBox.Image);
                }

                using (Graphics g = Graphics.FromImage(PictureBox.Image)){
                    g.DrawLine(Pens.Black, _startPoint, e.Location);
                }
                PictureBox.Invalidate();*/
                Bitmap clone = (Bitmap) _baseBitmap.Clone();
                using (Graphics g = Graphics.FromImage(clone))
                {
                    g.DrawLine(_pen, _startPoint, e.Location);
                }
                PictureBox.Image.Dispose();
                PictureBox.Image = clone;
            }
        }

        public void OnMouseUp(object sender, MouseEventArgs e)
        { 
            _currentModification.AddPoint(e.Location, _pen);
            Modifications.Add(_currentModification);
            _currentModification = null;      
        }

        public void OnButtonClick(object sender, EventArgs e){}

        public string GetButtonCaption()
        {
            return "Line";
        }

        public bool IsSelectable()
        {
            return true;
        }
    }

    public class PickColor : IAction
    {
        private ColorDialog _colorDialog = new ColorDialog();

        public PictureBox PictureBox { get; set; }
        public List<IImageModification> Modifications { get; set; }
        public Dictionary<string, string> GlobalProperties { get; set; }
        public void OnMouseDown(object sender, MouseEventArgs e)
        {
            throw new NotImplementedException();
        }

        public void OnMouseMove(object sender, MouseEventArgs e)
        {
            throw new NotImplementedException();
        }

        public void OnMouseUp(object sender, MouseEventArgs e)
        {
            throw new NotImplementedException();
        }

        public void OnButtonClick(object sender, EventArgs e)
        {
            DialogResult result = _colorDialog.ShowDialog();
            if (result == DialogResult.OK)
            {
                GlobalProperties["Color"] = ColorTranslator.ToHtml(_colorDialog.Color);
            }
        }

        public string GetButtonCaption()
        {
            return "Pick color";
        }

        public bool IsSelectable()
        {
            return false;
        }
    }

    class DrawLineModification : IImageModification
    {
        private List<Point> _points = new List<Point>();
        private List<Pen> _pens = new List<Pen>();

        public DrawLineModification(Point p)
        {
            _points.Add(p);
        }

        public bool IsEmpty()
        {
            return _points.Count < 2;
        }

        public void AddPoint(Point p, Pen color)
        {
            _points.Add(p);
            _pens.Add(color);
        }

        public void ModifyImage(Image bitmap)
        {
            using (Graphics g = Graphics.FromImage(bitmap))
            {
                for(int i = 0; i < _points.Count-1; i++)
                {
                    g.DrawLine(_pens[i], _points[i], _points[i+1]);
                }
            }
        }
    }

}
