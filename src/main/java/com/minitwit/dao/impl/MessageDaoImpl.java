package com.minitwit.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import com.minitwit.dao.MessageDao;
import com.minitwit.dao.impl.JdbcTemplate.RowMapper;
import com.minitwit.model.Message;
import com.minitwit.model.User;

public class MessageDaoImpl implements MessageDao {
	
	private final JdbcTemplate template;

	public MessageDaoImpl(DataSource ds) {
		template = new JdbcTemplate(ds);
	}

	@Override
	public List<Message> getUserTimelineMessages(User user) {
		String sql = "select message.*, user.* from message, user where " +
				"user.user_id = message.author_id and user.user_id = ? " +
				"order by message.pub_date desc";
		return template.query(sql, new Object[] { user.getId() }, messageMapper);
	}

	@Override
	public List<Message> getUserFullTimelineMessages(User user) {        
		String sql = "select message.*, user.* from message, user " +
				"where message.author_id = user.user_id and ( " +
				"user.user_id = ? or " +
				"user.user_id in (select followee_id from follower " +
                                    "where follower_id = ?))" +
                "order by message.pub_date desc";
		return template.query(sql, new Object[] { user.getId(), user.getId() }, messageMapper);
	}

	@Override
	public List<Message> getPublicTimelineMessages() {
		String sql = "select message.*, user.* from message, user " +
				"where message.author_id = user.user_id " +
				"order by message.pub_date desc";
		return template.query(sql, new Object[0], messageMapper);
	}

	@Override
	public void insertMessage(Message m) {
        String sql = "insert into message (author_id, text, pub_date) values (?, ?, ?)";
		template.update(sql, new Object[] { m.getUser().getId(), m.getText(), m.getPubDate() });
	}
	
	private final RowMapper<Message> messageMapper = (rs, rowNum) -> {
		Message m = new Message();
		
		m.setId(rs.getInt("message_id"));
		m.setUser(UserDaoImpl.userMapper.mapRow(rs, rowNum));
		m.setText(rs.getString("text"));
		m.setPubDate(rs.getTimestamp("pub_date"));
		
		return m;
	};

}
