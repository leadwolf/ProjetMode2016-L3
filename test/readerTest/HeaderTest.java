package readerTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import ply.read.reader.AsciiReader;
import ply.read.reader.Reader;
import ply.result.BasicResult;
import ply.result.BasicResult.BasicResultEnum;
import ply.result.MethodResult;
import ply.result.ReaderResult.ReaderResultEnum;

public class HeaderTest {

	private Reader reader;
	private MethodResult result;

	@Before
	public void setup() {
		reader = null;
		result = new BasicResult(BasicResultEnum.ALL_OK);
	}

	@Test
	public void testMissingPlyDeclaration() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testMissingPlyDeclaration.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.MISSING_PLY_DECLARATION, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testUnexpectedEnd() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testUnexpectedEnd.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.UNEXPECTED_END_OF_FILE_IN_HEADER, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testDuplicateFormat() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testDuplicateFormat.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.DUPLICATE_FORMAT, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testIncorrectElementDeclaration() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testIncorrectElementDeclaration.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.INCORRECT_ELEMENT_DECLARATION, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testDuplicateElementHeader() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testDuplicateElementHeader.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.DUPLICATE_ELEMENT_HEADER, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testPropertyBeforeElementDeclaration() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testPropertyBeforeElementDeclaration.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.PROPERTY_BEFORE_ELEMENT_DECLARATION, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testVertexNotDeclared() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testVertexNotDeclared.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.VERTEX_NOT_DECLARED, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testFaceNotDeclared() {
		try {
			reader = new AsciiReader(Paths.get("test-data/testFaceNotDeclared.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.FACE_NOT_DECLARED, result.getCode());
			return;
		}
		fail();
	}

}
