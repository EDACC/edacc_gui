package edacc;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author simon
 */
public class MultipleCellSelectionTable extends JTable {

    private HashSet<Point> selectedCells;
    
    private Point lastSelectedCell;
    private boolean ctrlDeactivate;

    public MultipleCellSelectionTable() {
        super();
        selectedCells = new HashSet<Point>();
        for (Object c : this.defaultRenderersByColumnClass.keySet()) {
            this.setDefaultRenderer((Class<?>) c, new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    isSelected = selectedCells.contains(new Point(row, column));
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
        }
        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                int row = MultipleCellSelectionTable.this.rowAtPoint(e.getPoint());
                int col = MultipleCellSelectionTable.this.columnAtPoint(e.getPoint());
                if (e.isControlDown()) {
                    if (ctrlDeactivate) {
                        selectedCells.remove(new Point(row, col));
                    } else {
                        selectedCells.add(new Point(row, col));
                    }
                    MultipleCellSelectionTable.this.repaint();
                } else if (e.isShiftDown()) {
                    selectedCells.clear();
                    Point current = new Point(row, col);
                    int r_lo = Math.min(current.x, lastSelectedCell.x);
                    int r_hi = Math.max(current.x, lastSelectedCell.x);
                    int c_lo = Math.min(current.y, lastSelectedCell.y);
                    int c_hi = Math.max(current.y, lastSelectedCell.y);
                    for (int r = r_lo; r <= r_hi; r++) {
                        for (int c = c_lo; c <= c_hi; c++) {
                            selectedCells.add(new Point(r, c));
                        }
                    }
                } else {
                    mousePressed(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int row = MultipleCellSelectionTable.this.rowAtPoint(e.getPoint());
                int col = MultipleCellSelectionTable.this.columnAtPoint(e.getPoint());
                if (e.isControlDown()) {
                    lastSelectedCell = new Point(row, col);
                    if (selectedCells.contains(lastSelectedCell)) {
                        selectedCells.remove(lastSelectedCell);
                        ctrlDeactivate = true;
                    } else {
                        selectedCells.add(lastSelectedCell);
                        ctrlDeactivate = false;
                    }
                } else if (e.isShiftDown()) {
                    selectedCells.clear();
                    Point current = new Point(row, col);
                    if (lastSelectedCell == null) {
                        lastSelectedCell = current;
                    }
                    int r_lo = Math.min(current.x, lastSelectedCell.x);
                    int r_hi = Math.max(current.x, lastSelectedCell.x);
                    int c_lo = Math.min(current.y, lastSelectedCell.y);
                    int c_hi = Math.max(current.y, lastSelectedCell.y);
                    for (int r = r_lo; r <= r_hi; r++) {
                        for (int c = c_lo; c <= c_hi; c++) {
                            selectedCells.add(new Point(r, c));
                        }
                    }

                } else {
                    selectedCells.clear();
                    lastSelectedCell = new Point(row, col);
                    selectedCells.add(lastSelectedCell);
                }
                MultipleCellSelectionTable.this.repaint();
            }
        };
        
        this.addMouseMotionListener(mouseAdapter);
        this.addMouseListener(mouseAdapter);
    }

    /**
     * Returns an array of all selected cell.<\br>
     * x coordinate represents row<\br>
     * y coordinate represents column
     * @return array of <code>Point</code>
     */
    public Point[] getSelectedCells() {
        return (Point[]) selectedCells.toArray();
    }

    /**
     * Returns the count of the selected cells.
     * @return count of the selected cells
     */
    public int getSelectedCount() {
        return selectedCells.size();
    }

    @Override
    public boolean isCellSelected(int row, int column) {
        return selectedCells.contains(new Point(row, column));
    }
}
