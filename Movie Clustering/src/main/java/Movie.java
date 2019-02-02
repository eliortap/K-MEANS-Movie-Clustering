import java.util.*;

/**
 * class that represent single movie
 * */
public class Movie extends Object implements Comparable, Vectorizable {

    private int id;
    private static int VECTORSIZE = MovieClustering.VECTORSIZE;
    private int publicationYear;
    private String name;
    private List<String> genres;
    private List<User> userWatchList;
    private double singleProbability;
    private Vector<Double> vector;
    /**
     * default constructor
     */
    public Movie() {
        this.id = -1;
        this.publicationYear = -1;
        this.name = "Null";
        this.genres = new ArrayList<String>();
        this.userWatchList = new ArrayList<User>();
        this.singleProbability = -1;
        this.vector = null;
    }

    /**
     * main constructor
     * @param id - id of movie, uses as the key of the main HashMap
     * @param name the name of the movie
     * @param genres list of genres of the movie
     * @param year release year of the movie
     */
    public Movie(int id, String name, String[] genres, int year) {
        this.id = id;
        this.publicationYear = year;
        this.name = name;
        this.genres = new ArrayList<String>(Arrays.asList(genres));
        this.userWatchList = new ArrayList<User>();
        this.vector = null;
    }

    /**
     * @return movie probability
     */
    public double getProbability() {
        return singleProbability;
    }

    public void setProbability(double probability) {
        this.singleProbability = probability;
    }

    /**
     * @return movie name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return list of genres of the movie
     */
    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    /**
     * @return list of users that watched the movie
     */
    public List<User> getUsersWatchList() {
        return userWatchList;
    }

    /**
     * add user to the users watch list
     * @param user
     */
    public void addUser(User user) {
        this.userWatchList.add(user);
    }


    /**
     * @return movie id
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return number of users that watched the movie
     */
    public int getCountOfWatches(){
        return userWatchList.size();
    }


    /**
     * @return publication year of the movie
     */
    public int getYear() {
        return publicationYear;
    }

    public void setYear(int year) {
        this.publicationYear = year;
    }


