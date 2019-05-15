package org.mule.extension.awsv4auth.internal;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

@Xml(prefix = "awsv4auth")
@Extension(name = "AWS Signature V4")
@Configurations(AWSV4AuthConfiguration.class)
public class AWSV4AuthExtension {

}
