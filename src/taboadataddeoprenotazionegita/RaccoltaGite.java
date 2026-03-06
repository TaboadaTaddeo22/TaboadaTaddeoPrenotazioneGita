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
public class RaccoltaGite {
    // Attributi
    private ArrayList<Gita> listaGite;
    private HashSet<Integer> insiemeId;
    private HashMap<Integer, Gita> mappaGite;

    /**
     * Costruttore di Anagrafe
     */
    public RaccoltaGite() {
        listaGite = new ArrayList<>();
        insiemeId = new HashSet<>();
        mappaGite = new HashMap<>();
    }

    /**
     * Metodo che aggiunge una gita
     * @param g la gita da aggiungere
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean aggiungiGita(Gita g) {
        if (g == null || insiemeId.contains(g.getId())) {
            return false;
        }
        listaGite.add(g);
        insiemeId.add(g.getId());
        mappaGite.put(g.getId(), g);
        return true;
    }

    /**
     * Metodo che rimuove uno studente usando la matricola
     * @param id l'id dello studente da rimuovere
     * @return true se l'operazione è riuscita, false altrimenti
     */
    public boolean eliminaGita(int id) {
        if (!insiemeId.contains(id)) {
            return false;
        }
        Gita g = mappaGite.remove(id);
        insiemeId.remove(id);
        listaGite.remove(g);
        return true;
    }
    
    /**
     * Metodo che rimuove tutti gli studenti dall'anagrafe
     */
    public void svuota() {
        listaGite.clear();
        insiemeId.clear();
        mappaGite.clear();
    }

    /**
     * Metodo che restituisce uno studente
     * @param id l'id dello studente
     * @return lo studente
     */
    public Gita cercaGita(int id) {
        return mappaGite.get(id);
    }

    /**
     * Metodo get di listaStudenti che la restituisce sottoforma di Collection
     * @return listaStudenti
     */
    public Collection<Gita> getTuttiStudenti() {
        return Collections.unmodifiableList(listaGite);
    }

    /**
     * Metodo che restituisce il numero di studenti
     * @return la dimensione di listaGite
     */
    public int numeroGite() {
        return listaGite.size();
    }

    /**
     * Metodo get di listaGite
     * @return listaGite
     */
    public ArrayList<Gita> getListaGite() {
        return listaGite;
    }

    /**
     * Metodo get di insiemeId
     * @return insiemeId
     */
    public HashSet<Integer> getInsiemeId() {
        return insiemeId;
    }

    /**
     * Metodo get di mappaGite
     * @return mappaGite
     */
    public HashMap<Integer, Gita> getMappaGite() {
        return mappaGite;
    }
}
