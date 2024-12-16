
package modelo;

public class Cambio {
    private int idPieza;
    private int cantidadDecrementada;

    public Cambio(int idPieza, int cantidadDecrementada) {
        this.idPieza = idPieza;
        this.cantidadDecrementada = cantidadDecrementada;
    }

    public int getIdPieza() {
        return idPieza;
    }

    public int getCantidadDecrementada() {
        return cantidadDecrementada;
    }
}
