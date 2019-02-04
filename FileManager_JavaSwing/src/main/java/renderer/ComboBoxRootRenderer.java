package renderer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by macie on 25/04/2017.
 */
public class ComboBoxRootRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        File file = (File) value;

        JLabel label = new JLabel();
        label.setText(file.getName());
        return label;
    }
}
