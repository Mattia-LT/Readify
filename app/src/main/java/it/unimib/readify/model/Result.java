package it.unimib.readify.model;

public abstract class Result {

    private Result() {}

    public boolean isSuccess(){
        return false;
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */

    public static final class SearchSuccess extends Result {
        private final OLSearchApiResponse olSearchApiResponse;
        public boolean isSuccess(){
            return true;
        }

        public SearchSuccess(OLSearchApiResponse olSearchApiResponse) {
            this.olSearchApiResponse = olSearchApiResponse;
        }

        public OLSearchApiResponse getData() {
            return olSearchApiResponse;
        }
    }


    public static final class WorkSuccess extends Result {
        private final OLWorkApiResponse olWorkApiResponse;

        public boolean isSuccess(){
            return true;
        }

        public WorkSuccess(OLWorkApiResponse olWorkApiResponse) {
            this.olWorkApiResponse = olWorkApiResponse;
        }
        public OLWorkApiResponse getData() {
            return olWorkApiResponse;
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
