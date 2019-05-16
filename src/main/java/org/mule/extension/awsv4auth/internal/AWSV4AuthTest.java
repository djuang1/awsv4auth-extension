package org.mule.extension.awsv4auth.internal;

public class AWSV4AuthTest {

    public static void main(String[] args) {

        AWSV4AuthOperations testOperation = new AWSV4AuthOperations();

        System.out.println(testOperation.getAuthorizationString("    { \"first_name\": \"Bill\" }  ","20190425T123154Z","ABC","123","us-east-1","lambda", "lambda.us-east-1.amazonaws.com" ,"/2015-03-31/functions/helloAWS/invocations",""));

    }
}
