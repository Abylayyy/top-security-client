package kz.topsecurity.client.helper;

import kz.topsecurity.client.R;

public class TextHelper {
    public static int getErrorMessage(int code){
        switch (code){
            case Constants.ERROR_STATES.WRONG_PASSWORD_CODE: return R.string.wrong_password_code;

            case Constants.ERROR_STATES.NOT_AUTHENTICATED_CODE: return R.string.not_authenticated_code;

            case Constants.ERROR_STATES.USER_NOT_FOUND_CODE: return R.string.user_not_found_code;

            case Constants.ERROR_STATES.ALREADY_AUTHENTICATE_CODE: return R.string.already_authenticate_code;

            case Constants.ERROR_STATES.SUCCESSFUL_AUTHENTICATE_CODE: return R.string.successful_authenticate_code;

            case Constants.ERROR_STATES.SUCCESSFUL_REGISTERED_CODE: return R.string.successful_registered_code;

            case Constants.ERROR_STATES.PASSWORD_MISSMATCH_CODE: return R.string.password_missmatch_code;

            case Constants.ERROR_STATES.PASSWORD_SUCCESSFUL_CHANGED_CODE: return R.string.password_successful_changed_code;

            case Constants.ERROR_STATES.PROFILE_SUCCESSFUL_SAVED_CODE: return R.string.profile_successful_saved_code;

            case Constants.ERROR_STATES.PHOTO_SUCCESSFUL_SAVED_CODE: return R.string.photo_successful_saved_code;

            case Constants.ERROR_STATES.SECRET_SUCCESS_SAVED_CODE: return R.string.secret_success_saved_code;

            case Constants.ERROR_STATES.DEVICE_ALREADY_LINKED_CODE: return R.string.device_already_linked_code;

            case Constants.ERROR_STATES.CONCURRENCY_ERROR_CODE: return R.string.concurrency_error_code;

            case Constants.ERROR_STATES.SUCCESSFUL_CREATED_CODE: return R.string.successful_created_code;

            case Constants.ERROR_STATES.SUCCESSFUL_UPDATED_CODE: return R.string.successful_updated_code;

            case Constants.ERROR_STATES.SUCCESSFUL_ACCEPTED_CODE: return R.string.successful_accepted_code;

            case Constants.ERROR_STATES.INTERNAL_ERROR_CODE: return R.string.internal_error_code;

            case Constants.ERROR_STATES.VALIDATION_ERROR_CODE: return R.string.validation_error_code;

            case Constants.ERROR_STATES.ACTION_NOT_FOUND_CODE: return R.string.action_not_found_code;

            case Constants.ERROR_STATES.ACTION_IS_EXPIRED_CODE: return R.string.action_is_expired_code;

            case Constants.ERROR_STATES.ALERT_NOT_EXISTS_CODE: return R.string.alert_not_exists_code;

            case Constants.ERROR_STATES.ALERT_IS_EXISTS_CODE: return R.string.alert_is_exists_code;

            case Constants.ERROR_STATES.ALERT_SUCCESSFUL_CREATED_CODE: return R.string.alert_successful_created_code;

            case Constants.ERROR_STATES.ALERT_SUCCESSFUL_CHECKED_CODE: return R.string.alert_successful_checked_code;

            case Constants.ERROR_STATES.ALERT_SUCCESSFUL_CANCELLED_CODE: return R.string.alert_successful_cancelled_code;
            default: return R.string.error_from_server;
        }
    }
}
