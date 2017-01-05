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
  IN plz char(5),
  IN tel varchar(15),
  IN mail varchar(45),
  IN beschreibung varchar(45),
  IN onto_nr varchar(12),
  IN blz char(8),
  IN bankname varchar(45),
  IN lieferzeit varchar(45),
  IN lieferpreis double,
  IN getraenkemarktName varchar(45))
BEGIN
    DECLARE idMarkt INT;        #idGetraenkemarkt
    DECLARE done INT DEFAULT 0; #Hilfsvariable
    DECLARE postlz CHAR(5);     #Postleitzahl des Lieferbezirks
    DECLARE plzTemp CHAR(5);    #Temp PLZ
    DECLARE idBezirk int(10);   #ID des Lieferbezirks
    DECLARE gic CURSOR FOR      #GetränkeIdCursor
        SELECT idGetraenkemarkt, plz
        FROM getraenkemarkt;

    SELECT plz INTO postlz
    FROM getraenkemarkt
    WHERE name=getraenkemarktName;

    SELECT idLiefererbezirk INTO idBezirk
    FROM lieferbezirk
    WHERE plz = postlz;

    INSERT INTO `lieferer`
    VALUES (idLieferer,
            passwort,
            anrede,
            vorname,
            nachname, 
            geburtstagsdatum,
            strasse,
            wohnort,
            plz,
            telefonnummer,
            mail,
            beschreibung,
            kontonummmer,           blz,
            bankname);

    INSERT INTO `Lieferer_Lieferbezirk`
    VALUES (idLieferbezirk, idLieferer, lieferzeit, lieferpreis);

    OPEN gic
        WHILE done = 0 DO
            FETCH gic INTO idMarkt, plzTemp;
                IF postlz = plzTemp
                THEN INSERT INTO `getraenkemarkt_has_lieferer`
                     VALUES (idLieferer, idMarkt);
                ENDIF;        
        END WHILE;
    CLOSE gic;

END $$

DELIMITER ;#
