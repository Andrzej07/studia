package util;

import locale.Context;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Created by macie on 27/04/2017.
 */
public class CommonButtons {

    private static Context context = ContextManager.getContext();

    public static JButton createCancelButton(JDialog dialog) {
        return createCancelButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    public static JButton createCancelButton(ActionListener al) {
        JButton cancelButton = new JButton(context.getBundle().getString("cancel"));
        cancelButton.addActionListener(al);
        return cancelButton;
    }

    public static JPanel createConfirmCancelButtons(ActionListener confirm, JDialog dialog) {
        return createConfirmCancelButtons(confirm, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    public static JButton createConfirmButton(ActionListener confirm) {
        JButton confirmButton = new JButton(context.getBundle().getString("confirm"));
        confirmButton.addActionListener(confirm);
        return confirmButton;
    }

    public static JPanel createConfirmCancelButtons(ActionListener confirm, ActionListener cancel) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(Box.createHorizontalGlue());

        buttonsPanel.add(createConfirmButton(confirm));
        buttonsPanel.add(createCancelButton(cancel));

        return buttonsPanel;
    }

}
