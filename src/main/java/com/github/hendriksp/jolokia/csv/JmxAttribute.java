package com.github.hendriksp.jolokia.csv;

import java.io.Serializable;
import java.util.Iterator;

import javax.management.MalformedObjectNameException;

import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pResponse;

public class JmxAttribute implements Serializable {
    private static final long serialVersionUID = 1L;

    private String columnName;
    private String beanPath;
    private String attributeName;

    public String getColumnName() {
        if (columnName == null)
            return attributeName;
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getBeanPath() {
        return beanPath;
    }

    public void setBeanPath(String beanPath) {
        this.beanPath = beanPath;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public J4pReadRequest createRequest() throws MalformedObjectNameException {
        return new J4pReadRequest(getBeanPath(), getAttributeName());
    }

    public Object extractValue(Iterator<J4pResponse<J4pReadRequest>> iterator) {
        if (!iterator.hasNext())
            throw new IllegalStateException();
        return iterator.next().getValue();
    }
}