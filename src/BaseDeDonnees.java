import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class BaseDeDonnees {
  public static void main(String[] args) throws ClassNotFoundException {
    // load the sqlite-JDBC driver using the current class loader
    Class.forName("org.sqlite.JDBC");
    
    Connection connection = null;
    try {
      
    	String[]items;
    	File path = new File("ply/");
		items = path.list();
		System.out.println(items.length);
		for(int i = 0 ; i < items.length;i++){
			System.out.println(items[i]);
		}
    	
    	// create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:test.sqlite");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.
      statement.executeUpdate("drop table PLY");
      statement.executeUpdate("create table PLY " + "(NOM varchar(20), CHEMIN varchar(50), DATE date, DESCRIPTION text)");
      
      for(int i = 0; i < items.length;i++){
    	  // insertion des ply dans la bdd
    	  System.out.println("toto"+ i);
    	  String nom = items[i].substring(0, items[i].lastIndexOf(".ply"));
    	  statement.executeUpdate("insert into PLY values "
    	  		+ "('"+ nom + "', 'ply/"+ items[i] + "','2016/10/10','toto')");
    	  
      }
      ResultSet rs = statement.executeQuery("select * from PLY");
      while(rs.next()) {
        // read the result set
        System.out.println("nom = " + rs.getString("NOM"));
        System.out.println("chemin = " + rs.getString("CHEMIN"));
        System.out.println("date = "+ rs.getString("DATE"));
        System.out.println("description = "+ rs.getString("DESCRIPTION"));
        System.out.println();
        
      }
    }
    catch(SQLException e) {
      // if the error message is "out of memory", 
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }
    finally {
      try {
        if(connection != null)
          connection.close();
      }
      catch(SQLException e) {
        // connection close failed.
        System.err.println(e);
      }
    }
  }
}
