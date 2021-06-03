package it.rdev.rubrica.model.impl.rdbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import it.rdev.rubrica.model.DAO;

/**
 * 
 * Classe astratta che contiene la logica comune per le operazioni di CRUD su database relazionale
 * 
 * @author Danilo Di Nuzzo
 *
 * @param <T> entità su cui effettuare le operazioni di CRUD
 * @param <D> tipo di cui è formato l'ID dell'entità da gestire (tipo del database)
 */
public abstract class AbstractDAO<T, D> implements DAO<T> {
	
	/**
	 * Metodo utilizzato per l'esecuzione di queries che non richiedono parametri di input
	 * e restituiscono un result set
	 * 
	 * @param query String che contiene la query da eseguire (es. SELECT campo1, campo2 FROM tabella)
	 * @return Restituisce un oggetto di tipo {@link ResultSet}
	 * @throws SQLException Potrebbe sollevare un'eventuale eccezione se ci sono problemi nella query
	 * 
	 */
	protected ResultSet executeQuery(String query) throws SQLException {
		return DataSource.getInstance().getConnection().createStatement().executeQuery(query);
	}
	
	/**
	 * Metodo utilizzato per l'esecuzione di queries che richiedono parametri di input e
	 * restituiscono un result set
	 * 
	 * @param query String che contiene la query da eseguire (es. SELECT campo1, campo2 FROM tabella WHERE campo3 = ?)
	 * @param params Restituisce un oggetto di tipo {@link ResultSet}
	 * @return Restituisce un oggetto di tipo {@link ResultSet}
	 * @throws SQLException Potrebbe sollevare un'eventuale eccezione se ci sono problemi nella query
	 */
	protected ResultSet executeQuery(String query, Object ... params) throws SQLException {
		PreparedStatement ps = composePreparedStatement(query, params);
		return ps.executeQuery();
	}
	
	/**
	 * Metodo da utilizzare per effettuare le insert.
	 * @param query Stringa che contiene l'insert nel formato SQL, utilizzando come placeholder
	 * per i parametri il carattere ?
	 * @param params parametri da sostituire nella query di insert
	 * @return chiave generata durante l'inserimento (il tipo dipende dal parametro generico utilizzato)
	 * @throws SQLException Potrebbe sollevare un'eventuale eccezione se ci sono problemi nella query
	 */
	@SuppressWarnings("unchecked")
	protected D executeInsert(String query, Object ... params) throws SQLException {
		PreparedStatement ps = composePreparedStatement(query, params);
		int row = ps.executeUpdate();
		if( row == 0 ) {
			throw new SQLException("Nessuna riga inserita");
		}
		D generatedId = null;
		try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
            if (generatedKeys.next()) {
            	generatedId = (D) generatedKeys.getObject(1);
            } else {
            	throw new SQLException("Errore nell'inserimento, nessun id restituito");
            }
        }
		return generatedId;
	}
	
	/**
	 * Metodo da utilizzare per effettuare operazioni di DELETE ed UPDATE
	 * @param query Stringa che contiene la query di UPDATE o DELETE in formato SQL, utilizzando come placeholder
	 * per i parametri il carattere ?
	 * @param params parametri da sostituire nella query di insert
	 * @return restituisce il numero di records che sono stati interessati dall'operazione
	 * @throws SQLException Potrebbe sollevare un'eventuale eccezione se ci sono problemi nella query
	 */
	protected Integer executeUpdate(String query, Object ... params) throws SQLException {
		return composePreparedStatement(query, params).executeUpdate();
	}
	
	/**
	 * Metodo utilizzato internamente da questa classe per automatizzare la creazione dei
	 * PreparedStatement. 
	 * @param query stringa che contiene la query nel fomato SQL, utilizzando come placeholder
	 * per i parametri il carattere ?
	 * @param params lista di parametri da sostituire nella query
	 * @return {@link PreparedStatement}
	 * @throws SQLException Potrebbe sollevare un'eventuale eccezione se ci sono problemi nella query
	 */
	private PreparedStatement composePreparedStatement(String query, Object ... params) throws SQLException {
		Connection conn = DataSource.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		for(int i = 1; i <= params.length; i++) {
			ps.setObject(i, params[i-1]);
		}
		return ps;
	}

}
