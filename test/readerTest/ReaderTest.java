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
import ply.result.ReaderResult.ReaderResultEnum;

public class ReaderTest {

	private HeaderReader reader;
	private MethodResult result;

	@Before
	public void setup() {
		reader = null;
		result = new BasicResult(BasicResultEnum.ALL_OK);
	}

	@Test
	public void testFileDoesNotExist() {
		try {
			reader = new AsciiReader(Paths.get("test-data/incorrect.ply"), result);
		} catch (IOException e) {
			assertEquals(ReaderResultEnum.FILE_DOES_NOT_EXIST, result.getCode());
			return;
		}
		fail();
	}

	@Test
	public void testFileDoesExist() {
		try {
			reader = new AsciiReader(Paths.get("test-data/cube.ply"), result);
		} catch (IOException e) {
		}
		assertEquals(BasicResultEnum.ALL_OK, result.getCode());
	}

	@Test
	public void testBadExtension() {
		try {
			reader = new AsciiReader(Paths.get("test-data/cube.lpy"), result);
		} catch (IOException e) {
		}
		assertEquals(ReaderResultEnum.BAD_EXTENSION, result.getCode());
	}

	@Test
	public void testNoExtension() {
		try {
			reader = new AsciiReader(Paths.get("test-data/cube"), result);
		} catch (IOException e) {
		}
		assertEquals(ReaderResultEnum.NO_EXTENSION, result.getCode());
	}

}
