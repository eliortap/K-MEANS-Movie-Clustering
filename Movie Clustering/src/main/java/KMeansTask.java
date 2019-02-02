import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * single task of KMeans
 * get number k and list of movies to cluster
 * and try to find proper clustering,
 *
 */
public class KMeansTask implements Runnable {

    private final int k;
    private List<Movie> MoviesToClusterKMeans;

    /**
     *
     * @param k - represent the k in KMeans
     * @param MoviesToClusterKMeans - list of movies that need to be clustered
     */
    public KMeansTask(int k, List<Movie> MoviesToClusterKMeans) {
        this.k = k;
        this.MoviesToClusterKMeans = MoviesToClusterKMeans;
    }

    /**
     * single iteration of KMEans algorithm, with a specific K
     * if this iteration provide low cost, the function update it and the new clusters
     */
    @Override
    public void run() {
        //choose k movies that will be the first centroids
        List<Movie> randomizedK = new LinkedList<>(KMeansClustering.chooseRandomizedKMovies(MoviesToClusterKMeans, k));
        KMeans KMeans = new KMeans(k, MovieClustering.VECTORSIZE, randomizedK);
        List<Set<Movie>> tempClusters = new LinkedList<>(KMeans.start(MoviesToClusterKMeans));
        double tempCost = MovieClustering.getTotalClustersCost(tempClusters);
        synchronized (MovieClustering.mutex) {
            if (tempCost < MovieClustering.minCost) { //checking if this task achieve the min cost
                MovieClustering.minK = k;
                MovieClustering.minCost = tempCost;
                MovieClustering.clusters = new LinkedList<>(tempClusters);
            }
        }

    }
}
