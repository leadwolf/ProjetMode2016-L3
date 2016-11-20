package modelTest;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Test;

import erreur.BasicResultEnum;
import erreur.ReaderErrorEnum;
import reader.Lecture;

/**
 * Test si {@link Lecture} détecte bien les erreurs dans le fichiers .ply
 * 
 * @author L3
 *
 */
public class LectureTest {

	/**
	 * Test tous les types d'erreurs dans {@link ReaderErrorEnum}
	 */
	@Test
	public void testLecture() {
		Lecture lecture = new Lecture(Paths.get("badPLY/missingCoord.ply"), true);
		assertEquals(ReaderErrorEnum.MISSING_COORD, lecture.getResult().getCode());

		// on ne peut pas directment détecter un point manquant, le seul moyen de savoir est lire le nombre de lignes dicté par l'entête et puis on ne peut que
		// voir qu'il y a trop de coordonneés
		lecture = new Lecture(Paths.get("badPLY/missingPoint.ply"), true);
		assertEquals(ReaderErrorEnum.TOO_MANY_COORDS, lecture.getResult().getCode());

		lecture = new Lecture(Paths.get("badPLY/missingFace.ply"), true);
		assertEquals(ReaderErrorEnum.MISSING_FACE, lecture.getResult().getCode());

		lecture = new Lecture(Paths.get("badPLY/missingPointInFace.ply"), true);
		assertEquals(ReaderErrorEnum.MISSING_POINT_IN_FACE, lecture.getResult().getCode());

		// une face fait référence vers un point non trouvé
		lecture = new Lecture(Paths.get("badPLY/pointNotFound.ply"), true);
		assertEquals(ReaderErrorEnum.POINT_NOT_FOUND, lecture.getResult().getCode());

		lecture = new Lecture(Paths.get("badPLY/tooManyLines.ply"), true);
		assertEquals(ReaderErrorEnum.TOO_MANY_LINES, lecture.getResult().getCode());

		lecture = new Lecture(Paths.get("badPLY/missingElementVertex.ply"), true);
		assertEquals(ReaderErrorEnum.MISSING_ELEMENT_VERTEX, lecture.getResult().getCode());

		lecture = new Lecture(Paths.get("badPLY/missingElementFace.ply"), true);
		assertEquals(ReaderErrorEnum.MISSING_ELEMENT_FACE, lecture.getResult().getCode());

		// fichier inexistant
		lecture = new Lecture(Paths.get("badPLY/allo.ply"), true);
		assertEquals(ReaderErrorEnum.FILE_NONEXISTING, lecture.getResult().getCode());

		// pas d'extension .ply
		lecture = new Lecture(Paths.get("badPLY/cube"), true);
		assertEquals(ReaderErrorEnum.BAD_EXTENSION, lecture.getResult().getCode());

		// la première ligne de l'entête n'est pas composé uniquement de "ply"
		lecture = new Lecture(Paths.get("badPLY/plyNotFound.ply"), true);
		assertEquals(ReaderErrorEnum.PLY_NOT_FOUND, lecture.getResult().getCode());

		lecture = new Lecture(Paths.get("badPLY/cube.ply"), true);
		assertEquals(BasicResultEnum.ALL_OK, lecture.getResult().getCode());

		lecture = new Lecture(Paths.get("badPLY/cube.ply"), true);
		assertEquals(BasicResultEnum.ALL_OK, lecture.getResult().getCode());
	}

}
