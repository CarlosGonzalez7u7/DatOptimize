
package Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
public class conexion {
  
    Connection conn=null;
    public Connection conectar(){
        String driver="com.mysql.jdbc.Driver";
        try {
            Class.forName(driver);
            System.out.println("Driver Cargado con exito");
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3307/datoptimize?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","Coppel2003");
            if(conn!=null){
                System.out.println("Conexion Realizada");
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return conn;
    }
    
  
            
}
