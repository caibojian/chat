package com.cai.chat_05;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.cai.chat_05.core.bean.ChatMessage;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table CHAT_MESSAGE.
*/
public class ChatMessageDao extends AbstractDao<ChatMessage, Long> {

    public static final String TABLENAME = "CHAT_MESSAGE";

    /**
     * Properties of entity ChatMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ChatMessageId = new Property(1, Integer.class, "chatMessageId", false, "CHAT_MESSAGE_ID");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property FromId = new Property(3, Integer.class, "fromId", false, "FROM_ID");
        public final static Property ToId = new Property(4, Integer.class, "toId", false, "TO_ID");
        public final static Property Date = new Property(5, java.util.Date.class, "date", false, "DATE");
        public final static Property Type = new Property(6, Integer.class, "type", false, "TYPE");
        public final static Property MsgType = new Property(7, Integer.class, "msgType", false, "MSG_TYPE");
        public final static Property ChatGroupId = new Property(8, Integer.class, "chatGroupId", false, "CHAT_GROUP_ID");
        public final static Property DiscussionGroupId = new Property(9, Integer.class, "discussionGroupId", false, "DISCUSSION_GROUP_ID");
        public final static Property WhoId = new Property(10, Integer.class, "whoId", false, "WHO_ID");
        public final static Property Checked = new Property(11, Boolean.class, "checked", false, "CHECKED");
        public final static Property AttachmentId = new Property(12, Long.class, "attachmentId", false, "ATTACHMENT_ID");
        public final static Property ContentType = new Property(13, Integer.class, "contentType", false, "CONTENT_TYPE");
        public final static Property FileGroupName = new Property(14, String.class, "fileGroupName", false, "FILE_GROUP_NAME");
        public final static Property FilePath = new Property(15, String.class, "filePath", false, "FILE_PATH");
        public final static Property Uuid = new Property(16, String.class, "uuid", false, "UUID");
        public final static Property Status = new Property(17, Integer.class, "status", false, "STATUS");
    };


    public ChatMessageDao(DaoConfig config) {
        super(config);
    }
    
    public ChatMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CHAT_MESSAGE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'CHAT_MESSAGE_ID' INTEGER," + // 1: chatMessageId
                "'CONTENT' TEXT," + // 2: content
                "'FROM_ID' INTEGER," + // 3: fromId
                "'TO_ID' INTEGER," + // 4: toId
                "'DATE' INTEGER," + // 5: date
                "'TYPE' INTEGER," + // 6: type
                "'MSG_TYPE' INTEGER," + // 7: msgType
                "'CHAT_GROUP_ID' INTEGER," + // 8: chatGroupId
                "'DISCUSSION_GROUP_ID' INTEGER," + // 9: discussionGroupId
                "'WHO_ID' INTEGER," + // 10: whoId
                "'CHECKED' INTEGER," + // 11: checked
                "'ATTACHMENT_ID' INTEGER," + // 12: attachmentId
                "'CONTENT_TYPE' INTEGER," + // 13: contentType
                "'FILE_GROUP_NAME' TEXT," + // 14: fileGroupName
                "'FILE_PATH' TEXT," + // 15: filePath
                "'UUID' TEXT," + // 16: uuid
                "'STATUS' INTEGER);"); // 17: status
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CHAT_MESSAGE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ChatMessage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer chatMessageId = entity.getChatMessageId();
        if (chatMessageId != null) {
            stmt.bindLong(2, chatMessageId);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        Integer fromId = entity.getFromId();
        if (fromId != null) {
            stmt.bindLong(4, fromId);
        }
 
        Integer toId = entity.getToId();
        if (toId != null) {
            stmt.bindLong(5, toId);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(6, date.getTime());
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(7, type);
        }
 
        Integer msgType = entity.getMsgType();
        if (msgType != null) {
            stmt.bindLong(8, msgType);
        }
 
        Integer chatGroupId = entity.getChatGroupId();
        if (chatGroupId != null) {
            stmt.bindLong(9, chatGroupId);
        }
 
        Integer discussionGroupId = entity.getDiscussionGroupId();
        if (discussionGroupId != null) {
            stmt.bindLong(10, discussionGroupId);
        }
 
        Integer whoId = entity.getWhoId();
        if (whoId != null) {
            stmt.bindLong(11, whoId);
        }
 
        Boolean checked = entity.getChecked();
        if (checked != null) {
            stmt.bindLong(12, checked ? 1l: 0l);
        }
 
        Long attachmentId = entity.getAttachmentId();
        if (attachmentId != null) {
            stmt.bindLong(13, attachmentId);
        }
 
        Integer contentType = entity.getContentType();
        if (contentType != null) {
            stmt.bindLong(14, contentType);
        }
 
        String fileGroupName = entity.getFileGroupName();
        if (fileGroupName != null) {
            stmt.bindString(15, fileGroupName);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(16, filePath);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(17, uuid);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(18, status);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ChatMessage readEntity(Cursor cursor, int offset) {
        ChatMessage entity = new ChatMessage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // chatMessageId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // fromId
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // toId
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // date
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // type
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // msgType
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // chatGroupId
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // discussionGroupId
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // whoId
            cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0, // checked
            cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12), // attachmentId
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // contentType
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // fileGroupName
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // filePath
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // uuid
            cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17) // status
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ChatMessage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setChatMessageId(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFromId(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setToId(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setDate(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setType(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setMsgType(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setChatGroupId(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setDiscussionGroupId(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setWhoId(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setChecked(cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0);
        entity.setAttachmentId(cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12));
        entity.setContentType(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setFileGroupName(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setFilePath(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setUuid(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setStatus(cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ChatMessage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ChatMessage entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
