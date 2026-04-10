# GestionaleUtenza — Gestionale di Vendite con Java + MySQL

Sistema gestionale a riga di comando per la gestione di utenti, prodotti, ordini, spedizioni e pagamenti, con ruoli differenziati e pattern architetturali classici (Observer, Decorator, Strategy, Singleton, DAO).

---

## Indice

1. [Struttura del Progetto](#struttura-del-progetto)
2. [Architettura e Pattern Utilizzati](#architettura-e-pattern-utilizzati)
3. [Modello Dati (Database)](#modello-dati-database)
4. [Ruoli e Permessi](#ruoli-e-permessi)
5. [Funzionalità Principali](#funzionalità-principali)
6. [Requisiti](#requisiti)
7. [Setup e Configurazione](#setup-e-configurazione)
8. [Come Testare — Guida Passo Passo](#come-testare--guida-passo-passo)
9. [Credenziali di Test (seed.sql)](#credenziali-di-test-seedsql)
10. [Flusso Completo di un Ordine](#flusso-completo-di-un-ordine)

---

## Struttura del Progetto

```
GestionaleUtenza/
├── main/
│   └── Main.java                  # Entry point, routing dei menu
├── db/
│   └── DatabaseConnection.java    # Singleton per la connessione JDBC
├── model/
│   ├── Utente.java
│   ├── Ruolo.java
│   ├── Prodotto.java
│   ├── Categoria.java
│   ├── Ordine.java
│   ├── DettaglioOrdine.java
│   ├── Pagamento.java
│   ├── Spedizione.java
│   └── TipoSpedizione.java
├── dao/
│   ├── UtenteDAO.java
│   ├── ProdottoDAO.java
│   ├── OrdineDAO.java
│   ├── SpedizioneDAO.java
│   └── PagamentoDAO.java
├── decorator/
│   ├── ProdottoConfigurabile.java      # Interfaccia
│   ├── ProdottoBase.java               # Wrapper base
│   ├── ProdottoDecoratorBase.java      # Decorator astratto
│   ├── AggiuntaGaranzia.java
│   ├── AggiuntaConfezioneGift.java
│   └── AggiuntaSpedizioneExpress.java
├── observer/
│   ├── OrdineObserver.java             # Interfaccia
│   ├── OrdineEventSource.java          # Interfaccia sorgente
│   ├── GestoreOrdini.java              # Soggetto osservato
│   ├── LogOrdineObserver.java
│   ├── NotificaEmailObserver.java
│   └── AggiornaStockObserver.java
└── strategy/
    ├── ScontoStrategy.java             # Interfaccia
    ├── ScontoNessuno.java
    ├── ScontoPercentuale.java
    └── ScontoProUtente.java
```

---

## Architettura e Pattern Utilizzati

### Singleton — `DatabaseConnection`
La connessione al database MySQL viene gestita con un'unica istanza condivisa in tutta l'applicazione. Questo evita connessioni duplicate e centralizza la configurazione JDBC.

### DAO (Data Access Object)
Ogni entità del dominio ha il proprio DAO che incapsula le query SQL (insert, findById, findAll, ecc.). Il `Main` non conosce SQL: interagisce solo con i DAO.

### Observer — Gestione Cambio Stato Ordine
`GestoreOrdini` è il soggetto. Quando un ordine cambia stato, notifica automaticamente tutti gli Observer registrati:

| Observer | Comportamento |
|---|---|
| `LogOrdineObserver` | Stampa un log su console con il nuovo stato |
| `NotificaEmailObserver` | Simula l'invio di una notifica email all'utente |
| `AggiornaStockObserver` | Scala lo stock dei prodotti quando l'ordine viene pagato |

### Decorator — Configurazione Prodotto
Prima di aggiungere un prodotto al carrello, l'utente può arricchirlo con aggiunte opzionali che modificano prezzo e descrizione senza alterare il modello base:

| Decorator | Effetto |
|---|---|
| `AggiuntaGaranzia` | Aggiunge garanzia estesa al prodotto |
| `AggiuntaConfezioneGift` | Aggiunge confezione regalo |
| `AggiuntaSpedizioneExpress` | Priorità di spedizione sul singolo articolo |

### Strategy — Calcolo Sconto
Il tipo di account dell'utente determina automaticamente la strategia di sconto applicata al totale dell'ordine:

| Strategia | Account | Comportamento |
|---|---|---|
| `ScontoNessuno` | Normal | Nessuno sconto applicato |
| `ScontoProUtente` | Pro | Sconto percentuale per utenti Pro |
| `ScontoPercentuale` | (uso generico) | Sconto configurabile |

---

## Modello Dati (Database)

**Tabelle principali:**

- **Ruoli**: livello `0` = utente normale, `1` = Admin L1, `2` = Admin L2
- **Utenti**: `tipo_account` può essere `Normal` o `Pro`; `attivo` permette di disabilitare un account
- **Prodotti**: hanno stock, categoria, prezzo base e flag `attivo`
- **Ordini**: stati possibili → `Creato → Pagato → In Spedizione → Consegnato` (o `Annullato`)
- **Dettagli_Ordine**: memorizza la descrizione configurata dal Decorator al momento dell'acquisto
- **Pagamenti**: registrano metodo, importo e stato del pagamento
- **Spedizioni**: tracking e stato logistico separato dall'ordine

---

## Ruoli e Permessi

| Ruolo | `livello` (DB) | Accesso |
|---|---|---|
| Utente Normal | 0 | Visualizza prodotti, crea ordini, vede i propri ordini |
| Utente Pro | 0 | Come Normal + sconto automatico sul totale |
| Admin L1 | 1 | Menu admin: gestione prodotti e ordini |
| Admin L2 | 2 | Stessi permessi di L1 (estendibile) |

Il routing del menu avviene in `Main.java` tramite `utente.getLivelloRuolo()`:
- `>= 1` → `menuAdmin()`
- `0` → `menuUtente()`

---

## Funzionalità Principali

### Area Utente
- **Login** con email e password (hash)
- **Registrazione** autonoma (crea account Normal)
- **Visualizza prodotti** attivi nel catalogo
- **Crea un ordine** con selezione prodotti, aggiunta opzionale di garanzia (Decorator), scelta tipo spedizione e pagamento immediato
- **Visualizza i propri ordini**

### Area Admin
- **Visualizza tutti i prodotti** (anche non attivi)
- **Aggiunge nuovi prodotti** al catalogo
- **Visualizza tutti gli ordini** e cambia manualmente lo stato (`In Spedizione`, `Consegnato`, ecc.)

---

## Setup e Configurazione

### 1. Creare il Database

Da MySQL Workbench aprire ed eseguire prima `script.sql`, poi `seed.sql`.

`script.sql` crea il database `gestionale_vendite` con tutte le tabelle, constraint e indici.  
`seed.sql` inserisce ruoli, utenti di test, prodotti, ordini e pagamenti di esempio.

> **Attenzione**: il `seed.sql` non inserisce i Ruoli. 
> Inserirli manualmente se non presenti, oppure aggiungere all'inizio del seed:
> ```sql
> INSERT INTO Ruoli (id_ruolo, nome_ruolo, livello) VALUES
> (1, 'Utente', 0),
> (2, 'Admin L1', 1),
> (3, 'Admin L2', 2);
> ```

### 2. Configurare la Connessione

Nel file `DatabaseConnection.java` (pacchetto `db`), modificare le costanti di connessione:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/gestionale_vendite";
private static final String USER = "root";
private static final String PASS = "tua_password";
```

---


## Come Testare — Guida Passo Passo

### Test 1 — Login come Utente Normal e creazione ordine

1. Avviare il programma
2. Selezionare `1. Login`
3. Inserire:
   - Email: `mario.rossi@email.it`
   - Password: `pass123`
4. Si apre il **Menu Utente**
5. Selezionare `1. Visualizza Prodotti` → compare la lista dei prodotti attivi
6. Selezionare `2. Crea Nuovo Ordine`
7. Scegliere un prodotto (es. ID `2` → Cuffie Wireless)
8. Alla domanda *"Aggiungere Garanzia?"* rispondere `s` → il Decorator aumenta prezzo e descrizione
9. Inserire quantità (es. `1`)
10. Inserire `0` per terminare la selezione
11. Scegliere un tipo di spedizione dalla lista
12. Rispondere `s` per pagare immediatamente
13. **Risultato atteso**: l'Observer stampa il log del cambio stato a `Pagato`, viene simulata la notifica email e lo stock del prodotto si aggiorna

### Test 2 — Login come Utente Pro (verifica sconto)

1. Login con `luca.pro@email.it` / `pass123`
2. Creare un ordine identico al Test 1
3. **Risultato atteso**: il totale mostrato sarà inferiore rispetto all'utente Normal grazie alla `ScontoProUtente` strategy

### Test 3 — Login come Admin e cambio stato ordine

1. Login con `admin@gestionale.it` / `admin123`
2. Si apre il **Menu Amministratore** (livello ruolo >= 1)
3. Selezionare `2. Gestione Ordini`
4. Vengono listati tutti gli ordini presenti nel DB
5. Inserire l'ID di un ordine esistente (es. `2`)
6. Inserire il nuovo stato: `In Spedizione`
7. **Risultato atteso**: il `GestoreOrdini` notifica tutti gli Observer e lo stato viene aggiornato nel DB

### Test 4 — Aggiunta di un nuovo prodotto (Admin)

1. Login come admin
2. Selezionare `1. Gestione Prodotti`
3. Selezionare `1. Aggiungi Prodotto`
4. Inserire nome, prezzo base e stock
5. **Risultato atteso**: il prodotto viene salvato nel DB ed è visibile al prossimo login utente

### Test 5 — Account disabilitato

1. Tentare il login con `ex.utente@email.it` / `pass123`
2. **Risultato atteso**: messaggio `Errore login: credenziali errate o account disabilitato.` — il flag `attivo = FALSE` blocca l'accesso

### Test 6 — Registrazione nuovo utente

1. Dal menu principale selezionare `2. Registrati`
2. Inserire nome, email e password
3. **Risultato atteso**: viene creato un account `Normal` con `fk_ruolo = 1`; si effettua automaticamente il login nel menu utente

---

## Credenziali di Test (seed.sql)

| Nome | Email | Password | Tipo | Ruolo |
|---|---|---|---|---|
| Mario Rossi | mario.rossi@email.it | pass123 | Normal | Utente |
| Luca Bianchi | luca.pro@email.it | pass123 | Pro | Utente |
| Admin Generale | admin@gestionale.it | admin123 | Normal | Admin L2 |
| Sara Verdi | sara.staff@email.it | pass123 | Normal | Admin L1 |
| Utente Disabilitato | ex.utente@email.it | pass123 | Normal | Utente (disabilitato) |

---

## Flusso Completo di un Ordine

```
[Utente] Crea Ordine
         │
         ├─ Seleziona Prodotti
         │   └─ Applica Decorator (Garanzia, Gift, Express) → prezzo/descrizione aggiornati
         │
         ├─ Seleziona Tipo Spedizione → costo aggiunto al lordo
         │
         ├─ Calcola Totale
         │   └─ Applica Strategy Sconto (Normal = 0%, Pro = sconto%)
         │
         ├─ Salva Ordine nel DB (stato: "Creato")
         ├─ Salva Dettagli Ordine (con descrizione configurata)
         │
         └─ Pagamento immediato? ──► SÌ ──► Salva Pagamento
                                              └─ GestoreOrdini.cambiaStato("Pagato")
                                                  ├─ LogOrdineObserver → log console
                                                  ├─ NotificaEmailObserver → email simulata
                                                  └─ AggiornaStockObserver → scala stock DB

[Admin] Cambia stato ordine
         └─ GestoreOrdini.cambiaStato("In Spedizione" / "Consegnato")
             └─ Tutti gli Observer vengono notificati
```