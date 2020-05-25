package com.example.pruebafirebase.model;

public class Garantia {
    private String uid;
    private String producto;
    private String tiempo;
    private String condiciones;
    private String tienda;
    private String id_usuario;

    public Garantia() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    public String getTienda() {
        return tienda;
    }

    public void setTienda(String tienda) {
        this.tienda = tienda;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String toString() {
        return "Producto: "+producto+"\n"
                +"Tiempo: "+tiempo+"\n"
                +"Condiciones: "+condiciones+"\n"
                +"Tienda: "+tienda
                ;
    }
}
