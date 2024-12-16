package Dao;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.cargo;
import modelo.notas;

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
public class DaoNotas {

    Connection con;
    conexion cn = new conexion();
    PreparedStatement ps;
    ResultSet rs;

    ////////////////////////////////////////////////////////
    public boolean insertar(notas n) {
        String sql = "insert into notarec (nom_Clientes, nom_maquina, marca_Maquina, modelo_Maquina, fecha, atencion, observaciones, estado_Maquina ) values (?,?,?,?,?,?,?,?)";
        try {
            con = cn.conectar();
            // Obtener el próximo valor para id_maquina

            ps = con.prepareStatement(sql);

            ps.setString(1, n.getNom_Clientes());
            ps.setString(2, n.getNom_maquina());
            ps.setString(3, n.getMarca_Maquina());
            ps.setString(4, n.getModelo_maquina());
            ps.setString(5, n.getFecha());
            ps.setString(6, n.getAtencion());
            ps.setString(7, n.getObservaciones());
            ps.setString(8, n.getEstado_Maquina());

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

    ///////////////
    public List ListarNotas() {
        List<notas> lista = new ArrayList<>();
        String sql = "select * from notarec";
        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                notas n = new notas();

                n.setId_notaRec(rs.getInt(1));
                n.setNom_Clientes(rs.getString(2));
                n.setNom_maquina(rs.getString(3));
                n.setMarca_Maquina(rs.getString(4));
                n.setModelo_maquina(rs.getString(5));
                n.setFecha(rs.getString(6));
                n.setAtencion(rs.getString(7));
                n.setObservaciones(rs.getString(8));
                n.setEstado_Maquina(rs.getString(9));

                lista.add(n);
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return lista;
    }

    public boolean editarNotas(notas no) {
        String sql = "update notarec set nom_Clientes=?, nom_maquina=?, marca_Maquina=?, modelo_maquina=?, fecha=?, atencion=?, observaciones=?, estado_Maquina=? where id_notaRec=?";

        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);

            ps.setString(1, no.getNom_Clientes());
            ps.setString(2, no.getNom_maquina());
            ps.setString(3, no.getMarca_Maquina());
            ps.setString(4, no.getModelo_maquina());
            ps.setString(5, no.getFecha());
            ps.setString(6, no.getAtencion());
            ps.setString(7, no.getObservaciones());
            ps.setString(8, no.getEstado_Maquina());
            ps.setInt(9, no.getId_notaRec());

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

    public boolean eliminarNotas(notas no) {
        String sql = "delete from notarec where id_notaRec=?";

        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);
            ps.setInt(1, no.getId_notaRec());
            int n = ps.executeUpdate();
            if (n != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "No puedes Eliminar esta nota, ya esta Reparada y Registrada");
            return false;
        }
    }

    public List<notas> BuscarNotas(notas no, boolean buscarPorId) {
    List<notas> resultados = new ArrayList<>();
    String sql = buscarPorId ? "SELECT * FROM notarec WHERE id_notaRec=?" : "SELECT * FROM notarec WHERE nom_Clientes LIKE ?";

    try {
        con = cn.conectar();
        ps = con.prepareStatement(sql);

        if (buscarPorId) {
            ps.setInt(1, no.getId_notaRec());
        } else {
            ps.setString(1, no.getNom_Clientes() + "%");
        }

        rs = ps.executeQuery();

        while (rs.next()) {
            notas nt = new notas();
            nt.setId_notaRec(rs.getInt(1));
            nt.setNom_Clientes(rs.getString(2));
            nt.setNom_maquina(rs.getString(3));
            nt.setMarca_Maquina(rs.getString(4));
            nt.setModelo_maquina(rs.getString(5));
            nt.setFecha(rs.getString(6));
            nt.setAtencion(rs.getString(7));
            nt.setObservaciones(rs.getString(8));
            nt.setEstado_Maquina(rs.getString(9));
            resultados.add(nt);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e);
    } finally {
        // Cerrar recursos (PreparedStatement, ResultSet, etc.) si es necesario
    }
    
    return resultados;
}
   /* public boolean BuscarNotas(notas no, boolean buscarPorId) {
    String sql = buscarPorId ? "SELECT * FROM notarec WHERE id_notaRec=?" : "SELECT * FROM notarec WHERE nom_Clientes LIKE ?";

    try {
        con = cn.conectar();
        ps = con.prepareStatement(sql);

        if (buscarPorId) {
            ps.setInt(1, no.getId_notaRec());
        } else {
            // Para la búsqueda por nombre, agregamos el patrón '%' al final del nombre
            ps.setString(1, no.getNom_Clientes() + "%");
        }

        rs = ps.executeQuery();

        if (rs.next()) {
            no.setId_notaRec(rs.getInt(1));
            no.setNom_Clientes(rs.getString(2));
            no.setNom_maquina(rs.getString(3));
            no.setMarca_Maquina(rs.getString(4));
            no.setModelo_maquina(rs.getString(5));
            no.setFecha(rs.getString(6));
            no.setAtencion(rs.getString(7));
            no.setObservaciones(rs.getString(8));
            no.setEstado_Maquina(rs.getString(9));
            return true;
        } else {
            return false;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e);
        return false;
    }
}*/
   
   
    public boolean ImprimirNota(notas no) throws BadElementException, IOException {
        Document documento = new Document();
        // Fuente para el formato del texto
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
        Font fontRojo = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.RED); // Agrega texto rojo

