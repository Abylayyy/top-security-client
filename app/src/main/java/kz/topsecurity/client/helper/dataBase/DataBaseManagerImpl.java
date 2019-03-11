package kz.topsecurity.client.helper.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;
import android.util.Log;

import kz.topsecurity.client.domain.ProfileScreen.ProfileActivity;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.model.other.Healthcard;
import kz.topsecurity.client.service.trackingService.model.DeviceData;

public class DataBaseManagerImpl implements DataBaseManager {

    public static final String REASON_VERSION = "REASON_VERSION";
    public static final String REASON_FORCE = "REASON_FORCE";
    DataBaseHelper mDbHelper;
    public DataBaseManagerImpl(Context ctx) {
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
    public DeviceData getDeviceData() throws SQLiteException {
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
        if (!(cursor.getCount() > 0))
            return null;
        String latitude = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_LAT));
        String longitude = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_LNG));
        String altitude = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_ALT));
        String barometricAltitude = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_ALT_BAROMETER));
        Integer battery = cursor.getInt(cursor.getColumnIndex(DeviceData.COLUMN_CHARGE));
        String streetLocation = cursor.getString(cursor.getColumnIndex(DeviceData.COLUMN_STREET_ADDRESS));
        boolean isAlertActive = cursor.getInt(cursor.getColumnIndex(DeviceData.COLUMN_IS_URGENT)) == 1;
        boolean isGpsActive = cursor.getInt(cursor.getColumnIndex(DeviceData.COLUMN_IS_GPS_ACTIVE)) == 1;
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

    public void dropDeviceDataTable() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("delete  from " + DeviceData.TABLE_NAME);
        db.close();
    }

    @Override
    public void saveClientData(Client data) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (data.getHealthcard() != null) {
            int healthCardID = saveHealthCard(data.getHealthcard());
            values.put(Client.COLUMN_HEALTH_CARD_ID, healthCardID);
        } else
            values.put(Client.COLUMN_HEALTH_CARD_ID, -1);
        values.put(Client.COLUMN_USER_ID, data.getId());
        values.put(Client.COLUMN_USERNAME, data.getUsername());
        values.put(Client.COLUMN_PHONE, data.getPhone());
        values.put(Client.COLUMN_EMAIL, data.getEmail());
        values.put(Client.COLUMN_PHOTO, data.getPhoto());
        values.put(Client.COLUMN_FIRST_NAME, data.getFirstname());
        values.put(Client.COLUMN_LAST_NAME, data.getLastname());
        values.put(Client.COLUMN_PATRONYMIC, data.getPatronymic());
        values.put(Client.COLUMN_IIN, data.getIin());
        long newRowId = db.insert(Client.TABLE_NAME, null, values);
    }

    public int saveHealthCard(Healthcard healthcard) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Healthcard.COLUMN_HEALTHCARD_ID, healthcard.getId());
        values.put(Healthcard.COLUMN_CLIENT_ID, healthcard.getClientId());
        values.put(Healthcard.COLUMN_BLOOD_GROUP, healthcard.getBloodGroup());
        values.put(Healthcard.COLUMN_BIRTHDAY, healthcard.getBirthday());
        values.put(Healthcard.COLUMN_WEIGHT, healthcard.getWeight().toString());
        values.put(Healthcard.COLUMN_HEIGHT, healthcard.getHeight().toString());
        values.put(Healthcard.COLUMN_ALLERGIC_REACTIONS, healthcard.getAllergicReactions());
        values.put(Healthcard.COLUMN_DRUGS, healthcard.getDrugs());
        values.put(Healthcard.COLUMN_DISEASE, healthcard.getDisease());
        long newRowId = db.insert(Healthcard.TABLE_NAME, null, values);
        return healthcard.getId();
    }

    @Override
    public Client getClientData() throws SQLiteException {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Client.COLUMN_ID,
                Client.COLUMN_USER_ID,
                Client.COLUMN_USERNAME,
                Client.COLUMN_PHONE,
                Client.COLUMN_EMAIL,
                Client.COLUMN_PHOTO,
                Client.COLUMN_FIRST_NAME,
                Client.COLUMN_LAST_NAME,
                Client.COLUMN_PATRONYMIC,
                Client.COLUMN_IIN,
                Client.COLUMN_HEALTH_CARD_ID
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
        if (!(cursor.getCount() > 0))
            return null;
        Integer user_id = cursor.getInt(cursor.getColumnIndex(Client.COLUMN_USER_ID));
        String username = cursor.getString(cursor.getColumnIndex(Client.COLUMN_USERNAME));
        String phone = cursor.getString(cursor.getColumnIndex(Client.COLUMN_PHONE));
        String email = cursor.getString(cursor.getColumnIndex(Client.COLUMN_EMAIL));
        String photo = cursor.getString(cursor.getColumnIndex(Client.COLUMN_PHOTO));
        String firstName = cursor.getString(cursor.getColumnIndex(Client.COLUMN_FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndex(Client.COLUMN_LAST_NAME));
        String patronymic = cursor.getString(cursor.getColumnIndex(Client.COLUMN_PATRONYMIC));
        String iin = cursor.getString(cursor.getColumnIndex(Client.COLUMN_IIN));
        int healthCardID = cursor.getInt(cursor.getColumnIndex(Client.COLUMN_HEALTH_CARD_ID));

        Client clientData = new Client();
        clientData.setId(user_id);
        clientData.setUsername(username);
        clientData.setPhone(phone);
        clientData.setEmail(email);
        clientData.setPhoto(photo);
        clientData.setFirstname(firstName);
        clientData.setLastname(lastName);
        clientData.setPatronymic(patronymic);
        clientData.setIin(iin);
        clientData.setHealthcard(getHealthCard(healthCardID));

        // close the db connection
        cursor.close();

        return clientData;
    }

    public  Healthcard getHealthCard(int healthCardID) throws SQLiteException {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Healthcard.COLUMN_ID,
                Healthcard.COLUMN_HEALTHCARD_ID,
                Healthcard.COLUMN_CLIENT_ID,
                Healthcard.COLUMN_BLOOD_GROUP,
                Healthcard.COLUMN_BIRTHDAY,
                Healthcard.COLUMN_WEIGHT,
                Healthcard.COLUMN_HEIGHT,
                Healthcard.COLUMN_ALLERGIC_REACTIONS,
                Healthcard.COLUMN_DRUGS,
                Healthcard.COLUMN_DISEASE
        };

        Healthcard healthCard = new Healthcard();
        if (healthCardID == -1) {
            return healthCard;
        }
// How you want the results sorted in the resulting Cursor
        String sortOrder =
                Healthcard.COLUMN_ID + " DESC";
        String[] whereClause = new String[]{"" + healthCardID};
        Cursor cursor = db.query(
                Healthcard.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                Healthcard.COLUMN_HEALTHCARD_ID + "=?",              // The columns for the WHERE clause
                whereClause,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        if (cursor != null)
            cursor.moveToFirst();
        else
            return healthCard;
        if (!(cursor.getCount() > 0))
            return healthCard;

        Integer _id = cursor.getInt(cursor.getColumnIndex(Healthcard.COLUMN_HEALTHCARD_ID));
        Integer client_id = cursor.getInt(cursor.getColumnIndex(Healthcard.COLUMN_CLIENT_ID));
        String bloodGroup = cursor.getString(cursor.getColumnIndex(Healthcard.COLUMN_BLOOD_GROUP));
        String birthGroup = cursor.getString(cursor.getColumnIndex(Healthcard.COLUMN_BIRTHDAY));
        String weight = cursor.getString(cursor.getColumnIndex(Healthcard.COLUMN_WEIGHT));
        String height = cursor.getString(cursor.getColumnIndex(Healthcard.COLUMN_HEIGHT));
        String allericReaction = cursor.getString(cursor.getColumnIndex(Healthcard.COLUMN_ALLERGIC_REACTIONS));
        String drugs = cursor.getString(cursor.getColumnIndex(Healthcard.COLUMN_DRUGS));
        String disease = cursor.getString(cursor.getColumnIndex(Healthcard.COLUMN_DISEASE));



        healthCard.setId(_id);
        healthCard.setClientId(client_id);
        healthCard.setBloodGroup(bloodGroup);
        healthCard.setBirthday(birthGroup);
        healthCard.setWeight(Float.valueOf(weight));
        healthCard.setHeight(Float.valueOf(height));
        healthCard.setAllergicReactions(allericReaction);
        healthCard.setDrugs(drugs);
        healthCard.setDisease(disease);


        // close the db connection
        cursor.close();

        return healthCard;
    }

    @Override
    public void dropClientData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("delete from " + Client.TABLE_NAME);
        db.close();
    }

    @Override
    public void updateHealthCard(Healthcard healthcard, Context context) {

        saveHealthCard(healthcard);
        updateClientHealthCard(healthcard.getId(),context);
    }

    @Override
    public void updateDatabase(String reason) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            if (reason.equals(REASON_VERSION) && db.getVersion() != DataBaseHelper.DATABASE_VERSION) {
                mDbHelper.onUpgrade(mDbHelper.getWritableDatabase(),db.getVersion(),DataBaseHelper.DATABASE_VERSION);
            }
            else{
                mDbHelper.onUpgrade(mDbHelper.getWritableDatabase(),db.getVersion(),DataBaseHelper.DATABASE_VERSION);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void updateClientHealthCard(Integer id,Context context) {
        Client clientData = getClientData();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Client.COLUMN_HEALTH_CARD_ID, id);

        int newRowId = db.update(Client.TABLE_NAME, values, Client.COLUMN_USER_ID + "=?", new String[]{clientData.getId().toString()});


   }
}
