# Dropwizard-Guicier
## About
This is a fork of [HubSpot/dropwizard-guicier](https://github.com/HubSpot/dropwizard-guicier) with following modifications:

  * Reduction in dependency footprint
  * Reduction in inter-dependency conflicts (hence, lesser chances of [jar hell](https://dzone.com/articles/what-is-jar-hell))
  * Compatilibity with latest versions of Dropwizard

## Usage
Add this library (`dropwizard-guicier`) as dependency:

```xml
<dependencies>
    <dependency>
        <groupId>com.flipkart.utils</groupId>
        <artifactId>dropwizard-guicier</artifactId>
        <version>${dropwizard-guicier-version}</version>
    </dependency>
</dependencies>
```
As of now, following versions of `${dropwizard-guicier-version}` are available in Maven Central Artifactory:

| Dropwizard version | `dropwizard-guicier-version` |
|--------------------|----------------------------------------|
| 1.3.17   | [1.3.17-1](https://repo1.maven.org/maven2/com/flipkart/utils/dropwizard-guicier/1.3.17-1/dropwizard-guicier-1.3.17-1.pom)     |

If you have a version of Dropwizard that isn't listed here, you can build one yourself within minutes.


