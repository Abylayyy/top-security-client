package kz.topsecurity.client.service.api;


import io.reactivex.Observable;
import kz.topsecurity.client.model.alert.AlertResponse;
import kz.topsecurity.client.model.alert.AlertStatusResponse;
import kz.topsecurity.client.model.alert.CancelAlertResponse;
import kz.topsecurity.client.model.alert.CheckAlertResponse;
import kz.topsecurity.client.model.alertList.AlertsListResponse;
import kz.topsecurity.client.model.auth.AuthResponse;
import kz.topsecurity.client.model.auth.GetClientResponse;
import kz.topsecurity.client.model.contact.GetContactsResponse;
import kz.topsecurity.client.model.contact.SaveContactsResponse;
import kz.topsecurity.client.model.device.SaveDeviceDataResponse;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.model.other.HealthCardPostResponse;
import kz.topsecurity.client.model.other.PlansResponse;
import kz.topsecurity.client.model.other.SampleRequest;
import kz.topsecurity.client.model.photo.PhotoResponse;
import kz.topsecurity.client.model.place.GetPlaceResponse;
import kz.topsecurity.client.model.place.SavePlaceResponse;
import kz.topsecurity.client.model.register.RegisterResponse;
import okhttp3.MultipartBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST("client/auth")
    Observable<AuthResponse> authorize(@Field("phone") String phone,
                                 @Field("password") String password,
                                 @Field("type") String type,
                                 @Field("platform") String platform,
                                 @Field("udid") String udid );

    @FormUrlEncoded
    @POST("client/register")
    Observable<RegisterResponse> register(@Field("phone") String phone,
                                          @Field("password") String password,
                                          @Field("email") String email,
                                          @Field("username") String username );

    @FormUrlEncoded
    @POST("client/register")
    Observable<RegisterResponse> register(@Field("phone") String phone,
                                          @Field("password") String password);

    @FormUrlEncoded
    @POST("client/device/data")
    Observable<SaveDeviceDataResponse> saveData(@Header("Authorization") String token,
                                                @Field("lat") Double lat,
                                                @Field("lng") Double lng,
                                                @Field("timestamp") Integer timestamp);

    @FormUrlEncoded
    @POST("client/device/data")
    Observable<SaveDeviceDataResponse> saveData(@Header("Authorization") String token,
                                                @Field("lat") Double lat,
                                                @Field("lng") Double lng,
                                                @Field("alt") Double alt,
                                                @Field("alt_barometer") Double alt_barometer,
                                                @Field("charge") Integer charge,
                                                @Field("address") String address,
                                                @Field("timestamp") Integer timestamp);


    @FormUrlEncoded
    @POST("client/alert")
    Observable<AlertResponse> sendAlert(@Header("Authorization") String token,
                                                @Field("lat") Double lat,
                                                @Field("lng") Double lng,
                                                @Field("alt") Double alt,
                                                @Field("alt_barometer") Double alt_barometer,
                                                @Field("charge") Integer charge,
                                                @Field("address") String address,
                                                @Field("timestamp") Integer timestamp,
                                                @Field("type") String type);

    @POST("client/alert/cancel")
    Observable<CancelAlertResponse> cancelAlert(@Header("Authorization") String token);

    @GET("client/alert/check")
    Observable<CheckAlertResponse> check(@Header("Authorization") String token);

    @Multipart
    @POST("client/photo")
    Observable<PhotoResponse> uploadPhoto(@Header("Authorization") String token, @Part MultipartBody.Part filePart);

    @GET("client/places")
    Observable<GetPlaceResponse> getPlaces(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("client/places")
    Observable<SavePlaceResponse> savePlace(@Header("Authorization") String token,
                                            @Field("name") String name,
                                            @Field("lat") String lat,
                                            @Field("lng") String lng,
                                            @Field("description") String description,
                                            @Field("radius") int radius);


    @DELETE("client/places/{place_id}")
    Observable<BasicResponse> deletePlace(@Header("Authorization") String token,
                                          @Path("place_id") int place_id);

    @GET("client/contacts")
    Observable<GetContactsResponse> getContacts(@Header("Authorization") String token);

    //TODO: LOGiC add description
    @FormUrlEncoded
    @POST("client/contacts")
    Observable<SaveContactsResponse> saveContact(@Header("Authorization") String token,
                                                 @Field("name") String name,
                                                 @Field("phone") String phone,
                                                 @Field("description")  String description);

    @FormUrlEncoded
    @PATCH("client/contacts/{contact_id}")
    Observable<SaveContactsResponse> editContact(@Header("Authorization") String requestToken,
                                                 @Path("contact_id") int id,
                                                 @Field("name") String name,
                                                 @Field("phone") String phone,
                                                 @Field("description")  String description);


    @DELETE("client/contacts/{contact_id}")
    Observable<BasicResponse> deleteContact(@Header("Authorization") String token,
                                            @Path("contact_id") int contact_id);

    @GET("client")
    Observable<GetClientResponse> getClientData(@Header("Authorization") String token);

    @FormUrlEncoded
    @PATCH("client")
    Observable<GetClientResponse> editUserData(@Header("Authorization") String token,
                                               @Field("username") String username,
                                               @Field("email") String email,
                                               @Field("firstname") String firstname,
                                               @Field("lastname") String lastname,
                                               @Field("iin") String iin);

    @FormUrlEncoded
    @POST("client/password")
    Observable<GetClientResponse> changeUserPassword(@Header("Authorization") String token,
                                                     @Field("old_password") String current_password,
                                                     @Field("new_password") String new_password,
                                                     @Field("new_password_confirmation") String password_confirmation);

    @FormUrlEncoded
    @POST("client/password/request")
    Observable<BasicResponse> forgetPassword(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("client/verificate/create")
    Observable<BasicResponse> requesteCode(@Field("action_type") String action_type ,
                                             @Field("phone") String phone) ;

    @FormUrlEncoded
    @POST("client/verificate")
    Observable<BasicResponse> verificateCode(@Field("action_type") String action_type ,
                                             @Field("phone") String phone,
                                             @Field("action_code") String action_code) ;

    @FormUrlEncoded
    @POST("client/password/change")
    Observable<BasicResponse> changePassword(@Field("new_password") String new_password,
            @Field("new_password_confirmation") String new_password_confirmation,
            @Field("verification_code") String verification_code);

    @FormUrlEncoded
    @POST("client/password")
    Observable<BasicResponse> changePassword(@Header("Authorization") String token, @Field("old_password") String oldPass,
                                             @Field("new_password") String newPass,
                                             @Field("new_password_confirmation") String confirmNewPass);


    @GET("client/alerts")
    Observable<AlertsListResponse> getAlertList(@Header("Authorization") String token,
                                                @Query("limit") int limit,
                                                @Query("offset") int offset);

    @FormUrlEncoded
    @POST("fcm/create")
    Observable<SampleRequest> setFcmToken(@Header("Authorization") String access_token, @Field("token") String token);

    @GET("client/alert/status")
    Observable<AlertStatusResponse> getAlertStatus(@Header("Authorization") String access_token);

    @GET("client/plans")
    Observable<PlansResponse> getPlan(@Header("Authorization") String access_token);


    @FormUrlEncoded
    @POST("client/register/confirm")
    Observable<BasicResponse> confirmCode( @Field("verification_code") String verification_code);

    @FormUrlEncoded
    @POST("client/register/phone")
    Observable<BasicResponse> checkPhone(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("client/register/email")
    Observable<BasicResponse> checkEmail(@Field("email") String phone);

    @Multipart
    @POST("client/register/photo")
    Observable<PhotoResponse> uploadPhotoForRegistration(@Header("Authorization") String token, @Part MultipartBody.Part filePart);


    @FormUrlEncoded
    @POST("client/healthcard")
    Observable<HealthCardPostResponse> addUserHealthCard(@Header("Authorization") String access_token,
                                                         @Field("blood_group") String blood_group,
                                                         @Field("birthday") String birthday,
                                                         @Field("weight") String weight,
                                                         @Field("height") String height,
                                                         @Field("allergic_reactions") String allergic_reactions,
                                                         @Field("drugs") String drugs,
                                                         @Field("disease") String disease);

}
