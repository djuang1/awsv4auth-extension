package org.mule.extension.awsv4auth.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

@Operations(AWSV4AuthOperations.class)
@ConnectionProviders(AWSV4AuthConnectionProvider.class)
public class AWSV4AuthConfiguration {

}
