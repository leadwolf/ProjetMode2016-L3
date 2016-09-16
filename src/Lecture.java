import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Lecture {
	
	
	Path file;
	List<Integer> liste;
	
	public Lecture (Path file) {
		Charset charset = Charset.forName("US-ASCII");
		liste = new ArrayList<>();
		this.file = file;
		int nbPoints;
		int nbFaces;
				
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
		    String line = null;
		    while ((line = reader.readLine()) != null && !line.matches("element vertex")) {
		        System.out.println(line);
		        line = reader.readLine();
		        // maintenant a la premiere ligne des points
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	public static void main(String[] args) {
		Path path = Paths.get("ply/cube.ply");
		Lecture lect = new Lecture(path);
	}
}
