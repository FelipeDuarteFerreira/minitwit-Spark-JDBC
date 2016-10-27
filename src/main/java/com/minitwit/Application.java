package com.minitwit;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;

import com.minitwit.config.WebConfig;
import com.minitwit.dao.MessageDao;
import com.minitwit.dao.UserDao;
import com.minitwit.dao.impl.MessageDaoImpl;
import com.minitwit.dao.impl.UserDaoImpl;
import com.minitwit.service.impl.MiniTwitService;

public class Application {

	public static void main(String[] args) throws SQLException {
		// wire the application together: DataSource -> DAOs -> Service -> WebConfig
		final JDBCDataSource dataSource = new JDBCDataSource();
		dataSource.setURL("minitwit");
		buildDatabase(dataSource);
		final UserDao userDAO = new UserDaoImpl(dataSource);
		final MessageDao messageDAO = new MessageDaoImpl(dataSource);
		final MiniTwitService twitSvc = new MiniTwitService(userDAO, messageDAO);
		new WebConfig(twitSvc);
	}

	private static void buildDatabase(final DataSource dataSource)
			throws SQLException {
		final Connection conn = dataSource.getConnection();
		final Statement stmt = conn.createStatement();
		// create schema
		readSqlStatementsFromResource("/sql/create-db.sql")
		.forEach(ddl -> executeStatement(stmt, ddl));
		// load data
		readSqlStatementsFromResource("/sql/insert-data.sql")
		.forEach(ddl -> executeStatement(stmt, ddl));
	}
	
	private static void executeStatement(final Statement stmt, final String sql) {
		try {
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Stream<String> readSqlStatementsFromResource(final String resource) {
		try (final BufferedReader reader =
		// build a closeable reader to the resource
		Files.newBufferedReader(Paths.get(Application.class.getResource(resource).toURI()))) {
			// parser the file into SQL statements (separated by semicolons)
			final List<String> statements = new LinkedList<String>();
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				if (line.endsWith(";")) {
					statements.add(buffer.toString());
					buffer = new StringBuilder();
				}
			}
			//
			return statements.stream();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
