
package modelo;

/**
 *
 * @author juanc
 */
public class notas {
    int id_notaRec;
    String nom_Clientes;
    
    String nom_maquina;
    String marca_Maquina;
    String modelo_maquina;
    String fecha;
    String atencion;
    String Observaciones;
    String estado_Maquina;
    
  

    public notas() {
    }

    public notas(int id_notaRec, String nom_Clientes, String nom_maquina, String marca_Maquina, String modelo_maquina, String fecha, String atencion, String Observaciones, String estado_Maquina) {
        this.id_notaRec = id_notaRec;
        this.nom_Clientes = nom_Clientes;
        this.nom_maquina = nom_maquina;
        this.marca_Maquina = marca_Maquina;
        this.modelo_maquina = modelo_maquina;
        this.fecha = fecha;
        this.atencion = atencion;
        this.Observaciones = Observaciones;
        this.estado_Maquina = estado_Maquina;
    }

    public int getId_notaRec() {
        return id_notaRec;
    }

    public void setId_notaRec(int id_notaRec) {
        this.id_notaRec = id_notaRec;
    }

    public String getNom_Clientes() {
        return nom_Clientes;
    }

    public void setNom_Clientes(String nom_Clientes) {
        this.nom_Clientes = nom_Clientes;
    }

    public String getNom_maquina() {
        return nom_maquina;
    }

    public void setNom_maquina(String nom_maquina) {
        this.nom_maquina = nom_maquina;
    }

    public String getMarca_Maquina() {
        return marca_Maquina;
    }

    public void setMarca_Maquina(String marca_Maquina) {
        this.marca_Maquina = marca_Maquina;
    }

    public String getModelo_maquina() {
        return modelo_maquina;
    }

    public void setModelo_maquina(String modelo_maquina) {
        this.modelo_maquina = modelo_maquina;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAtencion() {
        return atencion;
    }

    public void setAtencion(String atencion) {
        this.atencion = atencion;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String Observaciones) {
        this.Observaciones = Observaciones;
    }

    public String getEstado_Maquina() {
        return estado_Maquina;
    }

    public void setEstado_Maquina(String estado_Maquina) {
        this.estado_Maquina = estado_Maquina;
    }

   
    
}
