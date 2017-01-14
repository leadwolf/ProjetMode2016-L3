package ply.bdd.strategy;

import java.nio.file.Path;

import javax.swing.JOptionPane;

import main.Modelisationator;
import ply.bdd.base.BDDUtilities;
import ply.bdd.vues.BDDPanel;
import res.ButtonColumn;
import result.BDDResult;
import result.BDDResult.BDDResultEnum;
import result.BasicResult;
import result.BasicResult.BasicResultEnum;
import result.DataBaseCommandResult;
import result.MethodResult;

/**
 * Cette classe abstraite définit comment répondre face à des arguments demandant une opération sur la base de données. Elle done un
 * {@link DataBaseCommandResult} et selon la stratégie, cette réponse peut être un {@link MethodResult} ou un {@link BDDPanel}.
 * 
 * @author Christopher Caroni
 *
 */
public abstract class DataBaseStrategy {

	/**
	 * Noms de colonnes qu'on utilisera pour passer à la table crée.
	 */
	protected String[] columnNames;
	/**
	 * Nom des {@link ButtonColumn} qu'on utilisera pour passer à la table crée.
	 */
	protected String[] buttonColumns;
	/**
	 * Noms des JButtons qu'on utilisera pour passer à la table crée.
	 */
	protected String[] primaryButtons;
	/**
	 * Le nombre de mots clés max qu'on peut chercher avec --find.
	 */
	protected int nbKeywordsLimit;

	public DataBaseStrategy() {
		columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés", "Nombre de Points", "Nombre de Faces" };
		buttonColumns = new String[] { "Confirmer insertion/edition", "Reset", "Supprimer" };
		primaryButtons = new String[] { "Ajouter une ligne", "Reset table à base" };
		nbKeywordsLimit = 10;
	}

	public abstract DataBaseCommandResult treatArguments(String[] args, Path dbPath, boolean[] options);

	/**
	 * Initalise la connection, recree la table si besoin.
	 * 
	 * @param dbPath
	 * @param reset
	 * @param fill
	 */
	protected void initConnection(Path dbPath, boolean reset, boolean fill) {
		BDDUtilities.initConnection(dbPath);
		if (reset) {
			BDDUtilities.resetTable();
		}
		if (fill) {
			BDDUtilities.fillTable();
		}
		BDDUtilities.setColumnInfo();
	}

	/**
	 * Verifie s'il y a une seule commande et qu'elle est bien écrite. Affiche l'erreur si !quiet.
	 * 
	 * @param args les arguments passés au programme.
	 * @param quiet true pour empêcher affichge.
	 * @return un {@link MethodResult} décrivant la validité des arguments.
	 */
	public MethodResult verifArgs(String[] args, boolean quiet) {

		if (args == null || (args != null && args.length <= 0)) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous n'avez pas donné de commande", Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
			}
			return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
		}
		boolean empty = true;
		for (int i = 0; i < args.length; i++) {
			if (!args[i].equals("")) {
				empty = false;
			}
		}
		if (empty) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous n'avez pas donné de commande", Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
			}
			return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
		}
		// switch to verify we only have one command.
		boolean foundExecutableArg = false;
		// si la commande est --find
		boolean findCommand = false;
		// the counter of how many normal strings have been found (Strings in args that are not a command and not an option).
		int normalStringsFound = 0;
		// the counter of how many normal strings should be found
		int normalStringsNeeded = 0;

		for (int i = 0; i < args.length; i++) {
			boolean currrentIsExecutable = BDDUtilities.isExecutableArg(args[i]);
			if (currrentIsExecutable) {
				// si on a n'a pas encore trouvé une commande, vérifier cette commande à l'emplacement [i].
				if (!foundExecutableArg) {
					foundExecutableArg = true;
					if (args[i].equals("--name")) {
						normalStringsNeeded = 1;
					} else if (args[i].equals("--all")) {
						// no need to verify since the enclosing for verifies
						// multiple commands.
					} else if (args[i].equals("--find")) {
						findCommand = true;
						// limit find to 10;
						normalStringsNeeded = nbKeywordsLimit;
					} else if (args[i].equals("--add")) {
						normalStringsNeeded = 0;
						// no need to verify since the enclosing for verifies
						// multiple commands.
					} else if (args[i].equals("--delete")) {
						normalStringsNeeded = 1;
					} else if (args[i].equals("--edit")) {
						normalStringsNeeded = 1;
					}
				} else { // on a trouvé plus qu'une commande.
					if (!quiet) {
						JOptionPane.showMessageDialog(null, "Vous n'avez précisé plusieurs commandes incompatibles.\nVeuillez réessayer.",
								Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
					}
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			} else if (!currrentIsExecutable && !BDDUtilities.isDBOption(args[i])) {
				normalStringsFound++;
			}
		}
		if (!foundExecutableArg) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous n'avez pas donné de commande", Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
			}
			return new BasicResult(BasicResultEnum.NO_COMMAND_GIVEN);
		}
		if (!findCommand && normalStringsFound != normalStringsNeeded) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous n'avez pas précisé de modèle", Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
			}
			return new BDDResult(BDDResultEnum.NAME_NOT_SPECIFIED);
		} else if (findCommand && normalStringsFound == 0) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous n'avez pas précisé de modèle", Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
			}
			return new BDDResult(BDDResultEnum.NO_KEYWORDS_SPECIFIED);
		} else if (findCommand && normalStringsFound > normalStringsNeeded) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous avez mis plus de mots clés que la limite.\nLa limite est de " + nbKeywordsLimit,
						Modelisationator.NAME, JOptionPane.ERROR_MESSAGE);
			}
			return new BDDResult(BDDResultEnum.TOO_MANY_KEYWORDS_SPECIFIED);
		}
		return new BasicResult(BasicResultEnum.ALL_OK);
	}
}
