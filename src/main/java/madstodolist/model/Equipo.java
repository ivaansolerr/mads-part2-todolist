package madstodolist.model;

public class Equipo {
    private String nombre;

    public Equipo(String n){
        this.nombre = n;
    }

    public String getNombre(){
        return this.nombre;
    }

    public void setNombre(String n){
        this.nombre = n;
    }
}
