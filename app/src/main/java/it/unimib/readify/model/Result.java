package it.unimib.readify.model;

public abstract class Result {

    private Result() {}

    public boolean isSuccess(){
        return this instanceof Success;
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class Success extends Result {
        private final OLSearchApiResponse olSearchApiResponse;
        public Success(Object object) {
            this.olSearchApiResponse = (OLSearchApiResponse) object;
        }
        public OLSearchApiResponse getData() {
            return olSearchApiResponse;
        }
    }

    /**
     * Class that represents an error occurred during the interaction
     * with a Web Service or a local database.
     */
    public static final class Error extends Result {
        private final String message;
        public Error(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }




}
