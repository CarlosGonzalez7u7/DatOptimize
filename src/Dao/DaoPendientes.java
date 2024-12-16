package Dao;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.notas;
import modelo.pendientes;

///////Agrego PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import java.awt.HeadlessException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author juanc
 */
public class DaoPendientes {

    Connection con;
    conexion cn = new conexion();
    PreparedStatement ps;
    ResultSet rs;

    ////////////////////////////////////////////////////////
   public boolean insertarPendientes(pendientes pe) {
        String sql = "insert into pendientes (causante, estado_Maquina, fecha_Revision, Total, Piezas, id_notaRec, id_empleado, id_Pieza ) values (?,?,?,?,?,?,?,?)";
        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);

            ps.setString(1, pe.getCausante());
            ps.setString(2, pe.getEstado_Maquina());
            ps.setString(3, pe.getFecha_Revision());
            ps.setDouble(4, pe.getTotal());
            ps.setString(5, pe.getPiezas());
            ps.setInt(6, pe.getId_notarec());
            ps.setInt(7, pe.getId_empleado());
            ps.setInt(8, pe.getId_Pieza());

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
    
    
    
   
     public DefaultTableModel listarPendientes(){
        DefaultTableModel modelo;
        
        String [] titulos={"Folio", "Causa", "Estado", "se Reviso", "Cliente", "Maquina", "La Reviso", "Total", "Piezas", "ID","ID"};
        
        String [] registros = new String[11];
        modelo=new DefaultTableModel(null, titulos);
        
        String sql=
                    "SELECT  p.id_pendientes, p.causante, p.estado_Maquina, p.fecha_Revision, p.id_notaRec,"
                +   " n.nom_Clientes, n.nom_maquina, e.id_empleado, e.nom_empleado, pz.id_pieza, pz.nom_pieza,"
                +   " p.Total, p.Piezas FROM pendientes p INNER JOIN notarec n ON p.id_notaRec = n.id_notaRec INNER JOIN"
                +   " empleados e ON p.id_empleado = e.id_empleado INNER JOIN piezas pz ON p.id_pieza = pz.id_pieza;";
        
        try {
           con = cn.conectar();
            ps = con.prepareStatement(sql);
            rs=ps.executeQuery();
            while(rs.next()){
                registros[0]=rs.getString("id_notaRec");//---->quite - id_pendientes -
                registros[1]=rs.getString("causante");
                registros[2]=rs.getString("estado_Maquina");
                registros[3]=rs.getString("fecha_Revision");
                registros[4]=rs.getString("nom_Clientes");
                registros[5]=rs.getString("nom_maquina");
                registros[6]=rs.getString("nom_empleado");
                
                // Convertir "Total" a Double y luego formatear como String
            double total = rs.getDouble("Total");
            registros[7] = String.format("%.2f", total);

            registros[8] = rs.getString("Piezas");
            registros[9]=rs.getString("id_pendientes");//
            registros[10]=rs.getString("id_empleado");//
                /*registros[7]=rs.getString("Total");
                registros[8]=rs.getString("Piezas");*/
                modelo.addRow(registros);
            }
            return modelo;
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
            return null;
        }
    }
     
