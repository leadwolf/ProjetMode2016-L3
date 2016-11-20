package bddTest;

import static org.junit.Assert.*;

import org.junit.Test;

import bdd.BaseDeDonnees;
import erreur.BDDResultEnum;
import erreur.BasicResultEnum;

/**
 * Classe de test de fonctionnement de la classe BaseDeDonnees
 * 
 * @author L3
 *
 */
public class BaseDeDonneesTest {

	/**
	 * Vérifie que la commande --all liste correctement des modèles avec une base vide et remplie
	 */
	@Test
	public void testAll() {

		// on vient de reinitialiser et remplir la base (true, true)
		String[] args = new String[] { "--all" };
		assertEquals(BDDResultEnum.SHOW_ALL_SUCCESSFUL, BaseDeDonnees.parseArgs(args, true, true, true).getCode());
		BaseDeDonnees.closeConnection();

		// on a reinitialisé la base mais pas rempli
		assertEquals(BDDResultEnum.EMPTY_DB, BaseDeDonnees.parseArgs(args, true, false, true).getCode());
		BaseDeDonnees.closeConnection();
	}

	/**
	 * Vérifie que la commande --name répond correctement avec un modèle existant ainsi qu'un non existant
	 */
	@Test
	public void testName() {
		
		// on vient de reinitialiser et remplir la base
		String[] args = new String[] { ("--name"), ("weathervane") };
		assertEquals(BDDResultEnum.SHOW_NAME_SUCCESSFUL, BaseDeDonnees.parseArgs(args, true, true, true).getCode());
		BaseDeDonnees.closeConnection();

		// on a reinitialise mais pas rempli la base
		assertEquals(BDDResultEnum.EMPTY_DB, BaseDeDonnees.parseArgs(args, true, false, true).getCode());
		BaseDeDonnees.closeConnection();

		// le modèle hello n'existe pas
		String[] args2 = new String[] { ("--name"), ("hello") };
		assertEquals(BDDResultEnum.MODEL_NOT_FOUND, BaseDeDonnees.parseArgs(args2, false, true, true).getCode());
		BaseDeDonnees.closeConnection();
	}

	/**
	 * Vérifie que la commande --find répond correctement avec des mot clés existant ainsi que non existants
	 */
	@Test
	public void testFind() {
		
		// aucun modèle comporte le mot cle hello
		String[] args = new String[] { ("--find"), ("hello") };
		assertEquals(BDDResultEnum.MODEL_NOT_FOUND, BaseDeDonnees.parseArgs(args, true, true, true).getCode());
		BaseDeDonnees.closeConnection();

		// la base est vide
		String[] args2 = new String[] { ("--find"), ("mot") };
		assertEquals(BDDResultEnum.EMPTY_DB, BaseDeDonnees.parseArgs(args2, true, false, true).getCode());
		BaseDeDonnees.closeConnection();
		
		// tous les modèles comportent celui ci
		assertEquals(BDDResultEnum.FIND_SUCCESSFUL, BaseDeDonnees.parseArgs(args2, true, true, true).getCode());
		BaseDeDonnees.closeConnection();

		// la base est rempli et "mes mots" existent dans les mots cles
		String[] args3 = new String[] { ("--find"), ("mes mots") };
		assertEquals(BDDResultEnum.FIND_SUCCESSFUL, BaseDeDonnees.parseArgs(args3, false, false, true).getCode());
		BaseDeDonnees.closeConnection();
	}

	/**
	 * Vérifie que la commande --delete répond correctement
	 */
	@Test
	public void testdelete() {

		// la base est vide
		String[] args = new String[] { ("--delete"), ("weathervane") };
		assertEquals(BDDResultEnum.EMPTY_DB, BaseDeDonnees.parseArgs(args, true, false, true).getCode());
		BaseDeDonnees.closeConnection();

		// la base vient d'être remplie
		assertEquals(BDDResultEnum.DELTE_SUCCESSFUL, BaseDeDonnees.parseArgs(args, true, true, true).getCode());
		BaseDeDonnees.closeConnection();

		// on vient de la supprimer
		assertEquals(BDDResultEnum.MODEL_NOT_FOUND, BaseDeDonnees.parseArgs(args, false, false, true).getCode());
		BaseDeDonnees.closeConnection();

	}

}
