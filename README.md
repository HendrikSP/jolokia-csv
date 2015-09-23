# jolokia-csv

Connect to a jolokia (https://jolokia.org/) service and prints specifiable attributes to an cvs in a given interval.


## Usage

```
usage: Main
 -c,--config <arg>     The config file (default: config.xml)
 -i,--interval <arg>   Fetch interval in ms (default: 500)
 -o,--output <arg>     The output file (default: stdout)
 -p,--password <arg>   The jolokia password
 -s,--url <arg>        The URL for the jolokia service.
 -u,--user <arg>       The jolokia user
```

## Build

```
mvn clean install
```

## Example

```
java -jar target/jolokia-csv-<version>-app.jar -s http://localhost:8181
```

## Config file examlple

```
<?xml version="1.0" encoding="UTF-8"?>
<list>
  <com.github.hendriksp.jolokia.csv.JmxAttribute>
    <beanPath>java.lang:type=Runtime</beanPath>
    <attributeName>Uptime</attributeName>
  </com.github.hendriksp.jolokia.csv.JmxAttribute>
  <com.github.hendriksp.jolokia.csv.JmxPropertyAttribute>
    <columnName>NonHeapMemoryUsage.committed</columnName>
    <beanPath>java.lang:type=Memory</beanPath>
    <attributeName>NonHeapMemoryUsage</attributeName>
    <propertyName>committed</propertyName>
  </com.github.hendriksp.jolokia.csv.JmxPropertyAttribute>
</list>
```
