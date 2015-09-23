package com.github.hendriksp.jolokia.csv;

import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    private static final String DEFAULT_CONFIG = "config.xml";
    private static final String DEFAULT_INTERVAL = "500";

    private static final String CONFIG = "c";
    private static final String INTERVAL = "i";
    private static final String OUTPUT = "o";
    private static final String PASSWORD = "p";
    private static final String URL = "s";
    private static final String USER = "u";


    public static void main(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption(OUTPUT, "output", true, "The output file (default: stdout)");
        options.addOption(CONFIG, "config", true, "The config file (default: config.xml)");
        options.addOption(USER, "user", true, "The jolokia user");
        options.addOption(PASSWORD, "password", true, "The jolokia password");
        options.addOption(INTERVAL, "interval", true, "Fetch interval in ms (default: 500)");
        options.addOption(Option.builder(URL).longOpt("url").desc("The URL for the jolokia service.").hasArg().required().build());

        try {
            final CommandLine commandLine = new DefaultParser().parse(options, args);

            final String user = commandLine.getOptionValue(USER);
            final String password = commandLine.getOptionValue(PASSWORD);
            final String url = commandLine.getOptionValue(URL);
            final String configFilename = commandLine.getOptionValue(CONFIG, DEFAULT_CONFIG);
            final String outputFilename = commandLine.getOptionValue(OUTPUT);
            final int interval = Integer.valueOf(commandLine.getOptionValue(INTERVAL, DEFAULT_INTERVAL));

            final JolokiaGatherer gatherer;

            final Appendable writer = outputFilename == null ? System.out : Files.newBufferedWriter(Paths.get(outputFilename), Charset.forName("UTF-8"));

            final Reader configReader = Files.newBufferedReader(Paths.get(configFilename), Charset.forName("UTF-8"));

            gatherer = new JolokiaGatherer(user, password, url, configReader, writer, interval);

            gatherer.run();
        } catch (ParseException e) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Main", options);
        }
    }
}
