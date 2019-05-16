package org.mule.extension.awsv4auth.internal;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.license.RequiresEnterpriseLicense;

import static org.mule.runtime.api.meta.Category.CERTIFIED;

@Xml(prefix = "awsv4auth")
@Extension(name = "AWS Signature V4",  category = CERTIFIED, vendor = "Dejim Juang")
@RequiresEnterpriseLicense(allowEvaluationLicense = true)
@Configurations(AWSV4AuthConfiguration.class)
public class AWSV4AuthExtension {

}
