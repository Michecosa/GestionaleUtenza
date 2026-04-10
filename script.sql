-- GESTIONALE VENDITE
CREATE DATABASE IF NOT EXISTS gestionale_vendite;
USE gestionale_vendite;

-- 1. RUOLI (Normal, Pro, Admin L1, Admin L2)
CREATE TABLE Ruoli (
  id_ruolo INT NOT NULL AUTO_INCREMENT,
  nome_ruolo VARCHAR(50) NOT NULL,
  livello TINYINT NOT NULL DEFAULT 0 COMMENT '0=utente, 1=admin L1, 2=admin L2',
  CONSTRAINT pk_ruoli PRIMARY KEY (id_ruolo),
  CONSTRAINT uq_ruoli_nome UNIQUE (nome_ruolo),
  CONSTRAINT ck_ruoli_liv CHECK (livello BETWEEN 0 AND 2)
);

-- 2. UTENTI
CREATE TABLE Utenti (
  id_utente INT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  tipo_account ENUM('Normal','Pro') NOT NULL DEFAULT 'Normal',
  fk_ruolo INT NOT NULL DEFAULT 1,
  attivo BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_utenti PRIMARY KEY (id_utente),
  CONSTRAINT uq_utenti_email UNIQUE (email),
  CONSTRAINT fk_utenti_ruolo FOREIGN KEY (fk_ruolo)
    REFERENCES Ruoli(id_ruolo) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- 3. CATEGORIE PRODOTTO
CREATE TABLE Categorie_Prodotto (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  CONSTRAINT pk_categorie PRIMARY KEY (id_categoria)
);

-- 4. PRODOTTI
CREATE TABLE Prodotti (
  id_prodotto INT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(200) NOT NULL,
  descrizione TEXT,
  prezzo_base DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  fk_categoria INT,
  attivo BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT pk_prodotti PRIMARY KEY (id_prodotto),
  CONSTRAINT fk_prod_cat FOREIGN KEY (fk_categoria)
    REFERENCES Categorie_Prodotto(id_categoria) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT ck_prezzo_base CHECK (prezzo_base >= 0),
  CONSTRAINT ck_stock CHECK (stock >= 0)
);

-- 5. TIPI SPEDIZIONE
CREATE TABLE Tipi_Spedizione (
  id_tipo_sped INT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  corriere VARCHAR(100),
  costo DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  giorni_stima TINYINT NOT NULL DEFAULT 3,
  CONSTRAINT pk_tipi_sped PRIMARY KEY (id_tipo_sped),
  CONSTRAINT ck_costo_sped CHECK (costo >= 0),
  CONSTRAINT ck_giorni CHECK (giorni_stima >= 0)
);

-- 6. ORDINI
CREATE TABLE Ordini (
  id_ordine INT NOT NULL AUTO_INCREMENT,
  data_creazione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  stato ENUM('Creato','Pagato','In Spedizione','Consegnato','Annullato') NOT NULL DEFAULT 'Creato',
  prezzo_totale DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  sconto_applicato DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  indirizzo_spedizione VARCHAR(500),
  fk_utente INT NOT NULL,
  fk_tipo_spedizione INT,
  CONSTRAINT pk_ordini PRIMARY KEY (id_ordine),
  CONSTRAINT fk_ordini_utente FOREIGN KEY (fk_utente)
    REFERENCES Utenti(id_utente) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_ordini_sped FOREIGN KEY (fk_tipo_spedizione)
    REFERENCES Tipi_Spedizione(id_tipo_sped) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT ck_totale CHECK (prezzo_totale >= 0),
  CONSTRAINT ck_sconto CHECK (sconto_applicato >= 0)
);

-- 7. DETTAGLI ORDINE
CREATE TABLE Dettagli_Ordine (
  id_dettaglio INT NOT NULL AUTO_INCREMENT,
  fk_ordine INT NOT NULL,
  fk_prodotto INT,
  descrizione_configurata TEXT COMMENT 'Stringa generata dai decoratori',
  prezzo_unitario DECIMAL(10,2) NOT NULL,
  quantita INT NOT NULL DEFAULT 1,
  CONSTRAINT pk_dettagli PRIMARY KEY (id_dettaglio),
  CONSTRAINT fk_det_ordine FOREIGN KEY (fk_ordine)
    REFERENCES Ordini(id_ordine) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_det_prodotto FOREIGN KEY (fk_prodotto)
    REFERENCES Prodotti(id_prodotto) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT ck_prezzo_unit CHECK (prezzo_unitario >= 0),
  CONSTRAINT ck_quantita CHECK (quantita > 0)
);

-- 8. SPEDIZIONI
CREATE TABLE Spedizioni (
  id_spedizione INT NOT NULL AUTO_INCREMENT,
  fk_ordine INT NOT NULL,
  codice_tracking VARCHAR(100),
  stato ENUM('In Preparazione','Spedito','In Transito','Consegnato','Reso') NOT NULL DEFAULT 'In Preparazione',
  data_partenza TIMESTAMP NULL,
  data_consegna TIMESTAMP NULL,
  CONSTRAINT pk_spedizioni PRIMARY KEY (id_spedizione),
  CONSTRAINT uq_sped_ordine UNIQUE (fk_ordine),
  CONSTRAINT fk_sped_ordine FOREIGN KEY (fk_ordine)
    REFERENCES Ordini(id_ordine) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 9. PAGAMENTI
CREATE TABLE Pagamenti (
  id_pagamento INT NOT NULL AUTO_INCREMENT,
  fk_ordine INT NOT NULL,
  importo DECIMAL(10,2) NOT NULL,
  metodo VARCHAR(50) NOT NULL,
  stato ENUM('In Attesa','Completato','Fallito','Rimborsato') NOT NULL DEFAULT 'In Attesa',
  data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_pagamenti PRIMARY KEY (id_pagamento),
  CONSTRAINT fk_pag_ordine FOREIGN KEY (fk_ordine)
    REFERENCES Ordini(id_ordine) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT ck_importo CHECK (importo > 0)
);

-- INDICI
CREATE INDEX idx_utenti_ruolo ON Utenti(fk_ruolo);
CREATE INDEX idx_ordini_utente ON Ordini(fk_utente);
CREATE INDEX idx_ordini_stato ON Ordini(stato);
CREATE INDEX idx_dettagli_ordine ON Dettagli_Ordine(fk_ordine);
CREATE INDEX idx_prod_categoria ON Prodotti(fk_categoria);