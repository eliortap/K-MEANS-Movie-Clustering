import java.util.*;

public class KMeans <T extends Vectorizable> {
    private static final int base = 2;
    private static final double DELTA = 1;
    private int k;
    private List<Vector<Double>> centroids;
    private int sizeOfVector;

    /**
     * @param k - Number of centroids (clusters).
     * @param sizeOfVector - Size of the vectors, or in other words the dimension n of the space.
     * @param vectorizableCentroids - K initial vectorizable objects to be used as centroids.
     */
    //constructor that the centroids is movies
    public KMeans(int k, int sizeOfVector, List<T> vectorizableCentroids) {

        this.k = k;
        this.sizeOfVector = sizeOfVector;
        this.centroids = selectedCentroids(vectorizableCentroids);

    }

    /**
     * Converts the vectorizable centroids to their respective vectors.
     *
     * @param vectorizableCentroids - K vectorizable objects to be used as centroids.
     * @return - A list of centroid vectors.
     */
    private List<Vector<Double>> selectedCentroids(List<T> vectorizableCentroids) {

        List<Vector<Double>> cents = new LinkedList<>();
        for(T vectorizable : vectorizableCentroids) {
            cents.add(vectorizable.getVector());
        }

        return cents;
    }

    /**
     * The main function in the K-Means process, all iterations and updates of the process occur in this function.
     * Finally returns the clusters which the algorithm converged to according to the DELTA set in this class.
     *
     * @param vectorizables - A list of objects which can be represented as vectors, used as "observations" to which the
     *                      centroids will converge.
     * @return A list of disjoint clusters as sets of T (vectorizable) to which the centroids converged.
     */
    public List<Set<T>> start(List<T> vectorizables){

        List<Vector<Double>> lastCentroids = null;
        List<Vector<Double>> currCentroids = centroids;
        List<Set<T>> newClustering = null;

        //============================================================================================================//
        //Continue the KMeans process while the difference between the iterations is smaller than delta for convergence.
        //============================================================================================================//
        while(differenceInCentroids(lastCentroids, currCentroids) > DELTA){
            lastCentroids = new LinkedList<>(currCentroids);


            //==========================================================================//
            //Update the clustering in light of the centroids given by the last iteration.
            //==========================================================================//
            newClustering = fullIteration(vectorizables, currCentroids);


            //===================================================================//
            //Update each centroid as an average of the vectors in its new cluster.
            //===================================================================//
            int clusIndex=0;
            for(Set<T> clusterSet : newClustering){
                //Initialize a new centroid with 0s to be updated.
                Vector<Double> newCentroid = new Vector<>(sizeOfVector);
                for(int i = 0; i< sizeOfVector ; i++){
                    newCentroid.add(new Double(0));
                }
                //Sum the coordinates of each vector into the centroid
                for(T vectorizable : clusterSet){
                    Vector<Double> vector = vectorizable.getVector();
                    for(int i=0; i < sizeOfVector ; i++){
                        newCentroid.setElementAt(newCentroid.get(i) + vector.get(i),i);
                    }
                }
                int numOfMoviesInCluster = clusterSet.size();
                if(numOfMoviesInCluster > 0) {
                    //Divide each coordinate by the cluster size to get an average in each coordinate.
                    for (int i = 0; i < sizeOfVector; i++) {
                        newCentroid.setElementAt(newCentroid.get(i) / numOfMoviesInCluster, i);
                    }
                }
                //Finally update the centroid in the centroid list.
                currCentroids.set(clusIndex, newCentroid);
                clusIndex++;
            }

        }
        return newClustering;
    }

    /**
     * Sums the difference in distance between each updated centroid and its previous states, and returns that total
     * distance.
     *
     * @param lastCentroids - Previous list of centroids as vectors.
     * @param currCentroids - Current list of centroids as vectors.
     * @return Total distance between previous and current centroids.
     */
    private double differenceInCentroids(List<Vector<Double>> lastCentroids, List<Vector<Double>> currCentroids) {

        //Returns DELTA+1 if its the 0th iteration to ensure that there is no convergence without starting the process.
        if(lastCentroids == null)
            return DELTA+1;
        else{
            double difference = 0;
            //Sums the difference between updated centroids and their previous states.
            for(int i=0; i < centroids.size(); i++){
                difference += calculateDistance(lastCentroids.get(i), currCentroids.get(i));
            }

            return difference;
        }
    }

