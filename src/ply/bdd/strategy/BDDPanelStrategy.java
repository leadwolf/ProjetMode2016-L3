package ply.bdd.strategy;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import ply.bdd.base.BDDUtilities;
import ply.bdd.base.DAO;
import ply.bdd.table.Table;
import ply.bdd.vues.BDDPanel;
import ply.main.Modelisationator;
import ply.result.DataBaseCommandResult;
import ply.result.MethodResult;
import ply.result.BDDResult.BDDResultEnum;
import ply.result.BasicResult.BasicResultEnum;

/**
 * Implémentation de {@link DataBaseStrategy} qui traite les arguments et donne un {@link BDDPanel}.
 * 
 * @author Christopher Caroni
 *
 */
public class BDDPanelStrategy extends DataBaseStrategy {


	@Override
	public DataBaseCommandResult treatArguments(String[] args, Path dbPath, boolean[] options) {
		// VERIF ARGS
		if (options == null) {
			// Vous devez au moins spécifier l'option quiet, donc l'array doit
			// exister
			return null;
		}
		MethodResult testArgs = verifArgs(args, options[2]);
		if (!testArgs.getCode().equals(BasicResultEnum.ALL_OK)) {
			return null;
		} // else continue program

		initConnection(dbPath, options[0], options[1]);
		MethodResult checkResult = BDDUtilities.checkTable();
		if (checkResult.getCode().equals(BDDResultEnum.DB_NOT_EMPTY)) {
			return new DataBaseCommandResult(getSpecificPanel(args, options[2]));
		} else {
			if (!options[2]) {
				JOptionPane.showMessageDialog(null, "La base de données est vide.", Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
			}
			return null;
		}
	}

	/**
	 * Donne un {@link BDDPanel} correspondant à la commande dans args.
	 * 
	 * @param args la commande de l'utlisateur
	 * @param quiet true pour empecher affichage
	 * @return le panel pour cette commande
	 */
	private BDDPanel getSpecificPanel(String[] args, boolean quiet) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--name")) {
				return showName(args, quiet);
			}
			if (args[i].equals("--all")) {
				return showAll();
			}
			if (args[i].equals("--find")) {
				return find(args, quiet);
			}
			if (args[i].equals("--add")) {
				return add();
			}
			if (args[i].equals("--edit")) {
				return edit(args, quiet);
			}
		}
		return null;
	}

	/**
	 * Execute la requête donnant les informations sur le modèle et crée le panel concerné.
	 * 
	 * @param args le modèle à afficher
	 * @param quiet
	 * @return le {@link BDDPanel} contenant les données du modèle précisé ou null.
	 */
	private BDDPanel showName(String[] args, boolean quiet) {
		try {
			String modelName = "";
			for (int i = 0; i < args.length; i++) {
				if (!BDDUtilities.isDBOption(args[i]) && !BDDUtilities.isExecutableArg(args[i])) {
					modelName = args[i];
				}
			}
			ResultSet rsCount = DAO.INSTANCE.getAllByName(modelName);
			if (rsCount.next()) {
				ResultSet rs = DAO.INSTANCE.getAllByName(modelName);
				BDDPanel result = new BDDPanel(rs, columnNames, buttonColumns, primaryButtons);
				// result.setEditableColumns(new int[] { 0, 1, 3 }, true);
				result.setEditable(true);
				BDDUtilities.closeConnection();
				return result;
			} else {
				if (!quiet) {
					String message = "Le modèle " + modelName + " n'existe pas\nUtilisation: basededonneés --name <name>";
					JOptionPane.showMessageDialog(null, message, Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Vérifie les arguments et crée la fenetre listant toute la base
	 * 
	 * @return s'il a pu créer la fenêtre
	 */
	private BDDPanel showAll() {
		ResultSet rs = DAO.INSTANCE.getAll();
		BDDPanel result = new BDDPanel(rs, columnNames, buttonColumns, primaryButtons);
		// result.setEditableColumns(new int[] { 0, 1, 3 }, true);
		result.setEditable(true);
		BDDUtilities.closeConnection();
		return result;
	}

	/**
	 * Vérifie les arguments et puis crée la fenetre listant les informations des modèles correspondants aux keywords
	 * 
	 * @param args les mots clés à rechercher
	 * @param quiet
	 * @return le {@link BDDPanel} contenant les modèles ayant les mots cles ou null.
	 */
	private BDDPanel find(String[] args, boolean quiet) {
		try {
			ResultSet rsCount = DAO.INSTANCE.find(args);
			if (rsCount.next()) {
				ResultSet rs = DAO.INSTANCE.find(args);
				BDDPanel result = new BDDPanel(rs, columnNames, buttonColumns, primaryButtons);
				// result.setEditableColumns(new int[] { 0, 1, 3 }, true);
				result.setEditable(true);
				BDDUtilities.closeConnection();
				return result;
			} else {
				if (!quiet) {
					String message = "Aucun modèle trouvé comportant ces mot clés";
					JOptionPane.showMessageDialog(null, message, Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Crée la fenêtre d'insertion de modèle.
	 * 
	 * @return si fenêtre crée. Pour savoir si requête sql éxecutée, voir {@link Table}
	 */
	private BDDPanel add() {
		BDDPanel result = new BDDPanel(null, columnNames, buttonColumns, primaryButtons);
		result.setCanAddRow(false);
		result.setEditable(true); // tout est editable dans --edit
		return result;
	}

	/**
	 * Vérifie les argumuments et puis crée la fenetre de modification du modèle précisé s'ils sont valides
	 * 
	 * @param args le modèle à éditer
	 * @param quiet
	 * @return si fenêtre bien crée. Pour savoir si requête sql éxecutée, voir {@link Table}
	 */
	private BDDPanel edit(String[] args, boolean quiet) {
		try {
			String modelName = "";
			for (int i = 0; i < args.length; i++) {
				if (!BDDUtilities.isDBOption(args[i]) && !BDDUtilities.isExecutableArg(args[i])) {
					modelName = args[i];
				}
			}
			ResultSet rsCount = DAO.INSTANCE.getAllByName(modelName);
			if (rsCount.next()) {
				ResultSet rs = DAO.INSTANCE.getAllByName(modelName);
				BDDPanel result = new BDDPanel(rs, columnNames, buttonColumns, primaryButtons);
				result.setEditable(true); // tout est editable dans --edit
				BDDUtilities.closeConnection();
				return result;
			} else {
				if (!quiet) {
					String message = "Le modèle " + modelName + " n'existe pas";
					JOptionPane.showMessageDialog(null, message, Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
