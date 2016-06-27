import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Env {
	public static HashMap<String, String> envars = null;

	public Env(){
		Env.envars = new HashMap<String, String>();

		initialize();
		
	}
	public void initialize(){
		String sCurrentLine;
		BufferedReader in = null;
		try {
			 in = new BufferedReader(new FileReader(".env"));
			
			while ((sCurrentLine = in.readLine()) != null)	 {
				sCurrentLine = sCurrentLine.replaceAll("\\s","");
				
				
				String[] temp = sCurrentLine.split("=");
				envars.put(temp[0], temp[1]);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			try {
				if (in != null)in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	public static String getEnv(String search){
		new Env();
		if(envars.containsKey(search) && !envars.isEmpty() ){
			return envars.get(search);
		}
		return null;
		
	}

	public static void main(String[] args) {
		System.out.println(Env.getEnv("DB_HOST"));

	}

}
