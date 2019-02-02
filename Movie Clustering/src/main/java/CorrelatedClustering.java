import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CorrelatedClustering {


    /**
     * Correlated algorithm solution for the movie clustering problem
     * @param movieToCluster - List of movies that need to be clustered
     */
    protected static void correlatedClustering(List<Movie> movieToCluster){

        //Get the selected movies from the user.
        List<Movie> MoviesToClusterCorrelated = new LinkedList<>(movieToCluster);

        //Start the correlated clustering.
        MovieClustering.clusters = new LinkedList<Set<Movie>>();
        while(!MoviesToClusterCorrelated.isEmpty()){
            MovieClustering.clusters.add(findCluster(MoviesToClusterCorrelated));
        }
        double CorrelatedCost = MovieClustering.getTotalClustersCost(MovieClustering.clusters);
        System.out.println("The total cost - Correlated: " + CorrelatedCost + "\n");
    }


    /**
     * The function gets a list of movies, chooses one movie randomly, and creates a cluster of him
     * and all the movies that are correlated with him.
     *
     * @param moviesTag - Subset list of the list of movies that need to be clustered.
     * @return One new cluster that was created from moviesTag.
     */
    private static Set<Movie> findCluster(List<Movie> moviesTag) {
        Movie curr = null;

        //Chooses a movie randomly from the list.
        while(curr == null) {
            curr = moviesTag.get((int) (Math.random() * moviesTag.size()));
        }
        Set<Movie> currSet = new HashSet<Movie>();

        List<Movie> moviesToRemove = new LinkedList<Movie>();
        moviesToRemove.add(curr);
        currSet.add(curr);
        int index = curr.getId();

        //Adds movies that are correlated with 'curr' to the cluster.
        for(Movie m:moviesTag) {
            if ((m!=null) && (m.getId() != index) && (MovieClustering.correlatedMovies[index][m.getId()] == 1)) {
                currSet.add(m);
                moviesToRemove.add(m);
            }
        }

        //Removes movies that were added to the cluster(for the next iteration).
        for(Movie m : moviesToRemove)
            moviesTag.remove(m);

        return currSet;

    }
}
