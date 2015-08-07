import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * User table model
 * @author seth-list
 */
public class MyTable3 extends AbstractTableModel {

    private ArrayList<String> columnNames = new ArrayList<String>();
    private ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();

    public void ChColumn(ArrayList<String> col)
    {
        columnNames = col;
        this.fireTableDataChanged();
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    public ArrayList<Object> getRowAt(int row) {
        return data.get(row);
    }

    public Object getValueAt(int row, int col) {
        return data.get(row).get(col);
    }

    public void addRow(ArrayList<Object> row) {

        data.add(row);
        this.fireTableDataChanged();

    }

    public void delRow(int row) {

        data.remove(row);
        this.fireTableDataChanged();

    }

    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return col >= 15;
    }

}
