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
/**
 * 
 * @author Christian Quinde <christian24091992@gmail.com>
 */

public class ArbolBMasOrd {
    private RandomAccessFile file;
    private int nRegistros;
    private final int tamRegistros = 235;
    private final int inicio=10;

    public ArbolBMasOrd(File archivo) {
        crearArchivoArbol(archivo);
    }
    
    private void crearArchivoArbol(File archivo){
        try {
            file = new RandomAccessFile(archivo, "rw");
            nRegistros = (int) Math.ceil((double)file.length()/(double)tamRegistros);
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no Existe");
        } catch (IOException ex) {
            System.out.println("Error en el Archivo");
        }
    }
    
    public void cerrar(){
        try {
            file.close();
        } catch (IOException ex) {
            System.out.println("Error de Archivo");
        }
    }
    
    public boolean ingresar(int i, Nodo nodo) throws IOException{
        if (i>= 0 && i<= nRegistros){
            if(nodo.getTamano() > tamRegistros){
                System.out.println("\nTamanio de registro excedido");
            }else{
                nodo.setNumeroRegistro(i);
                file.seek(inicio+i*tamRegistros);
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
                    if (clav==null)
                        clav="";
                    file.writeUTF(clav);
                }
                for (String ord : nodo.getObj()) {
                    if (ord==null)
                        ord="";
                    file.writeUTF(ord);
                }
                return true;                
            }
        }else{
            System.out.println("\nNumero del registro esta fuera de limites");
        }
        return false;
    }
    public void ingresarRegistro(Nodo nodo) throws IOException{
        if(ingresar(nRegistros, nodo)){
            nRegistros++;
        }
    }
    
    public Nodo recuperarRegistro(int posicion ) throws IOException{
        if(posicion>=0 && posicion<nRegistros){
            Nodo nuevoNodo=new Nodo();
            file.seek(inicio+posicion*tamRegistros);
            nuevoNodo.setNumeroRegistro(file.readInt());
            nuevoNodo.setNodoHoja(file.readBoolean());
            nuevoNodo.setNumClaves(file.readInt());
            nuevoNodo.setSiguienteNodo(file.readInt());
            String[] claves = new String[3];
            String[] obj = new String[3];
            int[] valores = new int[3];
            int[] nodoHijos = new int[4];
            for(int j=0;j<(3);j++){
                valores[j]=file.readInt();
            }
            for(int j=0;j<(4);j++){
                nodoHijos[j]=file.readInt();
            }
            for(int j=0;j<(3);j++){
                claves[j]=file.readUTF();
            }
            for (int j=0;j<(3);j++) {
                obj[j]=file.readUTF();
            }
            nuevoNodo.setClave(claves);
            nuevoNodo.setNodosHijo(nodoHijos);
            nuevoNodo.setValor(valores);
            nuevoNodo.setObj(obj);
            return nuevoNodo;
        }else {
            System.out.println("Numero de registro fuera de limites");
            return null;
        }
    }
    
    public void agregarObjeto(String clave_cedula, String ord, int valor) throws ClaveNodoDuplicadaException, IOException{
        try {
            if(buscarClave(clave_cedula)==-1)
                throw new ClaveNodoDuplicadaException();       
        }catch(IOException | ClaveNodoDuplicadaException ex){
            agregarElemento(clave_cedula, ord, valor);
        }
    }
    
    public void agregarElemento(String clave, String ord, int valor) throws IOException {
        Nodo nodoRaiz;
        if(nRegistros==0){
            nodoRaiz=new Nodo();
            nodoRaiz.setNodoHoja(true);
            ingresarRegistro(nodoRaiz);
            setPosicionRaiz(nodoRaiz.getNumeroRegistro());
        }else{                    
            nodoRaiz = getNodoRaiz();
        }
        int antiguoNodoRaiz= getPosicionRaiz();
        if (nodoRaiz.getNumClaves() == 3) {
            Nodo nuevoNodoRaiz = new Nodo();
            nuevoNodoRaiz.setNodoHoja(false);
            nuevoNodoRaiz.getNodosHijo()[0] = antiguoNodoRaiz;
            ingresarRegistro(nuevoNodoRaiz);
            setPosicionRaiz(nuevoNodoRaiz.getNumeroRegistro());
            separarNodos(nuevoNodoRaiz, 0, nodoRaiz);
            verificar(nuevoNodoRaiz, clave, ord, valor);
        } else {
            verificar(nodoRaiz, clave, ord, valor); 
        }
    }
    
    public int getPosicionRaiz() throws IOException{
        file.seek(1);
        int retorno= file.readInt();
        return retorno;
    }
    
    public void setPosicionRaiz(int posicionRaiz) throws IOException{
        file.seek(1);            
        file.writeInt(posicionRaiz);
    }
    
    private Nodo getNodoRaiz() throws IOException{
        int pos=getPosicionRaiz();
        return recuperarRegistro(pos);
    }
    
    public void separarNodos(Nodo nuevoNodoRaiz, int i, Nodo antiguoNodoRaiz) throws IOException {
        Nodo nuevoNodo = new Nodo();
        ingresarRegistro(nuevoNodo);
        nuevoNodo.setNodoHoja(antiguoNodoRaiz.isNodoHoja());
        nuevoNodo.setNumClaves(2);
        for (int j = 0; j < 2; j++) {
            nuevoNodo.getClave()[j] = antiguoNodoRaiz.getClave()[j + 1];
            nuevoNodo.getValor()[j] = antiguoNodoRaiz.getValor()[j + 1];
            nuevoNodo.getObj()[j] = antiguoNodoRaiz.getObj()[j+1];
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
            antiguoNodoRaiz.getObj()[j] = "";
        }
        antiguoNodoRaiz.setNumClaves(1);
        for (int j = nuevoNodoRaiz.getNumClaves(); j >= i + 1; j--) {
            nuevoNodoRaiz.getNodosHijo()[j + 1] = nuevoNodoRaiz.getNodosHijo()[j];
        }
        nuevoNodoRaiz.getNodosHijo()[i + 1] = nuevoNodo.getNumeroRegistro();	
        for (int j = nuevoNodoRaiz.getNumClaves() - 1; j >= i; j--) {
            nuevoNodoRaiz.getClave()[j + 1] = nuevoNodoRaiz.getClave()[j];
            nuevoNodoRaiz.getValor()[j + 1] = nuevoNodoRaiz.getValor()[j];
            nuevoNodoRaiz.getObj()[j + 1] = nuevoNodoRaiz.getObj()[j];
        }
        nuevoNodoRaiz.getClave()[i] = nuevoNodo.getClave()[0];
        nuevoNodoRaiz.getValor()[i] = nuevoNodo.getValor()[0];
        nuevoNodoRaiz.getObj()[i] = nuevoNodo.getObj()[0];
        nuevoNodoRaiz.setNumClaves(nuevoNodoRaiz.getNumClaves()+1);
        actualizarNodo(nuevoNodo);
        actualizarNodo(antiguoNodoRaiz);
        actualizarNodo(nuevoNodoRaiz);
    }
    
    private void actualizarNodo(Nodo nodo) throws IOException{
        ingresar(nodo.getNumeroRegistro(),nodo);
    }
    
    public void verificar(Nodo nodo, String clave, String ord, int valor) throws IOException {
        int i = nodo.getNumClaves() - 1;
        if (nodo.isNodoHoja()) {
            while (i >= 0 && ord.compareTo(nodo.getObj()[i]) <0) {
                nodo.getClave()[i + 1] = nodo.getClave()[i];
                nodo.getValor()[i + 1] = nodo.getValor()[i];
                nodo.getObj()[i + 1] = nodo.getObj()[i];
                i--;
            }
            i++;
            nodo.getClave()[i] = clave;
            nodo.getValor()[i] = valor;
            nodo.getObj()[i] = ord;
            nodo.setNumClaves(nodo.getNumClaves()+1);
            actualizarNodo(nodo);
        } else {
            while (i >= 0 && ord.compareTo(nodo.getObj()[i]) <0) {
                i--;
            }
            i++;
            Nodo nodoHijo= recuperarRegistro(nodo.getNodosHijo()[i]); 
            if (nodoHijo.getNumClaves() == (3)) {
                separarNodos(nodo, i,nodoHijo);
                if (ord.compareTo(nodo.getObj()[i])>0) {
                    i++;
                }
            }
            actualizarNodo(nodo);
            actualizarNodo(nodoHijo);
            nodoHijo= recuperarRegistro(nodo.getNodosHijo()[i]);
            verificar(nodoHijo, clave, ord, valor);
        }
    }	
	
    public int buscar(Nodo nodo, String clave) throws IOException{
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
	
    public int buscarClave(String clave_cedula) throws IOException{
        if (nRegistros<=0) {
            return -1;
        }
        return buscar(recuperarRegistro(getPosicionRaiz()), clave_cedula);
    }
    
    public ArrayList<Integer> obtenerNumRegistro() throws IOException{
        ArrayList<Integer> lista= new ArrayList<>();
        Nodo nodo = recuperarRegistro(getPosicionRaiz());		
        while (!nodo.isNodoHoja()) {
            nodo = recuperarRegistro(nodo.getNodosHijo()[0]);
        }		
        while (nodo != null) {
            for (int i = 0; i < nodo.getNumClaves(); i++) {
                lista.add(nodo.getValor()[i]);
            }
            if (nodo.getSiguienteNodo()==0)
                nodo=null;
            else 
                nodo = recuperarRegistro(nodo.getSiguienteNodo());
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
                string += nodo.getValor()[i]+", ";
            }
            if (nodo.getSiguienteNodo()==0)
                nodo=null;
            else 
                nodo = recuperarRegistro(nodo.getSiguienteNodo());
        }
        return string;
    }
}
