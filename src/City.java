public class City {
    private String cityName;
    private int index; //position

    public City(String cityName, int index){
        this.cityName=cityName;
        this.index =index;
    }

    public String getCityName(){
        return cityName;
    }
    public int getIndex(){
        return index;
    }
}
