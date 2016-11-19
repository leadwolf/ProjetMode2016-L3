package bddTest;

import static org.junit.Assert.*;

import org.junit.Test;

import bdd.BaseDeDonnees;

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

		// true car on vient de reinitialiser et remplir la base (true, true)
		String[] args = new String[] { "--all" };
		assertTrue(BaseDeDonnees.parseArgs(args, true, true, true));
		BaseDeDonnees.closeConnection();

		// false car on a reinitialisé la base mais pas rempli
		assertFalse(BaseDeDonnees.parseArgs(args, true, false, true));
		BaseDeDonnees.closeConnection();
	}

	/**
	 * Vérifie que la commande --name répond correctement avec un modèle existant ainsi qu'un non existant
	 */
	@Test
	public void testName() {
		
		// true car on vient de reinitialiser et remplir la base
		String[] args = new String[] { ("--name"), ("weathervane") };
		assertTrue(BaseDeDonnees.parseArgs(args, true, true, true));
		BaseDeDonnees.closeConnection();

		// false car on a reinitialise mais pas rempli la base
		assertFalse(BaseDeDonnees.parseArgs(args, true, false, true));
		BaseDeDonnees.closeConnection();

		// false car le modèle hello n'existe pas
		String[] args2 = new String[] { ("--name"), ("hello") };
		assertFalse(BaseDeDonnees.parseArgs(args2, false, true, true));
		BaseDeDonnees.closeConnection();
	}

	/**
	 * Vérifie que la commande --find répond correctement avec des mot clés existant ainsi que non existants
	 */
	@Test
	public void testFind() {
		
		// false car aucun modèle comporte le mot cle hello
		String[] args = new String[] { ("--find"), ("hello") };
		assertFalse(BaseDeDonnees.parseArgs(args, true, true, true));
		BaseDeDonnees.closeConnection();

		// false car la base est vide
		String[] args2 = new String[] { ("--find"), ("mot") };
		assertTrue(BaseDeDonnees.parseArgs(args2, false, false, true));
		BaseDeDonnees.closeConnection();
		
		// true car tous les modèles comportent celui ci
		assertTrue(BaseDeDonnees.parseArgs(args2, true, true, true));
		BaseDeDonnees.closeConnection();

		// true car la base est rempli et "mes mots" existent dans les mots cles
		String[] args3 = new String[] { ("--find"), ("mes mots") };
		assertTrue(BaseDeDonnees.parseArgs(args3, false, false, true));
		BaseDeDonnees.closeConnection();
	}

	/**
	 * Vérifie que la commande --delete répond correctement
	 */
	@Test
	public void testdelete() {

		// false car la base est vide
		String[] args = new String[] { ("--delete"), ("weathervane") };
		assertFalse(BaseDeDonnees.parseArgs(args, true, false, true));
		BaseDeDonnees.closeConnection();

		// true car la base vient d'être remplie
		assertTrue(BaseDeDonnees.parseArgs(args, true, true, true));
		BaseDeDonnees.closeConnection();

		// false car on vient de la supprimer
		assertFalse(BaseDeDonnees.parseArgs(args, false, false, true));
		BaseDeDonnees.closeConnection();

	}

}
