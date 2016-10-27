package com.minitwit.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import com.minitwit.dao.MessageDao;
import com.minitwit.dao.impl.NamedParameterJdbcTemplate.RowMapper;
import com.minitwit.model.Message;
import com.minitwit.model.User;
import com.minitwit.util.GravatarUtil;

public class MessageDaoImpl implements MessageDao {
	
	private static final String GRAVATAR_DEFAULT_IMAGE_TYPE = "monsterid";
	private static final int GRAVATAR_SIZE = 48;
	private NamedParameterJdbcTemplate template;

	public MessageDaoImpl(DataSource ds) {
		template = new NamedParameterJdbcTemplate(ds);
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
        String sql = "insert into message (author_id, text, pub_date) values (:userId, :text, :pubDate)";
		template.update(sql, new Object[] { m.getUserId(), m.getText(), m.getPubDate() });
	}
	
	private final RowMapper<Message> messageMapper = (rs, rowNum) -> {
		Message m = new Message();
		
		m.setId(rs.getInt("message_id"));
		m.setUserId(rs.getInt("author_id"));
		m.setUsername(rs.getString("username"));
		m.setText(rs.getString("text"));
		m.setPubDate(rs.getTimestamp("pub_date"));
		m.setGravatar(GravatarUtil.gravatarURL(rs.getString("email"), GRAVATAR_DEFAULT_IMAGE_TYPE, GRAVATAR_SIZE));
		
		return m;
	};

}
