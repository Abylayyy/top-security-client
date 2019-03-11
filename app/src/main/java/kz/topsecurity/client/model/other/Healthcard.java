package kz.topsecurity.client.model.other;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.client.R;
import kz.topsecurity.client.application.TopSecurityClientApplication;

public class Healthcard {

    public static final String TABLE_NAME = "health_card_data";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_HEALTHCARD_ID = "healthcard_id";
    public static final String COLUMN_CLIENT_ID = "client_id";
    public static final String COLUMN_BLOOD_GROUP = "blood_group";
    public static final String COLUMN_BIRTHDAY = "birthday";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_ALLERGIC_REACTIONS = "allergic_reactions";
    public static final String COLUMN_DRUGS = "drugs";
    public static final String COLUMN_DISEASE = "disease";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_HEALTHCARD_ID + " INTEGER, "
                    + COLUMN_CLIENT_ID + " INTEGER,"
                    + COLUMN_BLOOD_GROUP+ " TEXT,"
                    + COLUMN_BIRTHDAY + " TEXT, "
                    + COLUMN_WEIGHT + " TEXT, "
                    + COLUMN_HEIGHT + " TEXT, "
                    + COLUMN_ALLERGIC_REACTIONS + " TEXT, "
                    + COLUMN_DRUGS + " TEXT, "
                    + COLUMN_DISEASE + " TEXT "
                    + ")";

    public static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Healthcard(){
        this.id = -1;
        this.clientId = -1;
        this.bloodGroup = "";
        this.birthday="";
        this.height = -1.0f;
        this.weight = -1.0f;
        this.allergicReactions ="";
        this.drugs = "";
        this.disease = "";
    }

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("client_id")
    @Expose
    private Integer clientId;
    @SerializedName("blood_group")
    @Expose
    private String bloodGroup;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("weight")
    @Expose
    private Float weight;
    @SerializedName("height")
    @Expose
    private Float height;
    @SerializedName("allergic_reactions")
    @Expose
    private String allergicReactions;
    @SerializedName("drugs")
    @Expose
    private String drugs;
    @SerializedName("disease")
    @Expose
    private String disease;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public String getAllergicReactions() {
        return allergicReactions;
    }

    public void setAllergicReactions(String allergicReactions) {
        this.allergicReactions = allergicReactions;
    }

    public String getDrugs() {
        return drugs;
    }

    public void setDrugs(String drugs) {
        this.drugs = drugs;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDataInStringFormat(Context context){
        if(getBirthday().isEmpty() || !(getWeight()>2.0f) ||
                !(getHeight() >2.0f) || getBloodGroup().isEmpty() ||
                getAllergicReactions().isEmpty() || getDrugs().isEmpty() ||
                getDisease().isEmpty()){
            return context.getString(R.string.empty_field);
        }
        else{
            return String.format("Рост: %s, Вес: %s, Алергические реакции: %s, Медикаменты: %s, Заболевания: %s", getHeight(), getWeight(), getAllergicReactions(), getDrugs(), getDisease());
        }
    }
}
