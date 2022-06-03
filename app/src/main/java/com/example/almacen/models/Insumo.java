package com.example.almacen.models;

public class Insumo {
    private String id;
    private String nombre;
    private String descripcion;
    private String marca;
    private int existencia;
    private int cant_minima;
    private String imagen;

    public Insumo(String id, String nombre, String marca, int existencia, int cant_minima, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.existencia = existencia;
        this.cant_minima = cant_minima;
        this.imagen = imagen;
    }

    public Insumo(String id, String nombre, String descripcion, String marca, int existencia, int cant_minima, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.existencia = existencia;
        this.cant_minima = cant_minima;
        this.imagen = imagen;
    }

    public Insumo(String imagen, String nombre, String descripcion, int existencia) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.existencia = existencia;
    }

    public Insumo() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }

    public int getCant_minima() {
        return cant_minima;
    }

    public void setCant_minima(int cant_minima) {
        this.cant_minima = cant_minima;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
