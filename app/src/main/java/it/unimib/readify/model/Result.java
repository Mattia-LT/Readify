package it.unimib.readify.model;

public abstract class Result {

    private Result() {}

    public boolean isSuccess(){
        if (this instanceof WorkSuccess || this instanceof UserSuccess) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */

    public static final class WorkSuccess extends Result {
        private final OLWorkApiResponse olWorkApiResponse;

        public WorkSuccess(OLWorkApiResponse olWorkApiResponse) {
            this.olWorkApiResponse = olWorkApiResponse;
        }
        public OLWorkApiResponse getData() {
            return olWorkApiResponse;
        }
    }

    public static final class UserSuccess extends Result{
        private final User user;
        public UserSuccess(User user) {
            this.user = user;
        }
        public User getData() {
            return user;
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
