package bddTest;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;

import bdd.BaseDeDonnees;

/**
 * Classe de test de fonctionnement de la classe BaseDeDonnees
 * @author L3
 *
 */
public class BaseDeDonneesTest {

	Connection connection;
	
	/**
	 * Initialise la connection JDBC vers le fichier sqlite
	 */
	@Before
	public void initConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:data/test.sqlite");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Vérifie que la commande --all liste correctement des modèles avec une base vide et remplie
	 */
	@Test
	public void testAll() {
		BaseDeDonnees.resetTable(connection);
		BaseDeDonnees.fillTable(connection);
		
		String[] args = new String[]{"--all"};
		assertTrue(BaseDeDonnees.parseArgs(args, true));
		

		BaseDeDonnees.resetTable(connection);
		assertFalse(BaseDeDonnees.parseArgs(args, true));
	}
	
	/**
	 * Vérifie que la commande --name répond correctement avec un modèle existant ainsi qu'un non existant
	 */
	@Test
	public void testName() {
		BaseDeDonnees.resetTable(connection);
		BaseDeDonnees.fillTable(connection);
		
		String[] args = new String[]{("--name"), ("weathervane")};
		assertTrue(BaseDeDonnees.parseArgs(args, true));
		
		BaseDeDonnees.resetTable(connection);
		assertFalse(BaseDeDonnees.parseArgs(args, true));
		
		BaseDeDonnees.fillTable(connection);
		String[] args2 = new String[]{("--name"), ("hello")};
		assertFalse(BaseDeDonnees.parseArgs(args2, true));
	}
	
	/**
	 * Vérifie que la commande --find répond correctement avec des mot clés existant ainsi que non existants
	 */
	@Test
	public void testFind() {
		BaseDeDonnees.resetTable(connection);
		BaseDeDonnees.fillTable(connection);
		
		String[] args = new String[]{("--find"), ("hello")};
		assertFalse(BaseDeDonnees.parseArgs(args, true));

		String[] args2 = new String[]{("--find"), ("mot")};
		assertTrue(BaseDeDonnees.parseArgs(args2, true));

		String[] args3 = new String[]{("--find"), ("mes mots")};
		assertTrue(BaseDeDonnees.parseArgs(args3, true));
		
		BaseDeDonnees.resetTable(connection);
		assertFalse(BaseDeDonnees.parseArgs(args3, true));
	}
		
	/**
	 * Vérifie que la commande --delete répond correctement
	 */
	@Test
	public void testdelete() {
		BaseDeDonnees.resetTable(connection);

		String[] args = new String[]{("--delete"), ("weathervane")};
		assertFalse(BaseDeDonnees.parseArgs(args, true));
		
		BaseDeDonnees.fillTable(connection);
		
		assertTrue(BaseDeDonnees.parseArgs(args, true));
		
		assertFalse(BaseDeDonnees.parseArgs(args, true));
		
	}

}
