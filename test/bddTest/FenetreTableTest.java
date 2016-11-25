package bddTest;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bdd.BaseDeDonnees;
import bddInterface.FenetreTable;
import erreur.BDDResultEnum;

/**
 * Classe de test de fonctionnement de la classe {@link FenetreTable},
 * spécifiquement de l'insertion et update
 * 
 * @author L3
 *
 */
public class FenetreTableTest {

	/**
	 * Cette connection est persistante pour toutes les méthodes de test
	 */
	Connection connection;
	/**
	 * Fenêtre qui sera utilisée pour exécuter les requêtes SQL
	 */
	FenetreTable fen;

	/**
	 * Initialise la connection JDBC vers le fichier sqlite avant chaque test
	 */
	@Before
	public void initConnection() {
		boolean success = false;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:data/test.sqlite");

			success = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
	}

	/**
	 * Ferme la connetion après chaque test
	 */
	@After
	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initialise une {@link FenetreTable} avec les données correspondantes au
	 * nom du modele fourni
	 * 
	 * @param model
	 *            le modèle concerné par la Fenêtre d'édition, null si veut
	 *            champs vides
	 */
	public void initFenetreTableEdit(String model) {
		boolean success = false;
		if (model != null) {
			try {
				PreparedStatement st;
				st = connection.prepareStatement("select * from ply where nom = ?");
				st.setString(1, model);
				ResultSet rs = st.executeQuery();

				fen = new FenetreTable(1, 4, rs, connection);

				success = true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {
						connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else {
			fen = new FenetreTable(1, 4, null, connection);
		}
	}

	
	
	public void reset() {
		BaseDeDonnees.resetTable(connection);
		BaseDeDonnees.fillTable(connection);
		BaseDeDonnees.closeConnection();
	}

	/**
	 * Vérifie que la commande --edit permet d'éxecuter une requête SQL
	 * correctement avec des paramètres totalement inxestants, à moitié
	 * renseignés ou complets
	 */
	@Test
	public void test_update_aucun_champ_valide() {
		initFenetreTableEdit("weathervane");
		assertEquals(BDDResultEnum.NO_VALUES_SPECIFIED, fen.updateTableAmorce("", "", "", true).getCode());
	}
	
	@Test
	public void test_update_meme_champ(){
		reset();
		initFenetreTableEdit("weathervane");
		assertEquals(BDDResultEnum.NO_DIFFERENT_VALUES,
				fen.updateTableAmorce("weathervane", "ply/weathervane.ply", "mes mots clés", true).getCode());
	}
	
	@Test
	public void test_update_au_moins_un_champ_valide(){
		reset();
		initFenetreTableEdit("weathervane");
		assertEquals(BDDResultEnum.UPDATE_SUCCESSFUL, fen.updateTableAmorce("weathervane2", "", "", true).getCode());

		
	}
	@Test
	public void test_update_champs_Valide(){
		reset();
		initFenetreTableEdit("weathervane");
		fen.updateTableAmorce("weathervane2", "", "", true);
		initFenetreTableEdit("weathervane2");
		assertEquals(BDDResultEnum.UPDATE_SUCCESSFUL,
				fen.updateTableAmorce("weathervane3", "weathervane3", "weathervane3", true).getCode());
	}
		@Test
		public void test_update_au_moins_un_valide(){
		initFenetreTableEdit("weathervane");
		fen.updateTableAmorce("weathervane2", "", "", true);
		initFenetreTableEdit("weathervane2");
		fen.updateTableAmorce("weathervane3", "weathervane3", "weathervane3", true);
		initFenetreTableEdit("weathervane3");
		
		assertEquals(BDDResultEnum.UPDATE_SUCCESSFUL, fen.updateTableAmorce("", "", "allo", true).getCode());
	}

	/**
	 * Vérifie que la commande --add permet d'éxecuter une requête SQL
	 * correctement avec des paramètres totalement inxestants, à moitié
	 * renseignés ou avec un nom qui existe déja
	 */
	@Test
	public void test_insert_nom_pas_renseigne() {
		initFenetreTableEdit(null);
		assertEquals(BDDResultEnum.NO_VALUES_SPECIFIED, fen.insertTableAmorce("", "", "", true).getCode());
	}
	
	@Test
	public void test_nom_existe_deja(){
		// nom existe déja
		initFenetreTableEdit(null);
		assertEquals(BDDResultEnum.PRE_EXISTING_MODEL, fen.insertTableAmorce("weathervane", "", "", true).getCode());
	}
	
	@Test
	public void test_non_renseigne_et_existe_pas(){
		reset();
		// nom renseigné et n'existe pas
		initFenetreTableEdit(null);
		assertEquals(BDDResultEnum.INSERT_SUCCESSFUL, fen.insertTableAmorce("weathervane2", "", "", true).getCode());
	}
		@Test
		public void test_valeurs_renseigne_qui_nexiste_pas(){
			reset();
		// valeurs renseignés avec nom inexistant dans la base
		initFenetreTableEdit(null);
		assertEquals(BDDResultEnum.INSERT_SUCCESSFUL,
				fen.insertTableAmorce("weathervane3", "allo", "allo", true).getCode());
	}

}
