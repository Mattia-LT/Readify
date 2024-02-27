package it.unimib.readify.util;

public class Constants {

    //TODO Eventuali costanti da usare nella progettazione andranno qui. esempio:

    // Constants for SharedPreferences
    public static final String SHARED_PREFERENCES_FILE_NAME = "it.unimib.worldnews.preferences";
    public static final String SHARED_PREFERENCES_COUNTRY_OF_INTEREST = "country_of_interest";
    public static final String SHARED_PREFERENCES_TOPICS_OF_INTEREST = "topics_of_interest";
    public static final String SHARED_PREFERENCES_FIRST_LOADING = "first_loading";

    // Constants for EncryptedSharedPreferences

    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "it.unimib.worldnews.encrypted_preferences";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "password";
    public static final String ID_TOKEN = "google_token";


    // Constants for encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "it.unimib.worldnews.encrypted_file.txt";





    // Constants for API

    public static final String OL_API_BASE_URL = "https://openlibrary.org";
    public static final String OL_SEARCH_ENDPOINT = "/search.json";
    public static final String OL_WORKS_ENDPOINT = "/works/";
    public static final String OL_ISBN_ENDPOINT = "/isbn/";
    public static final String OL_AUTHORS_ENDPOINT = "/search/authors.json";
    public static final String OL_COVERS_API_URL = "https://covers.openlibrary.org/";
    public static final String OL_COVERS_API_ID_PARAMETER = "w/id/";

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





    // Constants for recycler views
    public static final String TRENDING = "trending";
    public static final String SUGGESTED = "suggested";
    public static final String RECENT = "recent";
    public static final String SEARCH = "search";
    public static final String COLLECTION = "collection";





    //error messages
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

    public static final String TO_READ = "to_read";
    public static final String ALREADY_READ = "already_read";


    // Constants for Bundles
    public static final String BUNDLE_BOOK = "book";
    public static final String BUNDLE_USER = "user";



}
