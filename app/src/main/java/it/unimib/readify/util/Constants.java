package it.unimib.readify.util;

import java.util.Calendar;

public class Constants {

    // Constants for SharedPreferences
    public static final String SHARED_PREFERENCES_FILE_NAME = "it.unimib.readify.preferences";

    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "it.unimib.readify.encrypted_preferences";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "password";
    public static final String LOGIN_ID_TOKEN = "id_token";

    // Constants for encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "it.unimib.readify.encrypted_file.txt";

    public static String DARK_MODE = "dark_mode";
    public static String LIGHT_MODE = "light_mode";
    public static String PREFERRED_THEME = "preferred_theme";


    // Constants for API
    public static final String OL_API_BASE_URL = "https://openlibrary.org";
    public static final String OL_SEARCH_ENDPOINT = "/search.json";
    public static final String OL_TRENDING_ENDPOINT = "/trending/weekly.json";
    public static final String OL_WORKS_ENDPOINT = "/works/";
    public static final String OL_ISBN_ENDPOINT = "/isbn/";
    public static final String OL_AUTHORS_ENDPOINT = "/search/authors.json";
    public static final String OL_COVERS_API_URL = "https://covers.openlibrary.org/";
    public static final String OL_COVERS_API_ID_PARAMETER = "w/id/";
    public static final String OL_RECENT_BOOKS_QUERY = "subject:* AND first_publish_year:"+ Calendar.getInstance().get(Calendar.YEAR);

    public static final int CAROUSEL_SIZE = 10;

    public static final String OL_COVERS_API_IMAGE_SIZE_S = "-S.jpg";
    public static final String OL_COVERS_API_IMAGE_SIZE_M = "-M.jpg";
    public static final String OL_COVERS_API_IMAGE_SIZE_L = "-L.jpg";


    // Constants for Search Api
    public static final String OL_SEARCH_Q_PARAMETER = "q";
    public static final String OL_SEARCH_PAGE_PARAMETER = "page";
    public static final String OL_SEARCH_SORT_PARAMETER = "sort";
    public static final String OL_SEARCH_TITLE_PARAMETER = "title";
    public static final String OL_SEARCH_AUTHOR_PARAMETER = "author";
    public static final String OL_SEARCH_LIMIT_PARAMETER = "limit";
    public static final String OL_SEARCH_OFFSET_PARAMETER = "offset";
    public static final String OL_SEARCH_SUBJECT_PARAMETER = "subject";
    public static final String OL_SORT_RANDOM_DAILY = "random.daily";
    public static final String OL_SORT_NEW = "new";



    // Constants for recycler views
    public static final String TRENDING = "trending";
    public static final String RECOMMENDED = "recommended";
    public static final String RECENT = "recent";
    public static final String SEARCH = "search";
    public static final String COLLECTION = "collection";


    // Constants or error messages
    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";


    // Constants for Firebase
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_REALTIME_DATABASE = "https://readify-9b50c-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_WORKS_COLLECTION = "books";
    public static final String FIREBASE_COLLECTIONS_COLLECTION = "collections";
    public static final String FIREBASE_NOTIFICATIONS_COLLECTION = "notifications";

    public static final String FIREBASE_USERS_USERNAME_FIELD = "username";
    public static final String FIREBASE_WORKS_COMMENTS_FIELD = "comments";
    public static final String FIREBASE_COLLECTIONS_BOOKS_FIELD = "books";
    public static final String FIREBASE_COLLECTIONS_NAME_FIELD = "name";
    public static final String FIREBASE_COLLECTIONS_VISIBILITY_FIELD = "visible";


    public static final String FIREBASE_USERS_EMAILS_FIELD = "email";
    public static final String FIREBASE_COLLECTIONS_NUMBEROFBOOKS_FIELD = "numberOfBooks";
    public static final String FIREBASE_USERS_FOLLOWERS_FIELD = "followers";
    public static final String FIREBASE_USERS_FOLLOWING_FIELD = "following";
    //TODO magari rinominare o spostare numberOfFollowers come parametro dell'user stesso andando a eliminare external group
    public static final String FIREBASE_USERS_USERS_LIST_FIELD = "users";
    public static final String FIREBASE_USERS_GENDER_FIELD = "gender";
    public static final String FIREBASE_USERS_VISIBILITY_FIELD = "visibility";
    public static final String FIREBASE_USERS_RECOMMENDED_FIELD = "recommended";
    public static final String FIREBASE_USERS_AVATAR_FIELD = "avatar";
    public static final String FIREBASE_USERS_BIOGRAPHY_FIELD = "biography";
    public static final String FIREBASE_USERS_TOTAL_NUMBER_OF_BOOKS_FIELD = "totalNumberOfBooks";

    public static int COLLECTION_NAME_CHARACTERS_LIMIT = 12;

    public static String[] DESCRIPTION_TRIM_OPTIONS = { "----" , "([source]", "[Source]", "Contains:" };

    public static int DESTINATION_FRAGMENT_FOLLOWER = 0;
    public static int DESTINATION_FRAGMENT_FOLLOWING = 1;

    public static String BUNDLE_ID_TOKEN = "idToken";

    public static String RATING_SORT_SEARCH_MODE = "rating";
    public static String TITLE_SORT_SEARCH_MODE = "title";

    public static final int DATABASE_VERSION = 1;
    public static final String BOOK_DATABASE_NAME = "book_db";

    public static final String LOGGED_USER = "logged_user";
    public static final String OTHER_USER = "other_user";

    public static final String OPERATION_ADD_TO_COLLECTION = "add_to_collection";
    public static final String OPERATION_REMOVE_FROM_COLLECTION = "remove_from_collection";
    public static final String OPERATION_RENAME_COLLECTION = "rename_collection";
    public static final String OPERATION_CHANGE_COLLECTION_VISIBILITY = "change_collection_visibility";

    public static final String USER_VISIBILITY_PUBLIC = "public";
    public static final String USER_VISIBILITY_PRIVATE = "private";

    public static final String PROFILE_IMAGE_TAG = "avatar";

}