package it.rdev.rubrica.model.impl.file;

import java.io.*;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;

import it.rdev.rubrica.config.ConfigKeys;
import it.rdev.rubrica.model.Contact;
import it.rdev.rubrica.model.ContactDAO;
import it.rdev.rubrica.model.impl.rdbms.AbstractDAO;


public class ContactDAOFile extends AbstractDAO<Contact, BigInteger> implements ContactDAO {

	@Override
	public boolean persist(Contact t) throws SQLException {
		try {
			FileOutputStream f_out = new FileOutputStream(ConfigKeys.FILE_PATH.getKey()+"\\rubrica.txt");
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(t.toString());
			obj_out.close();
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Il file non è stato trovato.");
			return false;
		} catch (IOException e) {
			System.err.println("Si è verificato un errore.");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(Contact t) {
		File tmp = new File(ConfigKeys.FILE_PATH.getKey()+"\\tmpFileRubrica.txt");
		File source = new File(ConfigKeys.FILE_PATH.getKey()+"\\rubrica.txt");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(source));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
			String remove = t.getName()+","+t.getSurname()+","+t.getPhoneNumbers()+","+t.getEmails();
			String current;
			while ((current = reader.readLine()) != null){
				String trim = current.trim();
				if(trim.equals(remove)) {
					continue;
				}
				writer.write(current);
				writer.newLine();
			}
			writer.close();
			reader.close();
			copia();
			return true;
			
		} catch (Exception e) {
			System.err.println("Si è verificato un errore.");
			return false;
		}
	}

	@Override
	public boolean update(Contact t) throws SQLException {
		return false;
	}

	@Override
	public List<Contact> getAll() {
		
		
		return null;
	}
	
	
	public static void leggiFile() {
		try {
			File source = new File(ConfigKeys.FILE_PATH.getKey());
			Scanner lettore = new Scanner (System.in);
			while (lettore.hasNextLine()) {
				String record = lettore.nextLine();
				System.out.println(record);
			}
			lettore.close();
		} catch (Exception e) {
			System.err.println("Si e verificato un errore.");
			e.printStackTrace();
		}
	}
	
	
	public static void copia() {
		File tmp = new File(ConfigKeys.FILE_PATH.getKey()+"\\tmpFileRubrica.txt");
		File source = new File(ConfigKeys.FILE_PATH.getKey()+"\\rubrica.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(source));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
			String current;
			while((current = reader.readLine())!=null) {
				writer.write(current);
			}
			writer.close();
			reader.close();
		} catch (Exception e) {
			System.err.println("Si e verificato un errore.");
			e.printStackTrace();
		}
	}
	
	

	
	
}
