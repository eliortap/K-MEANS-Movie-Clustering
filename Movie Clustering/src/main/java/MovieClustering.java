import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

public class MovieClustering {
    private static String moviesData; //movies data path
    private static String ratingsData; // ratings data path
    private static String usersData; //users data path
    protected static final int maxMovieId = 3953; // max + 1
    protected static int numOfMovies; //number of movies that need to be clustered
    private static Map<Integer,Movie> movies; //key: movieID
    private static Map<Integer, List<Rating>> ratingsByUsers; //key: userID
    private static Map<Integer,User> users; //key: userID
    protected static double[][] pairsProbability; //array of all the probabilities of all pair of movies
    protected static int[][] correlatedMovies; //array of all the connectivities of all pair of movies
    protected static Movie[] chosenMovies;
    protected static Vector[] vectors;
    protected static final Object mutex = new Object();
    protected static double minCost = Double.MAX_VALUE;
    protected static int minK = -1;
    protected static List<Set<Movie>> clusters; //the chosen clusters
    protected static int VECTORSIZE = 51;

    public static void main(String[] args) throws IOException, InterruptedException {


        //=================Loading the Data===============//
        System.out.println("//=======Loading the data=======//\n");
        List<Movie> movieToCluster = loadTheData(args);
        //================================================//



        //=============Calculating probabilities==========//
        calculateSingleProbability();
        calculateCorrelatedMovies();
        //================================================//



        //===============Clustering algorithm==============//
        System.out.println("\n//=======Start Clustering=======//\n");
        if(args[1].compareTo("1") == 0) {
            CorrelatedClustering.correlatedClustering(movieToCluster);
        } else if(args[1].compareTo("2") == 0) {
            KMeansClustering.KMeansClustering(movieToCluster);
            //KMeansClustering.KMeansClusteringAfterCorrelate(numOfMovies, movieToCluster);
        } else {
            System.err.println("A specific algorithm was not selected\n");
            exit(-1);
        }
        //================================================//




        //==============Printing the results==============//
        writeClusters();
        System.out.println("//============Finish============//");
        //================================================//



    }


    /**
     * This function loads all the dataset, and loads the movies that we want to cluster
     * while also removing movies that have less then 10 ratings.
     *
     * @param args
     * @return List of movies that need to be clustered
     */
    private static List<Movie> loadTheData(String[] args) {
        if(args.length >= 3)
            readingArgs(args);
        else{
            System.err.println("Not enough args\n");
            exit(-1);
        }
        movies = new HashMap<Integer, Movie>();
        ratingsByUsers = new HashMap<Integer, List<Rating>>();
        users = new HashMap<Integer, User>();
        loadMoviesData();
        loadUsersData();
        loadRatingsData();

        List<Movie> movieToCluster = movieToCluster(args[2]);
        //select 100 movies
        //List<Movie> movieToCluster = getKRandomMovies(100);
        //List<Movie> movieToCluster = getKPopular(100);


        removeUnpopularMovies(movieToCluster);
        if(ratingsByUsers.isEmpty() || movies.isEmpty() || users.isEmpty()){
            exit(-1);
        }
        return movieToCluster;
    }

    //Updates the paths to the dataset.
    private static void readingArgs(String[] args) {
        moviesData = args[0] + "/movies.dat";
        ratingsData = args[0] + "/ratings.dat";
        usersData = args[0] + "/users.dat";

    }

