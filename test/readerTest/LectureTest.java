package readerTest;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Test;

import reader.Lecture;
import result.BasicResult.BasicResultEnum;
import result.ReaderResult.ReaderResultEnum;

/**
 * Test si {@link Lecture} détecte bien les erreurs dans le fichiers .ply
 * 
 * @author L3
 *
 */
public class LectureTest {

	@Test
	public void testMissingCoord() {
		Lecture lecture = new Lecture(Paths.get("test-data/missingCoord.ply"), true);
		assertEquals(ReaderResultEnum.MISSING_COORD, lecture.getResult().getCode());
	}

	@Test
	public void testMissingPoint() {
		// on ne peut pas directment détecter un point manquant, le seul moyen de savoir est lire le nombre de lignes dicté par l'entête et puis on ne peut que
		// voir qu'il y a trop de coordonneés
		Lecture lecture = new Lecture(Paths.get("test-data/missingPoint.ply"), true);
		assertEquals(ReaderResultEnum.TOO_MANY_COORDS, lecture.getResult().getCode());
	}

	@Test
	public void testMissingFace() {
		Lecture lecture = new Lecture(Paths.get("test-data/missingFace.ply"), true);
		assertEquals(ReaderResultEnum.MISSING_FACE, lecture.getResult().getCode());
	}

	@Test
	public void testMissingPointInFace() {
		Lecture lecture = new Lecture(Paths.get("test-data/missingPointInFace.ply"), true);
		assertEquals(ReaderResultEnum.MISSING_POINT_IN_FACE, lecture.getResult().getCode());
	}

	@Test
	public void testNonExistingPoint() {
		// une face fait référence vers un point non trouvé
		Lecture lecture = new Lecture(Paths.get("test-data/pointNotFound.ply"), true);
		assertEquals(ReaderResultEnum.POINT_NOT_FOUND, lecture.getResult().getCode());
	}

	@Test
	public void testTooManyLines() {
		Lecture lecture = new Lecture(Paths.get("test-data/tooManyLines.ply"), true);
		assertEquals(ReaderResultEnum.TOO_MANY_LINES, lecture.getResult().getCode());
	}

	@Test
	public void testMissingElementVertex() {
		Lecture lecture = new Lecture(Paths.get("test-data/missingElementVertex.ply"), true);
		assertEquals(ReaderResultEnum.MISSING_ELEMENT_VERTEX, lecture.getResult().getCode());
	}

	@Test
	public void testMissingElementFace() {
		Lecture lecture = new Lecture(Paths.get("test-data/missingElementFace.ply"), true);
		assertEquals(ReaderResultEnum.MISSING_ELEMENT_FACE, lecture.getResult().getCode());
	}

	@Test
	public void testNonExistingFile() {
		// fichier inexistant
		Lecture lecture = new Lecture(Paths.get("test-data/allo.ply"), true);
		assertEquals(ReaderResultEnum.FILE_NONEXISTING, lecture.getResult().getCode());
	}

	@Test
	public void testMissingExtension() {
		// pas d'extension .ply
		Lecture lecture = new Lecture(Paths.get("test-data/cube"), true);
		assertEquals(ReaderResultEnum.BAD_EXTENSION, lecture.getResult().getCode());
	}

	@Test
	public void testMissingHeader() {
		// la première ligne de l'entête n'est pas composé uniquement de "ply"
		Lecture lecture = new Lecture(Paths.get("test-data/plyNotFound.ply"), true);
		assertEquals(ReaderResultEnum.PLY_NOT_FOUND, lecture.getResult().getCode());
	}

	@Test
	public void testGoodFile() {
		Lecture lecture = new Lecture(Paths.get("test-data/cube.ply"), true);
		assertEquals(BasicResultEnum.ALL_OK, lecture.getResult().getCode());
	}

}
