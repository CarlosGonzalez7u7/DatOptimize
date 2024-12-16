
package modelo;

/**
 *
 * @author juanc
 */
public class empleados {
    int id_empleado;
    String nom_Empleado;
    String fecha_Empleado;
    int id_cargo;
    String Cargo;

    public empleados() {
    }

    public empleados(int id_empleado, String nom_Empleado, String fecha_Empleado, int id_cargo, String Cargo) {
        this.id_empleado = id_empleado;
        this.nom_Empleado = nom_Empleado;
        this.fecha_Empleado = fecha_Empleado;
        this.id_cargo = id_cargo;
        this.Cargo = Cargo;
    }

    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getNom_Empleado() {
        return nom_Empleado;
    }

    public void setNom_Empleado(String nom_Empleado) {
        this.nom_Empleado = nom_Empleado;
    }

    public String getFecha_Empleado() {
        return fecha_Empleado;
    }

    public void setFecha_Empleado(String fecha_Empleado) {
        this.fecha_Empleado = fecha_Empleado;
    }

    public int getId_cargo() {
        return id_cargo;
    }

    public void setId_cargo(int id_cargo) {
        this.id_cargo = id_cargo;
    }

    public String getCargo() {
        return Cargo;
    }

    public void setCargo(String Cargo) {
        this.Cargo = Cargo;
    }
    
    
    
}
