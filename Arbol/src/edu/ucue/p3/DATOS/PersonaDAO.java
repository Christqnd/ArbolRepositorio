/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucue.p3.DATOS;

import edu.ucue.p3.ARBOL.ArbolBMas;
import edu.ucue.p3.ARBOL.ArbolBMasOrd;
import edu.ucue.p3.ARBOL.ClaveNodoDuplicadaException;
import edu.ucue.p3.MODELO.Persona;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Quinde <christian24091992@gmail.com>
 */
public class PersonaDAO {

    private final Collection<Persona> listaPersonas;
    private static PersonaDAO instancia;
    //private ArbolBMas ab;
    private ArbolBMasOrd abo;
    private RandomAccessFile file;
    private final int dimRegistro = 140;
    private int numeroRegistros;

    private PersonaDAO() {
        iniciar();
        this.abo = new ArbolBMasOrd(new File("src\\edu\\ucue\\p3\\ARCHIVOS\\arbolPersonas.dat"));
        listaPersonas = new TreeSet<>();
    }

    private void iniciar() {
        crearArchivo(new File("src\\edu\\ucue\\p3\\ARCHIVOS\\Personas.dat"));
    }

    public void crearArchivo(File archivo) {
        if (!archivo.exists()) {
            try {
                file = new RandomAccessFile(archivo, "rw");
                PersonaDAO.instancia().personas();
                numeroRegistros = (int) Math.ceil((double) file.length() / (double) dimRegistro);
            } catch (FileNotFoundException ex) {
                System.out.println("El Archivo no Existe : "+ex.getMessage());
            } catch (IOException ex) {
                System.out.println("Error IOException: "+ex.getMessage());
            } catch (PersonaInvalidaException ex) {
                System.out.println("Error PersonaInvalidaException: "+ex.getMessage());
            } catch (ClaveNodoDuplicadaException ex) {
                System.out.println("Error ClaveNodoDuplicadaException: "+ex.getMessage());
            }
        } else {
            try {
                file = new RandomAccessFile(archivo, "rw");
                numeroRegistros = (int) Math.ceil((double) file.length() / (double) dimRegistro);
            } catch (FileNotFoundException ex) {
                System.out.println("El Archivo no Existe");
            } catch (IOException ex) {
                System.out.println("Error en el Archivo");
            }
        }
    }

    public void cerrar() {
        if (file != null) {
            try {
                file.close();
            } catch (IOException ex) {
                System.out.println("Error en el Archivo");
            }
        }
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

    private void personas() throws PersonaInvalidaException, ClaveNodoDuplicadaException {
        ingresarPersona(new Persona("Juan", "Quito", "0919234387"), 0);
        ingresarPersona(new Persona("Estevan", "Bernal", "0919283726"), 0);
        ingresarPersona(new Persona("Pedro", "Torres", "1234567890"), 0);
        ingresarPersona(new Persona("Joel", "Loja", "1231231231"), 0);
        ingresarPersona(new Persona("Daniel", "Toral", "4564564564"), 0);
        ingresarPersona(new Persona("Carlos", "Castro", "6786786788"), 0);
        ingresarPersona(new Persona("Mike", "Novoa", "2349823545"), 0);
        
        try {
            System.out.println("\t\tArbol ordenado \n"+getArbolBmas().mostrarArbol());
        } catch (IOException ex) {
            Logger.getLogger(PersonaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ingresarPersona(Persona persona, int i) throws PersonaInvalidaException, ClaveNodoDuplicadaException {
        if (ingresar(numeroRegistros, persona, i)) {
            numeroRegistros++;
        }
    }

    private boolean ingresar(int i, Persona cliente, int j) throws PersonaInvalidaException, ClaveNodoDuplicadaException {
        if (buscarPersona(cliente.getCedula()) != null & j == 0) {
            throw new PersonaInvalidaException("La persona ya existe");
        }
        if (i >= 0 && i <= numeroRegistros) {
            try {
                file.seek(i * dimRegistro);
                file.writeUTF(cliente.getCedula());
                String nombre = rellenar(cliente.getNombre(), 30);
                file.writeUTF(nombre);
                String apellido = rellenar(cliente.getApellido(), 30);
                file.writeUTF(apellido);
                if (j == 0) {
                    getArbolBmas().agregarObjeto(cliente.getCedula(), cliente.getApellido(), numeroRegistros);
                }
                return true;
            } catch (IOException ex) {
                System.out.println("Error en el Archivo");
            }
        } else {
            System.out.println("Numero de Registros fuera de Limite");
        }
        return false;
    }

    public String rellenar(String str, int i) {
        int a = i - str.length();
        for (int j = 0; j < a; j++) {
            str += " ";
        }
        return str;
    }

    public ArbolBMasOrd getArbolBmas() {
        return abo; 
    }

    public Persona recuperar(int i) {
        if (i >= 0 && i <= numeroRegistros) {
            try {
                file.seek(i * dimRegistro);
                Persona m = new Persona();
                m.setCedula(file.readUTF());
                m.setNombre(file.readUTF());
                m.setApellido(file.readUTF());
                return m;
            } catch (IOException ex) {
                System.out.println("Error en el Archivo");
                return null;
            }
        } else {
            System.out.println("Numero de Registros fuera de Limite");
            return null;
        }
    }

    public Persona buscarPersona(String cedula) {
        for (int i = 0; i < numeroRegistros; i++) {
            try {
                file.seek(i * dimRegistro);
                if (recuperar(i).getCedula().equals(cedula)) {
                    return recuperar(i);
                }
            } catch (IOException ex) {
                System.out.println("Error en el Archivo");
            }
        }
        return null;
    }

}
