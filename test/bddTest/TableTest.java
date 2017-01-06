package bddTest;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import ply.bdd.other.BDDUtilities;
import ply.bdd.other.Table;
import ply.bdd.other.TableDataModel;
import result.BDDResultEnum;

public class TableTest {

	private Table table;
	private TableDataModel dm;
	private String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés", "Nombre de Points",
			"Nombre de Faces" };
	private static String[] buttonColumns = new String[] { "Confirmer insertion/edition", "Reset", "Supprimer" };

	@After
	public void closeConnection() {
		BDDUtilities.closeConnection();
	}

	private void initTableWithDataFromDB(String modelName) {
		List<String[]> data = new ArrayList<>();
		try {
			BDDUtilities.initConnection(Paths.get("test-data/test.sqlite")); // get le fichier .sqlite dans le dossier
																				// test-data/
			BDDUtilities.resetTable();
			BDDUtilities.fillTable(); // remplit la table à partir des modèles dans le dossier data/
			BDDUtilities.setColumnInfo();

			PreparedStatement st = BDDUtilities.getConnection()
					.prepareStatement("select * from PLY where " + BDDUtilities.getColumnNames()[0] + " =  ?");
			st.setString(1, modelName);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				int dbColCount = rs.getMetaData().getColumnCount();
				String[] row = new String[dbColCount + 3];
				for (int i = 1; i <= dbColCount; i++) {
					row[i - 1] = rs.getString(i);
				}
				for (int i = 0; i < buttonColumns.length; i++) {
					row[dbColCount + i] = buttonColumns[i];
				}
				data.add(row);
			}
			dm = new TableDataModel(columnNames, data);
			dm.setEditable(true);
			table = new Table(dm, data, buttonColumns);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Crée table avec une ligne non insérée dans la bdd (avec buttonColumns).
	 */
	private void initTableEmpty() {
		List<String[]> data = new ArrayList<>();
		String[] row = new String[columnNames.length + buttonColumns.length];
		for (int i = 0; i < row.length; i++) {
			row[i] = "";
		}
		for (int i = 0; i < buttonColumns.length; i++) {
			row[columnNames.length + i] = buttonColumns[i];
		}
		data.add(row);
		dm = new TableDataModel(columnNames, data);
		dm.setEditable(true);
		table = new Table(dm, data, buttonColumns);
		table.setLastRowIsInDB(false);

	}

	private String[] actualData(String modelName) {
		try {
			PreparedStatement st = BDDUtilities.getConnection()
					.prepareStatement("select * from PLY where " + BDDUtilities.getColumnNames()[0] + " = ?");
			st.setString(1, modelName);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				String[] result = new String[rs.getMetaData().getColumnCount()];
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					result[i - 1] = rs.getString(i);
				}
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private int getNbRowsInDB() {
		int nbModelsBefore = 0;
		try {
			PreparedStatement st = BDDUtilities.getConnection().prepareStatement("select count(*) from PLY");
			ResultSet rs;
			rs = st.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/* UPDATE */

	@Test
	public void test_update_no_diff_values() {
		// INIT
		String modelName = "galleon";
		initTableWithDataFromDB(modelName);
		String[] original = actualData(modelName);

		// TRY OPERATION AND VERIFY
		assertEquals(BDDResultEnum.NO_DIFFERENT_VALUES, table.modifyTableDirect(0, true, true).getCode());

		// VERIFY IN DB
		String[] actual = actualData(modelName);
		for (int i = 0; i < original.length; i++) {
			assertEquals(original[i], actual[i]);
		}
	}

	@Test
	public void test_update_no_name() {
		// INIT
		String modelName = "galleon";
		initTableWithDataFromDB(modelName);
		String[] original = actualData(modelName);

		// TRY OPERATION AND VERIFY
		dm.setValueAt("   ", 0, 0);
		assertEquals(BDDResultEnum.NAME_NOT_SPECIFIED, table.modifyTableDirect(0, true, true).getCode());

		// VERIFY IN DB
		String[] actual = actualData(modelName);
		for (int i = 0; i < original.length; i++) {
			assertEquals(original[i], actual[i]);
		}
	}

	@Test
	public void test_update_bad_type() {
		// INIT
		String modelName = "galleon";
		initTableWithDataFromDB(modelName);
		String[] original = actualData(modelName);

		// TRY OPERATION AND VERIFY
		dm.setValueAt("NOT_AN_INTEGER", 0, 4);
		assertEquals(BDDResultEnum.INCORRECT_TYPES, table.modifyTableDirect(0, true, true).getCode());

		// VERIFY IN DB
		String[] actual = actualData(modelName);
		for (int i = 0; i < original.length; i++) {
			assertEquals(original[i], actual[i]);
		}
	}

	@Test
	public void test_update_successful() {
		// INIT
		String modelName = "galleon";
		initTableWithDataFromDB(modelName);
		String[] original = actualData(modelName);

		// TRY OPERATION AND VERIFY
		String newValue = "NEW_NAME";
		dm.setValueAt(newValue, 0, 0);
		dm.setValueAt("1", 0, 4);
		dm.setValueAt("1", 0, 5);
		assertEquals(BDDResultEnum.UPDATE_SUCCESSFUL, table.modifyTableDirect(0, true, true).getCode());

		// VERIFY IN DB
		String[] actual = actualData(newValue);
		assertEquals(newValue, actual[0]);
		for (int i = 1; i < original.length - 2; i++) {
			assertEquals(original[i], actual[i]);
		}
		assertEquals("1", actual[4]);
		assertEquals("1", actual[5]);
	}

	/* INSERT */

	@Test
	public void test_insert_no_diff_values() {
		// INIT
		initTableEmpty();
		int nbModelsBefore = getNbRowsInDB();

		// TRY OPERATION AND VERIFY
		assertEquals(BDDResultEnum.NO_DIFFERENT_VALUES, table.modifyTableDirect(0, false, true).getCode());

		// VERIFY IN DB
		int nbModelsAfter = getNbRowsInDB();
		assertEquals(nbModelsBefore, nbModelsAfter);
	}

	@Test
	public void test_insert_no_name() {
		// INIT
		initTableEmpty();
		int nbModelsBefore = getNbRowsInDB();
		dm.setValueAt("   ", 0, 0);
		dm.setValueAt("1", 0, 4); // set integers to avoid tripping the return INCORRECT_TYPES
		dm.setValueAt("1", 0, 5);

		// TRY OPERATION AND VERIFY
		assertEquals(BDDResultEnum.NAME_NOT_SPECIFIED, table.modifyTableDirect(0, false, true).getCode());

		// VERIFY IN DB
		int nbModelsAfter = getNbRowsInDB();
		assertEquals(nbModelsBefore, nbModelsAfter);
	}

	@Test
	public void test_insert_bad_numbers() {
		// INIT
		initTableEmpty();
		int nbModelsBefore = getNbRowsInDB();
		dm.setValueAt("CORRECT_STRING", 0, 0);
		dm.setValueAt("NOT_AN_INTEGER", 0, 4);
		dm.setValueAt("NOT_AN_INTEGER", 0, 5);

		// TRY OPERATION AND VERIFY
		assertEquals(BDDResultEnum.INCORRECT_TYPES, table.modifyTableDirect(0, false, true).getCode());

		// VERIFY IN DB
		int nbModelsAfter = getNbRowsInDB();
		assertEquals(nbModelsBefore, nbModelsAfter);
	}

	@Test
	public void test_insert_successful() {
		// INIT
		initTableEmpty();
		int nbModelsBefore = getNbRowsInDB();
		dm.setValueAt("CORRECT_STRING", 0, 0);
		dm.setValueAt("1", 0, 4);
		dm.setValueAt("1", 0, 5);

		// TRY OPERATION AND VERIFY
		assertEquals(BDDResultEnum.INSERT_SUCCESSFUL, table.modifyTableDirect(0, false, true).getCode());

		// VERIFY IN DB
		int nbModelsAfter = getNbRowsInDB();
		assertEquals(nbModelsBefore + 1, nbModelsAfter);
	}

	/* DELETE */

	@Test
	public void test_delete_successsful() {
		// INIT
		initTableWithDataFromDB("galleon");
		int nbModelsBefore = getNbRowsInDB();

		// TRY OPERATION AND VERIFY
		assertEquals(BDDResultEnum.DELETE_SUCCESSFUL, table.deleteRow(0, true).getCode());

		// VERIFY IN DB
		int nbModelsAfter = getNbRowsInDB();
		assertEquals(nbModelsBefore - 1, nbModelsAfter);
	}

	/* ADD ROW */

	@Test
	public void test_add_row_not_possible() {
		// INIT
		initTableEmpty();

		// VERIF
		table.addRow(buttonColumns);
		assertEquals(1, table.getRowCount());
	}

	/**
	 * On a inséré la dernière ligne, du coup on peut ajouter une nouvelle ligne.
	 */
	@Test
	public void test_add_row_correct() {
		// INIT
		initTableEmpty();

		dm.setValueAt("CORRECT_STRING", 0, 0);
		dm.setValueAt("1", 0, 4);
		dm.setValueAt("1", 0, 5);

		// TRY INSERT FIRST AND VERIFY
		assertEquals(BDDResultEnum.INSERT_SUCCESSFUL, table.modifyTableDirect(0, false, true).getCode());

		// VERIF
		table.addRow(buttonColumns);
		assertEquals(2, table.getRowCount());
	}
}
