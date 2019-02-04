package component;

import renderer.DateCellRenderer;
import renderer.FileCellRenderer;
import renderer.FileSizeCellRenderer;

import javax.swing.*;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by macie on 25/04/2017.
 */
public class FilesTable extends JTable {
    private List<File> files;

    public FilesTable(List<File> files) {
        super();
        this.files = files;
        setShowGrid(false);

        setModel(new FilesTableModel(files));
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        setDefaultRenderer(Long.class, new FileSizeCellRenderer());
        setDefaultRenderer(Date.class, new DateCellRenderer());
        setDefaultRenderer(File.class, new FileCellRenderer());

        setAutoCreateRowSorter(true);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
