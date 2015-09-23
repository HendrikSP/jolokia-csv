package com.github.hendriksp.jolokia.csv;

import java.util.Iterator;

import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pResponse;
import org.json.simple.JSONObject;

public class JmxPropertyAttribute extends JmxAttribute {
    private static final long serialVersionUID = 1L;

    private String propertyName;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public Object extractValue(Iterator<J4pResponse<J4pReadRequest>> iterator) {
        if (!iterator.hasNext())
            throw new IllegalStateException();
        final JSONObject o = (JSONObject) iterator.next().getValue();
        return o.get(getPropertyName());
    }
}