import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;

public class DBVerwaltung {

	public Connection connect() {

		Connection conn = null;

		String host = "jdbc:mysql://localhost:3306/";
		String dbName = "venenumbonus";
		String username = "root";
		String password = "";

		String url = host + dbName + "?user=" + username + "?password=" + password;

		try {
			String treiber = "com.mysql.jdbc.Driver";
			Class.forName(treiber);
			System.out.println("Info: Treiber " + treiber + "[ok]");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		try {
			System.out.print("Info: Verbindung zu " + url);
			// conn = DriverManager.getConnection(url);
			conn = DriverManager.getConnection(url, "root", "");
			System.out.println("[ok]");
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return null;
		}

		return conn;
	}

	public Connection disconnect(Connection conn) {
		try {
			conn.close();
			conn = null;
			System.out.println("Info: Connection geschlossen" + "[ok]");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void LieferbezirkAusgabe(Connection conn, int postleitzahl) {
		int idLieferbezirk = 0;

		// Frage ob Postleitzahl g�ltig ist

		if (postleitzahl == 39850)
			idLieferbezirk = 1;
		else if (postleitzahl == 39846)
			idLieferbezirk = 2;
		else if (postleitzahl == 39001)
			idLieferbezirk = 3;
		else if (postleitzahl == 39000)
			idLieferbezirk = 4;
		else
			System.out.println("Ung�ltige Postleitzahl: " + postleitzahl + "\n g�ltige zahlen sind: \n" + "39850\n"
					+ "39846\n" + "39001\n" + "39000\n");

		try {
			if (((Number) this.getAnzahlLieferer(conn, idLieferbezirk).getObject(1)).intValue() == 0) {
				System.out.println("Lieferbezirk ohne Lieferer");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getAnzahlLieferer(conn, idLieferbezirk);
		this.getLieferer(conn, idLieferbezirk);

	}

	public void getLieferer(Connection conn, int idLieferbezirk) {
		try {

			String sqlString = "Select Lieferer_idLieferer " + "from `lieferer_lieferbezirk` "
					+ "where Lieferbezirk_idLieferbezirk = ?";

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/****
	 * Aufgabe 2b/2c
	 * 
	 * @param vorname
	 *            Vorname des Lieferers
	 * @param liefererId
	 *            Lieferer ID
	 */
	public void newLieferer(Connection conn, String vorname, int liefererId) {
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
		int idLieferbezirk = 2;
		String lieferzeit = "12 , ?bis 13:35 Uhr";
		Double lieferpreis = 42.42;
		String getraenkemarktName = "Top";

		try {
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
			System.out.println("Lieferer erfolgreich hinzugefügt");
		} catch (SQLException e) {
			System.out.println("Fehler beim schreiben in die Datenbank");
		}

	}

	public ResultSet getAnzahlLieferer(Connection conn, int idLieferbezirk) {
		try {

			String sqlString = "SELECT count(Lieferer_idLieferer) " + "from `lieferer_lieferbezirk` "
					+ "where Lieferbezirk_idLieferbezirk = ?";

			PreparedStatement stmt = conn.prepareStatement(sqlString);
			stmt.setString(1, Integer.toString(idLieferbezirk));
			System.out.println(idLieferbezirk);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String anzahl = rs.getString(1);
				// interne Datenkonvertierung: Integer.toString(rs.getInt(1))

				System.out.println("Anzahl: " + anzahl);
			}
			stmt.close();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void LieferbezirkAendern(Connection conn, int liefererID, int lieferbezirkID) {
		try {
			String sqlString = "call LieferbezirkAendern(" + liefererID + "," + lieferbezirkID + ");";
			PreparedStatement stmt = conn.prepareStatement(sqlString);
			ResultSet rs = stmt.executeQuery();
			System.out.println("Änderungen Erfolgreich");
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Kein Getränkemarkt im Lieferbezirk Vorhanden");
			System.out.println("Wählen Sie einen anderen Lieferbezirk!");
			e.printStackTrace();

		}

	}

}
