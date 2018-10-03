package kz.topsecurity.client.helper.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.service.trackingService.model.DeviceData;

public class DataBaseManagerImpl implements DataBaseManager {

    DataBaseHelper mDbHelper;

    public DataBaseManagerImpl(Context ctx){
        mDbHelper = new DataBaseHelper(ctx);
    }

    @Override
    public void saveDeviceData(DeviceData deviceData) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DeviceData.COLUMN_LAT, deviceData.getLat().toString());
            values.put(DeviceData.COLUMN_LNG, deviceData.getLng().toString());
            values.put(DeviceData.COLUMN_ALT, deviceData.getAlt().toString());
            values.put(DeviceData.COLUMN_ALT_BAROMETER, deviceData.getAltBarometer().toString());
            values.put(DeviceData.COLUMN_STREET_ADDRESS, deviceData.getStreetAddress());
//            values.put(DeviceData.COLUMN_SIGNAL_STRENGTH, );
            values.put(DeviceData.COLUMN_CHARGE, deviceData.getCharge());
            values.put(DeviceData.COLUMN_IS_URGENT, deviceData.getIs_urgent());
            values.put(DeviceData.COLUMN_IS_GPS_ACTIVE, deviceData.getIs_gps_active());
            values.put(DeviceData.COLUMN_TIMESTAMP, deviceData.getTimestamp());
//            values.put(DeviceData.COLUMN_CREATED_AT, deviceData.getCreatedAt().getDate());

// Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(DeviceData.TABLE_NAME, null, values);
    }

    @Override
    public DeviceData getDeviceData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DeviceData.COLUMN_ID,
                DeviceData.COLUMN_LAT,
                DeviceData.COLUMN_LNG,
                DeviceData.COLUMN_ALT,
                DeviceData.COLUMN_ALT_BAROMETER,
                DeviceData.COLUMN_STREET_ADDRESS,
                DeviceData.COLUMN_CHARGE,
                DeviceData.COLUMN_IS_URGENT,
                DeviceData.COLUMN_IS_GPS_ACTIVE,
                DeviceData.COLUMN_TIMESTAMP,
        };


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                DeviceData.COLUMN_ID + " DESC";

        Cursor cursor = db.query(
                DeviceData.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;
        if(!(cursor.getCount()>0))
            return null;
        String latitude = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_LAT));
        String longitude = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_LNG));
        String altitude = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_ALT));
        String barometricAltitude = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_ALT_BAROMETER));
        Integer battery = cursor.getInt(cursor.getColumnIndex(DeviceData.COLUMN_CHARGE));
        String streetLocation = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_STREET_ADDRESS));
        boolean isAlertActive = cursor.getInt(cursor.getColumnIndex(DeviceData.COLUMN_IS_URGENT))==1;
        boolean isGpsActive = cursor.getInt(cursor.getColumnIndex(DeviceData.COLUMN_IS_GPS_ACTIVE))==1;
        String unixTime = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_TIMESTAMP));

        DeviceData deviceData = new DeviceData();
        deviceData.setLat(Double.parseDouble(latitude));
        deviceData.setLng(Double.parseDouble(longitude));
        deviceData.setAlt(Double.parseDouble(altitude));
        deviceData.setAltBarometer(Double.parseDouble(barometricAltitude));
        deviceData.setCharge(battery);
        deviceData.setSignalStrength(1); //signalLevel
        deviceData.setStreetAddress(streetLocation);
        deviceData.setIs_urgent(isAlertActive);
        deviceData.setIs_gps_active(isGpsActive);
//        deviceData.setTimestamp((int) unixTime);

        // close the db connection
        cursor.close();

        return deviceData;
    }

    public void dropDeviceDataTable(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("delete  from " + DeviceData.TABLE_NAME);
        db.close();
    }

    @Override
    public void saveClientData(Client data) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Client.COLUMN_USER_ID, data.getId());
        values.put(Client.COLUMN_USERNAME, data.getUsername());
        values.put(Client.COLUMN_PHONE, data.getPhone());
        values.put(Client.COLUMN_EMAIL, data.getEmail());
        values.put(Client.COLUMN_PHOTO, data.getPhoto());
        long newRowId = db.insert(Client.TABLE_NAME, null, values);
    }

    @Override
    public Client getClientData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Client.COLUMN_ID ,
                Client.COLUMN_USER_ID ,
                Client.COLUMN_USERNAME ,
                Client.COLUMN_PHONE ,
                Client.COLUMN_EMAIL ,
                Client.COLUMN_PHOTO ,
        };


// How you want the results sorted in the resulting Cursor
        String sortOrder =
                Client.COLUMN_ID + " DESC";

        Cursor cursor = db.query(
                Client.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;
        if(!(cursor.getCount()>0))
            return null;
        Integer user_id = cursor.getInt(cursor.getColumnIndex(Client.COLUMN_USER_ID));
        String username = cursor.getString(cursor.getColumnIndex(Client.COLUMN_USERNAME));
        String phone = cursor.getString(cursor.getColumnIndex(Client.COLUMN_PHONE));
        String email = cursor.getString(cursor.getColumnIndex(Client.COLUMN_EMAIL));
        String photo = cursor.getString(cursor.getColumnIndex(Client.COLUMN_PHOTO));


        Client clientData = new Client();
        clientData.setId(user_id);
        clientData.setUsername(username);
        clientData.setPhone(phone);
        clientData.setEmail(email);
        clientData.setPhoto(photo);

        // close the db connection
        cursor.close();

        return clientData;
    }

    @Override
    public void dropClientData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("delete from " + Client.TABLE_NAME);
        db.close();
    }
}
