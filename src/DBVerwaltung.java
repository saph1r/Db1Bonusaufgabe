import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;

/**
 * Datenbank Verwaltung
 *
 * @author Sebastian Donath, Christoph Weinzierl
 */
public class DBVerwaltung {

	/**
	 * Konfiguration der Konnektion zur Datenbank Lieferservice Venenum
	 * 
	 * @return DB Connection to venenumbonus
	 */
	public Connection connect() {

		Connection conn = null;

		// Konfiguration der JDBC URL
		String host = "jdbc:mysql://localhost:3306/";
		String dbName = "venenumbonus";
		String username = "root";
		String password = "";

		String url = host + dbName + "?user=" + username + "?password=" + password;

		try {
			//Checke ob JDBC Treiber geladen werden kann
			String treiber = "com.mysql.jdbc.Driver";
			Class.forName(treiber);
			System.out.println("Info: Treiber " + treiber + "[ok]");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		try {
			//Verbindungsaufbau zu DB
			System.out.print("Info: Verbindung zu " + url);
			conn = DriverManager.getConnection(url);
			System.out.println("[ok]");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return conn;
	}

	/**
	 * Boilerplate disconnect
	 * 
	 * @param conn
	 *            Connection to disconnect
	 * @return disconnected Connection
	 */
	public Connection disconnect(Connection conn) {
		try {
			//Schließe Verbindung zur DB
			conn.close();
			conn = null;
			System.out.println("Info: Connection geschlossen" + "[ok]");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * Aufgabe 1a: Zeige die Anzahl der Lieferer, die Anzahl der abgeschlossenen Lieferungen sowie die durchschnittliche 
	 * Bestellsumme der ausgelieferten Bestellungen. Melde wenn Bezirk keinen Lieferer hat.
	 * 
	 * @param conn
	 *            DB Connection
	 * @param Postleitzahl
	 *            des Lieferbezirks
	 */
	public void LieferbezirkAusgabe(Connection conn, int postleitzahl) {
		int idLieferbezirk = 0;

		// Frage ob Postleitzahl gueltig ist
		if (postleitzahl == 39850)
			idLieferbezirk = 1;
		else if (postleitzahl == 39846)
			idLieferbezirk = 2;
		else if (postleitzahl == 39001)
			idLieferbezirk = 3;
		else if (postleitzahl == 39000)
			idLieferbezirk = 4;
		else //Die nachfolgenden PLZ sind zum leichteren Test in der Ausgabe enthalten
			System.out.println("Ungueltige Postleitzahl: " + postleitzahl + "\n Moegliche Eingaben: \n" + "39850\n"
					+ "39846\n" + "39001\n" + "39000\n");

		if(this.isLieferbezirkEmpty(conn, idLieferbezirk) == true){
		    System.out.println("Lieferbezirk ohne Lieferer");
		}else{
		this.printAnzahlLieferer(conn, idLieferbezirk);
		this.printLieferer(conn, idLieferbezirk);
		this.printAnzahlAbgeschlosseneLieferungen(conn, idLieferbezirk);
		
		}
	}
	public void printAnzahlAbgeschlosseneLieferungen(Connection conn, int idLieferbezirk){
	    try {
		
		String sqlString = "select count(liefererbestaetigung.idLiefererbestaetigung)"+
			"from liefererbestaetigung join lieferer_lieferbezirk "+
"on lieferer_lieferbezirk.Lieferer_idLieferer = liefererbestaetigung.Lieferer_idLieferer"+
"join bestellung"+
"on liefererbestaetigung.Bestellung_idBestellung = bestellung.idBestellung"+
"where lieferer_lieferbezirk.Lieferbezirk_idLieferbezirk = ?"+
"and bestellung.bestellstatus = 'abgeschlossen'";
				

		PreparedStatement stmt = conn.prepareStatement(sqlString);
		stmt.setString(1, Integer.toString(idLieferbezirk));
		ResultSet rs = stmt.executeQuery();
		int anzahlLieferbestaetigungen = 0;
		while (rs.next()) {
			anzahlLieferbestaetigungen = rs.getInt(1);
			//anzahlLieferbestaetigungen = Integer.toString(rs.getInt(1));

			System.out.println("Anzahl der Lieferbestaetigungen fuer den Lieferbezirk: " + anzahlLieferbestaetigungen);
		}
		stmt.close();
		String sqlString2 = "select sum(artikel.preis * bestellposition.anzahl)"+
"from artikel JOIN bestellposition"+
"on artikel.idArtikel = bestellposition.Artikel_idArtikel"+
"JOIN bestellung"+
"on bestellposition.Bestellung_idBestellung = bestellung.idBestellung"+
"left join liefererbestaetigung"+
"on bestellung.idBestellung = liefererbestaetigung.Bestellung_idBestellung"+
"join lieferer_lieferbezirk"+
"on liefererbestaetigung.Lieferer_idLieferer = lieferer_lieferbezirk.Lieferer_idLieferer"+
"where bestellung.bestellstatus = 'abgeschlossen'"+
"and lieferer_lieferbezirk.Lieferbezirk_idLieferbezirk= ?"+
"order by artikel.idArtikel";
		PreparedStatement stmt2 = conn.prepareStatement(sqlString2);
		stmt2.setString(1, Integer.toString(idLieferbezirk));
		ResultSet rs2 = stmt.executeQuery();
		int summe = 0;
		while (rs.next()) {
			summe = rs2.getInt(1);
			int durchschnitt = summe/anzahlLieferbestaetigungen;
			System.out.println("Die durchschnittliche Bestellsumme der ausgelieferten Bestellungen ist: " + durchschnitt);
		}
		stmt2.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	public void printLieferer(Connection conn, int idLieferbezirk) {
		try {
			String sqlString = "SELECT Lieferer_idLieferer " + "FROM `lieferer_lieferbezirk` "
					+ "WHERE Lieferbezirk_idLieferbezirk = ?";

			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String idLieferer = rs.getString(1);
				// interne Datenkonvertierung: Integer.toString(rs.getInt(1));

				System.out.println("idLieferer: " + idLieferer);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wenn Bezirk keinen Lieferer hat gebe Meldung
	 * 
	 * @param conn DB Connection
	 * @param idLieferbezirk id des Lieferbezirks
	 * @return true wenn Lieferbezirk keine Lieferer hat
	 */
	public boolean isLieferbezirkEmpty(Connection conn, int idLieferbezirk) {
		try {
			String sqlString = "SELECT count(Lieferer_idLieferer) " + "from `lieferer_lieferbezirk` "
					+ "where Lieferbezirk_idLieferbezirk = ?";
			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));
			ResultSet rs = stmt.executeQuery();
			String anzahl = "";
			while (rs.next()) {
				anzahl = rs.getString(1);
			}
			if (anzahl.equals("0")) {
				stmt.close();
				return true;

			} else
				stmt.close();
			return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * "Melde wenn Bezirk keinen Lieferer hat."
	 * 
	 * @param conn DB Connection
	 * @param idLieferbezirk ID des Lieferbezirks
	 */
	public void printAnzahlLieferer(Connection conn, int idLieferbezirk) {
		try {

			String sqlString = "SELECT count(Lieferer_idLieferer) " + "from `lieferer_lieferbezirk` "
					+ "where Lieferbezirk_idLieferbezirk = ?";

			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String anzahl = rs.getString(1);
				System.out.println("Anzahl der Lieferer: " + anzahl);
			}
			stmt.close();
			
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Fehler beim schreiben in die Datenbank");
			e.printStackTrace();
		}
	}

	/**
	 * Aufgabe 2a: Aendere den Lieferbezirk eines Lieferers
	 * 
	 * @param conn DB Connection
	 * @param liefererID Lieferer, der neuen Bezirk bekommt
	 * @param lieferbezirkID neuer Bezirk des Lieferers
	 */
	public void LieferbezirkAendern(Connection conn, int liefererID, int lieferbezirkID) {
		try {
			String sqlString = "call LieferbezirkAendern(" + liefererID + "," + lieferbezirkID + ");";
			
			PreparedStatement stmt = conn.prepareStatement(sqlString);
			
			ResultSet rs = stmt.executeQuery();
			
			System.out.println("Aenderungen Erfolgreich");
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Kein Getraenkemarkt im Lieferbezirk Vorhanden");
			System.out.println("Waehlen Sie einen anderen Lieferbezirk!");
			e.printStackTrace();

		}

	}
	
	/**
	 * Aufgabe 2b/2c: Neuen Lieferer hinzufügen und dabei automatisch
	 * Zuweisungen
	 * 
	 * @param vorname Vorname des Lieferers
	 * @param liefererId Lieferer ID
	 */
	public void newLieferer(Connection conn, String vorname, int liefererId) {
		//Erstelle default Parameter
		String passwort = "letmein";
		String anrede = "Herr";
		String nachname = "Weinzierl";
		String geburtstagsdatum = "2016-2-28";
		String strasse = "Am Kehrplatz 42";
		String wohnort = "Dortmund";
		String plz = "44227";
		String tel = "0231/231231";
		String mail = "chwei012@stud.fh-dortmund.de";
		String beschreibung = "Ganz klar volle Punktzahl verdient :P";
		String konto_nr = "7095465";
		String blz = "12345678";
		String bankname = "Geldhaus";
		String lieferzeit = "12:00 bis 13:35 Uhr";
		Double lieferpreis = 42.42;
		String getraenkemarktName = "Top";

		try {
			//Erstelle Statement mit entsprechenden Parameterzuweisungen für die Proozedur
			CallableStatement stmt = conn
					.prepareCall("{call createLieferer ( ? , ? , ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			stmt.setInt(1, liefererId);
			stmt.setString(2, passwort);
			stmt.setString(3, anrede);
			stmt.setString(4, vorname);
			stmt.setString(5, nachname);
			stmt.setString(6, geburtstagsdatum);
			stmt.setString(7, strasse);
			stmt.setString(8, wohnort);
			stmt.setString(9, plz);
			stmt.setString(10, tel);
			stmt.setString(11, mail);
			stmt.setString(12, beschreibung);
			stmt.setString(13, konto_nr);
			stmt.setString(14, blz);
			stmt.setString(15, bankname);
			stmt.setString(16, lieferzeit);
			stmt.setDouble(17, lieferpreis);
			stmt.setString(18, getraenkemarktName);

			stmt.executeUpdate();
			System.out.println("Lieferer erfolgreich hinzugefuegt");
		} catch (SQLException e) {
			System.out.println("Fehler beim schreiben in die Datenbank");
			e.printStackTrace();
		}

	}
}
