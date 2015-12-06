/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucue.p3.SERVICIO;

import edu.ucue.p3.DATOS.PersonaDAO;
import edu.ucue.p3.MODELO.Persona;
import java.util.Collection;

/**
 *
 * @author Christian Quinde <christian24091992@gmail.com>
 */
public class PersonaSRV {

    public boolean creaPersona(String nombre, String apellido, String cedula) throws DataException {
        validarDatos(nombre, apellido);
        validarCedula(cedula);
        return PersonaDAO.instancia().create(new Persona(nombre, apellido, cedula));

    }

    public boolean EliminarPersona(String cedula) throws DataException {
        validarCedula(cedula);
        return PersonaDAO.instancia().delete(PersonaDAO.instancia().read(cedula));
    }

    public Persona recuperarPersona(String cedula) throws DataException {
        validarCedula(cedula);
        return PersonaDAO.instancia().read(cedula);
    }

    public boolean modificarPersona(String nombre, String apellido, String cedula) throws DataException {
        validarDatos(nombre, apellido);
        validarCedula(cedula);
        return PersonaDAO.instancia().update(PersonaDAO.instancia().read(cedula), new Persona(nombre, apellido, cedula));
    }
    public Collection<Persona> recuperarListadoPersonas(){
        return PersonaDAO.instancia().getListaPersonas();
    }

    private void validarDatos(String nombre, String apellido) throws DataException {
        if (nombre.equals("") || apellido.equals("")) {
            throw new DataException("Verifique que todos los datos esten ingresados correctamente");
        }
    }

    private void validarCedula(String cedula) throws DataException {
        if (cedula.equals("")) {
            throw new DataException("Verifique que todos los datos esten ingresados correctamente");
        }
    }

}
