package readerTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import ply.read.reader.body.AsciiReader;
import ply.read.reader.header.HeaderReader;
import ply.result.BasicResult;
import ply.result.BasicResult.BasicResultEnum;
import ply.result.MethodResult;
import ply.result.ReaderResult.FaceResultEnum;
import ply.result.ReaderResult.PointResultEnum;
import ply.result.ReaderResult.ReaderResultEnum;

public class AsciiBodyTest {

	private HeaderReader reader;
	private MethodResult result;

	@Before
	public void setup() {
		reader = null;
		result = new BasicResult(BasicResultEnum.ALL_OK);
	}

	@Test
	public void testUnexpectedEndOfFileInBody() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testUnexpectedEndOfFileInBody.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.UNEXPECTED_END_OF_FILE_IN_BODY, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testUnexpectedDataAtEnd() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testUnexpectedDataAtEnd.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.UNEXPECTED_DATA_AT_END, result.getCode());
			return;
		}
		fail();
	}

	/*
	 * Point
	 */

	@Test
	public void testPointNoMatchPattern() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testPointNoMatchPattern.ply"), result);
		} catch (IOException e) {
			assertEquals(PointResultEnum.NO_MATCH_PATTERN, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testPointTooManyCoords() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testPointTooManyCoords.ply"), result);
		} catch (IOException e) {
			assertEquals(PointResultEnum.INCORRECT_NUMBER_OF_COORDS, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testPointTooLittleCoords() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testPointTooLittleCoords.ply"), result);
		} catch (IOException e) {
			assertEquals(PointResultEnum.INCORRECT_NUMBER_OF_COORDS, result.getCode());
			return;
		}
		fail();
	}

	/*
	 * Face
	 */

	@Test
	public void testFaceNoMatchPattern() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testFaceNoMatchPattern.ply"), result);
		} catch (IOException e) {
			assertEquals(FaceResultEnum.NO_MATCH_PATTERN, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testFaceTooManyPoints() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testFaceTooManyPoints.ply"), result);
		} catch (IOException e) {
			assertEquals(FaceResultEnum.INCORRECT_NUMBER_OF_POINTS, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testFaceTooLittlePoints() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testFaceTooLittlePoints.ply"), result);
		} catch (IOException e) {
			assertEquals(FaceResultEnum.INCORRECT_NUMBER_OF_POINTS, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testFaceBadReference() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testFaceBadReference.ply"), result);
		} catch (IOException e) {
			assertEquals(FaceResultEnum.REFERENCE_POINT_NON_EXISTING, result.getCode());
			return;
		}
		fail();
	}

}
