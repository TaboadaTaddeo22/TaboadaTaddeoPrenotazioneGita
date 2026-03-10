package taboadataddeoprenotazionegita;

import java.io.*;

/**
 * Gestione della persistenza tramite RandomAccessFile.
 *
 * Struttura record GITE (gite.dat):
 *   valid(1) + id(4) + luogo(50 chars × 2 = 100) = 105 bytes
 *   Accesso diretto: posizione = id × GITA_RECORD_SIZE
 *
 * Struttura record STUDENTI (studenti.dat):
 *   valid(1) + gitaId(4) + id(4) + nome(25×2=50) + cognome(25×2=50) = 109 bytes
 *   Accesso sequenziale con slot riutilizzabili
 */
public class GestioneFile {

    private static final String FILE_GITE     = "gite.dat";
    private static final String FILE_STUDENTI = "studenti.dat";

    // --- dimensioni campi GITE ---
    private static final int GITA_LUOGO_LEN   = 50;          // caratteri
    private static final int GITA_RECORD_SIZE = 1 + 4 + GITA_LUOGO_LEN * 2; // 105

    // --- dimensioni campi STUDENTI ---
    private static final int STU_NOME_LEN     = 25;
    private static final int STU_COGNOME_LEN  = 25;
    private static final int STU_RECORD_SIZE  = 1 + 4 + 4
            + STU_NOME_LEN * 2 + STU_COGNOME_LEN * 2;        // 109

    // =========================================================
    //  GITE
    // =========================================================

    /**
     * Salva (o sovrascrive) una gita nel file gite.dat.
     * La posizione è calcolata direttamente dall'id (accesso diretto).
     */
    public static void salvaGita(Gita g) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(FILE_GITE, "rw")) {
            long pos = (long) g.getId() * GITA_RECORD_SIZE;
            raf.seek(pos);
            raf.writeByte(1);                               // record valido
            raf.writeInt(g.getId());
            writeFixedString(raf, g.getLuogo(), GITA_LUOGO_LEN);
        }
    }

    /**
     * Marca come eliminata la gita con l'id indicato (soft delete).
     */
    public static void eliminaGita(int id) throws IOException {
        File f = new File(FILE_GITE);
        if (!f.exists()) return;
        try (RandomAccessFile raf = new RandomAccessFile(FILE_GITE, "rw")) {
            long pos = (long) id * GITA_RECORD_SIZE;
            if (pos + GITA_RECORD_SIZE <= raf.length()) {
                raf.seek(pos);
                raf.writeByte(0);                           // record non valido
            }
        }
    }

    /**
     * Carica tutte le gite valide da gite.dat.
     */
    public static RaccoltaGite caricaGite() throws IOException {
        RaccoltaGite raccolta = new RaccoltaGite();
        File f = new File(FILE_GITE);
        if (!f.exists()) return raccolta;

        try (RandomAccessFile raf = new RandomAccessFile(FILE_GITE, "r")) {
            long numRecords = raf.length() / GITA_RECORD_SIZE;
            for (long i = 0; i < numRecords; i++) {
                raf.seek(i * GITA_RECORD_SIZE);
                byte valid = raf.readByte();
                int  id    = raf.readInt();
                String luogo = readFixedString(raf, GITA_LUOGO_LEN);
                if (valid == 1) {
                    raccolta.aggiungiGita(new Gita(id, luogo));
                }
            }
        }
        return raccolta;
    }

    // =========================================================
    //  STUDENTI / ISCRIZIONI
    // =========================================================

    /**
     * Salva l'iscrizione di uno studente a una gita.
     * Riutilizza il primo slot libero oppure appende in fondo.
     */
    public static void salvaIscrizione(int gitaId, Studente s) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(FILE_STUDENTI, "rw")) {
            long numRecords = raf.length() / STU_RECORD_SIZE;
            long writePos   = numRecords * STU_RECORD_SIZE; // default: append

            for (long i = 0; i < numRecords; i++) {
                raf.seek(i * STU_RECORD_SIZE);
                byte valid = raf.readByte();
                int  gId   = raf.readInt();
                int  sId   = raf.readInt();

                if (valid == 1 && gId == gitaId && sId == s.getId()) {
                    return; // iscrizione già presente
                }
                if (valid == 0 && writePos == numRecords * STU_RECORD_SIZE) {
                    writePos = i * STU_RECORD_SIZE; // riutilizza slot libero
                }
            }

            raf.seek(writePos);
            raf.writeByte(1);
            raf.writeInt(gitaId);
            raf.writeInt(s.getId());
            writeFixedString(raf, s.getNome(),    STU_NOME_LEN);
            writeFixedString(raf, s.getCognome(), STU_COGNOME_LEN);
        }
    }

    /**
     * Elimina (soft delete) l'iscrizione di uno studente a una gita.
     */
    public static void eliminaIscrizione(int gitaId, int studenteId) throws IOException {
        File f = new File(FILE_STUDENTI);
        if (!f.exists()) return;
        try (RandomAccessFile raf = new RandomAccessFile(FILE_STUDENTI, "rw")) {
            long numRecords = raf.length() / STU_RECORD_SIZE;
            for (long i = 0; i < numRecords; i++) {
                raf.seek(i * STU_RECORD_SIZE);
                byte valid = raf.readByte();
                int  gId   = raf.readInt();
                int  sId   = raf.readInt();
                if (valid == 1 && gId == gitaId && sId == studenteId) {
                    raf.seek(i * STU_RECORD_SIZE);
                    raf.writeByte(0);
                    return;
                }
            }
        }
    }

    /**
     * Carica nel oggetto Gita tutti gli studenti iscritti letti da file.
     */
    public static void caricaStudentiGita(Gita g) throws IOException {
        File f = new File(FILE_STUDENTI);
        if (!f.exists()) return;
        try (RandomAccessFile raf = new RandomAccessFile(FILE_STUDENTI, "r")) {
            long numRecords = raf.length() / STU_RECORD_SIZE;
            for (long i = 0; i < numRecords; i++) {
                raf.seek(i * STU_RECORD_SIZE);
                byte   valid   = raf.readByte();
                int    gId     = raf.readInt();
                int    sId     = raf.readInt();
                String nome    = readFixedString(raf, STU_NOME_LEN);
                String cognome = readFixedString(raf, STU_COGNOME_LEN);
                if (valid == 1 && gId == g.getId()) {
                    g.aggiungiStudente(new Studente(sId, nome, cognome));
                }
            }
        }
    }

    // =========================================================
    //  Utilità
    // =========================================================

    /** Scrive una stringa a lunghezza fissa (padding con spazi). */
    private static void writeFixedString(RandomAccessFile raf,
                                         String s, int length) throws IOException {
        int written = 0;
        for (int i = 0; i < s.length() && written < length; i++, written++) {
            raf.writeChar(s.charAt(i));
        }
        while (written < length) {
            raf.writeChar(' ');
            written++;
        }
    }

    /** Legge una stringa a lunghezza fissa e rimuove il padding. */
    private static String readFixedString(RandomAccessFile raf,
                                          int length) throws IOException {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(raf.readChar());
        }
        return sb.toString().trim();
    }
}
