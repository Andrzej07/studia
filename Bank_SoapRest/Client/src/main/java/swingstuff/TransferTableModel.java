package swingstuff;

import generated.Transfer;
import util.AmountFormatter;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by macie on 19/01/2018.
 */
public class TransferTableModel implements TableModel {
    String[] columnNames = {"Source", "Target", "Name", "Title", "Amount"};
    List<String[]> data;

    public TransferTableModel() {
        super();
        data = new LinkedList<>();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex)[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

    public void updateData(List<Transfer> transferList) {
        data.clear();
        for(Transfer transfer : transferList) {
            String[] arr = new String[5];
            arr[0] = transfer.getSourceAccount();
            arr[1] = transfer.getTargetAccount();
            arr[2] = transfer.getName();
            arr[3] = transfer.getTitle();
            arr[4] = AmountFormatter.formatValue(transfer.getAmount());
            data.add(arr);
        }
    }
}