    //Reads the 'movie subset filepath' and returns the list of movies to cluster.
    private static List<Movie> movieToCluster(String path) {
        List<Movie> toReturn = new LinkedList<>();
        File movieListFile = new File(path);
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(movieListFile));
            String id;
            while ((id = buffer.readLine()) != null) {
                Movie m = movies.get(Integer.valueOf(id));
                if(m != null)
                    toReturn.add(m);
                else
                    System.err.println("Movie " + Integer.valueOf(id) + " ignored because he is not exist");
            }

        } catch (FileNotFoundException e) {
            System.err.println("moviesubsetfilepath doesn't found\nexit from the program");
            exit(-1);
        } catch (IOException e) {
            System.err.println("reading the moviesubsetfile is failed\nexit from the program");
            exit(-1);
        }

        return toReturn;
    }







    //Prints the clusters' output
    private static void writeClusters() throws IOException {
        double totalCost = 0;
        int i = 1;
        System.out.println("//=========The Clusters=========//\n");
        for (Set<Movie> cluster : clusters) {
            double clusterCost = getClusterCost(cluster);
            int sizeOfCluster = cluster.size();
            totalCost += clusterCost;
            System.out.print("Cluster " + i + ":  " + clusterCost + "\n");
            for (Movie m : cluster) {
                if(sizeOfCluster == 1)
                    System.out.print(m.getId() + " " + m.getName() + "\n");
                else
                    System.out.print(m.getId() + " " + m.getName() + ", ");
                sizeOfCluster--;
            }
            //System.out.println();
            i++;
        }
        System.out.println();
        System.out.println("Total Cost: " + totalCost + "\n");
    }

    //Calculates the total cost for all clusters
    protected static double getTotalClustersCost(List<Set<Movie>> tempClusters) {
        double totalCost = 0;
        for(Set<Movie> cluster: tempClusters)
            totalCost += getClusterCost(cluster);
        return totalCost;
    }

    //Calculates the cost for single cluster
    private static double getClusterCost(Set<Movie> cluster) {
        double cost = 0;
        double clusterSize = cluster.size();
        if(clusterSize > 1) {
            for (Movie m1 : cluster) {
                for (Movie m2 : cluster) {
                    int m1id = m1.getId();
                    int m2id = m2.getId();
                    if(m1id < m2id) {
                        double m1m2prob = pairsProbability[m1id][m2id];
                        cost += ((Math.log((1 / m1m2prob))) / (clusterSize - 1));
                    }
                }
            }
        }
        else if(clusterSize == 1){
            Movie m = (Movie)cluster.toArray()[0];
            cost = Math.log((1/m.getProbability()));
        }

        return cost;
    }


    /**
     * This function filters movies that have less then 10 ratings
     *
     * @param movieToCluster - the movies that need to be checked
     */
    private static void removeUnpopularMovies(List<Movie> movieToCluster) {
        List<Movie> toRemove = new LinkedList<Movie>();
        for(Movie m:movieToCluster) {
            if (m.getCountOfWatches() < 10) {
                System.err.println("Movie " + m.getId() + " ignored because it has only " + m.getCountOfWatches() + " ratings");
                toRemove.add(m);
            }
        }
        for(Movie m : toRemove) {
            movieToCluster.remove(m);
        }
        numOfMovies = movieToCluster.size();
        VECTORSIZE += numOfMovies;
    }

    private static void loadMoviesData() {
        File moviesFile = new File(moviesData);
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new FileReader(moviesFile));
            String line;
            while ((line = buffer.readLine()) != null) {
                addNewMovie(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("\"movies.dat\" file is'nt found");

        } catch (IOException e) {
            System.err.println("\"movies.dat\" not valid");
        }

    }

    private static void addNewMovie(String line) {
        String[] parameters = line.split("::");
        int id = Integer.valueOf(parameters[0]);
        String movieNameAndYear = parameters[1];
        String movieName = movieNameAndYear.substring(0,movieNameAndYear.lastIndexOf('(') - 1);
        String year = movieNameAndYear.substring(movieNameAndYear.lastIndexOf('(') + 1,movieNameAndYear.length() - 1);
        String[] genres = parameters[2].split("\\|");
        Movie current = new Movie(id, movieName, genres, Integer.valueOf(year));
        movies.put(new Integer(id), current);
    }

    private static void loadUsersData() {
        File usersFile = new File(usersData);
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new FileReader(usersFile));
            String line;
            while ((line = buffer.readLine()) != null) {
                addNewUser(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("\"users.dat\" file is'nt found");

        } catch (IOException e) {
            System.err.println("\"users.dat\" not valid");
        }
    }

    private static void addNewUser(String line) {
        String[] parameters = line.split("::");
        int userId = Integer.valueOf(parameters[0]);
        String gender = parameters[1];
        int age = Integer.valueOf(parameters[2]);
        int profession = Integer.valueOf(parameters[3]);
        String zipCode = parameters[4];
        User current = new User(userId, gender, age, profession, zipCode);
        users.put(new Integer(userId), current);
    }

    private static void loadRatingsData() {
        File ratingsFile = new File(ratingsData);
        BufferedReader buffer = null;
        int count = 0;
        try {
            buffer = new BufferedReader(new FileReader(ratingsFile));
            String line;
            while ((line = buffer.readLine()) != null) {
                count++;
                addNewRating(line,count);
            }
        } catch (FileNotFoundException e) {
            System.err.println("\"ratings.dat\" file is'nt found");

        } catch (IOException e) {
            System.err.println("\"ratings.dat\" not valid");
        }


    }

    private static void addNewRating(String line, int count) {
        String[] parameters = line.split("::");
        int userId = Integer.valueOf(parameters[0]);
        int movieId = Integer.valueOf(parameters[1]);
        int rating = Integer.valueOf(parameters[2]);

        User currUser = users.get(userId);
        Movie currMovie = movies.get(movieId);

        currUser.incNumOfMoviesWatched();
        currMovie.addUser(currUser);


        Rating current = new Rating(currUser, currMovie, rating);
        Integer key = new Integer(userId);
        if(ratingsByUsers.containsKey(key)){
            List<Rating> currentList = ratingsByUsers.get(key);
            currentList.add(current);
        } else {
            List<Rating> newList = new LinkedList<Rating>();
            newList.add(current);
            ratingsByUsers.put(key, newList);
        }
    }

    /**
     * Iterates over all the users ratings and calculates probability for each movie.
     */
    private static void calculateSingleProbability() {
        double numOfUsers = (double)users.size();
        double numOfMovies = (double)movies.size();
        double[] moviescount = new double[maxMovieId];//the movie with the max ID is higher then the total count

        //Iterates the users ratings
        for(Map.Entry<Integer,List<Rating>> pair:ratingsByUsers.entrySet()){
            User currUser = users.get(pair.getKey());
            List<Rating> userRatingList = pair.getValue();

            //Iterates the ratings of the current user
            for(Rating rating:userRatingList) {
                int index = rating.getMovie().getId();
                moviescount[index] += ((double)2/(double)currUser.getNumOfMoviesWatched());
            }
        }

        //Calculates the movie probability as a formula of counts.
        for(Movie movie:movies.values()) {
            double startFactor = (2/numOfMovies);
            double movieCount = moviescount[movie.getId()];
            double probability = startFactor + movieCount;
            probability = probability / (numOfUsers + 1);
            movie.setProbability(probability);
        }
    }


    /**
     * Iterates over all the users ratings and calculate probability for each pair of movies in the dataset.
     */
    private static void calculateCorrelatedMovies() {
        double numOfMovies = (double)movies.size();
        double numOfUsers = (double)users.size();
        correlatedMovies = new int[maxMovieId][maxMovieId]; //the movie with the max ID is higher then the total count
        pairsProbability = new double[maxMovieId][maxMovieId]; //the movie with the max ID is higher then the total count

        //Iterates the users ratings
        for(Map.Entry<Integer,List<Rating>> pair:ratingsByUsers.entrySet()){
            User currUser = users.get(pair.getKey());
            List<Rating> userRatingList = pair.getValue();

            //Iterates the ratings of the current user
            List<Integer> ratedMoviesByCurrUser = new ArrayList<Integer>();
            for(Rating rating:userRatingList) {
                Integer index = new Integer(rating.getMovie().getId());
                ratedMoviesByCurrUser.add(index);
            }

            //Increase all the "matched" movies
            for(Integer i:ratedMoviesByCurrUser){
                for(Integer j:ratedMoviesByCurrUser){
                    if(i.compareTo(j) < 0){
                        int userMovies = currUser.getNumOfMoviesWatched();
                        double moviesXmovies = (2/((double)(userMovies)*(userMovies-1)));
                        pairsProbability[i][j] += moviesXmovies;
                    }
                }
            }
        }


        //updating from count to probability
        for(Movie m1:movies.values()) {
            double m1prob = m1.getProbability(); //movie 1 probability
            int m1Id = m1.getId();
            for(Movie m2:movies.values()){
                double m2prob = m2.getProbability();  //movie 2 probability
                int m2Id = m2.getId();
                if((m1Id < m2Id) && (correlatedMovies[m1Id][m2Id] == 0)) {
                    double count = pairsProbability[m1Id][m2Id];
                    count = count + (2/((numOfMovies)*(numOfMovies - 1))); //adding the "start" value
                    double m1m2prob = count / (numOfUsers + 1);
                    pairsProbability[m1Id][m2Id] = m1m2prob;
                    pairsProbability[m2Id][m1Id] = m1m2prob;

                    //check the condition for two movies to be correlated
                    if (m1m2prob >= (m1prob * m2prob)) {
                        correlatedMovies[m1Id][m2Id] = 1;
                        correlatedMovies[m2Id][m1Id] = 1;
                    } else {
                        correlatedMovies[m1Id][m2Id] = -1;
                        correlatedMovies[m2Id][m1Id] = -1;
                    }
                }
            }
        }

    }



    /**=========================================================================================================**
     ************************************************Legacy Functions*********************************************
     **=========================================================================================================**/



    //returns randomized k movies from the whole movie data
    private static List<Movie> getKRandomMovies(int k){
        List<Movie> moviesTag = new LinkedList<>();//movies.values());
        int count = 0;

        while(count < k){
            Movie toAdd = movies.get((int)(Math.random() * movies.size()));
            if(moviesTag.contains(toAdd)) {
                continue;
            } else {
                if(toAdd!=null) {
                    moviesTag.add(toAdd);
                    count++;
                }
            }
        }
        return moviesTag;
    }

    //finds the k popular movies from the whole movies data
    private static List<Movie> getKPopular(int numOfMovies) {
        List<Movie> allmovies = new ArrayList<>(movies.values());
        Movie[] ms = new Movie[allmovies.size()];
        allmovies.toArray(ms);
        Arrays.sort(ms);
        List<Movie> ToReturn = new LinkedList<>();
        for(int i=0 ; i < numOfMovies ; i++){
            ToReturn.add(ms[i]);
        }
        return ToReturn;
    }

    public static void printVectors() throws IOException {
        vectors = new Vector[numOfMovies];
        for(int i = 0; i < numOfMovies; i++) {
            vectors[i] = chosenMovies[i].getVector();
        }

        File newfile = new File("vectors");
        BufferedWriter writer = new BufferedWriter(new FileWriter(newfile));

        int i = 1;
        for (Vector vector : vectors) {
            writer.write("Vector " + i + ":  " + "\n");
            writer.write(String.valueOf(vector));
            writer.newLine();
            i++;
        }
        writer.close();
    }

    /**=========================================================================================================**
     ************************************************Legacy functions*********************************************
     **=========================================================================================================**/
}
