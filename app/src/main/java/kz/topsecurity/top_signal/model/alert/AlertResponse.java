package kz.topsecurity.top_signal.model.alert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kz.topsecurity.top_signal.model.other.BasicResponseTemplate;

public class AlertResponse extends BasicResponseTemplate {

//    @SerializedName("errors")
//    @Expose
//    private Errors errors;
//
//    public Errors getErrors() {
//        return errors;
//    }
//
//    public void setErrors(Errors errors) {
//        this.errors = errors;
//    }

    @SerializedName("errors")
    @Expose
    private List<String> errors = null;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