     public boolean buscarPendientePorIdONombre(pendientes pe, boolean buscarPorId) {
    String sql = buscarPorId ? "SELECT p.*, n.nom_Clientes, n.nom_maquina, e.nom_Empleado, pz.* FROM pendientes "
                             + "p JOIN notarec n ON p.id_notaRec = n.id_notaRec LEFT JOIN empleados e ON "
                             + "p.id_empleado = e.id_empleado LEFT JOIN piezas pz ON p.id_Pieza = pz.id_Pieza "
                             + "WHERE p.id_notaRec=?" : "SELECT p.*, n.nom_Clientes, n.nom_maquina, e.nom_Empleado, "
                             + "pz.* FROM pendientes p JOIN notarec n ON p.id_notaRec = n.id_notaRec LEFT JOIN empleados "
                             + "e ON p.id_empleado = e.id_empleado LEFT JOIN piezas pz ON p.id_Pieza = pz.id_Pieza WHERE n.nom_Clientes=?";
    
    try {
        con = cn.conectar();
        ps = con.prepareStatement(sql);
        
        if (buscarPorId) {
            ps.setInt(1, pe.getId_notarec());
        } else {
            ps.setString(1, pe.getCliente());
        }

        rs = ps.executeQuery();
        
        if (rs.next()) {
            llenarDatosPendienteDesdeResultSet(pe, rs);
            return true;
        } else {
            return false;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Error de SQL", JOptionPane.ERROR_MESSAGE);
        return false;
    } 
}


private void llenarDatosPendienteDesdeResultSet(pendientes pe, ResultSet rs) throws Exception {
    pe.setId_pendientes(rs.getInt("id_pendientes"));
    pe.setCausante(rs.getString("causante"));
    pe.setEstado_Maquina(rs.getString("estado_Maquina"));
    pe.setFecha_Revision(rs.getString("fecha_Revision"));
    pe.setTotal(rs.getDouble("Total"));
    pe.setId_notarec(rs.getInt("id_notaRec"));
    pe.setCliente(rs.getString("nom_Clientes"));
    pe.setMaquina(rs.getString("nom_maquina"));
    pe.setId_empleado(rs.getInt("id_empleado"));
    pe.setEmpleado(rs.getString("nom_Empleado"));
    pe.setId_Pieza(rs.getInt("id_Pieza"));
    pe.setPiezas(rs.getString("Piezas"));
}

 public boolean eliminarPendientes(pendientes pe) {
        String sql = "delete from pendientes where id_notaRec=?";

        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);
            ps.setInt(1, pe.getId_pendientes());
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
 
 
 
 public boolean ImprimirRecibo(pendientes pe) throws BadElementException, IOException {
    Document documento = new Document();
    Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    //Font fontRojo = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.RED); // Agrega texto rojo

    try {
        String ruta = System.getProperty("user.home");
        PdfWriter.getInstance(documento, new FileOutputStream(ruta + "/Desktop/Recibo.pdf"));

        CustomHeader event = new CustomHeader(pe.getId_notarec());
        PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(ruta + "/Desktop/Recibo.pdf"));
        writer.setPageEvent(event);

        documento.open();
        
        ////////////////////
            String rutaImagen = "C:\\Users\\juanc\\OneDrive\\Documentos\\NetBeansProjects\\DatOptimize\\src\\img\\logo.png";

            Image imagen = Image.getInstance(rutaImagen);
            imagen.setAlignment(Element.ALIGN_RIGHT);
            documento.add(imagen);
        /////////////////////
        

        String sql = "SELECT p.id_pendientes, p.causante, p.estado_Maquina, p.fecha_Revision, p.id_notaRec, "
                + "n.nom_Clientes, n.nom_maquina, e.nom_empleado, pz.nom_pieza, "
                + "p.Total, p.Piezas "
                + "FROM pendientes p "
                + "INNER JOIN notarec n ON p.id_notaRec = n.id_notaRec "
                + "INNER JOIN empleados e ON p.id_empleado = e.id_empleado "
                + "INNER JOIN piezas pz ON p.id_pieza = pz.id_pieza "
                + "WHERE p.id_notaRec = ?";

        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);
           ps.setInt(1, pe.getId_notarec());  // Cambié de pe.getId_pendientes() a pe.getId_notarec()
            rs = ps.executeQuery();

            while (rs.next()) {
                //documento.add(new Paragraph("Folio: " + rs.getInt(1)+" ", fontRojo));
                documento.add(new Paragraph("Causa: " + rs.getString(2), font));
                documento.add(new Paragraph("Estado Maquina(s): " + rs.getString(3), font));
                documento.add(new Paragraph("Fecha Revisión: " + rs.getString(4), font));
                documento.add(new Paragraph("Cliente: " + rs.getString(6), font));
                documento.add(new Paragraph("Maquina: " + rs.getString(7), font));
                documento.add(new Paragraph("Le reviso: " + rs.getString(8), font));
                documento.add(new Paragraph("Total: $" + rs.getString(10), font));
                documento.add(new Paragraph("Pieza(s) Usadas: " + rs.getString(11), font));

                documento.add(Chunk.NEWLINE);
            }

        } catch (DocumentException | SQLException e) {
                e.printStackTrace(); // Manejo adecuado de la excepción, puedes personalizar esto según tus necesidades
            }

            // Cierra el documento después de agregar contenido
            documento.close();
            //JOptionPane.showMessageDialog(null, "Reporte Creado.");
            return true;
        } catch (DocumentException | HeadlessException | FileNotFoundException e) {
            e.printStackTrace(); // Manejo adecuado de la excepción, puedes personalizar esto según tus necesidades
            return false;
        }
}

 public pendientes obtenerUltimoRecibo() {
    String sql = "SELECT p.id_pendientes, p.causante, p.estado_Maquina, p.fecha_Revision, p.id_notaRec, "
            + "n.nom_Clientes, n.nom_maquina, e.nom_empleado, pz.nom_pieza, "
            + "p.Total, p.Piezas "
            + "FROM pendientes p "
            + "INNER JOIN notarec n ON p.id_notaRec = n.id_notaRec "
            + "INNER JOIN empleados e ON p.id_empleado = e.id_empleado "
            + "INNER JOIN piezas pz ON p.id_pieza = pz.id_pieza "
            + "ORDER BY p.id_pendientes DESC LIMIT 1";

    try {
        con = cn.conectar();
        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();

        if (rs.next()) {
            pendientes lastPendientes = new pendientes();
            lastPendientes.setId_pendientes(rs.getInt(1));
            lastPendientes.setCausante(rs.getString(2));
            lastPendientes.setEstado_Maquina(rs.getString(3));
            lastPendientes.setFecha_Revision(rs.getString(4));
            lastPendientes.setId_notarec(rs.getInt(5));
            lastPendientes.setCliente(rs.getString(6));
            lastPendientes.setMaquina(rs.getString(7));
            lastPendientes.setEmpleado(rs.getString(8));
            //lastPendientes.setNom_pieza(rs.getString(9));
            lastPendientes.setTotal(rs.getDouble(10));
            lastPendientes.setPiezas(rs.getString(11));

            return lastPendientes;
        }
    } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return null;
    }
 
  public boolean editarPendientes(pendientes pe) {
    String sql = "update pendientes set causante=?, estado_Maquina=?, fecha_Revision=?, Total=?, id_notaRec=?, id_empleado=? where id_pendientes=?";

    try {
        con = cn.conectar();
        ps = con.prepareStatement(sql);

        ps.setString(1, pe.getCausante());
        ps.setString(2, pe.getEstado_Maquina());
        ps.setString(3, pe.getFecha_Revision());
        ps.setDouble(4, pe.getTotal());
        ps.setInt(5, pe.getId_notarec());
        ps.setInt(6, pe.getId_empleado());
        
        ps.setInt(7, pe.getId_pendientes());

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
  
  public boolean editarEstadoMaquina(pendientes pe) {
    String sql = "update pendientes set estado_Maquina=? where id_pendientes=?";

    try {
        con = cn.conectar();
        ps = con.prepareStatement(sql);

        ps.setString(1, pe.getEstado_Maquina());
        ps.setInt(2, pe.getId_pendientes());

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
