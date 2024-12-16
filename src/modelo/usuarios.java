
package modelo;
public class usuarios {
  
    int idUser;
    String nombre;
    String usuraio;
    String password;

    public usuarios() {
    }

    public usuarios(int idUser, String nombre, String usuraio, String password) {
        this.idUser = idUser;
        this.nombre = nombre;
        this.usuraio = usuraio;
        this.password = password;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuraio() {
        return usuraio;
    }

    public void setUsuraio(String usuraio) {
        this.usuraio = usuraio;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
