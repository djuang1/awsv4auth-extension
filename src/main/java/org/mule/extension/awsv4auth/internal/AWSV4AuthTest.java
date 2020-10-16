package org.mule.extension.awsv4auth.internal;

import org.json.simple.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.mule.extension.awsv4auth.api.Headers;

public class AWSV4AuthTest {

    @SuppressWarnings("null")
	public static void main(String[] args) {

        AWSV4AuthOperations testOperation = new AWSV4AuthOperations();
        
        Headers headers = new Headers();
        headers.setTimeStamp("20200121T123154Z");
        headers.setHostName("lambda.us-east-1.amazonaws.com");
        headers.setContentType("application/json");
        
        Map<String, Object> additionalHeaders  = new HashMap<String, Object>() {{
            //put("x-amz-access-token", "Atza|IQEBLjAsAhRmHjNgHpi0U-Dme37rR6CuUpSREXAMPLE");
            //put("user-agent", "My Selling Tool/2.0 (Language=Java/1.8.0.221; Platform=Windows/10)");
        }};        
		headers.setAdditionalHeaders(additionalHeaders);
		

        JSONObject obj = new JSONObject();
        obj.put("first_name", "Bill");

        InputStream is = new ByteArrayInputStream(obj.toString().getBytes());

        //System.out.println(testOperation.getAuthorizationString(is,"20200121T123154Z","ABC","123","us-east-1","lambda", "POST", headers, "lambda.us-east-1.amazonaws.com" ,"/2015-03-31/functions/helloLambda/invocations","test=1"));

        System.out.println(testOperation.getAuthorizationString(is,"ABC","123","us-east-1","lambda", "POST", headers,"/2015-03-31/functions/helloLambda/invocations",""));

    }
}
