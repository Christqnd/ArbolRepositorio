/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucue.p3.DATOS;

import edu.ucue.p3.MODELO.Persona;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Christian Quinde <christian24091992@gmail.com>
 */
public class PersonaDAO {

    private final Collection<Persona> listaPersonas;
    private static PersonaDAO instancia;

    private PersonaDAO() {
        listaPersonas = new TreeSet<>();
    }

    public static PersonaDAO instancia() {
        if (instancia == null) {
            instancia = new PersonaDAO();
        }
        return instancia;
    }

    public boolean create(Persona persona) {
        return listaPersonas.add(persona);
    }

    public boolean update(Persona personaAntigua, Persona personaNueva) {
        for (Persona persona : listaPersonas) {
            if (persona.equals(personaAntigua)) {
                persona = personaNueva;
                return true;
            }
        }
        return false;
    }

    public Persona read(String cedula) {
        for (Persona persona : listaPersonas) {
            if (persona.getCedula().equalsIgnoreCase(cedula)) {
                return persona;
            }
        }
        return null;
    }

    public boolean delete(Persona persona) {
        return listaPersonas.remove(persona);
    }

    public Collection<Persona> getListaPersonas() {
        return listaPersonas;
    }
    

}
