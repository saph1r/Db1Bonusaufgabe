DELIMITER $$

DROP PROCEDURE if exists LieferbezirkAendern $$
create PROCEDURE LieferbezirkAendern (IN AlterLiefererID INT, IN NeuerLieferbezirkID INT)
	BEGIN
    	DECLARE done	INT DEFAULT 0;
        DECLARE NeuePlz 	INT DEFAULT 0;
        DECLARE liefererID	INT DEFAULT 0;
        DECLARE	getraenkemarktID INT DEFAULT 0;
        DECLARE getraenkemarktPlz INT DEFAULT 0;
        
        DECLARE gec CURSOR FOR 
        	SELECT getraenkemarkt.idGetraenkemarkt,getraenkemarkt.plz 
            FROM getraenkemarkt;
        DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done  = 1;
        -- Am ende des cursors gibt es keinen datensatz - sqlstate02000 daten 		  -- nicht vorhanden
        
        -- abfrage ob lieferbezirk einen getraenkemarkt hat
        -- falls nicht call prozedure
        -- falls doch geh normal weiter
        IF  
        	(SELECT count(lieferbezirk.plz)
			from lieferbezirk join getraenkemarkt
			on getraenkemarkt.plz = lieferbezirk.plz
			where lieferbezirk.idLieferbezirk = NeuerLieferbezirkID) = 0
            
        THEN
            CALL KeinGetraenkemarktImLieferbezirkVorhanden();
        END IF;
        
        
		-- lieferbezirk besitzt einen getraenkemarkt
        -- also kann lieferbezirk des lieferers geändert werden
        UPDATE lieferer_lieferbezirk
        SET Lieferbezirk_idLieferbezirk = NeuerLieferbezirkID
        WHERE lieferer_lieferbezirk.Lieferer_idLieferer = AlterLiefererID;
        
        -- plz des Lieferbezirks einseichern
        SELECT lieferbezirk.plz INTO NeuePlz
        from lieferbezirk
        where lieferbezirk.idLieferbezirk = NeuerLieferbezirkID;
        
        -- lösche alten GM in getraenkemarkt_has_Lieferer
        DELETE FROM getraenkemarkt_has_lieferer
        where AlterLiefererID = getraenkemarkt_has_lieferer.Lieferer_idLieferer;
        
        -- jetzt noch persistenz in getraenkemarkt_has_lieferer herstellen
        -- lieferer 1 soll in lieferbezirk 2 arbeiten
        -- getraenkemaerkte 2 und 3 sind in lieferbezrik 2
        -- also muss lieferer 1 an gm2 und gm3 liefern
       	
        -- cursor läuft durch getraenkemarkt und guckt ob plz mit der 
        -- plz des neuen Lieferbezirks übereinstimmt
        -- dann wird der lieferer mit dem getraenkemakrt in ghl insertet
        OPEN gec;
        	while done = 0 DO
            	FETCH gec INTO getraenkemarktID, getraenkemarktPlz;
                	IF getraenkemarktPlz = NeuePlz
                    	THEN
                        INSERT INTO getraenkemarkt_has_lieferer(Lieferer_idLieferer, Getraenkemarkt_idGetraenkemarkt)
                        VALUES(AlterLiefererID, getraenkemarktID);
                    END IF;
            END while;
        CLOSE gec;
    END $$
DELIMITER ;
