package scheduler.Model;

/**
 *
 * @author Cory
 * This class represents a Customer's address. This class is modeled after the
 * address database table.
 */
public class Address {
    
    private int id;
    private String address1;
    private String address2;
    private int cityId;
    private String postalCode;
    private String phoneNumber;
    private String cityName;
    private int countryId;
    private String countryName;
    
    public Address(int id, String address1, String address2, int cityId,
            String postalCode, String phoneNumber, String cityName,
            int countryId, String countryName) {
        this.id = id;
        this.address1 = address1;
        this.address2 = address2;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.cityName = cityName;
        this.countryId = countryId;
        this.countryName = countryName;
    }
    
    public int getId() {
        return this.id;
    }
    
    private void setId(int id) {
        this.id = id;
    }
    
    public String getAddressField1() {
        return this.address1;
    }
    
    public void setAddressField1(String address1) {
        this.address1 = address1;
    }
    
    public String getAddressField2() {
        return this.address2;
    }
    
    public void setAddressField2(String address2) {
        this.address2 = address2;
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
    
    public String getPostalCode() {
        return this.postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getCountryName() {
        return this.countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    public String getCityName() {
        return this.cityName;
    }
    
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    
    public int getCountryId() {
        return this.countryId;
    }
    
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
    
}
