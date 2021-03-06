package com.sbbs.me.android.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sbbs.me.android.api.SbbsMeInboxUser;
import com.sbbs.me.android.api.SbbsMePrivateMessage;

public class PrivateMessageUtils {

	public static void saveMessages(Context context,
			List<SbbsMePrivateMessage> list) {
		if (context != null && list != null) {
			try {
				ContentResolver cr = context.getContentResolver();
				Uri req = ContentUris.withAppendedId(
						PrivateMessageProvider.CONTENT_URI,
						PrivateMessageProvider.ACTION_MESSAGE);
				for (int i = 0; i < list.size(); i++) {
					ContentValues cv = new ContentValues();
					cv.put("id", list.get(i).Id);
					Log.e("saveMessages", list.get(i).Id);
					cv.put("from_user_id", list.get(i).FromUserId);
					cv.put("from_user_name", list.get(i).FromUserName);
					cv.put("to_user_id", list.get(i).ToUserId);
					cv.put("to_user_name", list.get(i).ToUserName);
					cv.put("body", list.get(i).Body);
					cv.put("created_on", list.get(i).Created_on);
					cv.put("read", 0);
					cr.insert(req, cv);
				}
			} catch (Exception e) {

			}
		}
	}

	public static List<SbbsMePrivateMessage> queryMessages(Context context) {
		List<SbbsMePrivateMessage> list = null;
		if (context != null) {
			Cursor c = context.getContentResolver().query(
					ContentUris.withAppendedId(
							PrivateMessageProvider.CONTENT_URI,
							PrivateMessageProvider.ACTION_MESSAGE), null, null,
					null, null);
			list = buildMessageList(c);
		}
		return list;
	}

	public static List<SbbsMePrivateMessage> queryMessages(Context context,
			String toUserId) {
		List<SbbsMePrivateMessage> list = null;
		if (context != null) {
			Cursor c = context.getContentResolver().query(
					ContentUris.withAppendedId(
							PrivateMessageProvider.CONTENT_URI,
							PrivateMessageProvider.ACTION_MESSAGE), null,
					"from_user_id=? or to_user_id=?",
					new String[] { toUserId, toUserId }, null);
			list = buildMessageList(c);
		}
		return list;
	}

	private static List<SbbsMePrivateMessage> buildMessageList(Cursor c) {
		List<SbbsMePrivateMessage> list = null;

		if (c != null) {
			c.moveToFirst();
			list = new ArrayList<SbbsMePrivateMessage>();
			while (!c.isAfterLast()) {
				SbbsMePrivateMessage msg = new SbbsMePrivateMessage();
				msg._id = c.getString(c.getColumnIndex("id"));
				msg.Id = msg._id;
				msg.FromUserId = c.getString(c.getColumnIndex("from_user_id"));
				msg.FromUserName = c.getString(c
						.getColumnIndex("from_user_name"));
				msg.ToUserId = c.getString(c.getColumnIndex("to_user_id"));
				msg.ToUserName = c.getString(c.getColumnIndex("to_user_name"));
				msg.Body = c.getString(c.getColumnIndex("body"));
				msg.Created_on = c.getString(c.getColumnIndex("created_on"));
				msg.read = (c.getInt(c.getColumnIndex("read")) == 1);

				list.add(msg);
				c.moveToNext();
			}
			c.close();
		}

		return list;
	}

	public static void setReadState(Context context, String userId, boolean read) {
		ContentValues cv = new ContentValues();
		cv.put("read", (read ? 1 : 0));
		if (context != null) {
			try {
				context.getContentResolver().update(
						ContentUris.withAppendedId(
								PrivateMessageProvider.CONTENT_URI,
								PrivateMessageProvider.ACTION_MESSAGE), cv,
						"from_user_id=?", new String[] { userId });
			} catch (Exception e) {

			}
		}
	}

	public static String getLastMessageId(Context context) {
		String id = "0";
		if (context != null) {
			try {
				Cursor c = context.getContentResolver().query(
						ContentUris.withAppendedId(
								PrivateMessageProvider.CONTENT_URI,
								PrivateMessageProvider.ACTION_LAST_MESSAGE_ID),
						null, null, null, null);
				if (c != null) {
					c.moveToFirst();
					while (!c.isAfterLast()) {
						id = c.getString(c.getColumnIndex("id"));
						c.moveToNext();
					}
					c.close();
				}
			} catch (Exception e) {

			}
		}
		return id;
	}

	public static List<Boolean> getNewMessageStatus(Context context,
			List<SbbsMeInboxUser> list) {
		List<Boolean> listRet = new ArrayList<Boolean>();
		for (int i = 0; i < list.size(); i++) {
			Cursor c = context.getContentResolver().query(
					ContentUris.withAppendedId(
							PrivateMessageProvider.CONTENT_URI,
							PrivateMessageProvider.ACTION_QUERY_NEW), null,
					"read=? and from_user_id=?",
					new String[] { "0", list.get(i).UserId }, null);
			boolean stat = false;
			if (c != null) {
				c.moveToFirst();
				while (!c.isAfterLast()) {
					stat = true;
					c.moveToNext();
				}
				c.close();
			}
			listRet.add(stat);
		}
		return listRet;
	}

}
