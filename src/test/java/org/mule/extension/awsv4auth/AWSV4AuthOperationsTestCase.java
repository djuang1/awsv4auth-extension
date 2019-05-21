package org.mule.extension.awsv4auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.junit.Test;

public class AWSV4AuthOperationsTestCase extends MuleArtifactFunctionalTestCase {

  /**
   * Specifies the mule config xml with the flows that are going to be executed in the tests, this file lives in the test resources.
   */
  @Override
  protected String getConfigFile() {
    return "test-mule-config.xml";
  }

  @Test
  public void executeGetAuthorizationStringOperation() throws Exception {
    String payloadValue = ((String) flowRunner("getAuthorizationStringFlow").run()
                                      .getMessage()
                                      .getPayload()
                                      .getValue());
    assertThat(payloadValue, is("AWS4-HMAC-SHA256 Credential=ABC/20190521/us-east-1/lambda/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=b59075ac4e5e70e0c36e1dd33bc64f470fcca672d650908db1c96898a9ce21e7"));
  }

}
