package io.github.c_a_services.mule.maven;

import java.net.ProxySelector;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.TrackableBase;

/**
 * Overwrite the global proxy settings.
 *
 * Usage:
 *
 * mvn io.github.c-a-services.mule.maven:overwrite-proxy-setting-plugin:LATEST:proxy-overwrite
 * 
 * Inspired by https://github.com/volkertb/autoproxy-maven-plugin/blob/master/src/main/java/com/buisonje/AutoProxyMojo.java 
 * as user proxy cannot be set dynamically and sms has problems with http 503 after stub-creation.
 * See https://canda-services.atlassian.net/browse/SDI-1060
 *
 */
@Mojo(name = "proxy-overwrite")
public class OverwriteMavenProxyPlugin extends AbstractMojo {

	/**
	 * The Maven Settings.
	 */
	@Parameter(defaultValue = "${settings}", readonly = false)
	private Settings settings;

	@Parameter(property = "overwrite-proxyHost", required = false)
	private String proxyHost;

	@Parameter(property = "overwrite-proxyPort", required = false)
	private Integer proxyPort;

	@Parameter(property = "overwrite-proxyUser", required = false)
	private String proxyUser;

	@Parameter(property = "overwrite-proxyPassword", required = false)
	private String proxyPassword;

	@Parameter(property = "overwrite-nonProxyHosts", required = false)
	private String nonProxyHosts;

	/**
	 * Fast skip all activities of this plugin via -Doverwrite.proxy.skip=true
	 */
	@Parameter(property = "overwrite.proxy.skip", required = false)
	private boolean skip;

	@Override
	public void execute() {
		if (skip) {
			getLog().info("overwrite-proxy-setting-plugin is skipped");
			return;
		}
		getLog().debug("proxy-overwrite...");

		showMavenProxySettings(getLog());
		overrideMavenProxySettings(getLog());
		overrideJavaProxySettings(getLog());
		showMavenProxySettings(getLog());

		getLog().debug("proxy-overwrite finished...");
	}

	private void showMavenProxySettings(final Log mojoLog) {
		final org.apache.maven.settings.Proxy manuallyConfiguredActiveMavenProxy = settings.getActiveProxy();

		mojoLog.info("settings.getActiveProxy():");
		if (manuallyConfiguredActiveMavenProxy != null) {
			mojoLog.info("  * ID      : " + manuallyConfiguredActiveMavenProxy.getId());
			mojoLog.info("  * Hostname: " + manuallyConfiguredActiveMavenProxy.getHost());
			mojoLog.info("  * Port    : " + manuallyConfiguredActiveMavenProxy.getPort());
			mojoLog.info("  * Protocol: " + manuallyConfiguredActiveMavenProxy.getProtocol());
			mojoLog.info("  * NonProxy: " + manuallyConfiguredActiveMavenProxy.getNonProxyHosts());
			mojoLog.info("  * SourceLevel: " + manuallyConfiguredActiveMavenProxy.getSourceLevel());
			mojoLog.info("  * ProxySelector: " + ProxySelector.getDefault());
			mojoLog.info("  * https.proxyHost: " + System.getProperty("https.proxyHost"));
			mojoLog.info("  * https.proxyPort: " + System.getProperty("https.proxyPort"));
			mojoLog.info("  * http.nonProxyHosts: " + System.getProperty("http.nonProxyHosts"));
		} else {
			mojoLog.info("  * : No currently active proxy found.");
		}
	}

	/**
	* Override the Maven proxy settings with the first available (actual)
	* Proxy.
	* 
	* @param overridingProxy
	*            The {@link Proxy} to use instead of whatever is configured in
	*            the initial Maven settings (settings.xml).
	*/
	private void overrideMavenProxySettings(Log mojoLog) {

		org.apache.maven.settings.Proxy mavenProxy = settings.getActiveProxy();

		if (proxyHost == null) {
			mojoLog.info("Requested: no-proxy");
			if (mavenProxy == null) {
				mojoLog.info("Maven currently has no proxy, all fine");
			} else {
				mojoLog.info("The detected proxy configuration is a direct connection. Overriding active proxy configured in Maven settings...");
				/*
				 * There can be only one active proxy in the Maven settings at a
				 * time, so if the currently active one is set to inactive, that
				 * should imply a direct connection.
				 */
				mavenProxy.setActive(false);
			}
		} else {
			mojoLog.info("Requested: proxy " + proxyHost + ":" + proxyPort + " excluding " + nonProxyHosts);
			if (mavenProxy != null) {
				mojoLog.info("Disable current proxy");
				mavenProxy.setActive(false);
			}
			mojoLog.info("Added new proxy");
			mavenProxy = new org.apache.maven.settings.Proxy();
			mavenProxy.setId("created-by-" + this.getClass().getName() + "-" + System.currentTimeMillis());
			mavenProxy.setActive(true);
			mavenProxy.setSourceLevel(TrackableBase.USER_LEVEL);
			settings.getProxies().add(mavenProxy);

			mavenProxy.setHost(proxyHost);
			mavenProxy.setPort(proxyPort);
			mavenProxy.setNonProxyHosts(nonProxyHosts);
			mavenProxy.setProtocol("https");
			mavenProxy.setUsername(proxyUser);
			mavenProxy.setPassword(proxyPassword);
		}

		// force maven scanning org.apache.maven.settings.Settings.proxies again.
		settings.flushActiveProxy();
	}

	/**
	 * Overwrite for 
	 * 12:00:23      at org.apache.http.impl.client.InternalHttpClient.doExecute (InternalHttpClient.java:185)
	 * 12:00:23      at org.apache.http.impl.client.CloseableHttpClient.execute (CloseableHttpClient.java:83)
	 * 12:00:23      at org.apache.maven.wagon.shared.http.AbstractHttpClientWagon.execute (AbstractHttpClientWagon.java:1005)
	 * as well
	 */
	private void overrideJavaProxySettings(Log aLog) {
		ProxySelector tempDefaultProxySelector = new OverwriteProxySelector().withHost(proxyHost).withPort(proxyPort).withNonProxyHosts(nonProxyHosts)
				.withUser(proxyUser).withPassword(proxyPassword);
		ProxySelector.setDefault(tempDefaultProxySelector);

		// https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html
		if (proxyHost == null) {
			System.clearProperty("https.proxyHost");
			System.clearProperty("https.proxyPort");
			System.clearProperty("http.nonProxyHosts");
		} else {
			System.setProperty("https.proxyHost", proxyHost);
			System.setProperty("https.proxyPort", "" + proxyPort);
			System.setProperty("http.nonProxyHosts", nonProxyHosts);
		}

		aLog.info("Replaced " + ProxySelector.class.getName() + " with own settings.");
	}

}