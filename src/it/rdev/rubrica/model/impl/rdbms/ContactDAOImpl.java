package it.rdev.rubrica.model.impl.rdbms;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.rdev.rubrica.model.Contact;
import it.rdev.rubrica.model.ContactDAO;

/**
 * Implementazione del DAO per oggetti di tipo Contact
 * 
 * @author Danilo Di Nuzzo
 *
 */
public class ContactDAOImpl extends AbstractDAO<Contact, BigInteger> implements ContactDAO {
	
	private final String TABLE_NAME = "contacts";

	public List<Contact> getAll() {
		List<Contact> contacts = new ArrayList<>();
		try {
			// Recupero tutti i record dal database ATTENZIONE: La cardinalità dei risultati sarà data dal prodotto cartesiano
			// dei records di emails per phones per ogni contatto
			ResultSet rs = this.executeQuery("SELECT c.id, c.name, c.surname, e.email, p.number FROM " + TABLE_NAME + " c left outer join emails e on c.id = e.id left outer join phones p on p.id = c.id ");
			while(rs.next()) {
				Contact c = new Contact()
						.setId(rs.getInt("id"));
				// Verifico se ho giò processato questo contatto, se è in lista ne recupero il riferimento
				if( contacts.contains(c) ) {
					c = contacts.get( contacts.indexOf(c) );
				} else {
					// Contatto non ancora processato, valorizzo gli attributi di base
					c.setName(rs.getString("name"))
					.setSurname(rs.getString("surname"));
					// aggiungo il contatto alla lista
					contacts.add(c);
				}
				// imposto le informazioni di emails e numeri di telefono. Non mi preoccupo dei duplicati
				// perché ho utilizzato dei TreeSet
				if( rs.getString("email") != null )
					c.addEmail(rs.getString("email"));
				if( rs.getString("number") != null )
					c.addPhoneNumber(rs.getString("number"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contacts;
	}

	@Override
	public boolean persist(Contact c) throws SQLException {
		BigInteger id = this.executeInsert(
				"INSERT INTO " + TABLE_NAME + " (name, surname) VALUES (?, ?)",
				c.getName(),
				c.getSurname());
		
		insertEmailAndPhone(c, id.intValue());
		
		return id != null;
	}

	@Override
	public boolean delete(Contact t) throws SQLException {
		int rows = this.executeUpdate("DELETE FROM " + TABLE_NAME + " WHERE id = ?", t.getId());
		return rows > 0;
	}

	@Override
	public boolean update(Contact t) throws SQLException {
		this.executeUpdate("DELETE FROM emails WHERE id= ?", t.getId());
		this.executeUpdate("DELETE FROM phones WHERE id = ?", t.getId());
		
		insertEmailAndPhone(t, t.getId());
		return true;
	}

	private void insertEmailAndPhone(Contact c, Integer id) throws SQLException {
		// Per evitare di effettuare una INSERT per ogni email e numero di telefono
		// creo due insert multiple
		if( c.getEmails() != null ) {
			Object[] emailsParam = new Object[c.getEmails().size() * 2];
			Iterator<String> emailIt = c.getEmails().iterator();
			StringBuffer sb = new StringBuffer("INSERT INTO emails VALUES ");
			for(int i = 0; i<emailsParam.length && emailIt.hasNext();) {
				if(i > 0) {
					sb.append(", ");
				}
				sb.append("(?, ?)");
				emailsParam[i++] = emailIt.next();
				emailsParam[i++] = id;
			}
			this.executeUpdate(sb.toString(), emailsParam);
		}
		
		if( c.getPhoneNumbers() != null ) {
			Object[] phonesParam = new Object[c.getPhoneNumbers().size() * 2];
			Iterator<String> phoneIt = c.getPhoneNumbers().iterator();
			StringBuffer sb = new StringBuffer("INSERT INTO phones VALUES ");
			for(int i = 0; i<phonesParam.length && phoneIt.hasNext();) {
				if( i > 0) {
					sb.append(", ");
				}
				sb.append("(?, ?)");
				phonesParam[i++] = phoneIt.next();
				phonesParam[i++] = id;
			}
			this.executeUpdate(sb.toString(), phonesParam);
		}
	}
}
