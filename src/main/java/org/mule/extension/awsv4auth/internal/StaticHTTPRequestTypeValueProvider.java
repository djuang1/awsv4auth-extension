package org.mule.extension.awsv4auth.internal;

import org.mule.runtime.api.value.Value;
import org.mule.runtime.extension.api.values.*;

import java.util.Set;

public class StaticHTTPRequestTypeValueProvider implements ValueProvider {

    @Override
    public Set<Value> resolve() throws ValueResolvingException {
        return ValueBuilder.getValuesFor("POST", "GET", "PUT", "PATCH", "DELETE");
    }
}
