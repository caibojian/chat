package com.cai.chat_05.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.cai.chat_05.AttachmentDao;
import com.cai.chat_05.ChatMessageDao;
import com.cai.chat_05.DaoMaster;
import com.cai.chat_05.DaoSession;
import com.cai.chat_05.TodoDao;
import com.cai.chat_05.bean.Attachment;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.bean.Todo;
import com.cai.chat_05.core.bean.ChatMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;


public class DBHelper {
	private DaoMaster.DevOpenHelper helper;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private static DBHelper mDBHelper;

	private DBHelper(Context context) {
		helper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();

		QueryBuilder.LOG_SQL = true;
		QueryBuilder.LOG_VALUES = true;
	}

	public static DBHelper getgetInstance(Context context) {

		if (mDBHelper != null) {
			return mDBHelper;
		} else {
			mDBHelper = new DBHelper(context);
			return mDBHelper;
		}
	}

	public void addChatMessage(ChatMessage chatMessage, int whoId) {
		ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();
		// 先查询
		qb.where(ChatMessageDao.Properties.Uuid.eq(chatMessage.getUuid() + ""));
		ChatMessage cm = qb.unique();
		if (cm != null) {
			cm.setChatMessageId(chatMessage.getChatMessageId());
			chatMessageDao.update(cm);
		} else {
			chatMessageDao.insert(chatMessage);
		}

		/*
		 * switch (chatMessage.getMsgType()) { case ChatMessage.MSG_TYPE_UU:
		 * qb.where(Properties.FromId.eq(chatMessage.getFromId()),
		 * Properties.ToId.eq(chatMessage.getToId()),
		 * Properties.Content.eq(chatMessage.getContent()),
		 * Properties.MsgType.eq(ChatMessage.MSG_TYPE_UU), //
		 * Properties.Date.eq(chatMessage.getDate()), //
		 * Properties.Type.eq(chatMessage.getType()),
		 * Properties.WhoId.eq(whoId)); ChatMessage cm = qb.unique(); if (cm !=
		 * null) { if (cm.getChatMessageId() == chatMessage.getChatMessageId())
		 * {
		 *
		 * } else { if (cm.getChatMessageId() < 1) {
		 * cm.setChatMessageId(chatMessage.getChatMessageId());
		 * chatMessageDao.update(cm); } }
		 *
		 * } else { chatMessageDao.insert(chatMessage); } break; case
		 * ChatMessage.MSG_TYPE_UCG:
		 * qb.where(Properties.FromId.eq(chatMessage.getFromId()),
		 * Properties.ChatGroupId.eq(chatMessage.getChatGroupId()),
		 * Properties.Content.eq(chatMessage.getContent()),
		 * Properties.WhoId.eq(whoId)); ChatMessage cm1 = qb.unique(); if (cm1
		 * != null) { if (cm1.getChatMessageId() ==
		 * chatMessage.getChatMessageId()) {
		 *
		 * } else { if (cm1.getChatMessageId() < 1) {
		 * cm1.setChatMessageId(chatMessage.getChatMessageId());
		 * chatMessageDao.update(cm1); } }
		 *
		 * } else { chatMessageDao.insert(chatMessage); } break; case
		 * ChatMessage.MSG_TYPE_UDG:
		 * qb.where(Properties.FromId.eq(chatMessage.getFromId()),
		 * Properties.DiscussionGroupId.eq(chatMessage .getDiscussionGroupId()),
		 * Properties.Content .eq(chatMessage.getContent()), Properties.WhoId
		 * .eq(whoId)); ChatMessage cm2 = qb.unique(); if (cm2 != null) { if
		 * (cm2.getChatMessageId() == chatMessage.getChatMessageId()) {
		 *
		 * } else { if (cm2.getChatMessageId() < 1) {
		 * cm2.setChatMessageId(chatMessage.getChatMessageId());
		 * chatMessageDao.update(cm2); } }
		 *
		 * } else { chatMessageDao.insert(chatMessage); } break; default: break;
		 * }
		 */
	}

//	public void addTodo(Todo todo) {
//		TodoDao todoDao = daoSession.getTodoDao();
//		todoDao.insert(todo);
//	}

//	public Todo getTodoByTodoId(int todoId, int whoId) {
//
//		TodoDao todoDao = daoSession.getTodoDao();
//		QueryBuilder<Todo> qb = todoDao.queryBuilder();
//		qb.where(org.weishe.weichat.TodoDao.Properties.TodoId.eq(todoId),
//				org.weishe.weichat.TodoDao.Properties.WhoId.eq(whoId));
//		List<Todo> todos = qb.list();
//		if (todos != null && todos.size() > 0) {
//			return todos.get(0);
//		}
//		return null;
//	}

