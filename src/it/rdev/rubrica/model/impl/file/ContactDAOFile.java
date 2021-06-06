package it.rdev.rubrica.model.impl.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
		File source = DataSource.getInstance().getFile();
		String p = DataSource.getInstance().getPath();
		DataSource.getInstance().setPath(ConfigKeys.FILE_PATH.getKey()+"\\tmpFileRubrica.txt");
		File tmp = new File(DataSource.getInstance().getPath());
		DataSource.getInstance().setPath(p);
		
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
		List<Contact> contacts = new ArrayList<>();
		try {
			File source = DataSource.getInstance().getFile();
			Scanner lettore = new Scanner(source);
			while (lettore.hasNextLine()) {
				Contact c = new Contact()
						.setName(lettore.nextLine())
						.setSurname(lettore.nextLine());
				contacts.add(c);
			}
			lettore.close();
		} catch (FileNotFoundException e) {
			System.err.println("Il File non è stato trovato.");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Il path del file è nullo.");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Si e verificato un errore.");
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static void leggiFile() {
		try {
			File source = DataSource.getInstance().getFile();
			Scanner lettore = new Scanner (source);
			while (lettore.hasNextLine()) {
				String record = lettore.nextLine();
				System.out.println(record);
			}
			lettore.close();
		}catch (FileNotFoundException e) {
			System.err.println("Il File non è stato trovato.");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Il path del file è nullo.");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Si e verificato un errore.");
			e.printStackTrace();
		}
	}
	
	
	public static void copia() {
		File source = DataSource.getInstance().getFile();
		String p = DataSource.getInstance().getPath();
		DataSource.getInstance().setPath(ConfigKeys.FILE_PATH.getKey()+"\\tmpFileRubrica.txt");
		File tmp = new File(DataSource.getInstance().getPath());
		DataSource.getInstance().setPath(p);
		
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
