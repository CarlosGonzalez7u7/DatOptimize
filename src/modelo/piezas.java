
package modelo;

/**
 *
 * @author juanc
 */
public class piezas {
    int id_Pieza;
    String nom_Pieza;
    String marca_Pieza;
    String modelo_Pieza;
    int cantidad;
    String precio_Pieza;

    public piezas() {
    }

    public piezas(int id_Pieza, String nom_Pieza, String marca_Pieza, String modelo_Pieza, int cantidad, String precio_Pieza) {
        this.id_Pieza = id_Pieza;
        this.nom_Pieza = nom_Pieza;
        this.marca_Pieza = marca_Pieza;
        this.modelo_Pieza = modelo_Pieza;
        this.cantidad = cantidad;
        this.precio_Pieza = precio_Pieza;
    }

    public int getId_Pieza() {
        return id_Pieza;
    }

    public void setId_Pieza(int id_Pieza) {
        this.id_Pieza = id_Pieza;
    }

    public String getNom_Pieza() {
        return nom_Pieza;
    }

    public void setNom_Pieza(String nom_Pieza) {
        this.nom_Pieza = nom_Pieza;
    }

    public String getMarca_Pieza() {
        return marca_Pieza;
    }

    public void setMarca_Pieza(String marca_Pieza) {
        this.marca_Pieza = marca_Pieza;
    }

    public String getModelo_Pieza() {
        return modelo_Pieza;
    }

    public void setModelo_Pieza(String modelo_Pieza) {
        this.modelo_Pieza = modelo_Pieza;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio_Pieza() {
        return precio_Pieza;
    }

    public void setPrecio_Pieza(String precio_Pieza) {
        this.precio_Pieza = precio_Pieza;
    }
    
    
}