	/**
	 * 获取聊天记录
	 * 
	 * @param userId
	 *
	 * @param pageSize
	 * @return
	 */
	public List<ChatMessage> getChatMessageByPage(int userId, int chatWithId,
												  int chatType, int pageSize) {
		ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();
		switch (chatType) {
		case Constants.MSG_TYPE_UU:
			qb.where(ChatMessageDao.Properties.WhoId.eq(userId), ChatMessageDao.Properties.MsgType
					.eq(Constants.MSG_TYPE_UU), qb.or(qb.and(
					ChatMessageDao.Properties.FromId.eq(chatWithId),
					ChatMessageDao.Properties.Type.eq(Constants.TYPE_RECEIVE),
					ChatMessageDao.Properties.ToId.eq(userId)), qb.and(
					ChatMessageDao.Properties.FromId.eq(userId),
					ChatMessageDao.Properties.Type.eq(Constants.TYPE_SEND),
					ChatMessageDao.Properties.ToId.eq(chatWithId))));
			break;
		case Constants.MSG_TYPE_UCG:
			qb.where(ChatMessageDao.Properties.WhoId.eq(userId),
					ChatMessageDao.Properties.ChatGroupId.eq(chatWithId),
					ChatMessageDao.Properties.MsgType.eq(Constants.MSG_TYPE_UCG));
			break;
		case Constants.MSG_TYPE_UDG:
			qb.where(ChatMessageDao.Properties.WhoId.eq(userId),
					ChatMessageDao.Properties.DiscussionGroupId.eq(chatWithId),
					ChatMessageDao.Properties.MsgType.eq(Constants.MSG_TYPE_UDG));
			break;
		}

		qb.limit(pageSize);
		return qb.list();
	}

	/**
	 * 获取当前用户下载的最大messsageid
	 * 
	 * @param
	 * @return
	 */
	public int getMaxMessageIdByUserId(int whoId) {
		ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();
		qb.where(ChatMessageDao.Properties.WhoId.eq(whoId));
		qb.limit(1);
		qb.orderDesc(ChatMessageDao.Properties.ChatMessageId);
		ChatMessage m = qb.unique();
		int maxId = 0;
		if (m != null) {
			maxId = m.getChatMessageId();
		}
		return maxId;
	}

	public long getMaxMessageId() {
		ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();
		qb.limit(1);
		qb.orderDesc(ChatMessageDao.Properties.Id);
		ChatMessage m = qb.unique();
		long maxId = 0;
		if (m != null) {
			maxId = m.getId();
		}
		return maxId;
	}
	/**
	 * 获取当前用户下载的最大todoId
	 * 
	 * @return
	 */
	public int getMaxTodoIdByUserId(int whoId) {
		TodoDao todoDao = daoSession.getTodoDao();
		QueryBuilder<Todo> qb = todoDao.queryBuilder();
		qb.where(TodoDao.Properties.WhoId.eq(whoId));
		qb.limit(1);
		qb.orderDesc(TodoDao.Properties.TodoId);
		Todo m = qb.unique();
		int maxId = 0;
		if (m != null) {
			maxId = m.getId().intValue();
		}
		return maxId;
	}

	/**
	 * 获取代办事项
	 * 
	 * @param whoId
	 * @return
	 */
	public List<Todo> getTodo(int whoId) {
		TodoDao todoDao = daoSession.getTodoDao();
		QueryBuilder<Todo> qb = todoDao.queryBuilder();
		qb.where(TodoDao.Properties.WhoId.eq(whoId));
		qb.orderDesc(TodoDao.Properties.CreateDate);
		List<Todo> todos = qb.list();
		return todos;
	}

