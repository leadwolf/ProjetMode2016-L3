package mainTest;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import ply.main.Modelisationator;
import ply.result.BDDResult.BDDResultEnum;
import ply.result.BasicResult.BasicResultEnum;
import ply.result.ReaderResult.ReaderResultEnum;

public class ModelisationatorTest {

	private Modelisationator modelisationator;

	@Before
	public void setup() {
		modelisationator = new Modelisationator();
	}

	/* VERIF ARGS */

	@Test
	public void test_no_args() {
		String[] args = new String[0];
		assertEquals(BasicResultEnum.NO_ARGUMENTS,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_empty_args() {
		String[] args = new String[] { "" };
		assertEquals(BasicResultEnum.NO_ARGUMENTS,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_bad_arg() {
		String[] args = new String[] { "hello" };
		assertEquals(BasicResultEnum.NO_COMMAND_GIVEN,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	/* DB ARGS */

	@Test
	public void test_dbArg_correct() {
		String[] args = new String[] { "--all", "--reset", "--fill" };
		assertEquals(BasicResultEnum.ALL_OK, Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_dbArg_small_correct() {
		String[] args = new String[] { "--all", "--r", "--f" };
		assertEquals(BasicResultEnum.ALL_OK, Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_dbArg_forgot_fill() {
		String[] args = new String[] { "--all", "--r", };
		assertEquals(BDDResultEnum.INCORRECT_RESET,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_dbArg_forgot_reset() {
		String[] args = new String[] { "--all", "--f", };
		assertEquals(BDDResultEnum.INCORRECT_FILL,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_dbArg_incorrect_param() {
		String[] args = new String[] { "--all", "--h", };
		assertEquals(BasicResultEnum.UNKNOWN_ARG,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_dbArg_missing_string() {
		String[] args = new String[] { "--name", };
		assertEquals(BDDResultEnum.NAME_NOT_SPECIFIED,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_direct_execution() {
		String[] args = new String[] { "--delete", "galleon", "--rf" };
		assertEquals(BDDResultEnum.DELETE_SUCCESSFUL,
				Modelisationator.parseArgs(args, modelisationator, Paths.get("test-data/test.sqlite"), true).getCode());
	}

	/* 3D ARGS */

	@Test
	public void test_3DArg() {
		String[] args = new String[] { "data/galleon.ply" };
		assertEquals(BasicResultEnum.ALL_OK, Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_3DArg_with_params() {
		String[] args = new String[] { "data/galleon.ply", "-f", "-s", "-p" };
		assertEquals(BasicResultEnum.ALL_OK, Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_3DArg_with_bad_params() {
		String[] args = new String[] { "data/galleon.ply", "-h" };
		assertEquals(BasicResultEnum.UNKNOWN_ARG,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	/* PLY FILE */

	@Test
	public void test_3DArg_multiple_ply() {
		String[] args = new String[] { "data/galleon.ply", "data/egret.ply" };
		assertEquals(BasicResultEnum.MULTIPLE_PLY_ARGS,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	@Test
	public void test_3DArg_param_but_no_file() {
		String[] args = new String[] { "-f", };
		assertEquals(BasicResultEnum.NO_PLY_FILE_IN_ARG,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	/* LECTURE */

	@Test
	public void test_get_lecture_error() {
		String[] args = new String[] { "test-data/missingCoord.ply" };
		assertEquals(ReaderResultEnum.MISSING_COORD,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

	/* OTHER */

	@Test
	public void test_conflicting_args() {
		String[] args = new String[] { "data/galleon.ply", "--all" };
		assertEquals(BasicResultEnum.CONFLICTING_ARGUMENTS,
				Modelisationator.parseArgs(args, modelisationator, null, true).getCode());
	}

}
