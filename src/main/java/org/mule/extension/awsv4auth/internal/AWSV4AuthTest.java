package org.mule.extension.awsv4auth.internal;

import org.json.simple.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AWSV4AuthTest {

    public static void main(String[] args) {

        AWSV4AuthOperations testOperation = new AWSV4AuthOperations();

        JSONObject obj = new JSONObject();
        obj.put("first_name", "Bill");

        InputStream is = new ByteArrayInputStream(obj.toString().getBytes());

        System.out.println(testOperation.getAuthorizationString(is,"20190425T123154Z","ABC","123","us-east-1","lambda", AWSV4AuthOperations.HTTPRequestType.POST, "lambda.us-east-1.amazonaws.com" ,"/2015-03-31/functions/helloAWS/invocations",""));

    }
}
