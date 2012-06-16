package dto;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.json.Json;

import com.google.appengine.api.datastore.Key;

@Model
public class AuthDTO {
    @Attribute(primaryKey = true)
    @Json(ignore = true)
    Key key;

    boolean loggedIn;
    String loginURL;
    String logoutURL;

    public Key getKey() {
        return key;
    }
    public void setKey(Key key) {
        this.key = key;
    }
    public boolean isLoggedIn() {
        return loggedIn;
    }
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    public String getLoginURL() {
        return loginURL;
    }
    public void setLoginURL(String loginURL) {
        this.loginURL = loginURL;
    }
    public String getLogoutURL() {
        return logoutURL;
    }
    public void setLogoutURL(String logoutURL) {
        this.logoutURL = logoutURL;
    }
}
