package sa.com.sure.task.webservicesconsumer.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by HussainHajjar on 5/10/2017.
 */
@Root(name = "Table")
public class UsState {

    @Element(name = "CITY")
    private String city;
    @Element(name = "STATE")
    private String state;
    @Element(name = "ZIP")
    private String zipCode;
    @Element(name = "AREA_CODE")
    private String areaCode;
    @Element(name = "TIME_ZONE")
    private String timeZone;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
