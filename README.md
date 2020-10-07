# Dropwizard-Guicier (Dropwizard with Guice)
## About
This is a fork of [HubSpot/dropwizard-guicier](https://github.com/HubSpot/dropwizard-guicier) and [Squarespace/jersey2-guice](https://github.com/Squarespace/jersey2-guice) with following improvements:

  * Significant reduction in dependency footprint
  * Reduction in inter-dependency conflicts (hence, lesser chances of [jar hell](https://dzone.com/articles/what-is-jar-hell))
  * Compatibility with latest versions of Dropwizard

## Usage
Add this library as dependency:

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

| Dropwizard version | Guice version | `dropwizard-guicier-version` |
|--------------------|---------------|------------------------------|
| 1.3.5   | 4.2.2 | [1.3.5-2](https://mvnrepository.com/artifact/com.flipkart.utils/dropwizard-guicier/1.3.5-2)|
| 1.3.17  | 4.2.2 | [1.3.17-1](https://mvnrepository.com/artifact/com.flipkart.utils/dropwizard-guicier/1.3.17-1)|
| 1.3.18  | 4.2.2 | [1.3.18-1](https://mvnrepository.com/artifact/com.flipkart.utils/dropwizard-guicier/1.3.18-1)|
| 1.3.19  | 4.2.2 | [1.3.19-1](https://mvnrepository.com/artifact/com.flipkart.utils/dropwizard-guicier/1.3.19-1)|

If you have a version of Dropwizard or Guice that isn't listed here, [raise an issue](//github.com/flipkart-incubator/dropwizard-guicier/issues) or you can build one yourself.

Simply install a new instance of the bundle during your service initialization:

```java
public class ExampleApplication extends Application<ExampleConfiguration> {

  public static void main(String... args) throws Exception {
    new ExampleApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
    GuiceBundle<ExampleConfiguration> guiceBundle = GuiceBundle.defaultBuilder(ExampleConfiguration.class)
        .modules(new ExampleModule())
        .build();

    bootstrap.addBundle(guiceBundle);
  }

  @Override
  public void run(ExampleConfiguration configuration, Environment environment) throws Exception {}
}
```

## Related links
 * [**Dropwizard with Guice** on mvnrepository](https://mvnrepository.com/artifact/com.flipkart.utils/dropwizard-guicier)
 * [**Dropwizard with Guice** on Maven Central](https://search.maven.org/artifact/com.flipkart.utils/dropwizard-guicier)
