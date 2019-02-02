import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KMeansClustering {



    /**
     * K-Means algorithm solution for the movie clustering problem.
     *
     * @param movieToCluster - List of movies to be clustered.
     */
    protected static void KMeansClustering(List<Movie> movieToCluster) {

        int numOfMovies = movieToCluster.size();

        //Get the selected movies from the user
        List<Movie> MoviesToClusterKMeans = movieToCluster;
        MovieClustering.chosenMovies = new Movie[numOfMovies];
        MovieClustering.chosenMovies = MoviesToClusterKMeans.toArray(MovieClustering.chosenMovies);
        Arrays.sort(MovieClustering.chosenMovies);


        //Run the K-Means algorithm for all K's in the interval (numOfMovies/4) <= K <= (numOfMovies*3)/4
        //Each K will run (numOfMovies*2)/i times attempting to find the best clustering by a random initialization
        //Of centroids with K random movies in the subset.
        //Finally, the best clustering overall will be chosen.
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        for(int i = (numOfMovies/4) ; i <= ((numOfMovies*3)/4) ; i++) {
            for(int j = 0 ; j < (numOfMovies*2)/i ; j++) {
                KMeansTask task = new KMeansTask(i, MoviesToClusterKMeans);
                pool.submit(task);
            }
        }
        pool.shutdown();
        try{
            System.out.print("Wait for all tasks to be completed\r");
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted Thread Pool.");
        }
        System.out.println("The total cost - KMeans: " + MovieClustering.minCost);
        System.out.println("The chosen k: " + MovieClustering.minK);
        System.out.println();
    }



    /**
     * @param moviesToClusterKMeans - List of movies to clustered.
     * @param k - number of clusters in the current iteration.
     * @return randomized k movies from the list of movies(to be centroids in the K-Means algorithm).
     */
    protected static List<Movie> chooseRandomizedKMovies(List<Movie> moviesToClusterKMeans, int k) {

        List<Movie> temp = new LinkedList<>(moviesToClusterKMeans);
        List<Movie> chosenRandom = new LinkedList<>();
        int size = temp.size();
        for(int j = 1 ; j <= k ; j++){
            Movie chosen = null;
            while(chosen == null)
                chosen = temp.get((int)(Math.random() * size));
            chosenRandom.add(chosen);
            temp.remove(chosen);
            size = temp.size();
        }


        return chosenRandom;
    }



    /**=========================================================================================================**
     ************************************************Legacy functions*********************************************
     **=========================================================================================================**/

    /**
     * Runs the K-Means algorithm after running the correlated algorithm to obtain K = amount of clusters given by the
     * correlated algorithm.
     *
     * @param movieToCluster - List of movies to be clustered.
     */
    private static void KMeansClusteringAfterCorrelate(List<Movie> movieToCluster) {

        int numOfMovies = movieToCluster.size();

        //get the selected movies from the user
        List<Movie> MoviesToClusterKMeans = movieToCluster;
        MovieClustering.chosenMovies = new Movie[numOfMovies];
        MovieClustering.chosenMovies = MoviesToClusterKMeans.toArray(MovieClustering.chosenMovies);
        Arrays.sort(MovieClustering.chosenMovies);

        //selecting the centroids
        List<Set<Movie>> tempClusters;
        double minCost = Double.MAX_VALUE;

        int k = MovieClustering.clusters.size();
        for(int j = 0 ; j < 100 ; j++) {
            List<Movie> centroids = new LinkedList<>();
            for (int i = 0; i < k; i++) { //choose randomly k centroids from the k clusters
                //centroids.add(findPopularMovie(clusters.get(i)));   //choose the popular from the cluster
                int arrSize = MovieClustering.clusters.get(i).size();
                Movie[] temp = new Movie[arrSize];
                MovieClustering.clusters.get(i).toArray(temp);
                Movie m = temp[(int) (Math.random() * arrSize)];  //choose randomly from the cluster
                centroids.add(m);   //choose the first from the cluster
            }

            //starting the k-means when the started centroids are provided
            KMeans KMeans = new KMeans(k, MovieClustering.VECTORSIZE, centroids);
            tempClusters = KMeans.start(MoviesToClusterKMeans);
            double tempCost = MovieClustering.getTotalClustersCost(MovieClustering.clusters);
            if (tempCost < minCost) {
                minCost = tempCost;
                MovieClustering.clusters = new LinkedList<>(tempClusters);
            }
        }

        System.out.println("The total cost - KMeans: " + minCost);
        System.out.println("The chosen k: " + k);
        System.out.println();
    }


    //find the popular movie in some cluster
    private static Movie findPopularMovie(Set<Movie> currCluster) {
        Movie popular = (Movie)currCluster.toArray()[0];
        for(Movie current: currCluster){
            if(current.getProbability() > popular.getProbability())
                popular = current;
        }
        return popular;
    }


    /**=========================================================================================================**
     ************************************************Legacy functions*********************************************
     **=========================================================================================================**/
}
