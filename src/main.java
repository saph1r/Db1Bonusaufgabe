import java.sql.Connection;
import java.util.Scanner;

/**
 * Bonusaufgabe Datenbanken 1
 *
 * @author Sebastian Donath, Christoph Weinzierl
 */
public class main {

	public static void main(String[] args) {
		DBVerwaltung DBV = new DBVerwaltung();
		Connection conn = DBV.connect();

		if (conn != null) // Verbindungsaufbau war erfolgreich
		{
			try {
				Scanner sc = new Scanner(System.in);
				int plz;
				while (true) {
					buildMenu();
					int opt = sc.nextInt();
					switch (opt) {
					case 1:
						System.out.print("Bitte geben sie eine Postleitzahl ein :");
						plz = sc.nextInt();
						DBV.LieferbezirkAusgabe(conn, plz);
						break;
					case 2:
						System.out.print("Bitte geben sie einen Vornamen ein: ");
						String vorname = sc.next();
						System.out.print("Bitte geben sie eine freie Lieferer ID ein: ");
						int idLieferer = sc.nextInt();
						DBV.newLieferer(conn, vorname, idLieferer);
						break;
					case 3:
						System.out.print("LiefererID: ");
						int LiefererID = sc.nextInt();
						System.out.print("LieferbezirkID: ");
						int LieferbezirkID = sc.nextInt();
						DBV.LieferbezirkAendern(conn, LiefererID, LieferbezirkID);
						break;
					case 4:
						sc.close();
						DBV.disconnect(conn);
						System.exit(0);
						break;
					}
					System.out.print(String.format("\033[2J"));
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static void buildMenu() {
		System.out.println(" _     _       __                              _            _   _");
		System.out.println("| |   (_)     / _|                            (_)          | | | |");
		System.out.println(
				"| |    _  ___| |_ ___ _ __ ___  ___ _ ____   ___  ___ ___  | | | | ___ _ __   ___ _ __  _   _ _ __ ___");
		System.out.println(
				"| |   | |/ _ |  _/ _ | '__/ __|/ _ | '__\\ \\ / | |/ __/ _ \\ | | | |/ _ | '_ \\ / _ | '_ \\| | | | '_ ` _ \\");
		System.out.println(
				"| |___| |  __| ||  __| |  \\__ |  __| |   \\ V /| | (_|  __/ \\ \\_/ |  __| | | |  __| | | | |_| | | | | | |");
		System.out.println(
				"\\_____|_|\\___|_| \\___|_|  |___/\\___|_|    \\_/ |_|\\___\\___|  \\___/ \\___|_| |_|\\___|_| |_|\\__,_|_| |_| |_|");
		System.out.println(
				"========================================================================================================");
		System.out.println("[1] Auslastung der Lieferer");
		System.out.println("[2] Neuen Lieferer hinzufügen");
		System.out.println("[3] Lieferer neuen Lieferbezirk zuweisen - 2a");
		System.out.println("[4] Beenden");
		System.out.println("Bitte eine Zahl von 1-5 angeben!");
		System.out.print("Bitte Option wählen: ");
	}

}