	/**
	 * 获取最新的消息列表，最多20个
	 */
	public List<ChatMessage> getRecentChatMessage(int whoId) {
		// 1.好友消息
		String[] whereArgs = new String[] { whoId + "", whoId + "", whoId + "" };
		String queryString = "SELECT max( " + ChatMessageDao.Properties.Id.columnName
				+ " ) FROM  " + ChatMessageDao.TABLENAME + " WHERE "
				+ ChatMessageDao.Properties.MsgType.columnName + " = "
				+ Constants.MSG_TYPE_UU + " and "
				+ ChatMessageDao.Properties.WhoId.columnName + " = ? AND ("
				+ ChatMessageDao.Properties.FromId.columnName + "=? OR "
				+ ChatMessageDao.Properties.ToId.columnName + "=? )GROUP BY "
				+ ChatMessageDao.Properties.FromId.columnName + " , "
				+ ChatMessageDao.Properties.ToId.columnName + " ORDER BY "
				+ ChatMessageDao.Properties.Date.columnName + " DESC ";
		Log.v("org.weishe.weichat", "SQL:" + queryString);

		List<Long> list = new ArrayList<Long>();
		Cursor cs = this.db.rawQuery(queryString, whereArgs);
		while (cs.moveToNext()) {
			list.add(cs.getLong(0));
		}

		Map<Integer, ChatMessage> messages = new HashMap<Integer, ChatMessage>();
		Log.v("org.weishe.weichat", "查询出消息的个数:" + list.size());
		if (list.size() > 0) {
			for (Long id : list) {
				// ChatMessage m = getRecentChatMessageByFromId(id, whoId);
				ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
				ChatMessage m = chatMessageDao.load(id);
				// 获取该用户未读消息条数
				if (m != null) {

					switch (m.getType()) {
					case Constants.TYPE_RECEIVE:
						ChatMessage so = messages.get(m.getFromId());
						if (so == null
								|| (so != null && so.getId() < m.getId())) {
							messages.put(m.getFromId(), m);
							int count = getUncheckedChatMessageCount(
									m.getFromId(), whoId,
									Constants.MSG_TYPE_UU);
							m.setUnCheckedCount(count);
						}
						break;
					case Constants.TYPE_SEND:
						ChatMessage co = messages.get(m.getToId());
						if (co == null
								|| (co != null && co.getId() < m.getId())) {
							messages.put(m.getToId(), m);
							int count = getUncheckedChatMessageCount(
									m.getToId(), whoId, Constants.MSG_TYPE_UU);
							m.setUnCheckedCount(count);
						}
						break;
					default:
						break;
					}
				}
			}
		}

		// 2.群聊天记录
		String[] whereArgs2 = new String[] { whoId + "" };
		String queryString2 = "SELECT max( " + ChatMessageDao.Properties.Id.columnName
				+ " ) FROM  " + ChatMessageDao.TABLENAME + " WHERE "
				+ ChatMessageDao.Properties.MsgType.columnName + " = "
				+ Constants.MSG_TYPE_UCG + " and "
				+ ChatMessageDao.Properties.WhoId.columnName + " = ?  GROUP BY "
				+ ChatMessageDao.Properties.ChatGroupId.columnName + " ORDER BY "
				+ ChatMessageDao.Properties.Date.columnName + " DESC ";
		Log.v("org.weishe.weichat", "SQL2:" + queryString2);

		List<Long> list2 = new ArrayList<Long>();
		Cursor cs2 = this.db.rawQuery(queryString2, whereArgs2);
		while (cs2.moveToNext()) {
			list2.add(cs2.getLong(0));
		}
		if (list2 != null && list2.size() > 0) {
			ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
			for (long id : list2) {
				ChatMessage m = chatMessageDao.load(id);
				if (m != null) {
					messages.put(m.getChatGroupId() + 5000000, m);// 防止id重叠
				}
			}
		}
		// 3.讨论组
		String[] whereArgs3 = new String[] { whoId + "" };
		String queryString3 = "SELECT max( " + ChatMessageDao.Properties.Id.columnName
				+ " ) FROM  " + ChatMessageDao.TABLENAME + " WHERE "
				+ ChatMessageDao.Properties.MsgType.columnName + " = "
				+ Constants.MSG_TYPE_UDG + " and "
				+ ChatMessageDao.Properties.WhoId.columnName + " = ?  GROUP BY "
				+ ChatMessageDao.Properties.DiscussionGroupId.columnName + " ORDER BY "
				+ ChatMessageDao.Properties.Date.columnName + " DESC ";
		Log.v("org.weishe.weichat", "SQL2:" + queryString3);

		List<Long> list3 = new ArrayList<Long>();
		Cursor cs3 = this.db.rawQuery(queryString3, whereArgs3);
		while (cs3.moveToNext()) {
			list3.add(cs3.getLong(0));
		}
		if (list3 != null && list3.size() > 0) {
			ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
			for (long id : list3) {
				ChatMessage m = chatMessageDao.load(id);
				if (m != null) {
					messages.put(m.getDiscussionGroupId() + 8000000, m);// 防止id重叠
				}
			}
		}
		// 4.添加
		List<ChatMessage> l = new ArrayList<ChatMessage>();
		if (messages.size() > 0) {
			l.addAll(messages.values());
		}
		return l;
	}

