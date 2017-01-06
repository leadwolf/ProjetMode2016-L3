package bddTest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Test;

import ply.bdd.other.BDDUtilities;
import ply.bdd.other.BaseDeDonnees;
import result.BDDResultEnum;
import result.BasicResultEnum;

/**
 * Teste la classe {@link BaseDeDonnees} avec EclEmma. Taux de couverture de branches à 83.7%. Impossible d'avoir plus
 * car sinon il faut tester affichage des JOptionPane, qui va contre l'objectif des tests automatisés.
 * 
 * @author L3
 *
 */
public class BaseDeDonnesTest {

	@After
	public void closeConnection() {
		BDDUtilities.closeConnection();
	}

	/* GET PANEL */
	@Test
	public void test_null_args_get_panel() {
		String[] args = new String[0];
		assertEquals(null, BaseDeDonnees.INSTANCE.getPanel(null, null, null));
	}

	@Test
	public void test_empty_args_get_panel() {
		String[] args = new String[0];
		assertEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, null));
	}

	@Test
	public void test_no_args_with_options_get_panel() {
		assertEquals(null, BaseDeDonnees.INSTANCE.getPanel(null, null, new boolean[] { true, true, true }));
	}

	@Test
	public void test_db_is_empty_get_panel() {
		String[] args = new String[] { "--all" };
		assertEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, new boolean[] { true, false, true }));
	}

	/* EXECUTE COMMAND */
	@Test
	public void test_null_args_execute_command() {
		String[] args = new String[0];
		assertEquals(BasicResultEnum.MISSING_OPTIONS,
				BaseDeDonnees.INSTANCE.executeCommand(null, null, null).getCode());
	}

	@Test
	public void test_empty_args_execute_command() {
		String[] args = new String[] { "" };
		assertEquals(BasicResultEnum.NO_ARGUMENTS,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_db_is_empty_execute_command() {
		String[] args = new String[] { "--all" };
		assertEquals(BDDResultEnum.EMPTY_DB,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, false, true }).getCode());
	}

	/* VERIF ARGS */

	@Test
	public void test_no_executable_arg() {
		String[] args = new String[] { "--lol", "hello", "--rf" };
		assertEquals(BasicResultEnum.NO_COMMAND_GIVEN,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_more_than_one_command() {
		String[] args = new String[] { "--all", "--delete", "--rf" };
		assertEquals(BasicResultEnum.CONFLICTING_ARGUMENTS,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void testname_edit_modelName_not_specified() {
		String[] args = new String[] { "--edit", "--rf" };
		assertEquals(BDDResultEnum.NAME_NOT_SPECIFIED,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void testname_name_modelName_not_specified() {
		String[] args = new String[] { "--name", "--rf" };
		assertEquals(BDDResultEnum.NAME_NOT_SPECIFIED,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_find_no_keywords() {
		String[] args = new String[] { "--find", "--rf" };
		assertEquals(BDDResultEnum.NO_KEYWORDS_SPECIFIED,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_find_too_many_keywords() {
		String[] args = new String[] { "--find", "hello", "this", "find", "command", "array", "goes", "over", "then",
				"10", "length", "limit", "--rf" };
		assertEquals(BDDResultEnum.TOO_MANY_KEYWORDS_SPECIFIED,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, true, true }).getCode());
	}

	/* DELETE */

	@Test
	public void test_delete_success() {
		String[] args = new String[] { "--delete", "weathervane", "--rf" };
		assertEquals(BDDResultEnum.DELETE_SUCCESSFUL,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_delete_model_not_found() {
		String[] args = new String[] { "--delete", "FAKE_MODEL_NAME", "--rf" };
		assertEquals(BDDResultEnum.MODEL_NOT_FOUND,
				BaseDeDonnees.INSTANCE.executeCommand(args, null, new boolean[] { true, true, true }).getCode());
	}

	/* EDIT */

	@Test
	public void test_edit_model_not_found() {
		String[] args = new String[] { "--edit", "FAKE_MODEL_NAME", "--rf" };
		assertEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, new boolean[] { true, true, true }));
	}

	@Test
	public void test_edit_model_exists() {
		String[] args = new String[] { "--edit", "galleon", "--rf" };
		assertNotEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, new boolean[] { true, true, true }));
	}

	/* ADD */

	@Test
	public void test_add() {
		String[] args = new String[] { "--add", "--rf" };
		assertNotEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, new boolean[] { true, true, true }));
	}

	/* FIND */

	@Test
	public void test_find_keywords_not_found() {
		String[] args = new String[] { "--find", "FAKE_KEYWORD", "FAKE_KEYWORD_2", "--rf" };
		assertEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, new boolean[] { true, true, true }));
	}

	@Test
	public void test_find_keywords_exist() {
		String[] args = new String[] { "--find", "mes", "mots", "--rf" };
		assertNotEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, new boolean[] { true, true, true }));
	}

	/* NAME */

	@Test
	public void test_name_model_does_not_exist() {
		String[] args = new String[] { "--name", "FAKE_MODEL_NAME", "--rf" };
		assertEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, new boolean[] { true, true, true }));
	}

	@Test
	public void test_name_model_exists() {
		String[] args = new String[] { "--name", "galleon", "--rf" };
		assertNotEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, new boolean[] { true, true, true }));
	}

	/* MODEL INFO */

	@Test
	public void test_nameInfo_fake_name() {
		assertArrayEquals(null, BaseDeDonnees.INSTANCE.getNameInfo("FAKE_MODEL_NAME", true));
	}

	@Test
	public void test_nameInfo_real_name() {
		assertFalse(BaseDeDonnees.INSTANCE.getNameInfo("galleon", true).equals(null));
	}

	/* SHOW ALL */
	@Test
	public void test_show_all() {
		String[] args = new String[] { "--all", "--rf" };
		assertNotEquals(null, BaseDeDonnees.INSTANCE.getPanel(args, null, new boolean[] { true, true, true }));
	}

}
