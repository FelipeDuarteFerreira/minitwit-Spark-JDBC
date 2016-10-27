package com.minitwit.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import com.minitwit.dao.UserDao;
import com.minitwit.dao.impl.NamedParameterJdbcTemplate.RowMapper;
import com.minitwit.model.User;

public class UserDaoImpl implements UserDao {
	
	private NamedParameterJdbcTemplate template;

	public UserDaoImpl(DataSource ds) {
		template = new NamedParameterJdbcTemplate(ds);
	}

	@Override
	public User getUserbyUsername(String username) {
		String sql = "SELECT * FROM user WHERE username = ?";
		
        List<User> list = template.query(
                    sql,
                    new Object[] { username },
                    userMapper);
        
        User result = null;
        if(list != null && !list.isEmpty()) {
        	result = list.get(0);
        }
        
		return result;
	}

	@Override
	public void insertFollower(User follower, User followee) {        
		String sql = "insert into follower (follower_id, followee_id) values (?, ?)";
        template.update(sql, new Object[] { follower.getId(), followee.getId() });
	}

	@Override
	public void deleteFollower(User follower, User followee) {
		String sql = "delete from follower where follower_id = ? and followee_id = ?";
        template.update(sql, new Object[] { follower.getId(), followee.getId() });
	}
	
	@Override
	public boolean isUserFollower(User follower, User followee) {
		String sql = "select count(1) from follower where " +
            "follower.follower_id = ? and follower.followee_id = ?";
		Long l = template.queryForLong(sql, new Object[] { follower.getId(), followee.getId() });
		return l > 0;
	}

	@Override
	public void registerUser(User user) {
		String sql = "insert into user (username, email, pw) values (?, ?, ?)";
        template.update(sql, new Object[] { user.getUsername(), user.getEmail(), user.getPassword() });
	}

	private RowMapper<User> userMapper = (rs, rowNum) -> {
		User u = new User();
		
		u.setId(rs.getInt("user_id"));
		u.setEmail(rs.getString("email"));
		u.setUsername(rs.getString("username"));
		u.setPassword(rs.getString("pw"));
		
		return u;
	};
}
