package org.mule.extension.awsv4auth.internal;

import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;

public class AWSV4AuthConnectionProvider implements PoolingConnectionProvider<AWSV4AuthConnection> {

  @Override
  public AWSV4AuthConnection connect() {
    return new AWSV4AuthConnection();
  }

  @Override
  public void disconnect(AWSV4AuthConnection connection) {

  }

  @Override
  public ConnectionValidationResult validate(AWSV4AuthConnection connection) {
    return ConnectionValidationResult.success();
  }
}
