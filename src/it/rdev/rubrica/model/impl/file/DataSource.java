package it.rdev.rubrica.model.impl.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import it.rdev.rubrica.config.ConfigKeys;

class DataSource {

	
private static DataSource ds;
	
	public static DataSource getInstance() {
		if(ds == null) {
			ds = new DataSource();
		}
		return ds;
	}
	
	private File file; 
	private String path;
	
	private DataSource() {
		try {
			path = ConfigKeys.FILE_PATH+"\\rubrica.txt";
			file = new File (ConfigKeys.FILE_PATH+"\\rubrica.txt");
			Scanner sc = new Scanner(file);
			sc.close();
		} catch (FileNotFoundException e) {
			System.err.println("Il file non è stato trovato.");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Il path inserito è nullo.");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Si è verificato un errore.");
			e.printStackTrace();
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public void setPath(String path) {
		this.path=path;
	}

	public String getPath() {
		return path;
	}
	
	protected void finalize() {
		if (file != null) {
			file=null;
		}
	}
}
