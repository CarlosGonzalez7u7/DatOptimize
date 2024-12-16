
package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.cargo;

public class DaoCargo {
    Connection con;
    conexion cn=new conexion();
    PreparedStatement ps;
    ResultSet rs;
    
    public boolean insertar(cargo c){
       String sql="insert into cargos (nom_cargo) values (?)";
        try {
            con=cn.conectar();
            ps=con.prepareStatement(sql);
            ps.setString(1, c.getNombre());
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
    
    public List Listar(){
        List<cargo> lista=new ArrayList<>();
         String sql="select * from cargos";
         try {
            con=cn.conectar();
            ps=con.prepareStatement(sql);
           rs=ps.executeQuery();
           while(rs.next()){
               cargo c=new cargo();
               c.setId(rs.getInt(1));
               c.setNombre(rs.getString(2));
               lista.add(c);
           }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
         return lista;
    }
    
    public boolean editar(cargo cr){
        String sql="update cargos set nom_cargo=? where id_cargo=?";
         try {
            con=cn.conectar();
            ps=con.prepareStatement(sql);
            ps.setString(1, cr.getNombre());
            ps.setInt(2, cr.getId());
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
    
    public boolean eliminar(cargo cr){
        String sql="delete from cargos where id_cargo=?";
         try {
            con=cn.conectar();
            ps=con.prepareStatement(sql);
            ps.setInt(1, cr.getId());
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
}
