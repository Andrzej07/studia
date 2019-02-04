package panels;

import locale.Context;
import locale.ContextChangeListener;
import util.ActionsManager;
import util.ContextManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by macie on 25/04/2017.
 */
public class MainPanel {
    private ViewerPanel leftViewerPanel;
    private ViewerPanel rightViewerPanel;


    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(createTablesPanel(), BorderLayout.CENTER);
        panel.add(createBottomPanel(), BorderLayout.PAGE_END);
        return panel;
    }

    private JComponent createTablesPanel() {
        final GridLayout layout = new GridLayout(1, 2);
        layout.setHgap(5);
        layout.setVgap(5);

        final JPanel panel = new JPanel();
        panel.setLayout(layout);

        leftViewerPanel = new ViewerPanel();
        rightViewerPanel = new ViewerPanel();
        panel.add(leftViewerPanel.createViewerPanel());
        panel.add(rightViewerPanel.createViewerPanel());

        return panel;
    }


    private JComponent createBottomPanel() {
        Context context = ContextManager.getContext();
        final GridLayout layout = new GridLayout(1, 3);
        layout.setHgap(5);
        layout.setVgap(10);

        final JPanel panel = new JPanel();
        panel.setLayout(layout);
        JButton moveButton = new JButton(context.getBundle().getString("move"));
        JButton copyButton = new JButton(context.getBundle().getString("copy"));
        JButton removeButton = new JButton(context.getBundle().getString("remove"));
        panel.add(moveButton);
        panel.add(copyButton);
        panel.add(removeButton);

        context.addContextChangeListener(new ContextChangeListener() {
            @Override
            public void contextChanged() {
                moveButton.setText(context.getBundle().getString("move"));
                copyButton.setText(context.getBundle().getString("copy"));
                removeButton.setText(context.getBundle().getString("remove"));
            }
        });

        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionsManager.performMove();
            }
        });
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionsManager.performCopy();
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionsManager.performRemove();
            }
        });

        return panel;
    }
}
