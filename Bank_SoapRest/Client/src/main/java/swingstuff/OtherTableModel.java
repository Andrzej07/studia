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
public class OtherTableModel implements TableModel {
    String[] columnNames = {"Operation", "Amount"};
    List<String[]> data;

    public OtherTableModel() {
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
            String[] arr = new String[2];
            if(transfer.getTargetAccount() != null && !transfer.getTargetAccount().isEmpty()) {
                arr[0] = "Deposit";
            } else {
                arr[0] = "Withdrawal";
            }
            arr[1] = AmountFormatter.formatValue(transfer.getAmount());
            data.add(arr);
        }
    }
}
