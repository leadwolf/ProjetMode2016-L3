package ply.bdd.strategy;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import ply.bdd.base.BDDUtilities;
import ply.main.Modelisationator;
import ply.result.BDDResult;
import ply.result.BasicResult;
import ply.result.DataBaseCommandResult;
import ply.result.MethodResult;
import ply.result.BDDResult.BDDResultEnum;
import ply.result.BasicResult.BasicResultEnum;

/**
 * Implémentation de {@link DataBaseStrategy} qui traite les arguments et donne un {@link MethodResult}.
 * 
 * @author Christopher Caroni
 *
 */
public class ExecuteStrategy extends DataBaseStrategy {


	/**
	 * Execute une commande bdd sans interface. Vérifie que la base comporte des modèles et que le modèle à supprimer existe.
	 * 
	 * @param args
	 * @param dbPath path to the db.sqlite, leave null for default data/test.sqlite
	 * @param options [0] = reset, [1] = fill, [2] = quiet true pour empecher affichage
	 */
	@Override
	public DataBaseCommandResult treatArguments(String[] args, Path dbPath, boolean[] options) {
		// VERIF ARGS
		if (options == null) {
			// Vous devez au moins spécifier l'option quiet, donc l'array doit
			// exister
			return new DataBaseCommandResult(new BasicResult(BasicResultEnum.MISSING_OPTIONS));
		}
		MethodResult verifResult = verifArgs(args, options[2]);
		if (!verifResult.getCode().equals(BasicResultEnum.ALL_OK)) {
			return new DataBaseCommandResult(verifResult);
		}

		initConnection(dbPath, options[0], options[1]);
		MethodResult checkResult = BDDUtilities.checkTable();
		if (checkResult.getCode().equals(BDDResultEnum.DB_NOT_EMPTY)) {
			return new DataBaseCommandResult(delete(args, options[2]));
		} else {
			if (!options[2]) {
				JOptionPane.showMessageDialog(null, "La base de données est vide. Impossible de supprimmer un modèle.", "Erreur",
						JOptionPane.ERROR_MESSAGE);
			}
			return new DataBaseCommandResult(checkResult);
		}
	}

	/**
	 * Vérifie que le modèle existe dans la base et la supprime.
	 * 
	 * @param args le modèle à supprimer
	 * @param quiet afficher ou non les fenêtres
	 * @return un {@link MethodResult} décrivant le résultat de la requête ou si le modèle existait pas.
	 */
	private MethodResult delete(String[] args, boolean quiet) {
		try {
			String modelName = "";
			for (int i = 0; i < args.length; i++) {
				if (!BDDUtilities.isDBOption(args[i]) && !BDDUtilities.isExecutableArg(args[i])) {
					modelName = args[i];
				}
			}
			PreparedStatement stExists = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
			stExists.setString(1, modelName);
			ResultSet rs = stExists.executeQuery();
			if (rs.next()) {
				PreparedStatement stDelete = BDDUtilities.getConnection().prepareStatement("delete from PLY where NOM = ?");
				stDelete.setString(1, modelName);
				// result = nombre de lignes affectés par le delete
				int result = stDelete.executeUpdate();
				BDDUtilities.closeConnection();

				if (result > 0) {
					if (!quiet) {
						String message = "Le modèle " + modelName + " a été supprimé avec succès!";
						JOptionPane.showMessageDialog(null, message);
					}
					return new BDDResult(BDDResultEnum.DELETE_SUCCESSFUL);
				}
				if (!quiet) {
					String message = "Le modèle " + modelName + " n'a pas pu être supprimé!";
					JOptionPane.showMessageDialog(null, message);
				}
				return new BDDResult(BDDResultEnum.DELETE_NOT_SUCCESSFUL);
			} else {
				if (!quiet) {
					String message = "Le modèle " + modelName + " n'existe pas";
					JOptionPane.showMessageDialog(null, message, Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
				}
				BDDUtilities.closeConnection();
				return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new BDDResult(BDDResultEnum.DELETE_NOT_SUCCESSFUL);
	}

}
