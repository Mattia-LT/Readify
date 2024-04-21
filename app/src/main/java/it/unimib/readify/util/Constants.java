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

    public static final String FIREBASE_USERS_USERNAME_FIELD = "username";
    public static final String FIREBASE_WORKS_COMMENTS_FIELD = "comments";
    public static final String FIREBASE_COLLECTIONS_BOOKS_FIELD = "books";
    public static final String FIREBASE_COLLECTIONS_EMAILS_FIELD = "email";
    public static final String FIREBASE_COLLECTIONS_NUMBEROFBOOKS_FIELD = "numberOfBooks";
    public static final String FIREBASE_USERS_FOLLOWERS_FIELD = "followers";
    public static final String FIREBASE_USERS_FOLLOWING_FIELD = "following";
    //TODO magari rinominare o spostare numberOfFollowers come parametro dell'user stesso andando a eliminare external group
    public static final String FIREBASE_USERS_USERS_LIST_FIELD = "users";


    public static final String TO_READ = "to_read";
    public static final String ALREADY_READ = "already_read";

    public static int COLLECTION_NAME_CHARACTERS_LIMIT = 15;

    public static String[] DESCRIPTION_TRIM_OPTIONS = { "----" , "([source]", "Contains:" };

    public static int DESTINATION_FRAGMENT_FOLLOWER = 0;
    public static int DESTINATION_FRAGMENT_FOLLOWING = 1;

    public static String BUNDLE_ID_TOKEN = "idToken";

}