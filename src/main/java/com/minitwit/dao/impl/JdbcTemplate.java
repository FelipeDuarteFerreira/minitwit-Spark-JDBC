package com.minitwit.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

/**
 * 
 * 
 * @author <a href='mailto:basham47@gmail.com'>Bryan Basham</a> 
 */
public class JdbcTemplate {
	
	@FunctionalInterface
	public interface RowMapper<T> {
		/**
		 * Implementations must implement this method to map each row of data
		 * in the ResultSet. This method should not call {@code next()} on
		 * the ResultSet; it is only supposed to map values of the current row.
		 * @param rs the ResultSet to map (pre-initialized for the current row)
		 * @param rowNum the number of the current row
		 * @return the result object for the current row
		 * @throws SQLException if a SQLException is encountered getting
		 * column values (that is, there's no need to catch SQLException)
		 */
		T mapRow(ResultSet rs, int rowNum) throws SQLException;
	}

	
	private final DataSource dataSource;

	public JdbcTemplate(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public <T> List<T> query(
			final String sql,
			final Object[] params,
			final RowMapper<T> mapper) {
		try ( // closeable resources
				final Connection conn = dataSource.getConnection();
				final PreparedStatement stmt = conn.prepareStatement(sql)) {
			// perform query
			for (int idx = 0; idx < params.length; idx++) {
				stmt.setObject(idx+1, params[idx]);
			}
			final ResultSet rs = stmt.executeQuery();
			// map results
			final List<T> list = new LinkedList<T>();
			int rowNum = 0;
			while (rs.next()) {
				list.add(mapper.mapRow(rs, rowNum++));
			}
			return list;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Long queryForLong(final String sql, final Object[] params) {
		try ( // closeable resources
				final Connection conn = dataSource.getConnection();
				final PreparedStatement stmt = conn.prepareStatement(sql)) {
			// perform query
			for (int idx = 0; idx < params.length; idx++) {
				stmt.setObject(idx+1, params[idx]);
			}
			final ResultSet rs = stmt.executeQuery();
			// expecting only one result
			if (rs.next()) {
				return rs.getLong(1);
			} else {
				throw new IllegalStateException("expecting at least one result");
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void update(final String sql, final Object[] params) {
		try ( // closeable resources
				final Connection conn = dataSource.getConnection();
				final PreparedStatement stmt = conn.prepareStatement(sql)) {
			// perform statement
			for (int idx = 0; idx < params.length; idx++) {
				stmt.setObject(idx+1, params[idx]);
			}
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
