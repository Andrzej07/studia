package component;

import locale.Context;
import locale.ContextChangeListener;
import util.ContextManager;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by macie on 26/04/2017.
 */
public class FilesTableModel extends AbstractTableModel {
    private Context context;
    private List<File> files = new ArrayList<>();
    String[] columnNames = {"name", "date", "size"};

    public FilesTableModel(List<File> files) {
        this.context = ContextManager.getContext();
        context.addContextChangeListener(new ContextChangeListener() {
            @Override
            public void contextChanged() {
                fireTableStructureChanged();
            }
        });
        this.files = files;
    }

    public void changeList(List<File> files) {
        this.files = files;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return context.getBundle().getString(columnNames[columnIndex]);
    }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        File f = files.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return f;
            case 1:
                return f.lastModified();
            case 2:
                if(f.isFile()) return f.length();
                else return new Long(0);
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return File.class;
            case 1:
                return Date.class;
            case 2:
                return Long.class;
        }
        return null;
    }
}
