package kz.topsecurity.client.model.photo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.client.model.other.BasicResponseTemplate;

public class PhotoResponse  extends BasicResponseTemplate {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("src")
    @Expose
    private String src;
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }
}
