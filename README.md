# Dropwizard-Guicier
## About
This is a fork of [HubSpot/dropwizard-guicier](https://github.com/HubSpot/dropwizard-guicier) with following modifications:

  * Significant reduction in dependency footprint
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
| 1.3.5   | [1.3.5-2](https://mvnrepository.com/artifact/com.flipkart.utils/dropwizard-guicier/1.3.5-2)|
| 1.3.17   | [1.3.17-1](https://mvnrepository.com/artifact/com.flipkart.utils/dropwizard-guicier/1.3.17-1)|
| 1.3.18   | [1.3.18-1](https://mvnrepository.com/artifact/com.flipkart.utils/dropwizard-guicier/1.3.18-1)|


If you have a version of Dropwizard that isn't listed here, [raise an issue](//github.com/flipkart-incubator/dropwizard-guicier/issues) or you can build one yourself.

## Related links
 * [dropwizard-guicier on mvnrepository](https://mvnrepository.com/artifact/com.flipkart.utils/dropwizard-guicier)
 * [dropwizard-guicier on Maven Central](https://search.maven.org/artifact/com.flipkart.utils/dropwizard-guicier)
