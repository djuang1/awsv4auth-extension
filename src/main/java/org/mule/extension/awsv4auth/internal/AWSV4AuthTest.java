package org.mule.extension.awsv4auth.internal;

public class AWSV4AuthTest {

    public static void main(String[] args) {

        AWSV4AuthOperations testOperation = new AWSV4AuthOperations();

        System.out.println(testOperation.getAuthorizationString("    { \"first_name\": \"Bill\", \"last_name\": \"Smith\", \"test\": \"test\" }  ","20190425T123154Z","***","***","us-east-1","lambda", "lambda.us-east-1.amazonaws.com" ,"/2015-03-31/functions/helloAWS/invocations",""));

    }
}
