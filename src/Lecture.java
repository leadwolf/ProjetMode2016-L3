import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lecture {
	
	
	Path file;
	

	int nbPoints;
	String nbPointsStr;
	int nbFaces;
	String nbFacesStr;
	Pattern lastIntPattern;
	Charset charset;
	
	
	List<Point> listePoints;
	
	
	public Lecture (Path file) {
		this.file = file;
		listePoints = new ArrayList<>();
	    lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
		
	} 
	
	public void findNb() {
		charset = Charset.forName("US-ASCII");
				
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
		    String line = null;
		    
		    // lire jusque nbPoints
		    while ((line = reader.readLine()) != null && !line.startsWith("element vertex")) {
		        //System.out.println(line);
		    }
		    
		    // maintenant a nombre de points
		    Matcher matcher = lastIntPattern.matcher(line);
		    if (matcher.find()) {
		        nbPointsStr = matcher.group(1);
		        nbPoints = Integer.parseInt(nbPointsStr);
		        System.out.println("nb points = " + nbPoints);
		    }
		    
		    // lire jusqu'a nbFaces
		    while ((line = reader.readLine()) != null && !line.startsWith("element face")) {
		        //System.out.println(line);
		    }
		    
		    // maintenant a nombre de faces
		    Matcher matcherFaces = lastIntPattern.matcher(line);
		    if (matcherFaces.find()) {
		        nbFacesStr = matcherFaces.group(1);
		        nbFaces = Integer.parseInt(nbFacesStr);
		        System.out.println("nb faces = " + nbFaces);
		    }
		    
		    
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	
	public void stockerPoints() {
		int nbLignesLus = 0;
	    String line = null;
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			while (nbLignesLus < nbPoints) {
				line = reader.readLine();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void main(String[] args) {	
		Path path = Paths.get("ply/cube.ply");
		Lecture lect = new Lecture(path);
	}
}
