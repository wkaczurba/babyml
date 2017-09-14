-- This is ORACLE's SQL DB for playing + testing.
-- It is not used in this project.

DROP TABLE NAPPY_TB;
DROP TABLE NOTE_TB;
DROP TABLE FEED_TB;
DROP TABLE ENTRY_TB;

-- TODO: Add timestamp:
CREATE TABLE ENTRY_TB (
  ENTRY_ID number(8,0) PRIMARY KEY,
  ENTRY_TS timestamp DEFAULT SYSDATE NOT NULL
);

CREATE TABLE FEED_TB (
  FEED_ID NUMBER (8, 0) PRIMARY KEY,
  FEED_AMOUNT NUMBER (8, 0) CONSTRAINT FEED_AMOUNT_CK CHECK (FEED_AMOUNT > 0) NOT NULL,
  FEED_ENTRY_FK_ID NUMBER(8, 0),
  CONSTRAINT FEED_ENTRY_FK_ID FOREIGN KEY (FEED_ENTRY_FK_ID) REFERENCES ENTRY_TB(ENTRY_ID)
);

CREATE TABLE NAPPY_TB (
  NAPPY_ID NUMBER(8, 0) PRIMARY KEY,
  --NAPPY_WET NUMBER (1, 0),
  NAPPY_DIRTY NUMBER (1, 0) NOT NULL, 
  NAPPY_ENTRY_FK_ID number(8, 0) NOT NULL, 
  CONSTRAINT NAPPY_ENTRY_FK_ID FOREIGN KEY (NAPPY_ENTRY_FK_ID) REFERENCES ENTRY_TB(ENTRY_ID)
);

CREATE TABLE NOTE_TB (
  NOTE_ID NUMBER(8, 0) PRIMARY KEY,
  --NAPPY_WET NUMBER (1, 0),
  NOTE_VALUE VARCHAR(500) NOT NULL,
  NOTE_ENTRY_FK_ID number(8, 0) NOT NULL, 
  CONSTRAINT NOTE_ENTRY_FK_ID FOREIGN KEY (NOTE_ENTRY_FK_ID) REFERENCES ENTRY_TB(ENTRY_ID)
);

INSERT ALL
  INTO ENTRY_TB(ENTRY_ID, ENTRY_TS) VALUES (1, SYSDATE-1/24)
  INTO FEED_TB(FEED_ID, FEED_AMOUNT, FEED_TB.FEED_ENTRY_FK_ID) VALUES (1, 120, 1)
  SELECT * FROM dual;

INSERT ALL
  INTO ENTRY_TB(ENTRY_ID, ENTRY_TS) VALUES (2, SYSDATE-1/24)
  INTO NAPPY_TB(NAPPY_ID, NAPPY_DIRTY, NAPPY_ENTRY_FK_ID) VALUES (1, 1, 2)
  SELECT * FROM dual;

INSERT ALL
  INTO ENTRY_TB(ENTRY_ID, ENTRY_TS) VALUES (3, SYSDATE-1/48)
  INTO NOTE_TB(NOTE_ID, NOTE_VALUE, NOTE_ENTRY_FK_ID) VALUES (1, 'Burped 30 times', 3)
  SELECT * FROM dual;
  
CREATE VIEW ENTRY_V AS
  SELECT ENTRY_TB.*,
         CASE
           WHEN NAPPY_ID IS NOT NULL THEN 'NAPPY_TB'
           WHEN FEED_ID IS NOT NULL THEN 'FEED_TB'
           WHEN NOTE_ID IS NOT NULL THEN 'NOTE_TB'
           ELSE 'INVALID'
         END AS ENTRY_TYPE,
         NOTE_TB.*,
         FEED_TB.*,
         NAPPY_TB.*
         FROM ENTRY_TB
    FULL JOIN NAPPY_TB ON (NAPPY_ENTRY_FK_ID = ENTRY_ID)
    FULL JOIN FEED_TB ON (FEED_ENTRY_FK_ID = ENTRY_ID)
    FULL JOIN NOTE_TB ON (NOTE_ENTRY_FK_ID = ENTRY_ID);
  
