package renderer;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;

/**
 * Created by macie on 26/04/2017.
 */
public class FileCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
       /* File f = (File) value;

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(noFocusBorder);

        label.setText(f.getName());
        label.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));

        return label;*/
        File f = (File) value;

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(noFocusBorder);

        setText(f.getName());
        setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));

        return this;
    }
}