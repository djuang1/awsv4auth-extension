package org.mule.extension.awsv4auth.api;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.util.Map;
import java.util.Objects;

public class Headers {

	@Parameter
	@Summary("Header value for x-amz-date")
    private String timeStamp;
	
    public String getTimeStamp(){
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp){
        this.timeStamp = timeStamp;
    }
    
    @Parameter
	@Summary("Header value for host")
    @Optional(defaultValue = "#[\"lambda.us-east-1.amazonaws.com\"]")
    public String hostName;

    public String getHostName(){
        return hostName;
    }

    public void setHostName(String hostName){
        this.hostName = hostName;
    }
    
    @Parameter
	@Summary("Header value for content-type")
    @Optional(defaultValue = "#[\"application/json\"]")
    
    public String contentType;

    public String getContentType(){
        return contentType;
    }

    public void setContentType(String contentType){
        this.contentType = contentType;
    }
	
	@Parameter
    @Optional
    @NullSafe
    private Map<String, Object> additionalHeaders;
    
    public Map<String, Object> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public void setAdditionalHeaders(Map<String, Object> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Headers that = (Headers) o;
        return Objects.equals(additionalHeaders, that.additionalHeaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(additionalHeaders);
    }

    @Override
    public String toString() {
        return "Headers{" +
                "additionalHeaders=" + additionalHeaders +
                '}';
    }
}