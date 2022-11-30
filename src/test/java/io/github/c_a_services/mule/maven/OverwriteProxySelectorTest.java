package io.github.c_a_services.mule.maven;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

/**
 * 
 */
public class OverwriteProxySelectorTest {

	@Test
	public void testGoogleIsProxy() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com").withPort(3128)
				.withNonProxyHosts("*.canda.com|*.canda.biz");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(new URL("https://www.google.de").toURI());
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals("proxy.canda.com:3128", tempProxy.get(0).address().toString());
	}

	@Test
	public void testTopLevelDomainIsProxy() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com").withPort(3128)
				.withNonProxyHosts("*.canda.com|*.canda.biz");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(new URL("https://www.canda.de").toURI());
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals("proxy.canda.com:3128", tempProxy.get(0).address().toString());
	}

	@Test
	public void testNonProxyMatchesFirst() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com").withPort(3128)
				.withNonProxyHosts("*.canda.com|*.canda.biz");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(new URL("https://www.canda.com").toURI());
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals(Proxy.NO_PROXY, tempProxy.get(0));
	}

	@Test
	public void testNonProxyMatchesSecond() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com").withPort(3128)
				.withNonProxyHosts("*.canda.com|*.canda.biz");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(new URL("https://www.canda.biz").toURI());
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals(Proxy.NO_PROXY, tempProxy.get(0));
	}

}
