package dto;

import java.util.ArrayList;
import java.util.List;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.json.Json;

import com.google.appengine.api.datastore.Key;

@Model
public class LogDTO {
    @Attribute(primaryKey = true)
    @Json(ignore = true)
    Key key;

    String combined;
    String offset;

    List<String> logLevels = new ArrayList<String>();
    List<String> logLines = new ArrayList<String>();

    public Key getKey() {
        return key;
    }
    public void setKey(Key key) {
        this.key = key;
    }
    public String getCombined() {
        return combined;
    }
    public void setCombined(String combined) {
        this.combined = combined;
    }
    public String getOffset() {
        return offset;
    }
    public void setOffset(String offset) {
        this.offset = offset;
    }
    public List<String> getLogLevels() {
        return logLevels;
    }
    public void setLogLevels(List<String> logLevels) {
        this.logLevels = logLevels;
    }
    public List<String> getLogLines() {
        return logLines;
    }
    public void setLogLines(List<String> logLines) {
        this.logLines = logLines;
    }

}
