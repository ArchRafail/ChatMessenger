package com.example.chatmessenger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.chatmessenger.chat_message.ChatMessage;
import com.example.chatmessenger.chat_user.ChatUser;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dBHelper;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "chatMessengerDb_001";

    public static final String TABLE_USERS = "users";
    public static final String KEY_USER_ID = "_id";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PASSWORD_MD5 = "passwordMD5";
    public static final String KEY_IS_ONLINE = "isOnline";

    public static final String TABLE_MESSAGES = "daddyMessages";
    public static final String KEY_MESSAGE_ID = "_id";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIME = "time";
    public static final String KEY_ID_USER = "idUser";
    public static final String KEY_TABLE_USERS = "users";

    public static synchronized DBHelper getInstance(Context context) {
        if (dBHelper == null) {
            dBHelper = new DBHelper(context.getApplicationContext());
        }
        return dBHelper;
    }

    private DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_USERS + "("
                + KEY_USER_ID + " integer primary key,"
                + KEY_LOGIN + " text,"
                + KEY_NICKNAME + " text,"
                + KEY_PHONE + " text,"
                + KEY_EMAIL + " text,"
                + KEY_PASSWORD + " text,"
                + KEY_PASSWORD_MD5 + " text,"
                + KEY_IS_ONLINE + " integer"
                + ")");
        db.execSQL("create table " + TABLE_MESSAGES + "("
                + KEY_MESSAGE_ID + " integer primary key,"
                + KEY_MESSAGE + " text,"
                + KEY_TIME + " text,"
                + KEY_ID_USER + " integer,"
                + " FOREIGN KEY" + "(" + KEY_ID_USER + ")" + " REFERENCES " + KEY_TABLE_USERS + "(_id)"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void setInitialData() throws NoSuchAlgorithmException {
        SQLiteDatabase database = dBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_LOGIN, "Admin");
        contentValues.put(KEY_NICKNAME, "Admin");
        contentValues.put(KEY_PHONE, "+380970123456");
        contentValues.put(KEY_EMAIL, "admin@gmail.com");
        contentValues.put(KEY_PASSWORD, "adm");
        contentValues.put(KEY_PASSWORD_MD5, md5("adm"));
        contentValues.put(KEY_IS_ONLINE, 0);
        database.insert(TABLE_USERS, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_LOGIN, "User");
        contentValues.put(KEY_NICKNAME, "Panas");
        contentValues.put(KEY_PHONE, "+380951234567");
        contentValues.put(KEY_EMAIL, "usr@post.ua");
        contentValues.put(KEY_PASSWORD, "1234");
        contentValues.put(KEY_PASSWORD_MD5, md5("1234"));
        contentValues.put(KEY_IS_ONLINE, 1);
        database.insert(TABLE_USERS, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_LOGIN, "Roman");
        contentValues.put(KEY_NICKNAME, "Roman");
        contentValues.put(KEY_PHONE, "+380983371711");
        contentValues.put(KEY_EMAIL, "roman@gmail.com");
        contentValues.put(KEY_PASSWORD, "rom123");
        contentValues.put(KEY_PASSWORD_MD5, md5("rom123"));
        contentValues.put(KEY_IS_ONLINE, 0);
        database.insert(TABLE_USERS, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_LOGIN, "Daddy");
        contentValues.put(KEY_NICKNAME, "Daddy");
        contentValues.put(KEY_PHONE, "+380992776875");
        contentValues.put(KEY_EMAIL, "dad@ukr.net");
        contentValues.put(KEY_PASSWORD, "d1a2d3");
        contentValues.put(KEY_PASSWORD_MD5, md5("d1a2d3"));
        contentValues.put(KEY_IS_ONLINE, 1);
        database.insert(TABLE_USERS, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_LOGIN, "Julia");
        contentValues.put(KEY_NICKNAME, "Julia");
        contentValues.put(KEY_PHONE, "+380965554444");
        contentValues.put(KEY_EMAIL, "jul@masyanya.ua");
        contentValues.put(KEY_PASSWORD,"juljul");
        contentValues.put(KEY_PASSWORD_MD5, md5("juljul"));
        contentValues.put(KEY_IS_ONLINE, 0);
        database.insert(TABLE_USERS, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_LOGIN, "Cartel");
        contentValues.put(KEY_NICKNAME, "Cartel");
        contentValues.put(KEY_PHONE, "none");
        contentValues.put(KEY_EMAIL, "cart@gmail.com");
        contentValues.put(KEY_PASSWORD, "cart");
        contentValues.put(KEY_PASSWORD_MD5, md5("cart"));
        contentValues.put(KEY_IS_ONLINE, 0);
        database.insert(TABLE_USERS, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_LOGIN, "Bombik");
        contentValues.put(KEY_NICKNAME, "Bombik");
        contentValues.put(KEY_PHONE, "none");
        contentValues.put(KEY_EMAIL, "bob@trust.com");
        contentValues.put(KEY_PASSWORD, "bob");
        contentValues.put(KEY_PASSWORD_MD5, md5("bob"));
        contentValues.put(KEY_IS_ONLINE, 1);
        database.insert(TABLE_USERS, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGE, "Hi Roman. How are you?");
        contentValues.put(KEY_TIME, "07.02.23 14:05");
        contentValues.put(KEY_ID_USER, 4);
        database.insert(TABLE_MESSAGES, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGE, "Hi Dad. I'm fine. Thank you. And how are You.");
        contentValues.put(KEY_TIME, "07.02.23 14:11");
        contentValues.put(KEY_ID_USER, 3);
        database.insert(TABLE_MESSAGES, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGE, "I'm Ok. Do you come to us today?");
        contentValues.put(KEY_TIME, "07.02.23 14:13");
        contentValues.put(KEY_ID_USER, 4);
        database.insert(TABLE_MESSAGES, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGE, "We are going to cover the dinner table.");
        contentValues.put(KEY_TIME, "07.02.23 14:13");
        contentValues.put(KEY_ID_USER, 4);
        database.insert(TABLE_MESSAGES, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGE, "Melissa is also coming for the holiday.");
        contentValues.put(KEY_TIME, "07.02.23 14:14");
        contentValues.put(KEY_ID_USER, 4);
        database.insert(TABLE_MESSAGES, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGE, "I can't promise You. I will try.");
        contentValues.put(KEY_TIME, "07.02.23 14:15");
        contentValues.put(KEY_ID_USER, 3);
        database.insert(TABLE_MESSAGES, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGE, "Hi. Are are you here?");
        contentValues.put(KEY_TIME, "08.02.23 15:42");
        contentValues.put(KEY_ID_USER, 3);
        database.insert(TABLE_MESSAGES, null, contentValues);

        dBHelper.close();
    }

    public ArrayList<ChatUser> getAllUsers() {
        ArrayList<ChatUser> users = new ArrayList<>();
        SQLiteDatabase database = dBHelper.getReadableDatabase();
        Cursor cursor =
                database.query(TABLE_USERS,
                        new String[]{KEY_LOGIN, KEY_NICKNAME, KEY_PHONE, KEY_EMAIL, KEY_PASSWORD_MD5, KEY_IS_ONLINE},
                        null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int loginIndex = cursor.getColumnIndex(KEY_LOGIN);
            int nicknameIndex = cursor.getColumnIndex(KEY_NICKNAME);
            int phoneIndex = cursor.getColumnIndex(KEY_PHONE);
            int emailIndex = cursor.getColumnIndex(KEY_EMAIL);
            int passwordIndex = cursor.getColumnIndex(KEY_PASSWORD_MD5);
            int isOnlineIndex = cursor.getColumnIndex(KEY_IS_ONLINE);
            do {
                users.add(new ChatUser(cursor.getString(loginIndex), cursor.getString(nicknameIndex),
                        cursor.getString(phoneIndex), cursor.getString(emailIndex), cursor.getString(passwordIndex),
                        convertIntToBoolean(cursor.getInt(isOnlineIndex))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public String getNicknameByLogin(String login) {
        SQLiteDatabase database = dBHelper.getReadableDatabase();
        Cursor cursor =
                database.query(TABLE_USERS, new String[] {KEY_NICKNAME}, KEY_LOGIN + "=?",
                        new String[] {login}, null, null, null);

        String nickname = null;

        if (cursor.moveToFirst()) {
            int nicknameIndex = cursor.getColumnIndex(KEY_NICKNAME);
            nickname =  cursor.getString(nicknameIndex);
        }
        cursor.close();
        return nickname;
    }

    public String getPasswordByEmail(String email) {
        SQLiteDatabase database = dBHelper.getReadableDatabase();
        Cursor cursor =
                database.query(TABLE_USERS, new String[] {KEY_PASSWORD}, KEY_EMAIL + "=?",
                        new String[] {email}, null, null, null);

        String password = null;

        if (cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex(KEY_PASSWORD);
            password =  cursor.getString(passwordIndex);
        }
        cursor.close();
        return password;
    }

    public void addUser(String login, String email, String password) throws NoSuchAlgorithmException {
        SQLiteDatabase database = dBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_LOGIN, login);
        contentValues.put(KEY_NICKNAME, login);
        contentValues.put(KEY_PHONE, "none");
        contentValues.put(KEY_EMAIL, email);
        contentValues.put(KEY_PASSWORD, password);
        contentValues.put(KEY_PASSWORD_MD5, md5(password));
        contentValues.put(KEY_IS_ONLINE, 1);
        database.insert(TABLE_USERS, null, contentValues);
    }

    public void changeOnlineStatus(String login, int isOnline) {
        SQLiteDatabase database = dBHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_IS_ONLINE, isOnline);
        database.update(TABLE_USERS, contentValues, KEY_LOGIN + "=?", new String[]{login});
    }

    public Boolean writeInfo(String login, String reason, String parameter) throws NoSuchAlgorithmException {
        SQLiteDatabase database = dBHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        switch (reason) {
            case "nickname":
                contentValues.put(KEY_NICKNAME, parameter);
                break;
            case "phone":
                contentValues.put(KEY_PHONE, parameter);
                break;
            case "email":
                contentValues.put(KEY_EMAIL, parameter);
                break;
            case "password":
                contentValues.put(KEY_PASSWORD, parameter);
                contentValues.put(KEY_PASSWORD_MD5, md5(parameter));
                break;
            default: return false;
        }
        database.update(TABLE_USERS, contentValues, KEY_LOGIN + "=?", new String[]{login});
        return true;
    }

    public ArrayList<ChatMessage> getAllMessages() {
        ArrayList<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase database = dBHelper.getReadableDatabase();
        Cursor cursor =
                database.query(TABLE_MESSAGES, null, null,
                        null, null, null, null);

        if (cursor.moveToFirst()) {
            int messageIndex = cursor.getColumnIndex(KEY_MESSAGE);
            int timeIndex = cursor.getColumnIndex(KEY_TIME);
            int userIndex = cursor.getColumnIndex(KEY_ID_USER);
            String login;
            do {
                login = getLogin(cursor.getInt(userIndex));
                messages.add(new ChatMessage(login, cursor.getString(messageIndex),
                        cursor.getString(timeIndex)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }

    public void addMessage(String login, String message, String time) {
        SQLiteDatabase database = dBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor cursor =
                database.query(TABLE_USERS, new String[] {KEY_USER_ID}, KEY_LOGIN + "=?",
                        new String[] {login}, null, null, null);
        Integer userID = null;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_USER_ID);
            userID = cursor.getInt(idIndex);
        }
        cursor.close();

        contentValues.put(KEY_MESSAGE, message);
        contentValues.put(KEY_TIME, time);
        contentValues.put(KEY_ID_USER, userID);
        database.insert(TABLE_MESSAGES, null, contentValues);
    }

    private Boolean convertIntToBoolean(int number) {
        return number != 0;
    }

    private String getLogin(int idUser) {
        SQLiteDatabase database = dBHelper.getReadableDatabase();
        String sql = "SELECT " + KEY_LOGIN + " FROM " + TABLE_USERS
                + " WHERE " + KEY_USER_ID + "=" + idUser ;
        Cursor cursor = database.rawQuery(sql, null);
        String login = null;
        if (cursor.moveToFirst()) {
            int loginIndex = cursor.getColumnIndex(KEY_LOGIN);
            login = cursor.getString(loginIndex);
        }
        cursor.close();
        return login;
    }

    public String md5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] hashPassword = md.digest();
        StringBuilder sb = new StringBuilder(32);
        for (byte b : hashPassword) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

}
