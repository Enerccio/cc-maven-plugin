# Contents Checker Maven plugin

`contents-checker-maven-plugin` is plugin that will check if required files are inside the result `jar`/`war`. 

I made this plugin because sometimes, especially when run inside eclipse, maven will forget to include non class files into final jar/war, making it unusable. This plugin simply checks whether result jar/war file contains specific files.

# Parameters
| Parameter | Description |
| ------ | ------ |
| `maven.contents-checker.skip` | Skip this check, true for skipping, false (default) for not skipping|
| `mustContain` | List of files that jar/war must contain |
| `mustNotContain` | List of files that jar/war must not contain |

# Example configuration
```xml
	<plugin>
		<groupId>com.en-circle</groupId>
		<artifactId>contents-checker-maven-plugin</artifactId>
		<version>1.0.0</version>
		<configuration>
			<mustContain>
				<param>com/enerccio/test/App.class</param>
			</mustContain>
		</configuration>
		<executions>
			<execution>
				<phase>package</phase>
				<goals>
		       			<goal>cc</goal>
	       			</goals>
			</execution>
		</executions>
	</plugin>
```
