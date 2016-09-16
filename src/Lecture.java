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
	int nbFaces;
	Pattern lastIntPattern;
	Charset charset;
	List<Point> points;
	
	
	List<Point> listePoints;
	
	
	public Lecture (Path file) {
		this.file = file;
		listePoints = new ArrayList<>();
	    lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
	    points = new ArrayList<>();
		
	} 
	
	public void findNb() {
		charset = Charset.forName("US-ASCII");
				
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
		    String line = null;
		    
		    do {
		    	line = reader.readLine();
		    }
		    while (line != null && !line.startsWith("element vertex"));
		    nbPoints = getNombre(line);
		    
		    do {
		    	line = reader.readLine();
		    }
		    while (line != null && !line.startsWith("element face"));
		    nbFaces = getNombre(line);
		    
		} catch (Exception x) {
		}
	}
	
	
	public void stockerPoints() {
		int nbLignesLus = 0;
		boolean startCount = false;
	    String line = null;
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			do {			
				if (startCount) {
					getDoubles(line);
					nbLignesLus++;
				}
				line = reader.readLine();
				if (line.equals("end_header")) {
					startCount = true;
					// lis encore une ligne pour commencer conversion
					line = reader.readLine();
				}
			} while (nbLignesLus < nbPoints);
		}
		catch (Exception e) {
		}
		
		System.out.println("\n Liste des points\n");
		for (Point pt : points) {
			System.out.println(pt.toString2()	);
		}
	}
	
	private void getDoubles(String input) {
	    Matcher matcher = Pattern.compile( "[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?" ).matcher( input );
        points.add(new Point());
	    while ( matcher.find() )
	    {
	        double element = Double.parseDouble( matcher.group() );
	        points.get(points.size()-1).add(element);
	    }
	}
	
	private int getNombre(String line) {
		int number = 0;
		String convert = "";
	    Matcher matcher = lastIntPattern.matcher(line);
	    if (matcher.find()) {
	    	convert = matcher.group(1);
	        number = Integer.parseInt(convert);
	        System.out.println("nb points = " + number);
	    }
        return number;
	}
	
	public static void main(String[] args) {	
		Path path = Paths.get("ply/cube.ply");
		Lecture lect = new Lecture(path);
		lect.findNb();
		lect.stockerPoints();
	}
}
