import java.sql.Connection;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {
	DBVerwaltung DBV = new DBVerwaltung();
	Connection conn = DBV.connect();
	
	if(conn != null) // Verbindungsaufbau war erfolgreich
	{
	    try {
		Scanner sc = new Scanner(System.in);
		
		buildMenu();
		int plz;
		while(true) {
		int opt = sc.nextInt();
		switch (opt) {
			case 1: System.out.println("Bitte geben sie eine Postleitzahl ein :");
					plz = sc.nextInt();
					//DO Something
			case 2: System.out.println("Bitte geben sie eine Postleitzahl ein :");
					plz = sc.nextInt();
					//DO Something
			case 3:
			case 4:
			case 5: sc.close();
					System.exit(0);
					
			default: System.out.print(String.format("\033[2J"));
					 buildMenu();
					 System.out.println("Bitte eine Zahl von 1-5 angeben!");
			}
		break;
		}
		//System.out.println(plz);
		
		

		//DBV.LieferbezirkAusgabe(conn, plz);

		//DBV.disconnect(conn);
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	}
    }
    
    public static void buildMenu() {
    	System.out.println(" _     _       __                              _            _   _");
		System.out.println("| |   (_)     / _|                            (_)          | | | |");
		System.out.println("| |    _  ___| |_ ___ _ __ ___  ___ _ ____   ___  ___ ___  | | | | ___ _ __   ___ _ __  _   _ _ __ ___");
		System.out.println("| |   | |/ _ |  _/ _ | '__/ __|/ _ | '__\\ \\ / | |/ __/ _ \\ | | | |/ _ | '_ \\ / _ | '_ \\| | | | '_ ` _ \\");
		System.out.println("| |___| |  __| ||  __| |  \\__ |  __| |   \\ V /| | (_|  __/ \\ \\_/ |  __| | | |  __| | | | |_| | | | | | |");
		System.out.println("\\_____|_|\\___|_| \\___|_|  |___/\\___|_|    \\_/ |_|\\___\\___|  \\___/ \\___|_| |_|\\___|_| |_|\\__,_|_| |_| |_|");
		System.out.println("========================================================================================================");
		System.out.println("[1] Statusübersicht des Lieferbezirks");
		System.out.println("[2] Auslastung der Lieferer im Bezirk");
		System.out.println("[3] Neuen Lieferer hinzufügen");
		System.out.println("[4] Lieferer neuen Lieferbezirk zuweisen");
		System.out.println("[5] Beenden");
		System.out.println("Bitte Option wählen: ");
    }

}