    /**
     *
     * @return vector that represented the movie
     */
    public Vector<Double> getVector() {
        if(this.vector != null)
            return new Vector<>(this.vector);

        //Final variables for comfort when tweaking values.
        final double GENRE_VALUE = (double)1/(double)genres.size();
        final double GENRE_MULTIPLIER = 1000; //1000
        final double YEAR_VALUE = (double)1;
        final double YEAR_MULTIPLIER = 10; //10
        final double INCREMENT_VALUE = (double)1/(double)userWatchList.size();
        final double AGE_MULTIPLIER = 1000; //500
        final double PROFESSION_MULTIPLIER = 1000; //1000
        final double GENDER_MULTIPLIER = 100; // 100
        final double PROBABILITY_MULTIPLIER = 1000000000;
        final int VECTORSIZE = MovieClustering.VECTORSIZE;
        Vector<Double> vector = new Vector<Double>(VECTORSIZE);
        //initialize the whole vector
        for(int i = 0; i< VECTORSIZE ; i++){
            vector.add(i,new Double(0));
        }
        int movieWatchCount = userWatchList.size();

        //Initialize genre values in vector.
        double normalizedGenreValue = GENRE_VALUE*GENRE_MULTIPLIER;
        for(String genre : genres) {
            vector.setElementAt(normalizedGenreValue, Genres.getGenreIndex(genre));
        }

        //Initialize publication year field in vector.
        if(publicationYear <= 1980) {
            vector.setElementAt(YEAR_VALUE*YEAR_MULTIPLIER, 18);
        } else if(publicationYear <=1990) {
            vector.setElementAt(YEAR_VALUE*YEAR_MULTIPLIER, 19);
        } else {
            //publicationYear is 1990-2000 according to our dataset.
            vector.setElementAt(YEAR_VALUE*YEAR_MULTIPLIER, 20);
        }

        //Collect user data to initialize rest of fields.
        //Iterate over users and apply data for each user.
        int numMales = 0;
        int numFemales = 0;

        for(User user : userWatchList) {
            //Increment gender count
            if(user.getGender().equals("M")) {
                numMales++;
            } else if(user.getGender().equals("F")) {
                numFemales++;
            } else {
                throw new IllegalStateException("User is neither 'M' or 'F'.");
            }

            //Increment age group by normalized value
            if(user.getAge() == 1) {
                vector.setElementAt(vector.get(23) + INCREMENT_VALUE*AGE_MULTIPLIER,23);
            } else if(user.getAge() == 18) {
                vector.setElementAt(vector.get(24) + INCREMENT_VALUE*AGE_MULTIPLIER,24);
            } else if(user.getAge() == 25) {
                vector.setElementAt(vector.get(25) + INCREMENT_VALUE*AGE_MULTIPLIER,25);
            } else if(user.getAge() == 35) {
                vector.setElementAt(vector.get(26) + INCREMENT_VALUE*AGE_MULTIPLIER,26);
            } else if(user.getAge() == 45) {
                vector.setElementAt(vector.get(27) + INCREMENT_VALUE*AGE_MULTIPLIER,27);
            } else if(user.getAge() == 50) {
                vector.setElementAt(vector.get(28) + INCREMENT_VALUE*AGE_MULTIPLIER,28);
            } else if(user.getAge() == 56) {
                vector.setElementAt(vector.get(29) + INCREMENT_VALUE*AGE_MULTIPLIER,29);
            } else {
                throw new IllegalStateException("User has illegal age.");
            }

            //Increment profession field by normalized value
            vector.setElementAt(vector.get(30 + user.getProfession()) + INCREMENT_VALUE*PROFESSION_MULTIPLIER,30 + user.getProfession());
        }

        //Set males and females fields
        //Males
        vector.setElementAt(((double)numMales/(double)movieWatchCount)*GENDER_MULTIPLIER,21);
        //Females
        vector.setElementAt(((double)numFemales/(double)movieWatchCount)*GENDER_MULTIPLIER,22);

        //PROBABILITIES
        double maxDoubleProbability = 0;
        int thisMovieCoordinateIndex = 0;
        for(int i = 0; i < MovieClustering.chosenMovies.length; i++) {
            Movie currMovie = MovieClustering.chosenMovies[i];
            int currMovieId = currMovie.getId();
            if(currMovieId != this.getId()) {
                double currDoubleProbability = MovieClustering.pairsProbability[this.id][currMovieId];
                vector.setElementAt(currDoubleProbability*PROBABILITY_MULTIPLIER, 51 + i);
                if(currDoubleProbability > maxDoubleProbability) {
                    maxDoubleProbability = currDoubleProbability;
                }
            } else {
                thisMovieCoordinateIndex = i;
            }
        }
        if(maxDoubleProbability > this.singleProbability) {
            vector.setElementAt(maxDoubleProbability * PROBABILITY_MULTIPLIER, 51 + thisMovieCoordinateIndex);
        } else {
            vector.setElementAt(this.singleProbability * PROBABILITY_MULTIPLIER, 51 + thisMovieCoordinateIndex);
        }
        //PROBABILITIES
        this.vector = vector;
        return vector;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", publicationYear=" + publicationYear +
                ", name='" + name + '\'' +
                ", genres=" + genres +
                ", userWatchList=" + userWatchList +
                ", singleProbability=" + singleProbability +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id &&
                publicationYear == movie.publicationYear &&
                Double.compare(movie.singleProbability, singleProbability) == 0 &&
                Objects.equals(name, movie.name) &&
                Objects.equals(genres, movie.genres) &&
                Objects.equals(userWatchList, movie.userWatchList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicationYear, name, genres, userWatchList, singleProbability);
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof Movie)) {
            throw new IllegalArgumentException("Must be compared to a movie");
        }
        Movie other = (Movie) o;
        return (int)(this.getProbability() - other.getProbability());
    }
}
