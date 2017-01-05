import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBVerwaltung {

    public Connection connect() {

	Connection conn = null;

	String host = "jdbc:mysql://localhost:3306/";
	String dbName = "venenumbonus";
	String username = "root";
	String password = "";

	String url = host + dbName + "?user=" + username + "?password="
		+ password;

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

	// Frage ob Postleitzahl gültig ist

	if (postleitzahl == 39850)
	    idLieferbezirk = 1;
	else if (postleitzahl == 39846)
	    idLieferbezirk = 2;
	else if (postleitzahl == 39001)
	    idLieferbezirk = 3;
	else if (postleitzahl == 39000)
	    idLieferbezirk = 4;
	else
	    System.out.println("Ungültige Postleitzahl: " + postleitzahl
		    + "\n gültige zahlen sind: \n" + "39850\n" + "39846\n"
		    + "39001\n" + "39000\n");

	try {
	    if (((Number) this.getAnzahlLieferer(conn, idLieferbezirk).getObject(1)).intValue() != 0) {
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

	    String sqlString = "Select Lieferer_idLieferer "
		    + "from `lieferer_lieferbezirk` "
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

    public ResultSet getAnzahlLieferer(Connection conn, int idLieferbezirk) {
	try {

	    String sqlString = "SELECT count(Lieferer_idLieferer) "
		    + "from `lieferer_lieferbezirk` "
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
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }
}

