package com.github.hendriksp.jolokia.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MalformedObjectNameException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pResponse;

import com.thoughtworks.xstream.XStream;

public class JolokiaGatherer {
    private static final String END_OF_LINE = System.getProperty("line.separator", "\n");

    private final String user;
    private final String password;
    private final String url;

    private final Reader configReader;
    private final Appendable outputWriter;
    private final long interval;

    private J4pClient client;

    private List<JmxAttribute> attributes;

    public JolokiaGatherer(String user, String password, String url, Reader configReader, Appendable outputWriter, long interval) {
        this.user = user;
        this.password = password;
        this.url = url;
        this.configReader = configReader;
        this.outputWriter = outputWriter;
        this.interval = interval;
    }

    public void run() throws Exception {
        createJolokiaClient();

        attributes = readConfig();

        try (final CSVPrinter printer = createCsvPrinter()) {
            while (true) {
                final Iterable<?> row = fetchData();
                printer.printRecord(row);
                printer.flush();
                Thread.sleep(interval);
            }
        }
    }

    private void createJolokiaClient() {
        client = J4pClient
                .url(url)
                .user(user)
                .password(password)
                .pooledConnections()
                .build();
    }

    private Iterable<?> fetchData() throws MalformedObjectNameException, J4pException {
        final List<J4pReadRequest> requests = getRequests();
        final List<J4pResponse<J4pReadRequest>> response = client.execute(requests);
        return toValues(response);
    }

    private List<J4pReadRequest> getRequests() throws MalformedObjectNameException {
        final List<J4pReadRequest> answer = new ArrayList<>(attributes.size());
        for (JmxAttribute a : attributes) {
            answer.add(a.createRequest());
        }
        return answer;
    }

    private Iterable<?> toValues(List<J4pResponse<J4pReadRequest>> responses) {
        final ArrayList<Object> answer = new ArrayList<>(responses.size());
        final Iterator<J4pResponse<J4pReadRequest>> iterator = responses.iterator();
        for (JmxAttribute a : attributes) {
            answer.add(a.extractValue(iterator));
        }
        return answer;
    }

    private CSVPrinter createCsvPrinter() throws IOException {
        final CSVFormat format = CSVFormat.newFormat(',')
                .withRecordSeparator(END_OF_LINE)
                .withHeader(getColumnNames());
        return new CSVPrinter(outputWriter, format);
    }

    private String[] getColumnNames() {
        final Set<String> dupCheck = new HashSet<>(attributes.size());
        final List<String> answer = new ArrayList<>(attributes.size());
        for (JmxAttribute a : attributes) {
            final String name = a.getColumnName();
            if (!dupCheck.add(name)) {
                throw new IllegalArgumentException("Duplicate column name: " + name);
            }
            answer.add(name);
        }
        return answer.toArray(new String[0]);
    }

    private List<JmxAttribute> readConfig() throws IOException {
        final XStream xStream = new XStream();
        @SuppressWarnings("unchecked")
        final List<JmxAttribute> attributes = (List<JmxAttribute>) xStream.fromXML(configReader);
        return attributes;
    }
}
