# AWS Signature Version 4 Connector - Mule 4.x

AWS Signature Version 4 is the process to add authentication information to AWS requests sent over HTTP. This connector allows you to build the signature to allow you to call an AWS service over HTTP. 

### Date Format
```
#[output application/json --- (now() >> "UTC") as DateTime  {format:"yyyyMMdd'T'HHmmss'Z'"}]
```

### HTTP Request Headers
```
#[output application/java
---
{
    "Authorization" : vars.authString,
    "Content-Type" : "application/json",
    "X-Amz-Date" : vars.xAmzDate ++ "",
    "Host" : "lambda.us-east-1.amazonaws.com"
}]
```