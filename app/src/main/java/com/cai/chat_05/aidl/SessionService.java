/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\workspace_android\\weichatApp\\weichatApp\\src\\main\\aidl\\src\\org\\weishe\\weichat\\aidl\\SessionService.aidl
 */
package com.cai.chat_05.aidl;

public interface SessionService extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements SessionService {
        private static final java.lang.String DESCRIPTOR = "org.weishe.weichat.aidl.SessionService";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an org.weishe.weichat.aidl.SessionService interface,
         * generating a proxy if needed.
         */
        public static SessionService asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof SessionService))) {
                return ((SessionService) iin);
            }
            return new SessionService.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_sendMessage: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    int _arg1;
                    _arg1 = data.readInt();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    int _arg3;
                    _arg3 = data.readInt();
                    int _arg4;
                    _arg4 = data.readInt();
                    java.lang.String _arg5;
                    _arg5 = data.readString();
                    java.lang.String _arg6;
                    _arg6 = data.readString();
                    this.sendMessage(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_sendAttachment: {
                    data.enforceInterface(DESCRIPTOR);
                    long _arg0;
                    _arg0 = data.readLong();
                    this.sendAttachment(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getFriendList: {
                    data.enforceInterface(DESCRIPTOR);
                    this.getFriendList();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getFriendGroupsList: {
                    data.enforceInterface(DESCRIPTOR);
                    this.getFriendGroupsList();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getChatGroupList: {
                    data.enforceInterface(DESCRIPTOR);
                    this.getChatGroupList();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getChatGroupMemberList: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.getChatGroupMemberList(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getDiscussionGroupList: {
                    data.enforceInterface(DESCRIPTOR);
                    this.getDiscussionGroupList();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getDiscussionGroupMemberList: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.getDiscussionGroupMemberList(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getMessageList: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.getMessageList(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getTodoList: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.getTodoList(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getUserId: {
                    data.enforceInterface(DESCRIPTOR);
                    int _result = this.getUserId();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_getUserName: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _result = this.getUserName();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_getToken: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _result = this.getToken();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_getRelateUser: {
                    data.enforceInterface(DESCRIPTOR);
                    this.getRelateUser();
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements SessionService {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            /**
             * 发送消息
             */
            @Override
            public void sendMessage(java.lang.String uuid, int contentType, java.lang.String message, int toId, int msgType, java.lang.String fileGroupName, java.lang.String filePath) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(uuid);
                    _data.writeInt(contentType);
                    _data.writeString(message);
                    _data.writeInt(toId);
                    _data.writeInt(msgType);
                    _data.writeString(fileGroupName);
                    _data.writeString(filePath);
                    mRemote.transact(Stub.TRANSACTION_sendMessage, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 发送文件消息
             */
            @Override
            public void sendAttachment(long id) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(id);
                    mRemote.transact(Stub.TRANSACTION_sendAttachment, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取朋友列表
             */
            @Override
            public void getFriendList() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getFriendList, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取朋友分组列表
             */
            @Override
            public void getFriendGroupsList() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getFriendGroupsList, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取朋友群组列表
             */
            @Override
            public void getChatGroupList() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getChatGroupList, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取群组成员列表
             */
            @Override
            public void getChatGroupMemberList(int groupId) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(groupId);
                    mRemote.transact(Stub.TRANSACTION_getChatGroupMemberList, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取所在讨论组列表
             */
            @Override
            public void getDiscussionGroupList() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getDiscussionGroupList, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取讨论组成员列表
             */
            @Override
            public void getDiscussionGroupMemberList(int dGroupId) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(dGroupId);
                    mRemote.transact(Stub.TRANSACTION_getDiscussionGroupMemberList, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取用户聊天消息
             */
            @Override
            public void getMessageList(int fromMessageId) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(fromMessageId);
                    mRemote.transact(Stub.TRANSACTION_getMessageList, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /**
             * 获取用户待办消息
             */
            @Override
            public void getTodoList(int fromMessageId) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(fromMessageId);
                    mRemote.transact(Stub.TRANSACTION_getTodoList, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public int getUserId() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getUserId, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public java.lang.String getUserName() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getUserName, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public java.lang.String getToken() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getToken, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * 获取与我相关的用户
             */
            @Override
            public void getRelateUser() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getRelateUser, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_sendMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_sendAttachment = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_getFriendList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_getFriendGroupsList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_getChatGroupList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
        static final int TRANSACTION_getChatGroupMemberList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
        static final int TRANSACTION_getDiscussionGroupList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
        static final int TRANSACTION_getDiscussionGroupMemberList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
        static final int TRANSACTION_getMessageList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
        static final int TRANSACTION_getTodoList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
        static final int TRANSACTION_getUserId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
        static final int TRANSACTION_getUserName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
        static final int TRANSACTION_getToken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
        static final int TRANSACTION_getRelateUser = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
    }

    /**
     * 发送消息
     */
    public void sendMessage(java.lang.String uuid, int contentType, java.lang.String message, int toId, int msgType, java.lang.String fileGroupName, java.lang.String filePath) throws android.os.RemoteException;

    /**
     * 发送文件消息
     */
    public void sendAttachment(long id) throws android.os.RemoteException;

    /**
     * 获取朋友列表
     */
    public void getFriendList() throws android.os.RemoteException;

    /**
     * 获取朋友分组列表
     */
    public void getFriendGroupsList() throws android.os.RemoteException;

    /**
     * 获取朋友群组列表
     */
    public void getChatGroupList() throws android.os.RemoteException;

    /**
     * 获取群组成员列表
     */
    public void getChatGroupMemberList(int groupId) throws android.os.RemoteException;

    /**
     * 获取所在讨论组列表
     */
    public void getDiscussionGroupList() throws android.os.RemoteException;

    /**
     * 获取讨论组成员列表
     */
    public void getDiscussionGroupMemberList(int dGroupId) throws android.os.RemoteException;

    /**
     * 获取用户聊天消息
     */
    public void getMessageList(int fromMessageId) throws android.os.RemoteException;

    /**
     * 获取用户待办消息
     */
    public void getTodoList(int fromMessageId) throws android.os.RemoteException;

    public int getUserId() throws android.os.RemoteException;

    public java.lang.String getUserName() throws android.os.RemoteException;

    public java.lang.String getToken() throws android.os.RemoteException;

    /**
     * 获取与我相关的用户
     */
    public void getRelateUser() throws android.os.RemoteException;
}

