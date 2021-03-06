# Amazon AWS Signature Version 4 Connector - Mule 4.x

[Amazon AWS Signature Version 4](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html) is the process to add authentication information to AWS requests sent over HTTP. This connector allows you to build the signature to allow you to call an AWS service over HTTP. 

<img src="https://github.com/djuang1/djuang1.github.io/blob/master/img/aws-sig-v4/aws-sig-v4-config.png?raw=true" width="600px">

The diagram below shows the process to create the signature.

<img src="https://docs.aws.amazon.com/AmazonS3/latest/API/images/sigV4-using-auth-header.png">


### Exmple Project

An example project can be found [here](https://github.com/djuang1/aws-sig-v4-example-mule4)

> :exclamation: **This connector has been created and provided free of charge to the MuleSoft developer community. While issues can be reported, there is no guarantee for support.**

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

### Instructions

1.  Clone the Repo
2.  Change the pom.xml groupId to match your organization id in your Anypoint Platform organization
3.  Modify your Maven settings.xml file and add the following server
```
<server>
    <id>exchange-server</id>
    <username>YOUR_ANYPOINT_PLATFORM_ID</username>
    <password>YOUR_ANYPOINT_PLATFORM_PASSWORD</password>
</server>
```
4.  Deploy the connector to your [Exchange using Maven](https://docs.mulesoft.com/exchange/to-publish-assets-maven):  mvn clean -DskipTests deploy
5.  Consume connector in Anypoint Studio by downloading from Exchange.

## Updates

Last Updated September 14, 2020


## Reporting Issues

You can report new issues at this link https://github.com/djuang1/awsv4auth-extension/issues.