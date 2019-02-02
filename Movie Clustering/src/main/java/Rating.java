/**
 * class that represent single rating
 * */
public class Rating {
    private User user;
    private Movie movie;
    private int rating;

    /**
     * default constructor
     */
    public Rating() {
        this.user = null;
        this.movie = null;
        this.rating = -1;
    }

    /**
     *
     * @param user - the user that rated
     * @param movie - the movie that was rated
     * @param rating - the rating value
     */
    public Rating(User user, Movie movie, int rating) {
        this.user = user;
        this.movie = movie;
        this.rating = rating;
    }

    /**
     *
     * @return the user that rated
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     *
     * @return the movie that was rated
     */
    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    /**
     *
     * @return the rating value - unused
     */
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "User{" +
                "user=" + user +
                ", movie=" + movie +
                ", rating=" + rating +
                '}';
    }

}
