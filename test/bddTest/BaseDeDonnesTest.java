package bddTest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Test;

import ply.bdd.base.BDDUtilities;
import ply.bdd.base.DAO;
import ply.bdd.strategy.BDDPanelStrategy;
import ply.bdd.strategy.DataBaseStrategy;
import ply.bdd.strategy.ExecuteStrategy;
import result.BDDResult.BDDResultEnum;
import result.BasicResult.BasicResultEnum;
import result.DataBaseCommandResult;

/**
 * Teste les stratégies {@link ExecuteStrategy} et {@link BDDPanelStrategy}.
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
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertEquals(null, strategy.treatArguments(null, null, null));
	}

	@Test
	public void test_empty_args_get_panel() {
		String[] args = new String[0];
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertEquals(null, strategy.treatArguments(args, null, null));
	}

	@Test
	public void test_no_args_with_options_get_panel() {
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertEquals(null,
				strategy.treatArguments(null, null, new boolean[] { true, true, true }));
	}

	@Test
	public void test_db_is_empty_get_panel() {
		String[] args = new String[] { "--all" };
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertEquals(null,
				strategy.treatArguments(args, null, new boolean[] { true, false, true }));
	}

	/* EXECUTE COMMAND */
	@Test
	public void test_null_args_execute_command() {
		String[] args = new String[0];
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BasicResultEnum.MISSING_OPTIONS,
				strategy.treatArguments(null, null, null).getCode());
	}

	@Test
	public void test_empty_args_execute_command() {
		String[] args = new String[] { "" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BasicResultEnum.NO_ARGUMENTS,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_db_is_empty_execute_command() {
		String[] args = new String[] { "--all" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BDDResultEnum.EMPTY_DB,
				strategy.treatArguments(args, null, new boolean[] { true, false, true }).getCode());
	}

	/* VERIF ARGS */

	@Test
	public void test_no_executable_arg() {
		String[] args = new String[] { "--lol", "hello", "--rf" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BasicResultEnum.NO_COMMAND_GIVEN,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_more_than_one_command() {
		String[] args = new String[] { "--all", "--delete", "--rf" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BasicResultEnum.CONFLICTING_ARGUMENTS,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void testname_edit_modelName_not_specified() {
		String[] args = new String[] { "--edit", "--rf" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BDDResultEnum.NAME_NOT_SPECIFIED,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void testname_name_modelName_not_specified() {
		String[] args = new String[] { "--name", "--rf" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BDDResultEnum.NAME_NOT_SPECIFIED,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_find_no_keywords() {
		String[] args = new String[] { "--find", "--rf" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BDDResultEnum.NO_KEYWORDS_SPECIFIED,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_find_too_many_keywords() {
		String[] args = new String[] { "--find", "hello", "this", "find", "command", "array", "goes", "over", "then",
				"10", "length", "limit", "--rf" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BDDResultEnum.TOO_MANY_KEYWORDS_SPECIFIED,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getCode());
	}

	/* DELETE */

	@Test
	public void test_delete_success() {
		String[] args = new String[] { "--delete", "weathervane", "--rf" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BDDResultEnum.DELETE_SUCCESSFUL,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getCode());
	}

	@Test
	public void test_delete_model_not_found() {
		String[] args = new String[] { "--delete", "FAKE_MODEL_NAME", "--rf" };
		DataBaseStrategy strategy = new ExecuteStrategy();
		assertEquals(BDDResultEnum.MODEL_NOT_FOUND,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getCode());
	}

	/* EDIT */

	@Test
	public void test_edit_model_not_found() {
		String[] args = new String[] { "--edit", "FAKE_MODEL_NAME", "--rf" };
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertEquals(null,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getPanelResult());
	}

	@Test
	public void test_edit_model_exists() {
		String[] args = new String[] { "--edit", "galleon", "--rf" };
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertNotEquals(null,
				new DataBaseCommandResult(strategy.treatArguments(args, null, new boolean[] { true, true, true }).getPanelResult()));
	}

	/* ADD */

	@Test
	public void test_add() {
		String[] args = new String[] { "--add", "--rf" };
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertNotEquals(null,
				new DataBaseCommandResult(strategy.treatArguments(args, null, new boolean[] { true, true, true }).getPanelResult()));
	}

	/* FIND */

	@Test
	public void test_find_keywords_not_found() {
		String[] args = new String[] { "--find", "FAKE_KEYWORD", "FAKE_KEYWORD_2", "--rf" };
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertEquals(null,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getPanelResult());
	}

	@Test
	public void test_find_keywords_exist() {
		String[] args = new String[] { "--find", "mes", "mots", "--rf" };
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertNotEquals(null,
				new DataBaseCommandResult(strategy.treatArguments(args, null, new boolean[] { true, true, true }).getPanelResult()));
	}

	/* NAME */

	@Test
	public void test_name_model_does_not_exist() {
		String[] args = new String[] { "--name", "FAKE_MODEL_NAME", "--rf" };
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertEquals(null,
				strategy.treatArguments(args, null, new boolean[] { true, true, true }).getPanelResult());
	}

	@Test
	public void test_name_model_exists() {
		String[] args = new String[] { "--name", "galleon", "--rf" };
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertNotEquals(null,
				new DataBaseCommandResult(strategy.treatArguments(args, null, new boolean[] { true, true, true }).getPanelResult()));
	}

	/* MODEL INFO */

	@Test
	public void test_nameInfo_fake_name() {
		assertArrayEquals(null, DAO.INSTANCE.getNameInfo("FAKE_MODEL_NAME", true));
	}

	@Test
	public void test_nameInfo_real_name() {
		assertFalse(DAO.INSTANCE.getNameInfo("galleon", true).equals(null));
	}

	/* SHOW ALL */
	@Test
	public void test_show_all() {
		String[] args = new String[] { "--all", "--rf" };
		DataBaseStrategy strategy = new BDDPanelStrategy();
		assertNotEquals(null,
				new DataBaseCommandResult(strategy.treatArguments(args, null, new boolean[] { true, true, true }).getPanelResult()));
	}

}