-- FINALLY:
SELECT * FROM ENTRY_V;
SELECT ENTRY_ID, ENTRY_TYPE, NAPPY_ID, FEED_ID, NOTE_ID  FROM ENTRY_V;


--INSERT ALL
--  INTO entries(id, fk_entry_type, fk_id) VALUES (4, 'NAPPY_ENTRY', 1)
--  INTO nappy_entry (nappy_id, nappy_wet, nappy_dirty, nappy_timestamp, NAPPY_ENTRY_TYPE, nappy_entry_fk_id) VALUES (1, 0, 1, sysdate-2, 'NAPPY_ENTRY', 1)
--  SELECT * FROM dual;
--
--
---- 
--INSERT ALL
--  INTO entries(id, fk_entry_type, fk_id) VALUES (5, 'NAPPY_ENTRY', 2)
--  INTO nappy_entry (nappy_id, nappy_wet, nappy_dirty, nappy_timestamp, NAPPY_ENTRY_TYPE, nappy_entry_fk_id) VALUES (2, 2, 2, sysdate-1, 'NAPPY_ENTRY', 2)
--  SELECT * FROM dual;
--
--INSERT ALL
--  INTO entries(id, fk_entry_type, fk_id) VALUES (6, 'NAPPY_ENTRY', 3)
--  INTO nappy_entry (nappy_id, nappy_wet, nappy_dirty, nappy_timestamp, NAPPY_ENTRY_TYPE, nappy_entry_fk_id) VALUES (3, 3, 0, sysdate-0.05, 'NAPPY_ENTRY', 3)
--  SELECT * FROM dual;
--  
---- PRINT:
--
--SELECT e.id, fk_id, fk_entry_type, f.feed_id, f.feed_amount, f.feed_timestamp, n.* FROM entries e
--  FULL JOIN feeding_entry f ON e.fk_id = f.feed_entry_fk_id AND e.fk_entry_type = f.feed_entry_type
--  FULL JOIN nappy_entry n   ON e.fk_id = n.nappy_entry_fk_id AND e.fk_entry_type = n.nappy_entry_type;
--  
--  
------DELETE FROM entries WHERE id = 4;
----  
----SELECT * FROM entries;
----SELECT * FROM nappy_entry;
----SELECT * FROM feeding_entry;
----  
----INSERT INTO nappy_entry (nappy_id, nappy_wet, nappy_dirty, nappy_timestamp)
----  VALUES (2, 2, 1, sysdate-1);
----INSERT INTO nappy_entry (nappy_id, nappy_wet, nappy_dirty, nappy_timestamp)
----  VALUES (3, 2, 0, sysdate);
----
----CREATE INDEX nappy_timestamp_idx ON nappy_entry(nappy_timestamp);
----ALTER TABLE nappy_entry ADD CONSTRAINT nappy_ck CHECK (nappy_dirty + nappy_wet > 0);
----
------CREATE VIEW events AS
------  SELECT 
------);
----
------SELECT id, feed_amount, feed_timestamp AS TIMESTAMP, 'milk' AS EVENT FROM feeding_entry
------  RIGHT JOIN nappy_id, nappy_wet, nappy_dirty, nappy_timestamp AS TIMESTAMP FROM nappy_entry;
----SELECT * FROM nappy_entry NATURAL JOIN feeding_entry;
----
----CREATE VIEW feed_v AS
----  SELECT id, feed_amount, feed_timestamp AS TIMESTAMP, 'milk' AS EVENT FROM feeding_entry;
----  
----CREATE VIEW nappy_v AS
----  SELECT nappy_id, nappy_wet, nappy_dirty, nappy_timestamp, 'nappy' AS EVENT FROM nappy_entry;
----
----select * from feed_v full outer join nappy_v ON feed_v.event = nappy_v.event;
----
----SELECT id, feed_amount, feed_timestamp AS TIMESTAMP, 'milk' AS EVENT FROM feeding_entry;
----SELECT nappy_id, 'nappy' AS EVENT FROM nappy_entry;
----
----DROP TABLE entries;
----