        try {
            String ruta = System.getProperty("user.home");
            PdfWriter.getInstance(documento, new FileOutputStream(ruta + "/Desktop/Nota_Herramientas.pdf"));

            // Configurar el evento de la página (header)
            CustomHeader event = new CustomHeader(no.getId_notaRec());
            PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(ruta + "/Desktop/Nota_Herramientas.pdf"));
            writer.setPageEvent(event);

            documento.open();

            // Añade una imagen (cambia la ruta de la imagen según tu caso)
            String rutaImagen = "C:\\Users\\juanc\\OneDrive\\Documentos\\NetBeansProjects\\DatOptimize\\src\\img\\logo.png";

            Image imagen = Image.getInstance(rutaImagen);
            imagen.setAlignment(Element.ALIGN_RIGHT);
            documento.add(imagen);

            String sql = "SELECT * FROM notarec WHERE id_notaRec=?";
            try {
                con = cn.conectar();
                ps = con.prepareStatement(sql);
                ps.setInt(1, no.getId_notaRec());
                rs = ps.executeQuery();

                while (rs.next()) {
                    // Agrega cada conjunto de datos en forma de lista
                    //documento.add(new Paragraph("No_ " + rs.getInt(1)+" ", fontRojo));
                    documento.add(new Paragraph("Cliente: " + rs.getString(2), font));
                    documento.add(new Paragraph("Nombre Maquina(s): " + rs.getString(3), font));
                    documento.add(new Paragraph("Marca: " + rs.getString(4), font));
                    documento.add(new Paragraph("Modelo: " + rs.getString(5), font));
                    documento.add(new Paragraph("Fecha: " + rs.getString(6), font));
                    documento.add(new Paragraph("Tipo de Servicio: " + rs.getString(7), font));
                    documento.add(new Paragraph("Observaciones: " + rs.getString(8), font));
                    documento.add(new Paragraph("Estado Maquina(s): " + rs.getString(9), font));

                    // Agrega un espacio en blanco entre cada conjunto de datos
                    documento.add(Chunk.NEWLINE);
                }

            } catch (DocumentException | SQLException e) {
                e.printStackTrace(); // Manejo adecuado de la excepción, puedes personalizar esto según tus necesidades
            }

            // Cierra el documento después de agregar contenido
            documento.close();
            JOptionPane.showMessageDialog(null, "Reporte Creado.");
            return true;
        } catch (DocumentException | HeadlessException | FileNotFoundException e) {
            e.printStackTrace(); // Manejo adecuado de la excepción, puedes personalizar esto según tus necesidades
            return false;
        }
    }

    public notas obtenerUltimaNota() {
        String sql = "SELECT * FROM notarec ORDER BY id_notaRec DESC LIMIT 1";

        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                notas lastNota = new notas();
                lastNota.setId_notaRec(rs.getInt(1));
                lastNota.setNom_Clientes(rs.getString(2));
                lastNota.setNom_maquina(rs.getString(3));
                lastNota.setMarca_Maquina(rs.getString(4));
                lastNota.setModelo_maquina(rs.getString(5));
                lastNota.setFecha(rs.getString(6));
                lastNota.setAtencion(rs.getString(7));
                lastNota.setObservaciones(rs.getString(8));
                lastNota.setEstado_Maquina(rs.getString(9));

                return lastNota;
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return null;
    }

    public boolean insertarFilaResaltada(int idNotaRec, int indiceFila) {
        String sql = "INSERT INTO filas_resaltadas (id_notaRec, indice_fila) VALUES (?, ?)";

        try {
            con = cn.conectar();
            ps = con.prepareStatement(sql);

            ps.setInt(1, idNotaRec);
            ps.setInt(2, indiceFila);

            int n = ps.executeUpdate();

            return n != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*public Set<Integer> cargarFilasResaltadas(int idNotaRec) {
    Set<Integer> filasResaltadas = new HashSet<>();
    String sql = "SELECT indice_fila FROM filas_resaltadas WHERE id_notaRec = ?";
    
    try {
        con = cn.conectar();
        ps = con.prepareStatement(sql);
        ps.setInt(1, idNotaRec);
        
        rs = ps.executeQuery();
        
        while (rs.next()) {
            int indiceFila = rs.getInt("indice_fila");
            filasResaltadas.add(indiceFila);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } 
    
    return filasResaltadas;
}*/
}
