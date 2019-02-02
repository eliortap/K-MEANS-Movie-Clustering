**This project was created as part of a Mini-Project of the Big Data program at Ben-Gurion University**



**The directory 'Movie Clustering' includes all the source code for our solution in java** 





============================ AIM ============================

The project aims at providing an optimal clustering to a dataset of movies.



====================== PROJECT GUIDELINES ======================

In our assignment we were given a cost function we were to minimize, movie watch probability formulas,
and a fixed data-set of movies, users, and ratings. Although the project data-set is not big data, the
aim was to treat the problem as a problem that could learn best with big-data. In addition, for simplicity,
we were allowed to assume that a movie was rated if and only if it was watched. That is, in the frame of our mini-project
we used the ratings only to get some sort of metric for who watched which movie. Obviously, for a real life
solution that would be a huge simplification, but such a change to our solution would be very straightforward.


====================== SOLUTION OVERVIEW ====================== 

Our algorithm uses K-Means to cluster the movies, the algorithm and our purpose for choosing K-Means is described
in depth in the 'Data Analysis Mini Project Report.pdf' file in the repository. If one is to use our algorithm for
a similar problem the main areas to change would be the getVector() function in the movie class, or whichever class you will
treat as a vector, to match the parameters to be taken into account in the new problem. The K-Means algorithm we implemented
is largely generic and  assumes that it gets some 'Vectorizable' class which essentially verifies that the objects it
is clustering have a getVector() function. The only thing that must be changed is the getDistance() function which
implements our heuristic (Euclidean and weighted probabilities). Generally the default K-Means works with a Euclidean
distance function so one could go with that, or otherwise use some other distance function, or some other heuristic.
