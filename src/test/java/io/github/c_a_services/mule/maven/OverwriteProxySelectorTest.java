package io.github.c_a_services.mule.maven;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

/**
 * 
 */
public class OverwriteProxySelectorTest {

	@Test
	public void testGoogleIsProxy() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com")
				.withPort(3128)
				.withNonProxyHosts("*.dachser.com|*.dachser.biz").withProtocol("http");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(URI.create("https://www.google.de"));
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals("proxy.canda.com", ((InetSocketAddress) tempProxy.getFirst().address()).getHostName());
		assertEquals(3128, ((InetSocketAddress) tempProxy.getFirst().address()).getPort());
	}

	@Test
	public void testTopLevelDomainIsProxy() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com")
				.withPort(3128)
				.withNonProxyHosts("*.canda.com|*.canda.biz");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(URI.create("https://www.canda.de"));
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals("proxy.canda.com", ((InetSocketAddress) tempProxy.getFirst().address()).getHostName());
		assertEquals(3128, ((InetSocketAddress) tempProxy.getFirst().address()).getPort());
	}

	@Test
	public void testNonProxyMatchesFirst() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com")
				.withPort(3128)
				.withNonProxyHosts("*.canda.com|*.canda.biz");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(URI.create("https://www.canda.com"));
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals(Proxy.NO_PROXY, tempProxy.getFirst());
	}

	@Test
	public void testNonProxyMatchesSecond() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com")
				.withPort(3128)
				.withNonProxyHosts("*.canda.com|*.canda.biz");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(URI.create("https://www.canda.biz"));
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals(Proxy.NO_PROXY, tempProxy.get(0));
	}

	@Test
	public void testGoogleIsHttpProxy() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com")
				.withPort(3128)
				.withNonProxyHosts("*.dachser.com|*.dachser.biz").withProtocol("http");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(URI.create("https://www.google.de"));
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals("proxy.canda.com", ((InetSocketAddress) tempProxy.getFirst().address()).getHostName());
		assertEquals(3128, ((InetSocketAddress) tempProxy.getFirst().address()).getPort());
	}

	@Test
	public void testGoogleIsHttpsProxy() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com")
				.withPort(3128)
				.withNonProxyHosts("*.dachser.com|*.dachser.biz").withProtocol("https");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(URI.create("https://www.google.de"));
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals("proxy.canda.com", ((InetSocketAddress) tempProxy.getFirst().address()).getHostName());
		assertEquals(3128, ((InetSocketAddress) tempProxy.getFirst().address()).getPort());
	}

	@Test
	public void testTopLevelDomainIsHttpProxy() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com")
				.withPort(3128)
				.withNonProxyHosts("*.canda.com|*.canda.biz").withProtocol("http");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(URI.create("https://www.canda.de"));
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals("proxy.canda.com", ((InetSocketAddress) tempProxy.getFirst().address()).getHostName());
		assertEquals(3128, ((InetSocketAddress) tempProxy.getFirst().address()).getPort());
		assertEquals(OverwriteProxySelector.class, ProxySelector.getDefault().getClass());
	}

	@Test
	public void testTopLevelDomainIsHttpsProxy() throws MalformedURLException, URISyntaxException {
		OverwriteProxySelector tempOverwriteProxySelector = new OverwriteProxySelector().withHost("proxy.canda.com")
				.withPort(3128)
				.withNonProxyHosts("*.canda.com|*.canda.biz").withProtocol("https");
		List<Proxy> tempProxy = tempOverwriteProxySelector.select(URI.create("https://www.canda.de"));
		assertEquals("Expected on proxy entry " + tempProxy, 1, tempProxy.size());
		assertEquals("proxy.canda.com", ((InetSocketAddress) tempProxy.getFirst().address()).getHostName());
		assertEquals(3128, ((InetSocketAddress) tempProxy.getFirst().address()).getPort());
		assertEquals(OverwriteProxySelector.class, ProxySelector.getDefault().getClass());
	}

}
