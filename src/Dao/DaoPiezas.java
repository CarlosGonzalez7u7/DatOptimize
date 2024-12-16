package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.piezas;

/**
 *
 * @author juanc
 */
public class DaoPiezas {

    Connection con;
    conexion cn = new conexion();
    PreparedStatement ps;
    ResultSet rs;

    ////////////////////////////////////////////////////////
    public boolean insertarPiezas(piezas n) {
        String sql = "insert into piezas (nom_Pieza, marca_Pieza, modelo_Pieza, cantidad, precio_Pieza ) values (?,?,?,?,?)";
        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);

            ps.setString(1, n.getNom_Pieza());
            ps.setString(2, n.getMarca_Pieza());
            ps.setString(3, n.getModelo_Pieza());
            ps.setInt(4, n.getCantidad());
            ps.setString(5, n.getPrecio_Pieza());

            int n1 = ps.executeUpdate();
            if (n1 != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
            return false;
        }
    }
    ///////////////////////

    public List ListarPiezas() {
        List<piezas> lista = new ArrayList<>();
        String sql = "select * from piezas";
        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                piezas pi = new piezas();

                pi.setId_Pieza(rs.getInt(1));
                pi.setNom_Pieza(rs.getString(2));
                pi.setMarca_Pieza(rs.getString(3));
                pi.setModelo_Pieza(rs.getString(4));
                pi.setCantidad(rs.getInt(5));
                pi.setPrecio_Pieza(rs.getString(6));

                lista.add(pi);
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return lista;
    }

    public boolean editarNotas(piezas pi) {
        String sql = "update piezas set nom_Pieza=?, marca_Pieza=?, modelo_Pieza=?, cantidad=?, precio_Pieza=? where id_Pieza=?";

        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);

            ps.setString(1, pi.getNom_Pieza());
            ps.setString(2, pi.getMarca_Pieza());
            ps.setString(3, pi.getModelo_Pieza());
            ps.setInt(4, pi.getCantidad());
            ps.setString(5, pi.getPrecio_Pieza());
            ps.setInt(6, pi.getId_Pieza());

            int n = ps.executeUpdate();

            if (n != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
            return false;
        }
    }

    public int eliminarPiezas(int idPieza, int cantidadEliminar) {
        String sqlSelect = "SELECT cantidad FROM piezas WHERE id_Pieza = ?";
        String sqlUpdate = "UPDATE piezas SET cantidad=? WHERE id_Pieza=?";
        String sqlDelete = "DELETE FROM piezas WHERE id_Pieza=?";

        try {
            con = cn.conectar();
            con.setAutoCommit(false);  // Desactivar la auto confirmación

            // Obtener la cantidad actual de piezas
            ps = con.prepareStatement(sqlSelect);
            ps.setInt(1, idPieza);
            rs = ps.executeQuery();

            if (rs.next()) {
                int cantidadActual = rs.getInt("cantidad");

                // Verificar que la cantidad a eliminar no sea mayor que la cantidad actual
                if (cantidadEliminar <= cantidadActual) {
                    // Restar la cantidad deseada a eliminar de la cantidad actual
                    int cantidadFinal = cantidadActual - cantidadEliminar;

                    // Actualizar la cantidad en la base de datos
                    ps = con.prepareStatement(sqlUpdate);
                    ps.setInt(1, cantidadFinal);
                    ps.setInt(2, idPieza);
                    ps.executeUpdate();

                    // Eliminar la pieza si la cantidad llega a cero
                    if (cantidadFinal == 0) {
                        ps = con.prepareStatement(sqlDelete);
                        ps.setInt(1, idPieza);
                        ps.executeUpdate();
                    }

                    // Confirmar la transacción
                    con.commit();
                    con.setAutoCommit(true);  // Reactivar la auto confirmación

                    return cantidadFinal;
                } else {
                    // No se puede eliminar más piezas de las que están registradas
                    JOptionPane.showMessageDialog(null, "No puede dar de Baja más piezas de las que están registradas.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            try {
                // Deshacer la transacción en caso de error
                con.rollback();
                con.setAutoCommit(true);  // Reactivar la auto confirmación
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showConfirmDialog(null, e);
        }
        return -1;  // Retorna -1 si hay un error o no se encuentra la cantidad
    }
    
     
    public boolean BuscarPiezas(piezas pi, boolean buscarPorId) {
    String sql = buscarPorId ? "SELECT * FROM piezas WHERE id_Pieza=?" : "SELECT * FROM piezas WHERE nom_Pieza=?";
    
    try {
        con = cn.conectar();
        ps = con.prepareStatement(sql);
        
        if (buscarPorId) {
            ps.setInt(1, pi.getId_Pieza());
        } else {
            ps.setString(1, pi.getNom_Pieza());
        }

        rs = ps.executeQuery();
        
        if (rs.next()) {
            pi.setId_Pieza(rs.getInt(1));
            pi.setNom_Pieza(rs.getString(2));
            pi.setMarca_Pieza(rs.getString(3));
            pi.setModelo_Pieza(rs.getString(4));
            pi.setCantidad(rs.getInt(5));
            pi.setPrecio_Pieza(rs.getString(6));
            
            return true;
        } else {
            return false;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e);
        return false;
    } 
}
    
    public int incrementarCantidadPiezas(int idPieza, int cantidad) {
    String sqlIncrementar = "UPDATE piezas SET cantidad = cantidad + ? WHERE id_Pieza = ?";
    String sqlObtenerCantidad = "SELECT cantidad FROM piezas WHERE id_Pieza = ?";

    try {
        con = cn.conectar();

        // Incrementar la cantidad
        try (PreparedStatement psIncrementar = con.prepareStatement(sqlIncrementar)) {
            psIncrementar.setInt(1, cantidad);
            psIncrementar.setInt(2, idPieza);

            int n = psIncrementar.executeUpdate();

            if (n == 0) {
                return -1; // Indicar error si no se actualiza ninguna fila
            }
        }

        // Obtener la nueva cantidad
        try (PreparedStatement psObtenerCantidad = con.prepareStatement(sqlObtenerCantidad)) {
            psObtenerCantidad.setInt(1, idPieza);
            ResultSet rs = psObtenerCantidad.executeQuery();

            if (rs.next()) {
                return rs.getInt("cantidad");
            } else {
                return -1; // Indicar error si no se encuentra la fila
            }
        }
    } catch (Exception e) {
        JOptionPane.showConfirmDialog(null, e);
        return -1; // Indicar error
    } finally {
        // Cerrar recursos aquí si es necesario
    }
}
    public double obtenerPrecioPieza(int idPieza) {
        String sql = "SELECT precio_Pieza FROM piezas WHERE id_Pieza = ?";

        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idPieza);

            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("precio_Pieza");
            } else {
                throw new RuntimeException("No se encontró la pieza con ID " + idPieza);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Error al obtener el precio de la pieza", e);
        } 
    }
  
}