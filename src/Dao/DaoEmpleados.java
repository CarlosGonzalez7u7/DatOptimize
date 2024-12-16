
package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.empleados;

/**
 *
 * @author juanc
 */
public class DaoEmpleados {
    Connection con;
    conexion cn = new conexion();
    PreparedStatement ps;
    ResultSet rs;

        ////////////////////////////////////////////////////////
    public boolean insertarEmpleados(empleados em) {
        String sql = "insert into empleados (nom_Empleado, fecha_Empleado, id_cargo ) values (?,?,?)";
        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);

            ps.setString(1, em.getNom_Empleado());
            ps.setString(2, em.getFecha_Empleado());
            ps.setInt(3, em.getId_cargo());
          

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
    
    public DefaultTableModel listarEmpleados(){
        DefaultTableModel modelo;
        
        String [] titulos={"ID Empleado", "Nombre", "Fecha de Registro", "ID Cargo", "Cargo"};
        
        String [] registros = new String[5];
        modelo=new DefaultTableModel(null, titulos);
        
        String sql=
                    "SELECT  e.id_empleado, e.nom_Empleado, e.fecha_Empleado, \n" +
                    "e.id_cargo, c.nom_cargo\n" +
                    "from empleados e\n" +
                    "INNER JOIN cargos c\n" +
                    "on e.id_cargo=c.id_cargo;";
        
        try {
           con = cn.conectar();
            ps = con.prepareStatement(sql);
            rs=ps.executeQuery();
            while(rs.next()){
                registros[0]=rs.getString("id_empleado");
                registros[1]=rs.getString("nom_empleado");
                registros[2]=rs.getString("fecha_Empleado");
                registros[3]=rs.getString("id_cargo");
                registros[4]=rs.getString("nom_cargo");
                modelo.addRow(registros);
            }
            return modelo;
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
            return null;
        }
    }
    
    public boolean editarEmpleados(empleados em){
        String sql="update empleados set nom_Empleado=?, fecha_Empleado=?, id_cargo=? where id_empleado=?";
         try {
            con=cn.conectar();
            ps=con.prepareStatement(sql);
            ps.setString(1, em.getNom_Empleado());
            ps.setString(2, em.getFecha_Empleado());
            ps.setInt(3, em.getId_cargo());
            ps.setInt(4, em.getId_empleado());
            int n=ps.executeUpdate();
            if(n!=0){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
            return false;
        }
    }
    
    public boolean eliminarEmpleados(empleados em) {
        String sql = "delete from empleados where id_empleado=?";

        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);
            ps.setInt(1, em.getId_empleado());
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
}