    /**
     * Creates a new clustering based off of the current centroids provided by the K-Means iteration. Each vector will
     * choose the closest centroid to it according to the distance given by the calculateDistance function.
     *
     * @param vectorizables - A list of objects which can be represented as vectors to cluster.
     * @param currCentroids - Current list of centroids as vectors.
     * @return A list of disjoint clusters as sets of T (vectorizable).
     */
    private List<Set<T>> fullIteration(List<T> vectorizables, List<Vector<Double>> currCentroids){

        //Initializes a new list of K sets representing the new clusteirng.
        List<Set<T>> newClusters = new ArrayList<Set<T>>(k);
        for(int i=0; i<k ; i++){
            Set<T> newCluster = new HashSet<>();
            newClusters.add(i, newCluster);
        }

        //For each vector: find the closest centroid and add the vector into that centroid's cluster set.
        for(T vectorizable : vectorizables){
            Vector<Double> movieVect = vectorizable.getVector();
            double minDistance = Double.MAX_VALUE;
            int minClusterIndex = -1;
            for(Vector<Double> cent:currCentroids){
                double currDistance = calculateDistance(cent, movieVect);
                if(currDistance < minDistance){
                    minDistance = currDistance;
                    minClusterIndex = currCentroids.indexOf(cent);
                }
            }
            try {
                newClusters.get(minClusterIndex).add(vectorizable);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return newClusters;
    }

    /**
     * Specific implementation of a distance function for our movie clustering project.
     *
     * Calculates the euclidean distance for all coordinates apart from the movie probability coordinates.
     *
     * For the movie probability coordinates (coordinates 51 up to vector.size()) the euclidean distance is weighted
     * by the vector v2's probability in that same coordinate. Vector v1 will always be a centroid in our use case, and
     * therefore the distance function gives weight according to the coordinate probability of v2, the movie's vector.
     *
     * @param v1 - A centroid vector.
     * @param v2 - A movie vector.
     * @return The total distance in double from the centroid v1 to the vector v2.
     */
    private double calculateDistance(Vector<Double> v1, Vector<Double> v2) {
        int size = v1.size();
        double distance = 0;
        double iteration;
        if (size - v2.size() != 0)
            return -1;

        //Calculate euclidean distance for the first 51 coordinates.
        for (int i = 0; i < 51; i++) {
            iteration = v2.get(i) - v1.get(i);
            iteration = Math.pow(iteration, 2);
            distance += iteration;

        }

        //Calculate a weighted euclidean distance for the movie probability coordinates.
        double sum = 0;
        for (int i = 51; i < size; i++) {
            iteration = v2.get(i) - v1.get(i);
            double differenceSquared = Math.pow(iteration, 2);
            double weightedDifference = differenceSquared * v2.get(i);
            sum += weightedDifference;
        }
        distance += sum;
        return Math.pow(distance, 0.5);
    }

}




    /**=========================================================================================================**
     ************************************************Legacy Functions*********************************************
     **=========================================================================================================**/

/*    final double RANDOM_GENRE_MULTIPLIER = 100;
    final double RANDOM_YEAR_MULTIPLIER = 100;
    final double RANDOM_INCREMENT_MULTIPLIER = 100;
    final double RANDOM_GENDER_MULTIPLIER = 100;
    final double PROBABILITY_MULTIPLIER = 100;*/

/*    public KMeans(int k, int sizeOfVector) {
        this.k = k;
        this.sizeOfVector = sizeOfVector;
        this.centroids = randomizedCentroids(k);


    }*/

/*    private List<Vector<Double>> randomizedCentroids(int k) {
        List<Vector<Double>> centroids = new LinkedList<>();
        while(k!=0){
            Vector<Double> currVect = new Vector<Double>(sizeOfVector);
            for(int i = 0; i< sizeOfVector ; i++){
                currVect.add(i,new Double(0));
            }
            for(int i = 0; i < sizeOfVector; i++) {
                if(i < 18) {
                    //currVect.setElementAt(Math.random()*RANDOM_GENRE_MULTIPLIER, i);
                } else if(i < 21) {
                    currVect.setElementAt(Math.random()*RANDOM_YEAR_MULTIPLIER, i);
                } else if(i < 23) {
                    currVect.setElementAt(Math.random()*RANDOM_GENDER_MULTIPLIER, i);
                } else if(i < 51){
                    currVect.setElementAt(Math.random()*RANDOM_INCREMENT_MULTIPLIER, i);
//                } else {
//                    currVect.setElementAt(Math.random()*RANDOM_PROBABILITY_MULTIPLIER/2, i);
                }
            }
            centroids.add(currVect);
            k--;
        }
        return centroids;
    }*/

/**=========================================================================================================**
 ************************************************Legacy Functions*********************************************
 **=========================================================================================================**/