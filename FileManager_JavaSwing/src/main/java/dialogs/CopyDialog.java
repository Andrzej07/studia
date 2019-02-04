package dialogs;

import locale.Context;
import locale.JCommander;
import panels.ProgressPanel;
import util.CommonButtons;
import util.ContextManager;
import workers.CopyFilesWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;
import java.io.File;

/**
 * Created by macie on 26/04/2017.
 */
public class CopyDialog {
    private JDialog dialog;

    public CopyDialog(File destination, List<File> files) {
        Context context = ContextManager.getContext();
        dialog = new JDialog(JCommander.frame, true);
        JPanel panel = new JPanel();
        //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(context.getBundle().getString("copyConfirm1") + ' ' + files.size() + ' ' + context.getBundle().getString("copyConfirm2")));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        JTextField locationTextField = new JTextField();
        locationTextField.setEditable(false);
        locationTextField.setText(destination.toString());
        panel.add(locationTextField);


        panel.add(CommonButtons.createConfirmCancelButtons(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.getContentPane().removeAll();
                ProgressPanel pp = new ProgressPanel(dialog);
                JPanel panel = pp.createPanel(new CopyFilesWorker(pp, destination, files));

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
