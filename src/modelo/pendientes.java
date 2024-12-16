
package modelo;

import java.util.List;

/**
 *
 * @author juanc
 */
public class pendientes {
    int id_pendientes;
    String causante;
    String estado_Maquina;
    String fecha_Revision;
    double Total;
    
    int id_notarec;
    String Cliente;
    String Maquina;
    
    int id_empleado;
    String empleado;
    
    int id_Pieza;
    String Piezas;

    public pendientes() {
    }

    public pendientes(int id_pendientes, String causante, String estado_Maquina, String fecha_Revision, double Total, int id_notarec, String Cliente, String Maquina, int id_empleado, String empleado,int id_Pieza, String Piezas) {
        this.id_pendientes = id_pendientes;
        this.causante = causante;
        this.estado_Maquina = estado_Maquina;
        this.fecha_Revision = fecha_Revision;
        this.Total = Total;
        this.id_notarec = id_notarec;
        this.Cliente = Cliente;
        this.Maquina = Maquina;
        this.id_empleado = id_empleado;
        this.empleado = empleado;
        this.id_Pieza = id_Pieza;
        this.Piezas = Piezas;
    }

    public int getId_pendientes() {
        return id_pendientes;
    }

    public void setId_pendientes(int id_pendientes) {
        this.id_pendientes = id_pendientes;
    }

    public String getCausante() {
        return causante;
    }

    public void setCausante(String causante) {
        this.causante = causante;
    }

    public String getEstado_Maquina() {
        return estado_Maquina;
    }

    public void setEstado_Maquina(String estado_Maquina) {
        this.estado_Maquina = estado_Maquina;
    }

    public String getFecha_Revision() {
        return fecha_Revision;
    }

    public void setFecha_Revision(String fecha_Revision) {
        this.fecha_Revision = fecha_Revision;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double Total) {
        this.Total = Total;
    }

    public int getId_notarec() {
        return id_notarec;
    }

    public void setId_notarec(int id_notarec) {
        this.id_notarec = id_notarec;
    }

    public String getCliente() {
        return Cliente;
    }

    public void setCliente(String Cliente) {
        this.Cliente = Cliente;
    }

    public String getMaquina() {
        return Maquina;
    }

    public void setMaquina(String Maquina) {
        this.Maquina = Maquina;
    }

    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }
    
    

    public int getId_Pieza() {
        return id_Pieza;
    }

    public void setId_Pieza(int id_Pieza) {
        this.id_Pieza = id_Pieza;
    }

    public String getPiezas() {
        return Piezas;
    }

    public void setPiezas(String Piezas) {
        this.Piezas = Piezas;
    }
    
    
}
