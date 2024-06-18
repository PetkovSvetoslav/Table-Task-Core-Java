package org.example;

import javax.swing.table.AbstractTableModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyTableModel extends AbstractTableModel implements Serializable {
    private final String[] columnNames = {"Text (Left)", "Text (Center)", "Number (Right)"};
    private final Class<?>[] columnTypes = {String.class, String.class, Double.class};
    private List<List<Object>> data = new ArrayList<>();

    public MyTableModel() {
        // Empty constructor - if needed, initial rows can be added here
    }

    public void addRow() {
        List<Object> row = new ArrayList<>();
        row.add("");
        row.add("");
        row.add(0.0);
        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void removeRow(int rowIndex) {
        data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void setData(List<List<Object>> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public List<List<Object>> getData() {
        return data;
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
        return columnTypes[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex).get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data.get(rowIndex).set(columnIndex, aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(data.size());
        for (List<Object> row : data) {
            out.writeInt(row.size());
            for (Object obj : row) {
                out.writeObject(obj);
            }
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int rowSize = in.readInt();
            List<Object> row = new ArrayList<>(rowSize);
            for (int j = 0; j < rowSize; j++) {
                row.add(in.readObject());
            }
            data.add(row);
        }
    }
}


