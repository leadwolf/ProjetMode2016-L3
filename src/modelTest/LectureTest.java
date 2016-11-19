package modelTest;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Test;

import reader.Lecture;
import reader.LectureErreurType.ErreurType;

/**
 * Test si {@link Lecture} détecte bien les erreurs dans le fichiers .ply
 * 
 * @author L3
 *
 */
public class LectureTest {

	/**
	 * Test tous les types d'erreurs dans {@link ErreurType}
	 */
	@Test
	public void testLecture() {
		Lecture lecture = new Lecture(Paths.get("badPLY/missingCoord.ply"), true);
		assertEquals(ErreurType.MISSING_COORD, lecture.getErreurType().getErreur());

		// on ne peut pas directment détecter un point manquant, le seul moyen de savoir est lire le nombre de lignes dicté par l'entête et puis on ne peut que
		// voir qu'il y a trop de coordonneés
		lecture = new Lecture(Paths.get("badPLY/missingPoint.ply"), true);
		assertEquals(ErreurType.TOO_MANY_COORDS, lecture.getErreurType().getErreur());

		lecture = new Lecture(Paths.get("badPLY/missingFace.ply"), true);
		assertEquals(ErreurType.MISSING_FACE, lecture.getErreurType().getErreur());

		lecture = new Lecture(Paths.get("badPLY/missingPointInFace.ply"), true);
		assertEquals(ErreurType.MISSING_POINT_IN_FACE, lecture.getErreurType().getErreur());

		// une face fait référence vers un point non trouvé
		lecture = new Lecture(Paths.get("badPLY/pointNotFound.ply"), true);
		assertEquals(ErreurType.POINT_NOT_FOUND, lecture.getErreurType().getErreur());

		lecture = new Lecture(Paths.get("badPLY/tooManyLines.ply"), true);
		assertEquals(ErreurType.TOO_MANY_LINES, lecture.getErreurType().getErreur());

		lecture = new Lecture(Paths.get("badPLY/missingElementVertex.ply"), true);
		assertEquals(ErreurType.MISSING_ELEMENT_VERTEX, lecture.getErreurType().getErreur());

		lecture = new Lecture(Paths.get("badPLY/missingElementFace.ply"), true);
		assertEquals(ErreurType.MISSING_ELEMENT_FACE, lecture.getErreurType().getErreur());

		// fichier inexistant
		lecture = new Lecture(Paths.get("badPLY/allo.ply"), true);
		assertEquals(ErreurType.FILE_NONEXISTING, lecture.getErreurType().getErreur());

		// pas d'extension .ply
		lecture = new Lecture(Paths.get("badPLY/cube"), true);
		assertEquals(ErreurType.BAD_EXTENSION, lecture.getErreurType().getErreur());

		// la première ligne de l'entête n'est pas composé uniquement de "ply"
		lecture = new Lecture(Paths.get("badPLY/plyNotFound.ply"), true);
		assertEquals(ErreurType.PLY_NOT_FOUND, lecture.getErreurType().getErreur());

		lecture = new Lecture(Paths.get("badPLY/cube.ply"), true);
		assertFalse(lecture.isErreur());
	}

}
