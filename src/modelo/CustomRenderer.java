
package modelo;
import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

// En tu clase



public class CustomRenderer extends DefaultTableCellRenderer {
    private Set<Integer> filasResaltadas;

    public CustomRenderer(Set<Integer> filasResaltadas) {
        this.filasResaltadas = filasResaltadas;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Cambia el color de fondo si la fila está seleccionada o resaltada
        if (filasResaltadas.contains(row) || isSelected) {
            c.setBackground(Color.GREEN);
        } else {
            c.setBackground(table.getBackground());
        }

        return c;
    }
}
