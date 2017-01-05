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
  IN idLieferbezierk int(10),
  IN lieferzeit varchar(45),
  IN lieferpreis double,
  IN idGetraenkemarkt int(10))
BEGIN
    
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
        kontonummmer,
        blz,
        bankname);

INSERT INTO `Lieferer_Lieferbezirk`
VALUES (idLieferbezirk, idLieferer, lieferzeit, lieferpreis);

INSERT INTO `getraenkemarkt_has_lieferer`
VALUES (idLieferer, idGetraenkemarkt);

END$$

DELIMITER ;
