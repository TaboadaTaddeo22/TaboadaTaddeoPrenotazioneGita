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
public class Gita {
    // Attributi
    private int id;
    private String luogo;
    private ArrayList<Studente> listaStudenti;
    private HashSet<Integer> insiemeMatricole;
    private HashMap<Integer, Studente> mappaStudenti;

    /**
     * Costruttore di Anagrafe
     */
    public Gita(int id, String luogo) {
        this.id = id;
        this.luogo = luogo;
        listaStudenti = new ArrayList<>();
        insiemeMatricole = new HashSet<>();
        mappaStudenti = new HashMap<>();
    }

    /**
     * Metodo che aggiunge uno studente
     * @param s lo studente da aggiungere
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean aggiungiStudente(Studente s) {
        if (s == null || insiemeMatricole.contains(s.getId())) {
            return false;
        }
        listaStudenti.add(s);
        insiemeMatricole.add(s.getId());
        mappaStudenti.put(s.getId(), s);
        return true;
    }

    /**
     * Metodo che rimuove uno studente usando la matricola
     * @param id l'id dello studente da rimuovere
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean eliminaStudente(int id) {
        if (!insiemeMatricole.contains(id)) {
            return false;
        }
        Studente s = mappaStudenti.remove(id);
        insiemeMatricole.remove(id);
        listaStudenti.remove(s);
        return true;
    }
    
    /**
     * Metodo che rimuove tutti gli studenti dall'anagrafe
     */
    public void svuota() {
        listaStudenti.clear();
        insiemeMatricole.clear();
        mappaStudenti.clear();
    }

    /**
     * Metodo che restituisce uno studente
     * @param id l'id dello studente
     * @return lo studente
     */
    public Studente cercaStudente(int id) {
        return mappaStudenti.get(id);
    }

    /**
     * Metodo get di listaStudenti che la restituisce sottoforma di Collection
     * @return listaStudenti
     */
    public Collection<Studente> getTuttiStudenti() {
        return Collections.unmodifiableList(listaStudenti);
    }

    /**
     * Metodo che restituisce il numero di studenti
     * @return la dimensione di listaStudenti
     */
    public int numeroStudenti() {
        return listaStudenti.size();
    }

    /**
     * Metodo get di listaStudenti
     * @return listaStudenti
     */
    public ArrayList<Studente> getListaStudenti() {
        return listaStudenti;
    }

    /**
     * Metodo get di insiemeMatricole
     * @return insiemeMatricole
     */
    public HashSet<Integer> getInsiemeMatricole() {
        return insiemeMatricole;
    }

    /**
     * Metodo get di mappaStudenti
     * @return mappaStudenti
     */
    public HashMap<Integer, Studente> getMappaStudenti() {
        return mappaStudenti;
    }

    public int getId() {
        return id;
    }

    public String getLuogo() {
        return luogo;
    }
    
}
