import java.sql.Connection;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {
	DBVerwaltung DBV = new DBVerwaltung();

	Connection conn = DBV.connect();

	if (conn != null) // Verbindungsaufbau war erfolgreich
	{
	    try {
		Scanner sc = new Scanner(System.in);

		System.out.println("Bitte geben sie eine Postleitzahl ein :");

		int plz = sc.nextInt();
		System.out.println(plz);

		DBV.LieferbezirkAusgabe(conn, plz);

		DBV.disconnect(conn);
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	}
    }

}
