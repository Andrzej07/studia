package panels;

import locale.Context;
import util.ActionsManager;
import util.CommonButtons;
import util.ContextManager;
import workers.CopyFilesWorker;
import workers.RemoveFilesWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

/**
 * Created by macie on 27/04/2017.
 */
public class ProgressPanel {
    JPanel panel;
    JProgressBar progressBar;

    SwingWorker sw;
    JDialog dialog;
    JButton button;

    public ProgressPanel(JDialog dialog) {
        this.dialog = dialog;
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
    }

    public JPanel createPanel(SwingWorker sw) {
        this.sw = sw;
        panel = new JPanel();

        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        panel.add(progressBar);

        button = CommonButtons.createCancelButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel();
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        });

        panel.add(button);

        sw.execute();

        return panel;
    }

    public void setProgressBarMaximum(int val) {
        progressBar.setMaximum(val);
    }

    public void setProgressBarValue(int val)  {
        progressBar.setValue(val);
    }

    public void operationFinished() {
      //  button.removeActionListener(button.getActionListeners()[0]);
        button.setText(ContextManager.getContext().getBundle().getString("confirm"));
        ActionsManager.operationFinished();
    }

    public void cancel() {
        System.out.println("Cancelling operation");
        sw.cancel(true);
    }
}
