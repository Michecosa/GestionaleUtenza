USE gestionale_vendite;

-- 1. UTENTI (Password 'pass123' per tutti per semplicità di test)
-- N.B. Il fk_ruolo 1 è 'Utente', 2 è 'Admin L1', 3 è 'Admin L2' (basato sui seed del tuo script)
INSERT INTO Utenti (nome, email, password_hash, tipo_account, fk_ruolo, attivo) VALUES
('Mario Rossi', 'mario.rossi@email.it', 'pass123', 'Normal', 1, TRUE),
('Luca Bianchi', 'luca.pro@email.it', 'pass123', 'Pro', 1, TRUE),
('Admin Generale', 'admin@gestionale.it', 'admin123', 'Normal', 3, TRUE),
('Sara Verdi', 'sara.staff@email.it', 'pass123', 'Normal', 2, TRUE),
('Utente Disabilitato', 'ex.utente@email.it', 'pass123', 'Normal', 1, FALSE);

-- 2. PRODOTTI
-- Assumendo: 1=Elettronica, 2=Abbigliamento, 3=Libri, 4=Casa
INSERT INTO Prodotti (nome, descrizione, prezzo_base, stock, fk_categoria, attivo) VALUES
('Smartphone Alpha', 'Display 6.5 pollici, 128GB', 599.99, 50, 1, TRUE),
('Cuffie Wireless Noise Cancelling', 'Autonomia 30 ore', 149.50, 100, 1, TRUE),
('Macchina Caffè Espresso', 'Pressione 15 bar, colore nero', 89.90, 20, 4, TRUE),
('T-shirt Cotone Bio', 'Colore Bianco, Taglia L', 19.99, 200, 2, TRUE),
('Manuale Java Advanced', 'Guida completa ai design pattern', 45.00, 15, 3, TRUE),
('Prodotto Fuori Catalogo', 'Non più disponibile', 10.00, 0, 1, FALSE);

-- 3. ORDINI (Test per diversi stati)
-- Ordine 1: Completato da utente Pro
INSERT INTO Ordini (fk_utente, fk_tipo_spedizione, stato, prezzo_totale, sconto_applicato, indirizzo_spedizione) VALUES
(2, 2, 'Pagato', 612.89, 60.00, 'Via Roma 10, Milano');

-- Ordine 2: Nuovo ordine appena creato da utente Normal
INSERT INTO Ordini (fk_utente, fk_tipo_spedizione, stato, prezzo_totale, sconto_applicato, indirizzo_spedizione) VALUES
(1, 1, 'Creato', 154.40, 0.00, 'Via Napoli 5, Roma');

-- 4. DETTAGLI ORDINE
-- Dettagli per Ordine 1 (Smartphone + Spedizione Express)
INSERT INTO Dettagli_Ordine (fk_ordine, fk_prodotto, descrizione_configurata, prezzo_unitario, quantita) VALUES
(1, 1, 'Smartphone Alpha + Garanzia Estesa (Decorator)', 599.99, 1);

-- Dettagli per Ordine 2 (Cuffie + Spedizione Standard)
INSERT INTO Dettagli_Ordine (fk_ordine, fk_prodotto, descrizione_configurata, prezzo_unitario, quantita) VALUES
(2, 2, 'Cuffie Wireless Noise Cancelling', 149.50, 1);

-- 5. PAGAMENTI
INSERT INTO Pagamenti (fk_ordine, importo, metodo, stato) VALUES
(1, 612.89, 'Carta di Credito', 'Completato');

-- 6. SPEDIZIONI
INSERT INTO Spedizioni (fk_ordine, codice_tracking, stato, data_partenza) VALUES
(1, 'TRK-ALPHA-9988', 'In Preparazione', NULL);

-- 7. RUOLI
INSERT INTO Ruoli (id_ruolo, nome_ruolo, livello) VALUES
	(1, 'Utente', 0),
	(2, 'Admin L1', 1),
	(3, 'Admin L2', 2);