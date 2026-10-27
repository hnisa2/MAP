/** A named vertex and its position in the travel-time matrix. */
public final class City {
    private final String cityName;
    private final int index;
    public City(String cityName, int index) {
        if (cityName == null || cityName.isBlank() || index < 0) {
            throw new IllegalArgumentException("A city needs a name and a non-negative index.");
        }
        this.cityName = cityName;
        this.index = index;
    }
    public String getCityName() { return cityName; }
    public int getIndex() { return index; }
}
