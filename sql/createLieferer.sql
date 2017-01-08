DELIMITER $$

DROP PROCEDURE IF EXISTS `createLieferer` $$
CREATE PROCEDURE `createLieferer`(
  IN idLieferer int(10),
  IN passwort varchar(45),
  IN anrede varchar(45),
  IN vorname varchar(45),
  IN nachname varchar(45),
  IN geburtsdatum date,
  IN strasse varchar(45),
  IN wohnort varchar(45),
  IN liefererPlz char(5),
  IN tel varchar(15),
  IN mail varchar(45),
  IN beschreibung varchar(45),
  IN konto_nr varchar(12),
  IN blz char(8),
  IN bankname varchar(45),
  IN lieferzeit varchar(45),
  IN lieferpreis double,
  IN getraenkemarktName varchar(45))
BEGIN
    DECLARE idMarkt INT;        #idGetraenkemarkt
    DECLARE done INT DEFAULT 0; #Hilfsvariable
    DECLARE marktPlz CHAR(5);   #Postleitzahl des Getraenkemarkts
    DECLARE plzTemp CHAR(5);    #Temp PLZ
    DECLARE idBezirk int(10);   #ID des Lieferbezirks
    DECLARE gic CURSOR FOR      #GetränkeIdCursor
        SELECT idGetraenkemarkt, plz
        FROM getraenkemarkt;    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
    
    -- Anhand des Getränkemarktnames die Postleitzahl
    -- des Marktes ermitteln
    SELECT plz INTO marktPlz
    FROM getraenkemarkt
    WHERE name=getraenkemarktName;

    -- Da die Postleitzahl des Marktes auch der 
    -- des Lieferbezirks entspricht -> Bestimmung der ID 
    SELECT idLieferbezirk INTO idBezirk
    FROM lieferbezirk
    WHERE plz = marktPlz;

    -- Neuen Lieferer hinzufügen
    INSERT INTO `lieferer`
    VALUES (idLieferer,
            passwort,
            anrede,
            vorname,
            nachname, 
            geburtsdatum,
            strasse,
            wohnort,
            LiefererPlz,
            tel,
            mail,
            beschreibung,
            konto_nr,
            blz,
            bankname);

    -- Neuen Lieferer, Bezirk zuordnen
    INSERT INTO `Lieferer_Lieferbezirk`
    VALUES (idBezirk, idLieferer, lieferzeit, lieferpreis);

    -- Mit dem Cursor nach Marktplätzen im gleichen Lieferbezirk suchen,
    -- da der Lieferer alle Getränke in seinem Bezirk anfährt
    OPEN gic;
        WHILE done = 0 DO
            FETCH gic INTO idMarkt, plzTemp;
                IF marktPlz = plzTemp
                -- und bei Übereinstimmung entsprechend eine Relation
                -- und Getränkemarkt setzen
                THEN INSERT INTO `getraenkemarkt_has_lieferer`
                     VALUES (idLieferer, idMarkt);
                END IF;        
        END WHILE;
    CLOSE gic;

END $$

DELIMITER ;
