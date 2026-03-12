package taboadataddeoprenotazionegita;

import java.io.*;
import java.util.*;

public class GestioneFile {
    // Attributi
    private static final int LEN_LUOGO   = 30;
    private static final int LEN_NOME    = 20;
    private static final int LEN_COGNOME = 20;

    private static final int DIM_RECORD_GITA     = (LEN_LUOGO * 2) + 4;
    private static final int DIM_RECORD_STUDENTE = 4 + (LEN_NOME * 2) + (LEN_COGNOME * 2) + 4;

    private String FILE_GITE      = "gite.dat";
    private String FILE_STUDENTI  = "studenti.dat";

    /**
     * Adatta la lunghezza di una stringa alla dimensione fissa richiesta:
     * - se troppo corta, la riempie con '*'
     * - se troppo lunga, la tronca
     *
     * @param s      la stringa da adattare
     * @param lungh  la lunghezza fissa desiderata
     * @return la stringa adattata
     */
    private String aggiustaLunghezza(String s, int lungh) {
        if (s == null) s = "";
        if (s.length() < lungh) {
            StringBuilder sb = new StringBuilder(s);
            for (int i = 0; i < (lungh - s.length()); i++) {
                sb.append('*');
            }
            return sb.toString();
        } else if (s.length() > lungh) {
            return s.substring(0, lungh);
        }
        return s;
    }

    /**
     * Legge n caratteri dal RandomAccessFile e restituisce la stringa
     * ripulita dai caratteri di padding '*'.
     *
     * @param file  il file da cui leggere
     * @param n     il numero di caratteri da leggere
     * @return la stringa letta senza padding
     * @throws IOException in caso di errore di lettura
     */
    private String leggiStringa(RandomAccessFile file, int n) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(file.readChar());
        }
        // Rimuove il padding '*' usato per riempire la lunghezza fissa
        return sb.toString().replace("*", "").trim();
    }

    /**
     * Sovrascrive TUTTE le gite presenti in gite.dat con quelle della lista.
     * Questo metodo riscrive il file da zero.
     *
     * @param listaGite la lista completa di gite da salvare
     */
    public void salvaGite(ArrayList<Gita> listaGite) {
        try (RandomAccessFile file = new RandomAccessFile(FILE_GITE, "rw")) {

            // Azzera il file (tronca a 0)
            file.setLength(0);

            for (int i = 0; i < listaGite.size(); i++) {
                Gita g = listaGite.get(i);
                file.seek((long) i * DIM_RECORD_GITA);
                file.writeInt(g.getId());
                file.writeChars(aggiustaLunghezza(g.getLuogo(), LEN_LUOGO));
            }
        } catch (FileNotFoundException ex) {
            System.out.println("GestioneFile: file " + FILE_GITE + " non trovato.");
        } catch (IOException e) {
            System.out.println("GestioneFile: problema lettura/scrittura su " + FILE_GITE);
        }
    }

    /**
     * Legge tutte le gite da gite.dat e le restituisce come ArrayList.
     * Gli studenti NON vengono caricati qui: usare caricaStudenti() separatamente.
     *
     * @return ArrayList di Gita (senza studenti)
     */
    public ArrayList<Gita> caricaGite() {
        ArrayList<Gita> lista = new ArrayList<>();

        try (RandomAccessFile file = new RandomAccessFile(FILE_GITE, "r")) {

            int nRecord = (int) (file.length() / DIM_RECORD_GITA);
            for (int i = 0; i < nRecord; i++) {
                file.seek((long) i * DIM_RECORD_GITA);
                int    id    = file.readInt();
                String luogo = leggiStringa(file, LEN_LUOGO);
                lista.add(new Gita(id, luogo));
            }

        } catch (FileNotFoundException ex) {
            System.out.println("GestioneFile: file " + FILE_GITE + " non trovato.");
        } catch (IOException e) {
            System.out.println("GestioneFile: problema lettura su " + FILE_GITE);
        }

        return lista;
    }

    /**
     * Sovrascrive TUTTI gli studenti in studenti.dat con quelli di tutte le gite.
     * Per ogni gita scorre i suoi studenti e li salva con il relativo idGita.
     *
     * @param listaGite la lista completa di gite (ognuna con i propri studenti)
     */
    public void salvaStudenti(ArrayList<Gita> listaGite) {
        try (RandomAccessFile file = new RandomAccessFile(FILE_STUDENTI, "rw")) {

            // Azzera il file
            file.setLength(0);

            int posizione = 0;
            for (Gita g : listaGite) {
                for (Studente s : g.getListaStudenti()) {
                    file.seek((long) posizione * DIM_RECORD_STUDENTE);
                    file.writeInt(s.getId());
                    file.writeChars(aggiustaLunghezza(s.getNome(),    LEN_NOME));
                    file.writeChars(aggiustaLunghezza(s.getCognome(), LEN_COGNOME));
                    file.writeInt(g.getId()); // idGita: collega lo studente alla sua gita
                    posizione++;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("GestioneFile: file " + FILE_STUDENTI + " non trovato.");
        } catch (IOException e) {
            System.out.println("GestioneFile: problema lettura/scrittura su " + FILE_STUDENTI);
        }
    }

    /**
     * Legge tutti gli studenti da studenti.dat e li inserisce nelle gite
     * corrispondenti presenti in listaGite (collega tramite idGita).
     *
     * @param listaGite la lista di gite già caricate (da caricaGite())
     */
    public void caricaStudenti(ArrayList<Gita> listaGite) {
        try (RandomAccessFile file = new RandomAccessFile(FILE_STUDENTI, "r")) {

            int nRecord = (int) (file.length() / DIM_RECORD_STUDENTE);
            for (int i = 0; i < nRecord; i++) {
                file.seek((long) i * DIM_RECORD_STUDENTE);

                int    id      = file.readInt();
                String nome    = leggiStringa(file, LEN_NOME);
                String cognome = leggiStringa(file, LEN_COGNOME);
                int    idGita  = file.readInt();

                Studente s = new Studente(id, nome, cognome);

                // Cerca la gita corrispondente e aggiunge lo studente
                for (Gita g : listaGite) {
                    if (g.getId() == idGita) {
                        g.aggiungiStudente(s);
                        break;
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            System.out.println("GestioneFile: file " + FILE_STUDENTI + " non trovato.");
        } catch (IOException e) {
            System.out.println("GestioneFile: problema lettura su " + FILE_STUDENTI);
        }
    }

    /**
     * Metodo di comodo: carica gite e studenti in un'unica chiamata.
     * Restituisce la lista di gite ognuna già popolata con i propri studenti.
     *
     * @return ArrayList di Gita con studenti già associati
     */
    public ArrayList<Gita> caricaTutto() {
        ArrayList<Gita> listaGite = caricaGite();
        caricaStudenti(listaGite);
        return listaGite;
    }

    /**
     * Metodo di comodo: salva gite e studenti in un'unica chiamata.
     *
     * @param listaGite la lista completa di gite con i relativi studenti
     */
    public void salvaTutto(ArrayList<Gita> listaGite) {
        salvaGite(listaGite);
        salvaStudenti(listaGite);
    }

    public String getFILE_GITE() {
        return FILE_GITE;
    }

    public void setFILE_GITE(String FILE_GITE) {
        this.FILE_GITE = FILE_GITE;
    }

    public String getFILE_STUDENTI() {
        return FILE_STUDENTI;
    }

    public void setFILE_STUDENTI(String FILE_STUDENTI) {
        this.FILE_STUDENTI = FILE_STUDENTI;
    }
    
}