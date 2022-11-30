package io.github.c_a_services.mule.maven;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class OverwriteProxySelector extends ProxySelector {

	private static final Logger LOGGER = LoggerFactory.getLogger(OverwriteProxySelector.class);

	private String proxyHost;

	private Integer proxyPort;

	private String proxyUser;

	private byte[] proxyPassword;

	private String nonProxyHosts;

	public OverwriteProxySelector() {
		super();
		LOGGER.debug("Created {}", this);
	}

	/**
	 * 
	 */
	@Override
	public void connectFailed(URI aUri, SocketAddress aSocketAddress, IOException aIOException) {
		LOGGER.error("connectFailed aUri=" + aUri + " aSocketAddress=" + aSocketAddress, aIOException);
	}

	/**
	 * 
	 */
	@Override
	public List<Proxy> select(URI aUri) {
		List<Proxy> tempProxies = new ArrayList<>();
		if (proxyHost == null) {
			tempProxies.add(java.net.Proxy.NO_PROXY);
		} else {
			if (isNonProxyHost(aUri, nonProxyHosts)) {
				tempProxies.add(java.net.Proxy.NO_PROXY);
			} else {
				Proxy tempProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
				tempProxies.add(tempProxy);
			}
		}
		LOGGER.debug("Proxies for aUri={} tempProxies={}", aUri, tempProxies);
		return tempProxies;
	}

	/**
	 * 
	 */
	private boolean isNonProxyHost(URI aUri, String aNonProxyHosts) {
		if (aNonProxyHosts == null) {
			return false;
		}
		String[] tempSplitted = aNonProxyHosts.split("|");
		String tempExternalForm;
		try {
			tempExternalForm = aUri.toURL().toExternalForm();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Error with URI=" + aUri, e);
		}
		for (String tempString : tempSplitted) {
			String tempRegEx = tempString.replaceAll("*", ".*");
			if (tempExternalForm.matches(tempRegEx)) {
				LOGGER.debug("Detected nonproxy with tempString={} (tempRegEx={}) for tempExternalForm={}", tempString, tempRegEx, tempExternalForm);
			}
		}
		return false;
	}

	/**
	 * 
	 */
	public OverwriteProxySelector withHost(String aProxyHost) {
		proxyHost = aProxyHost;
		return this;
	}

	/**
	 * 
	 */
	public OverwriteProxySelector withPort(Integer aProxyPort) {
		proxyPort = aProxyPort;
		return this;
	}

	/**
	 * 
	 */
	public OverwriteProxySelector withUser(String aProxyUser) {
		proxyUser = aProxyUser;
		return this;
	}

	/**
	 * 
	 */
	public OverwriteProxySelector withPassword(String aProxyPassword) {
		proxyPassword = aProxyPassword.getBytes();
		return this;
	}

	/**
	 * 
	 */
	public OverwriteProxySelector withNonProxyHosts(String aNonProxyHosts) {
		nonProxyHosts = aNonProxyHosts;
		return this;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "OverwriteProxySelector [proxyHost=" + proxyHost + ", proxyPort=" + proxyPort + ", proxyUser=" + proxyUser + ", nonProxyHosts=" + nonProxyHosts
				+ "]";
	}

}
