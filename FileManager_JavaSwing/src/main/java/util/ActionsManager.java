package util;

import dialogs.CopyDialog;
import dialogs.MoveDialog;
import dialogs.RemoveDialog;
import locale.Context;
import locale.JCommander;
import panels.ViewerPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by macie on 26/04/2017.
 */
public class ActionsManager {
    private static List<ViewerPanel> viewerPanels = new ArrayList<>();

    private static ViewerPanel source;
    private static ViewerPanel target;

    public static void handleFocusChanged(ViewerPanel vp, ListSelectionEvent lse) {
        source = vp;
        if(viewerPanels.get(0) == vp) {
            target = viewerPanels.get(1);
        } else {
            target = viewerPanels.get(0);
        }
        //target.clearFocus();
    }

    public static void register(ViewerPanel vp) {
        viewerPanels.add(vp);
    }

    public static void performCopy() {
        if(verifyDirection()) {
            new CopyDialog(target.getCurrentDirectory(), source.getSelectedFiles()).show();
        }
    }

    public static void performMove() {
        if(verifyDirection()) {
            new MoveDialog(target.getCurrentDirectory(), source.getSelectedFiles()).show();
        }
    }

    public static void performRemove() {
        if(source != null && source.getSelectedFiles().size() > 0) {
            new RemoveDialog(source.getSelectedFiles()).show();
        } else if(target != null && target.getSelectedFiles().size() > 0) {
            new RemoveDialog(target.getSelectedFiles()).show();
        } else {
            createNothingSelectedDialog();
        }
    }

    private static boolean verifyDirection() {
        if(source == null || target == null) {
            createNothingSelectedDialog();
            return false;
        }
        if(source.getSelectedFiles().size() > 0) {
            return true;
        }
        if(source.getSelectedFiles().size() == 0 && target.getSelectedFiles().size() > 0) {
            ViewerPanel tmp = source;
            source = target;
            target = tmp;
            return true;
        }

        createNothingSelectedDialog();

        return false;
    }

    private static void createNothingSelectedDialog() {
        Context context = ContextManager.getContext();
        JOptionPane.showMessageDialog(JCommander.frame, context.getBundle().getString("nothingSelectedMessage"), context.getBundle().getString("nothingSelectedTitle"), JOptionPane.INFORMATION_MESSAGE);
        /*
        JDialog dialog = new JDialog(JCommander.frame, true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        dialog.setTitle();
        panel.add(Box.createRigidArea(new Dimension(100, 20)));

        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(100, 20)));
        dialog.add(panel);
        dialog.pack();
        dialog.setVisible(true);*/
    }

    public static void operationFinished() {
        for(ViewerPanel vp : viewerPanels) {
            vp.onCurrentDirChange();
        }
    }
}
