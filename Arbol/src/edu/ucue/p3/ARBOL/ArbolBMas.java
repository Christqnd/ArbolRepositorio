/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucue.p3.ARBOL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author khrist
 */
public class ArbolBMas {

    private RandomAccessFile file;
    private int nRegistros;
    private final int tamRegistros = 101;
    private final int inicio = 10;

    public ArbolBMas(File archivo) {
        crearArchivoArbol(archivo);
    }

    private void crearArchivoArbol(File archivo) {
        try {
            file = new RandomAccessFile(archivo, "rw");
            nRegistros = (int) Math.ceil((double) file.length() / (double) tamRegistros);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }

    public void cerrar() {
        try {
            file.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public void agregarObjeto(String clave, int valor) throws IOException {
        try {
            if (buscarClave(clave) == -1) {
                throw new ClaveNodoDuplicadaException();
            }
        } catch (ClaveNodoDuplicadaException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            agregarElemento(clave, valor);
        }
    }

    public int buscarClave(String clave) throws IOException {
        if (nRegistros <= 0) {
            return -1;
        }
        return buscar(recuperarRegistro(getPosicionRaiz()), clave);
    }

    public int getPosicionRaiz() throws IOException {
        file.seek(1);
        int retorno = file.readInt();
        return retorno;
    }

    public void setPosicionRaiz(int posicionRaiz) throws IOException {
        file.seek(1);
        file.writeInt(posicionRaiz);
    }

    public Nodo recuperarRegistro(int i) throws IOException {
        if (i >= 0 && i < nRegistros) {
            Nodo nuevoNodo = new Nodo();
            file.seek(inicio + i * tamRegistros);
            nuevoNodo.setNumeroRegistro(file.readInt());
            nuevoNodo.setNodoHoja(file.readBoolean());
            nuevoNodo.setNumClaves(file.readInt());
            nuevoNodo.setSiguienteNodo(file.readInt());
            String[] claves = new String[3];
            int[] valores = new int[3];
            int[] nodoHijos = new int[4];
            for (int j = 0; j < 3; j++) {
                valores[j] = file.readInt();
            }
            for (int j = 0; j < 4; j++) {
                nodoHijos[j] = file.readInt();
            }
            for (int j = 0; j < 3; j++) {
                claves[j] = file.readUTF();
            }
            nuevoNodo.setClave(claves);
            nuevoNodo.setNodosHijo(nodoHijos);
            nuevoNodo.setValor(valores);
            return nuevoNodo;
        } else {
            JOptionPane.showMessageDialog(null, "Numero de registros fuera de limites");
            return null;
        }
    }

    private int buscar(Nodo nodo, String clave) throws IOException {
        while (nodo != null) {
            int i = 0;
            while (i < nodo.getNumClaves() && clave.compareTo(nodo.getClave()[i]) < 0) {
                i++;
            }
            if (i < nodo.getNumClaves() && clave.equals(nodo.getClave()[i])) {
                return nodo.getValor()[i];
            }
            if (nodo.isNodoHoja()) {
                return -1;
            } else {
                nodo = recuperarRegistro(nodo.getNodosHijo()[i]);
            }
        }
        return -1;
    }

    private void agregarElemento(String clave, int valor) throws IOException {
        Nodo nodoRaiz;
        if (nRegistros == 0) {
            nodoRaiz = new Nodo();
            nodoRaiz.setNodoHoja(true);
            ingresarRegistro(nodoRaiz);
            setPosicionRaiz(nodoRaiz.getNumeroRegistro());
        } else {
            nodoRaiz = getNodoRaiz();
        }
        int antiguoNodoRaiz = getPosicionRaiz();
        if (nodoRaiz.getNumClaves() == 3) {
            Nodo nuevoNodoRaiz = new Nodo();
            nuevoNodoRaiz.setNodoHoja(false);
            nuevoNodoRaiz.getNodosHijo()[0] = antiguoNodoRaiz;
            ingresarRegistro(nuevoNodoRaiz);
            setPosicionRaiz(nuevoNodoRaiz.getNumeroRegistro());
            separarNodos(nuevoNodoRaiz, 0, nodoRaiz);
            verificar(nuevoNodoRaiz, clave, valor);
        } else {
            verificar(nodoRaiz, clave, valor);
        }
    }

    private void ingresarRegistro(Nodo nodo) throws IOException {
        if (ingresar(nRegistros, nodo)) {
            nRegistros++;
        }
    }

    private boolean ingresar(int i, Nodo nodo) throws IOException {
        if (i >= 0 && i <= nRegistros) {
            if (nodo.getTamano() > tamRegistros) {
                JOptionPane.showMessageDialog(null, "Tamanho  de registros excedido");
            } else {
                nodo.setNumeroRegistro(i);
                file.seek(inicio + i * tamRegistros);
                file.writeInt(i);
                file.writeBoolean(nodo.isNodoHoja());
                file.writeInt(nodo.getNumClaves());
                file.writeInt(nodo.getSiguienteNodo());
                for (int val : nodo.getValor()) {
                    file.writeInt(val);
                }
                for (int nHijo : nodo.getNodosHijo()) {
                    file.writeInt(nHijo);
                }
                for (String clav : nodo.getClave()) {
                    if (clav == null) {
                        clav = "";
                    }
                    file.writeUTF(clav);
                }
                return true;
            }
        } else {
            JOptionPane.showMessageDialog(null, "numero del registro esta fuera de limites");
        }
        return false;
    }

    private Nodo getNodoRaiz() throws IOException {
        int pos = getPosicionRaiz();
        return recuperarRegistro(pos);
    }

    private void separarNodos(Nodo nuevoNodoRaiz, int i, Nodo antiguoNodoRaiz) throws IOException {
        Nodo nuevoNodo = new Nodo();
        ingresarRegistro(nuevoNodo);
        nuevoNodo.setNodoHoja(antiguoNodoRaiz.isNodoHoja());
        nuevoNodo.setNumClaves(2);
        for (int j = 0; j < 2; j++) {
            nuevoNodo.getClave()[j] = antiguoNodoRaiz.getClave()[j + 1];
            nuevoNodo.getValor()[j] = antiguoNodoRaiz.getValor()[j + 1];
        }
        if (!nuevoNodo.isNodoHoja()) {
            for (int j = 0; j < 3; j++) {
                nuevoNodo.getNodosHijo()[j] = antiguoNodoRaiz.getNodosHijo()[j + 1];
            }
            for (int j = 2; j <= antiguoNodoRaiz.getNumClaves(); j++) {
                antiguoNodoRaiz.getNodosHijo()[j] = 0;
            }
        } else {
            nuevoNodo.setSiguienteNodo(antiguoNodoRaiz.getSiguienteNodo());
            antiguoNodoRaiz.setSiguienteNodo(nuevoNodo.getNumeroRegistro());
        }
        for (int j = 1; j < antiguoNodoRaiz.getNumClaves(); j++) {
            antiguoNodoRaiz.getClave()[j] = "";
            antiguoNodoRaiz.getValor()[j] = -1;
        }
        antiguoNodoRaiz.setNumClaves(1);
        for (int j = nuevoNodoRaiz.getNumClaves(); j >= i + 1; j--) {
            nuevoNodoRaiz.getNodosHijo()[j + 1] = nuevoNodoRaiz.getNodosHijo()[j];
        }
        nuevoNodoRaiz.getNodosHijo()[i + 1] = nuevoNodo.getNumeroRegistro();
        for (int j = nuevoNodoRaiz.getNumClaves() - 1; j >= i; j--) {
            nuevoNodoRaiz.getClave()[j + 1] = nuevoNodoRaiz.getClave()[j];
            nuevoNodoRaiz.getValor()[j + 1] = nuevoNodoRaiz.getValor()[j];
        }
        nuevoNodoRaiz.getClave()[i] = nuevoNodo.getClave()[0];
        nuevoNodoRaiz.getValor()[i] = nuevoNodo.getValor()[0];
        nuevoNodoRaiz.setNumClaves(nuevoNodoRaiz.getNumClaves() + 1);
        actualizarNodo(nuevoNodo);
        actualizarNodo(antiguoNodoRaiz);
        actualizarNodo(nuevoNodoRaiz);
    }

    private void actualizarNodo(Nodo nodo) throws IOException {
        ingresar(nodo.getNumeroRegistro(), nodo);
    }

    private void verificar(Nodo nodo, String clave, int valor) throws IOException {
        int i = nodo.getNumClaves() - 1;
        if (nodo.isNodoHoja()) {
            while (i >= 0 && clave.compareTo(nodo.getClave()[i]) < 0) {
                nodo.getClave()[i + 1] = nodo.getClave()[i];
                nodo.getValor()[i + 1] = nodo.getValor()[i];
                i--;
            }
            i++;
            nodo.getClave()[i] = clave;
            nodo.getValor()[i] = valor;
            nodo.setNumClaves(nodo.getNumClaves() + 1);
            actualizarNodo(nodo);
        } else {
            while (i >= 0 && clave.compareTo(nodo.getClave()[i]) < 0) {
                i--;
            }
            i++;
            Nodo nodoHijo = recuperarRegistro(nodo.getNodosHijo()[i]);
            if (nodoHijo.getNumClaves() == 3) {
                separarNodos(nodo, i, nodoHijo);
                if (clave.compareTo(nodo.getClave()[i]) > 0) {
                    i++;
                }
            }
            actualizarNodo(nodo);
            actualizarNodo(nodoHijo);
            nodoHijo = recuperarRegistro(nodo.getNodosHijo()[i]);
            verificar(nodoHijo, clave, valor);
        }
    }

    public ArrayList<Integer> obtenerNumRegistro() throws IOException {
        ArrayList<Integer> lista = new ArrayList<>();
        Nodo nodo = recuperarRegistro(getPosicionRaiz());
        while (!nodo.isNodoHoja()) {
            nodo = recuperarRegistro(nodo.getNodosHijo()[0]);
        }
        while (nodo != null) {
            for (int i = 0; i < nodo.getNumClaves(); i++) {
                lista.add(nodo.getValor()[i]);
            }
            if (nodo.getSiguienteNodo() == 0) {
                nodo = null;
            } else {
                nodo = recuperarRegistro(nodo.getSiguienteNodo());
            }
        }
        return lista;
    }

    public String mostrarArbol() throws IOException {
        String string = "";
        Nodo nodo = recuperarRegistro(getPosicionRaiz());
        while (!nodo.isNodoHoja()) {
            nodo = recuperarRegistro(nodo.getNodosHijo()[0]);
        }
        while (nodo != null) {
            for (int i = 0; i < nodo.getNumClaves(); i++) {
                string += nodo.getValor()[i] + ", ";
            }
            if (nodo.getSiguienteNodo() == 0) {
                nodo = null;
            } else {
                nodo = recuperarRegistro(nodo.getSiguienteNodo());
            }
        }
        return string;
    }
}
