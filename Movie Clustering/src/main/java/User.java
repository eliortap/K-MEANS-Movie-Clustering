/**
 * class that represent single user
 * */
public class User {
    private int userId;
    private String gender;
    private int age;
    private int profession;
    private int numOfMoviesWatched;
    private String professionName;
    private String zipCode;

    /**
     * default constructor
     */
    public User() {
        this.userId = -1;
        this.gender = "Null";
        this.age = -1;
        this.profession = -1;
        this.numOfMoviesWatched = 0;
        this.professionName = "Null";
        this.zipCode = "Null";
    }

    /**
     *
     * @param userId - id of user, uses as the key of the main HashMap
     * @param gender - gender of user
     * @param age - age of user
     * @param profession - profession of user
     * @param zipCode - unused
     */
    public User(int userId, String gender, int age, int profession, String zipCode) {
        this.userId = userId;
        this.gender = gender;
        this.age = age;
        this.profession = profession;
        this.professionName = getProfessionByNum(profession);
        this.zipCode = zipCode;
    }

    /**
     *
     * @return user's id
     */
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     *
     * @return user's gender
     */
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     *
     * @return user's age
     */
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     *
     * @return number of movies that the user watched
     */
    public int getNumOfMoviesWatched() {
        return numOfMoviesWatched;
    }

    /**
     * incrementing the counter of movies that watched
     */
    public void incNumOfMoviesWatched() {
         this.numOfMoviesWatched++;
    }
    public void setNumOfMoviesWatched(int numOfMoviesWatched) {
        this.numOfMoviesWatched = numOfMoviesWatched;
    }

    /**
     *
     * @return user's profession index
     */
    public int getProfession() {
        return profession;
    }

    /**
     *
     * @return user's profession name
     */
    public String getProfessionName() {
        return professionName;
    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", profession=" + profession +
                ", professionName='" + professionName + '\'' +
                ", zipCode=" + zipCode +
                '}';
    }

    /**
     *
     * @param profession index
     * @return profession name
     */
    private String getProfessionByNum(int profession) {
        switch(profession){
            case 0:
                return "other";
            case 1:
                return "academic/educator";
            case 2:
                return "artist";
            case 3:
                return "clerical/admin";
            case 4:
                return "college/grad student";
            case 5:
                return "customer service";
            case 6:
                return "doctor/health care";
            case 7:
                return "executive/managerial";
            case 8:
                return "farmer";
            case 9:
                return "homemaker";
            case 10:
                return "K-12 student";
            case 11:
                return "lawyer";
            case 12:
                return "programmer";
            case 13:
                return "retired";
            case 14:
                return "sales/marketing";
            case 15:
                return "scientist";
            case 16:
                return "self-employed";
            case 17:
                return "technician/engineer";
            case 18:
                return "tradesman/craftsman";
            case 19:
                return "unemployed";
            case 20:
                return "writer";
            default:
                return "Null";

        }

    }
}
