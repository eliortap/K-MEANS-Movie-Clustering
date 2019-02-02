public class Genres {
    /**
     *
     * @param genre
     * @return index representing the genre
     */
    public static int getGenreIndex(String genre) {
        int index;
        switch(genre) {
            case "Action":
                index = 0;
                break;
            case "Adventure":
                index = 1;
                break;
            case "Animation":
                index = 2;
                break;
            case "Children's":
                index = 3;
                break;
            case "Comedy":
                index = 4;
                break;
            case "Crime":
                index = 5;
                break;
            case "Documentary":
                index = 6;
                break;
            case "Drama":
                index = 7;
                break;
            case "Fantasy":
                index = 8;
                break;
            case "Film-Noir":
                index = 9;
                break;
            case "Horror":
                index = 10;
                break;
            case "Musical":
                index = 11;
                break;
            case "Mystery":
                index = 12;
                break;
            case "Romance":
                index = 13;
                break;
            case "Sci-Fi":
                index = 14;
                break;
            case "Thriller":
                index = 15;
                break;
            case "War":
                index = 16;
                break;
            case "Western":
                index = 17;
                break;
            default:
                throw new IllegalArgumentException("Unexpected genre.");
        }
        return index;
    }

}
