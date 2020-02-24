package kz.topsecurity.client.helper.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.model.other.Healthcard;
import kz.topsecurity.client.service.trackingService.model.DeviceData;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "topsecurity_client.db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DeviceData.CREATE_TABLE);
        sqLiteDatabase.execSQL(Client.CREATE_TABLE);
        sqLiteDatabase.execSQL(Healthcard.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DeviceData.DELETE_TABLE);
        sqLiteDatabase.execSQL(Client.DELETE_TABLE);
        sqLiteDatabase.execSQL(Healthcard.DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }


    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
