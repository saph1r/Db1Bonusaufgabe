import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			// Checke ob JDBC Treiber geladen werden kann
			String treiber = "com.mysql.jdbc.Driver";
			Class.forName(treiber);
			System.out.println("Info: Treiber " + treiber + "[ok]");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		try {
			// Verbindungsaufbau zu DB
			System.out.print("Info: Verbindung zu " + url);
			conn = DriverManager.getConnection(url, "root", "");
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
			// Schließe Verbindung zur DB
			conn.close();
			conn = null;
			System.out.println("Info: Connection geschlossen" + "[ok]");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * Aufgabe 1a: Zeige die Anzahl der Lieferer, die Anzahl der abgeschlossenen
	 * Lieferungen sowie die durchschnittliche Bestellsumme der ausgelieferten
	 * Bestellungen. Melde wenn Bezirk keinen Lieferer hat.
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
		else System.out.println("Ungueltige Postleitzahl: " + postleitzahl + "\n Moegliche Eingaben: \n" + "39850\n"
					+ "39846\n" + "39001\n" + "39000\n");
		// Die nachfolgenden PLZ sind zum leichteren Test in der Ausgabe enthalten

		if (this.isLieferbezirkEmpty(conn, idLieferbezirk) == true) {
			System.out.println("Lieferbezirk ohne Lieferer");
		} else {
			this.printAnzahlLieferer(conn, idLieferbezirk);
			this.printLieferer(conn, idLieferbezirk);
			this.getAnzahlAbgeschlosseneLieferungen(conn, idLieferbezirk);
			this.printBestellSumme(conn, idLieferbezirk);
		}
	}

	/**
	 * Ausgabe der Lieferer Informationen
	 * 
	 * @param conn
	 *            DB Connection
	 * @param idLieferbezirk
	 *            id des betreffenden Lieferbezirks
	 */
	public void printLieferer(Connection conn, int idLieferbezirk) {
		try {
			//Hole Lieferer ID(s) zu entsprechendem Lieferbezirk 
			String sqlString = "SELECT Lieferer_idLieferer " + "FROM `lieferer_lieferbezirk` "
					+ "WHERE Lieferbezirk_idLieferbezirk = ?";

			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String idLieferer = rs.getString(1);
				System.out.println("idLieferer: " + idLieferer);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Erfasse "die Anzahl der abgeschlossenen Lieferungen"
	 * 
	 * @param conn
	 *            DB Connection
	 * @param idLieferbezirk
	 *            id des Lieferbezirks
	 * @return liefert Anzahl der abgeschlossenen Lieferungen zurück
	 */
	public int getAnzahlAbgeschlosseneLieferungen(Connection conn, int idLieferbezirk) {
		int anzahlLieferbestaetigungen = 0;
		try {

			// Erfasse die Anzahl der Abgeschlossen Lieferungen aus einem
			// Leiferbezirk,
			// indem lieferbestaetigungen und Lieferbezirk sowie bestellung
			// gejoint werden
			String sqlString = "SELECT count(liefererbestaetigung.idLiefererbestaetigung) "
					+ "FROM liefererbestaetigung JOIN lieferer_lieferbezirk "
					+ "ON lieferer_lieferbezirk.Lieferer_idLieferer = liefererbestaetigung.Lieferer_idLieferer "
					+ "JOIN bestellung " + "ON liefererbestaetigung.Bestellung_idBestellung = bestellung.idBestellung "
					+ "WHERE lieferer_lieferbezirk.Lieferbezirk_idLieferbezirk = ? "
					+ "AND bestellung.bestellstatus = 'abgeschlossen' ";

			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				anzahlLieferbestaetigungen = rs.getInt(1);
			}

			stmt.close();
			return anzahlLieferbestaetigungen;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return anzahlLieferbestaetigungen;
	}

	/**
	 * Erfasse Bestellsumme um den Durchschnitt auszugeben: "sowie die
	 * durchschnittliche Bestellsumme der ausgelieferten Bestellungen"
	 * 
	 * @param conn
	 * @param idLieferbezirk
	 */
	public void printBestellSumme(Connection conn, int idLieferbezirk) {
		try {

			int anzahlLieferbestaetigungen = getAnzahlAbgeschlosseneLieferungen(conn, idLieferbezirk);
			System.out.println("Anzahl der Lieferbestaetigungen fuer den Lieferbezirk: " + anzahlLieferbestaetigungen);

			// Erfasse die Summe von Anzahl der bestellten Artikel mal den
			// Artikelpreis, joine dafür vgl. oben so,dass abschlossene
			// Lieferungen einem Lieferbezirk erfasst werden und mit den
			// Artikeln sowie Bestellpositionen abgegelichen und vereinigt
			// werden können
			String sqlString = "SELECT sum(artikel.preis * bestellposition.anzahl) "
					+ "FROM artikel JOIN bestellposition " + "on artikel.idArtikel = bestellposition.Artikel_idArtikel "
					+ "JOIN bestellung " + "ON bestellposition.Bestellung_idBestellung = bestellung.idBestellung "
					+ "LEFT JOIN liefererbestaetigung "
					+ "ON bestellung.idBestellung = liefererbestaetigung.Bestellung_idBestellung "
					+ "JOIN lieferer_lieferbezirk "
					+ "ON liefererbestaetigung.Lieferer_idLieferer = lieferer_lieferbezirk.Lieferer_idLieferer "
					+ "WHERE bestellung.bestellstatus = 'abgeschlossen' "
					+ "AND lieferer_lieferbezirk.Lieferbezirk_idLieferbezirk= ? " + "order by artikel.idArtikel";

			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));
			ResultSet rs = stmt.executeQuery();
			double summe = 0;
			while (rs.next()) {
				summe = rs.getDouble(1);
				double durchschnitt = summe / anzahlLieferbestaetigungen;
				System.out.println(
						"Die durchschnittliche Bestellsumme der ausgelieferten Bestellungen ist: " + durchschnitt);
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * "Melde wenn Bezirk keinen Lieferer hat."
	 * 
	 * @param conn
	 *            DB Connection
	 * @param idLieferbezirk
	 *            ID des Lieferbezirks
	 */
	public void getAnzahlLieferer(Connection conn, int idLieferbezirk) {
		try {
			
			String sqlString = "SELECT count(Lieferer_idLieferer) " + "FROM `lieferer_lieferbezirk` "
					+ "WHERE Lieferbezirk_idLieferbezirk = ?";

			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int anzahl = rs.getInt(1);
				System.out.println("Anzahl der Lieferer: " + anzahl);
			}
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wenn Bezirk keinen Lieferer hat gebe Meldung
	 * 
	 * @param conn
	 *            DB Connection
	 * @param idLieferbezirk
	 *            id des Lieferbezirks
	 * @return true wenn Lieferbezirk keine Lieferer hat
	 */
	public boolean isLieferbezirkEmpty(Connection conn, int idLieferbezirk) {
		try {
			String sqlString = "SELECT count(Lieferer_idLieferer) " + "FROM `lieferer_lieferbezirk` "
					+ "WHERE Lieferbezirk_idLieferbezirk = ?";
			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));

			// ausführen
			ResultSet rs = stmt.executeQuery();
			int anzahl = 0;
			// null guard
			while (rs.next()) {
				anzahl = rs.getInt(1);
			}
			if (anzahl == 0) {
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
	 * @param conn
	 *            DB Connection
	 * @param idLieferbezirk
	 *            ID des Lieferbezirks
	 */
	public void printAnzahlLieferer(Connection conn, int idLieferbezirk) {
		try {
			String sqlString = "SELECT count(Lieferer_idLieferer) " + "FROM `lieferer_lieferbezirk` "
					+ "WHERE Lieferbezirk_idLieferbezirk = ?";

			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));

			// ausführen
			ResultSet rs = stmt.executeQuery();
			// NULL guard
			while (rs.next()) {
				String anzahl = rs.getString(1);
				System.out.println("Anzahl der Lieferer: " + anzahl);
			}

			stmt.close();
		} catch (SQLException e) {
			System.out.println("Fehler beim schreiben in die Datenbank");
			e.printStackTrace();
		}
	}

	/**
	 * Aufgabe 2a: Aendere den Lieferbezirk eines Lieferers
	 * 
	 * @param conn
	 *            DB Connection
	 * @param liefererID
	 *            Lieferer, der neuen Bezirk bekommt
	 * @param lieferbezirkID
	 *            neuer Bezirk des Lieferers
	 */
	public void LieferbezirkAendern(Connection conn, int liefererID, int lieferbezirkID) {
		try {
			String sqlString = "call LieferbezirkAendern(" + liefererID + "," + lieferbezirkID + ");";

			PreparedStatement stmt = conn.prepareStatement(sqlString);

			// ausführen und schließen
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
	 * @param vorname
	 *            Vorname des Lieferers
	 * @param liefererId
	 *            Lieferer ID
	 */
	public void newLieferer(Connection conn, String vorname, int liefererId) {
		// Erstelle default Parameter
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
			// Erstelle Statement mit entsprechenden Parameterzuweisungen für
			// die Proozedur
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

			// ausführen und schließen
			stmt.executeUpdate();

			System.out.println("Lieferer erfolgreich hinzugefuegt");
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Fehler beim schreiben in die Datenbank");
			e.printStackTrace();
		}

	}

}