	public List getRecentMessage(int whoId) {
		List<ChatMessage> cl = getRecentChatMessage(whoId);
		List<Todo> tl = getTodo(whoId);
		List l = new ArrayList();

		int cIndex = 0;
		int tIndex = 0;
		int total = 0;

		if (cl != null && cl.size() > 0 && tl != null && tl.size() > 0) {

			total = cl.size() + tl.size();
			for (int i = 0; i < total; i++) {
				if (cIndex <= (cl.size() - 1) && tIndex <= (tl.size() - 1)) {
					ChatMessage cm = cl.get(cIndex);
					Todo t = tl.get(tIndex);

					if (cm.getDate().getTime() > t.getCreateDate().getTime()) {
						l.add(cm);
						cIndex++;
					} else {
						l.add(t);
						tIndex++;
					}
				} else if (cIndex > (cl.size() - 1)) {
					Todo t = tl.get(tIndex);
					l.add(t);
					tIndex++;
				} else if (tIndex > (tl.size() - 1)) {
					ChatMessage cm = cl.get(cIndex);
					l.add(cm);
					cIndex++;
				}
			}
		} else if (cl == null || cl.size() < 1) {
			return tl;
		} else if (tl == null || tl.size() < 1) {
			return cl;
		}
		return l;
	}

	public int getUncheckedChatMessageCount(int fromId, int whoId, int msgType) {
		ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();
		qb.where(ChatMessageDao.Properties.FromId.eq(fromId), ChatMessageDao.Properties.WhoId.eq(whoId),
				ChatMessageDao.Properties.Checked.eq(false), ChatMessageDao.Properties.MsgType.eq(msgType));
		List l = qb.list();
		int count = 0;
		if (l != null) {
			count = l.size();
		}
		return count;
	}

	public ChatMessage getRecentChatMessageByFromId(int fromId, int whoId) {
		ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();
		qb.where(ChatMessageDao.Properties.FromId.eq(fromId), ChatMessageDao.Properties.WhoId.eq(whoId));
		qb.limit(1);
		qb.orderDesc(ChatMessageDao.Properties.Id);
		ChatMessage m = qb.unique();
		return m;
	}

	/**
	 * 添加 先查询是否存在在本地，如果存在更新服务端id字段即可
	 * 
	 * @param attachment
	 */
	public Attachment addAttachment(Attachment attachment) {
		AttachmentDao attachmentDao = daoSession.getAttachmentDao();

		QueryBuilder<Attachment> qb = attachmentDao.queryBuilder();
		qb.where(AttachmentDao.Properties.GroupName
				.eq(attachment.getGroupName()),
				AttachmentDao.Properties.Path.eq(attachment
						.getPath()));

		Attachment a = qb.unique();
		if (a != null) {
			if (attachment.getAttachmentId() > 0) {
				a.setAttachmentId(attachment.getAttachmentId());
				attachmentDao.update(a);
				return a;
			}
		} else {
			long id = attachmentDao.insert(attachment);
			if (id > 0) {
				attachment.setId(id);
				return attachment;
			}
		}
		return null;
	}

	public Attachment getAttachment(String groupName, String path) {
		AttachmentDao attachmentDao = daoSession.getAttachmentDao();

		QueryBuilder<Attachment> qb = attachmentDao.queryBuilder();
		qb.where(AttachmentDao.Properties.GroupName
				.eq(groupName),
				AttachmentDao.Properties.Path.eq(path));
		Attachment a = qb.unique();
		return a;
	}

	public Attachment getAttachment(long attachmentId) {
		AttachmentDao attachmentDao = daoSession.getAttachmentDao();

		QueryBuilder<Attachment> qb = attachmentDao.queryBuilder();
		qb.where(AttachmentDao.Properties.Id
				.eq(attachmentId));
		Attachment a = qb.unique();
		return a;
	}

	/**
	 * 更新本地数据库中为附件消息而又没有附件id的数据
	 * 
	 * @param file
	 */
	public void updateChatMessageAttachment(Attachment file) {
		if (file == null || file.getId() < 1) {
			return;
		}
		ChatMessageDao cdao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = cdao.queryBuilder();
		qb.where(
				ChatMessageDao.Properties.ContentType.eq(Constants.CONTENT_TYPE_ATTACHMENT),
				ChatMessageDao.Properties.FileGroupName.eq(file.getGroupName()),
				ChatMessageDao.Properties.FilePath.eq(file.getPath()));
		List<ChatMessage> cl = qb.list();
		if (cl != null && cl.size() > 0) {
			for (ChatMessage cm : cl) {
				cm.setAttachmentId(file.getId());
				cdao.update(cm);
			}
		}
	}

