package williamgbranco.comune.util;

/**
 * Created by William Gomes de Branco on 28/09/2015.
 */
public final class Constants
{
    public static final String PERM_PRIVATE = "williamgbranco.comune.PRIVATE";

    public static final boolean ALARM_ON = true;
    public static final boolean ALARM_OFF = false;


    /*
    *  MAIN ACTIVITY related constants
    * ***********************************************************************/
    public static final String ACTION_STORED_SURVEYS_ACTVITY = "williamgbranco.comune.activity.MainFragment.action.stored_surveys_activity";


    /*
    *  DATABASE related constants
    * ***********************************************************************/
    public static final int ERROR_ON_INSERT = -1;


    public static final String ACTION_REPORT_PICTURE_DOWNLOADED = "williamgbranco.comune.ACTION_REPORT_PICTURE_DOWNLOADED";
    public static final String ACTION_REPORT_FOOTAGE_DOWNLOADED = "williamgbranco.comune.ACTION_REPORT_FOOTAGE_DOWNLOADED";
    public static final String ACTION_EXTRA_FILE_REPORT_ID = "comune.downloaded_file.report_id";
    public static final String ACTION_EXTRA_FILE_URI = "comune.downloaded_file.uri";
    public static final String ACTION_EXTRA_FILE_CONTENT_LENGTH = "comune.downloaded_file.content_length";


    /*
    *  SERVER CONNECTION related constants
    * ***********************************************************************/
    public static final String WEB_SERVICE_URL = "http://ws.consultaopiniao.com:8080/comuneWS"; //endpoint

    // SURVEY related methods
    public static final String WS_METHOD_GET_SURVEY_BY_ID = "/surveys/getSurveyById";
    public static final String WS_METHOD_SAVE_SURVEY_ANSWERS = "/surveys/saveSurveyAnswers";
    public static final String WS_METHOD_GET_AVAILABLE_SURVEYS = "/surveys/getAvailableSurveys";

    // REPORT related methods
    public static final String WS_METHOD_GET_USER_REPORT_BY_ID = "/reports/getReportById";
    public static final String WS_METHOD_GET_REPORT_RESPONSE_BY_ID = "/reports/getReportResponseById";
    public static final String WS_METHOD_GET_PLACE_REPORTS_SUBMITTED_BY_USER = "/reports/getPlaceReportsSubmittedByUser";
    public static final String WS_METHOD_GET_NEW_RESPONSES_FOR_USER_REPORTS = "/reports/getNewResponsesForUserReports";
    public static final String WS_METHOD_MARK_RESPONSE_AS_VISUALIZED = "/reports/markReportResponseAsVisualized";
    public static final String WS_METHOD_SAVE_NEW_REPORT = "/reports/saveNewReport";
    public static final String WS_METHOD_UPLOAD_REPORT_PICTURE = "/reports/uploadReportPicture";
    public static final String WS_METHOD_DOWNLOAD_REPORT_PICTURE = "/users/downloadFile";
    public static final String WS_METHOD_UPLOAD_REPORT_FOOTAGE = "/reports/uploadReportFootage";
    public static final String WS_METHOD_DOWNLOAD_REPORT_FOOTAGE = "/users/downloadFile";

    // PLACE related methods
    public static final String WS_METHOD_GET_PLACE_BY_ID = "/places/getPlaceById";
    public static final String WS_METHOD_GET_NEARBY_PLACES = "/places/getNearbyPlaces";
    public static final String WS_METHOD_FIND_NEARBY_PLACES = "/places/findNearbyPlaces";
    public static final String WS_METHOD_GET_PLACES_REPORTED_BY_USER = "/places/getPlacesReportedByUser";

    // USER related methods
    public static final String WS_METHOD_AUTHENTICATE_USER = "/users/authenticateUser";
    public static final String WS_METHOD_REGISTER_USER = "/users/registerUser";
    public static final String WS_METHOD_UPLOAD_USER_PHOTO = "/users/uploadUserPhoto";
    public static final String WS_METHOD_DOWNLOAD_USER_PHOTO = "/users/downloadFile";

    public static final int CODE_DOWNLOAD_PICTURE = 1;
    public static final int CODE_DOWNLOAD_FOOTAGE = 2;

    /*
    *  PUBLIC INSTITUTION related constants
    * ***********************************************************************/
    public static final float MAXIMUM_DISTANCE_FROM_BUILDING = (float) 500.00; //distância em metros do usuário em relação ao prédio da instituição
    public static final int SEARCHED_AREA_RADIUS_IN_KM = 5;

    /*
    *  SURVEY related constants
    * ***********************************************************************/
    public static final int SURVEY_MAX_DAYS_AVAILABLE = 2;
    public static final int SURVEY_MAX_DAYS_AVAILABLE_IN_MILLIS = SURVEY_MAX_DAYS_AVAILABLE * 24 * 60 * 60 * 1000; //2d em milisegundos
    public static final int SURVEY_ALERT_FEW_HOURS_REMAINING = 12; //em horas
    public static final int SURVEY_ALERT_FEW_HOURS_REMAINING_IN_MILLIS = SURVEY_ALERT_FEW_HOURS_REMAINING * 60 * 60 * 1000; //10h em milisegundos


}
