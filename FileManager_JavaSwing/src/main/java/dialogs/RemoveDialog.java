package dialogs;

import locale.Context;
import locale.JCommander;
import panels.ProgressPanel;
import util.CommonButtons;
import util.ContextManager;
import workers.RemoveFilesWorker;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

/**
 * Created by macie on 26/04/2017.
 */
public class RemoveDialog {
    private JDialog dialog;

    public RemoveDialog(List<File> files) {
        Context context = ContextManager.getContext();
        dialog = new JDialog(JCommander.frame, true);
        JPanel panel = new JPanel();
        //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(context.getBundle().getString("removeConfirm1")+ ' '  + files.size()+ ' '  + context.getBundle().getString("removeConfirm2")));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        for(File file : files) {
            JLabel lab = new JLabel(file.getName());
            lab.setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));

            panel.add(lab);
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(Box.createVerticalGlue());

        panel.add(CommonButtons.createConfirmCancelButtons(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.getContentPane().removeAll();
                ProgressPanel pp = new ProgressPanel(dialog);
                JPanel panel = pp.createPanel(new RemoveFilesWorker(pp, files));
                dialog.add(panel);
                dialog.validate();
                dialog.repaint();
                dialog.setVisible(true);
            }
        }, dialog));


        dialog.add(panel);
        dialog.pack();
    }

    public void show() {
        dialog.setVisible(true);
    }
}
