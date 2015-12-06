/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucue.p3.MODELO;

import java.io.Serializable;

/**
 *
 * @author Christian Quinde <christian24091992@gmail.com>
 */
public class Persona implements Serializable, Comparable<Persona> {

    private String nombre;
    private String apellido;
    private String cedula;

    public Persona() {
    }

    public Persona(String nombre, String apellido, String cedula) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the apellido
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * @param apellido the apellido to set
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    @Override
    public String toString() {
        return "Cedula : " + getCedula() + "  Nombre y apellido: " + getNombre() + " " + getApellido() + "\n";
    }

    @Override
    public int compareTo(Persona t) {
        return this.getCedula().compareTo(t.getCedula());
    }

}