	public void updateChatMessageChecked(int whoId, int friendId) {

		ContentValues cv = new ContentValues();
		cv.put(ChatMessageDao.Properties.Checked.columnName, true);

		String[] args = { whoId + "", friendId + "" };
		db.update(ChatMessageDao.TABLENAME, cv, ChatMessageDao.Properties.WhoId.columnName
				+ " =? and " + ChatMessageDao.Properties.FromId.columnName + " =? ", args);

	}

	public void updateChatMessageUnCheck(Long id) {
		ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();
		qb.where(ChatMessageDao.Properties.Id.eq(id));
		ChatMessage msg = qb.unique();
		if (msg != null) {
			if (msg.getType() != Constants.TYPE_SEND) {
				msg.setChecked(false);
				chatMessageDao.update(msg);
			} else {
				// 查询一个最近的接收消息至为未读
				QueryBuilder<ChatMessage> qb2 = chatMessageDao.queryBuilder();
				qb2.where(ChatMessageDao.Properties.FromId.eq(msg.getToId()),
						ChatMessageDao.Properties.WhoId.eq(msg.getFromId()));
				qb2.limit(1);
				qb2.orderDesc(ChatMessageDao.Properties.Id);
				ChatMessage m = qb2.unique();
				if (m != null) {
					m.setChecked(false);
					chatMessageDao.update(m);
				}
			}
		}
	}

	/**
	 * 根据聊天对象删除这一类的聊天消息
	 * 
	 * @param
	 */
	public void deleteChatMessageByType(Long id) {
		ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();
		qb.where(ChatMessageDao.Properties.Id.eq(id));
		ChatMessage msg = qb.unique();
		if (msg != null) {
			String whereClause = "1=2";
			String[] whereArgs = null;
			switch (msg.getMsgType()) {
			case Constants.MSG_TYPE_UU:
				whereClause = ChatMessageDao.Properties.MsgType.columnName + " = ? and (("
						+ ChatMessageDao.Properties.FromId.columnName + " =  ? and "
						+ ChatMessageDao.Properties.ToId.columnName + " =  ? ) or ( "
						+ ChatMessageDao.Properties.FromId.columnName + " =  ? and "
						+ ChatMessageDao.Properties.ToId.columnName + " = ?  ))";
				String arg[] = { Constants.MSG_TYPE_UU + "",
						msg.getFromId() + "", msg.getToId() + "",
						msg.getToId() + "", msg.getFromId() + "" };
				whereArgs = arg;
				break;
			case Constants.MSG_TYPE_UCG:
				whereClause = ChatMessageDao.Properties.MsgType.columnName + " = ? and  "
						+ ChatMessageDao.Properties.ChatGroupId + " = ? ";
				String argc[] = { Constants.MSG_TYPE_UCG + "",
						msg.getChatGroupId() + "" };
				whereArgs = argc;
				break;
			case Constants.MSG_TYPE_UDG:
				whereClause = ChatMessageDao.Properties.MsgType.columnName + " = ? and "
						+ ChatMessageDao.Properties.DiscussionGroupId.columnName + " =? ";
				String argd[] = { Constants.MSG_TYPE_UDG + "",
						msg.getDiscussionGroupId() + "" };
				whereArgs = argd;
				break;
			}
			db.delete(ChatMessageDao.TABLENAME, whereClause, whereArgs);

		}
	}

	public void updateTodo(int todoId, boolean complete, boolean agree,
			String handleMsg) {
		TodoDao todoDao = daoSession.getTodoDao();
		QueryBuilder<Todo> qb = todoDao.queryBuilder();
		qb.where(TodoDao.Properties.TodoId.eq(todoId));
		Todo todo = qb.unique();
		todo.setComplete(complete);
		todo.setAgree(agree);
		todo.setChecked(true);
		todo.setHandleDate(StringUtils.getCurrentStringDate());
		todo.setHandleMsg(handleMsg);
		todoDao.update(todo);
	}

	public void updateChatMessageStatus(String uuid, int status) {
		ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
		QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();
		qb.where(ChatMessageDao.Properties.Uuid.eq(uuid));
		ChatMessage cm = qb.unique();
		if (cm != null) {
			if (cm.getStatus() < status) {
				cm.setStatus(status);
				chatMessageDao.update(cm);
			}
		}

	}
}
