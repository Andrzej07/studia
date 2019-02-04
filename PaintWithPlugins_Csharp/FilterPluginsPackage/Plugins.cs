using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using Interfaces;

namespace FilterPluginsPackage
{
    public class InverseColors : IAction
    {
        public PictureBox PictureBox { get; set; }
        public List<IImageModification> Modifications { get; set; }
        public Dictionary<string, string> GlobalProperties { get; set; }
        public void OnButtonClick(object sender, EventArgs e)
        {
            Bitmap bitmap = (Bitmap) PictureBox.Image;
            LockBitmap img = new LockBitmap(bitmap);
            img.LockBits();
            for (int i = 0; i < img.Width; i++)
            {
                for (int j = 0; j < img.Height; j++)
                {
                    Color cl = img.GetPixel(i, j);
                    img.SetPixel(i, j, Color.FromArgb(Byte.MaxValue - cl.R, Byte.MaxValue - cl.G, Byte.MaxValue - cl.B)); ;
                }
            }
            img.UnlockBits();
            PictureBox.Invalidate();
            Modifications.Add(new InvertColorsModification());
        }

        public string GetButtonCaption()
        {
            return "Inverse colors";
        }

        public bool IsSelectable()
        {
            return false;
        }

        public void OnMouseUp(object sender, MouseEventArgs e){}

        public void OnMouseMove(object sender, MouseEventArgs e){}

        public void OnMouseDown(object sender, MouseEventArgs e){}
    }

    public class InvertColorsModification : IImageModification
    {
        public void ModifyImage(Image bitmap)
        {
            Bitmap img = (Bitmap) bitmap;
            for (int i = 0; i < img.Width; i++)
            {
                for (int j = 0; j < img.Height; j++)
                {
                    Color cl = img.GetPixel(i, j);
                    img.SetPixel(i, j, Color.FromArgb(Byte.MaxValue - cl.R, Byte.MaxValue - cl.G, Byte.MaxValue - cl.B)); ;
                }
            }
        }
    }
}
