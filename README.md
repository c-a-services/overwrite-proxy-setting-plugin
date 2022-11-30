# overwrite-proxy-settings-plugin

Keep your UserProfile/.m2/settings.xml clean without `<proxy>` it can be set dynamically with this plugin.


Usage:

```
	<build>
		<plugins>
			<plugin>
				<groupId>io.github.c-a-services.mule.maven</groupId>
				<artifactId>overwrite-proxy-setting-plugin</artifactId>
				<version>${version.overwrite-proxy-setting-plugin}</version>
				<executions>
					<execution>
						<id>overwrite-proxy</id>
						<phase>clean</phase>
						<goals>
							<goal>proxy-overwrite</goal>
						</goals>
						<configuration>
							<proxyHost>${cloudhub2.proxyHost}</proxyHost>
							<proxyPort>${cloudhub2.proxyPort}</proxyPort>
							<nonProxyHosts>${cloudhub2.nonProxyHosts}</nonProxyHosts>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
```

or place the config in a profile.

Commandline attribtes needs to be prefixed to the configuration ones with "overwrite.":

```
-Doverwrite.proxyHost=yourValue
-Doverwrite.proxyPort=yourValue
-Doverwrite.nonProxyHosts=yourValue
-Doverwrite.proxyUser=yourValue
-Doverwrite.proxyPassword=yourValue
```

nonProxyHosts has same syntax as UserProfile/.m2/settings.xml

For auto-detection of proxy you may want to look to <https://github.com/volkertb/autoproxy-maven-plugin/>

Fast skip all activities of this plugin via `-Doverwrite.proxy.skip=true`

---
This readme is placed here: <https://github.com/c-a-services/overwrite-proxy-setting-plugin>