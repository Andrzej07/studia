using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Interfaces
{
    public interface IAction
    {
        PictureBox PictureBox { set; }
        List<IImageModification> Modifications { set; }
        Dictionary<string, string> GlobalProperties { set; }
        void OnMouseDown(object sender, MouseEventArgs e);
        void OnMouseMove(object sender, MouseEventArgs e);
        void OnMouseUp(object sender, MouseEventArgs e);
        void OnButtonClick(object sender, EventArgs e);
        string GetButtonCaption();
        bool IsSelectable();
    }

    public interface IImageModification
    {
        void ModifyImage(Image bitmap);
    }
}
