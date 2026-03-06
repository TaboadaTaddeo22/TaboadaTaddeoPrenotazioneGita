/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package taboadataddeoprenotazionegita;

import java.util.*;

/**
 *
 * @author taboada.taddeo
 */
public class Studente {
    // Attributi
    private ArrayList<Integer> idGite;
    private int id;
    private String nome;
    private String cognome;
    

    /**
     * Costruttore di Studente
     * @param id
     * @param nome
     * @param cognome 
     */
    public Studente(int id, String nome, String cognome) {
        this.idGite = new ArrayList<>();
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
    }

    /**
     * Metodo get di id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Metodo get di nome
     * @return nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Metodo get di cognome
     * @return cognome
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Metodo set di id
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Metodo set di nome
     * @param nome 
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Metodo set di cognome
     * @param cognome 
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Override del metodo toString
     * @return Una stringa con gli attributi di Studente
     */
    @Override
    public String toString() {
        return id + " - " + nome + " " + cognome;
    }
}
