/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucue.p3.ARBOL;

/**
 *
 * @author Christian Quinde <christian24091992@gmail.com>
 */
public class Nodo {

    private int numClaves = 0;
    private String[] clave = new String[3];
    private String[] obj = new String[3];
    private int[] valor = new int[3];
    private int[] nodosHijo = new int[4];
    private boolean nodoHoja;
    private int siguienteNodo;
    private int numeroRegistro;

    public Nodo() {
    }

    public int getTamano() {
        return 30;
    }

    /**
     * @return the numClaves
     */
    public int getNumClaves() {
        return numClaves;
    }

    /**
     * @param numClaves the numClaves to set
     */
    public void setNumClaves(int numClaves) {
        this.numClaves = numClaves;
    }

    /**
     * @return the clave
     */
    public String[] getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String[] clave) {
        this.clave = clave;
    }

    /**
     * @return the obj
     */
    public String[] getObj() {
        return obj;
    }

    /**
     * @param obj the obj to set
     */
    public void setObj(String[] obj) {
        this.obj = obj;
    }

    /**
     * @return the valor
     */
    public int[] getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(int[] valor) {
        this.valor = valor;
    }

    /**
     * @return the nodosHijo
     */
    public int[] getNodosHijo() {
        return nodosHijo;
    }

    /**
     * @param nodosHijo the nodosHijo to set
     */
    public void setNodosHijo(int[] nodosHijo) {
        this.nodosHijo = nodosHijo;
    }

    /**
     * @return the nodoHoja
     */
    public boolean isNodoHoja() {
        return nodoHoja;
    }

    /**
     * @param nodoHoja the nodoHoja to set
     */
    public void setNodoHoja(boolean nodoHoja) {
        this.nodoHoja = nodoHoja;
    }

    /**
     * @return the siguienteNodo
     */
    public int getSiguienteNodo() {
        return siguienteNodo;
    }

    /**
     * @param siguienteNodo the siguienteNodo to set
     */
    public void setSiguienteNodo(int siguienteNodo) {
        this.siguienteNodo = siguienteNodo;
    }

    /**
     * @return the numeroRegistro
     */
    public int getNumeroRegistro() {
        return numeroRegistro;
    }

    /**
     * @param numeroRegistro the numeroRegistro to set
     */
    public void setNumeroRegistro(int numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

}
