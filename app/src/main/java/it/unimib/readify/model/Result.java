package it.unimib.readify.model;

public abstract class Result {

    private Result() {}

    public boolean isSuccess(){
        return this instanceof WorkSuccess ||
                this instanceof UserSuccess ||
                this instanceof CommentSuccess ||
                this instanceof CollectionSuccess ||
                this instanceof FollowUserSuccess;
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

    public static final class CommentSuccess extends Result{
        private final Comment comment;
        public CommentSuccess(Comment comment) {
            this.comment = comment;
        }
        public Comment getData() {
            return comment;
        }
    }
    public static final class CollectionSuccess extends Result{
        private final Collection collection;
        public CollectionSuccess(Collection collection) {
            this.collection = collection;
        }
        public Collection getData() {
            return collection;
        }
    }

    public static final class FollowUserSuccess extends Result{
        private final FollowUser followUser;
        public FollowUserSuccess(FollowUser followUser) {
            this.followUser = followUser;
        }
        public FollowUser getData() {
            return followUser;
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
