/**
 *
 */
package ro.isdc.wro.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test {@link WroUtil} class.
 *
 * @author Alex Objelean
 */
public class TestWroUtil {
  @Test(expected = IllegalArgumentException.class)
  public void cannotComputeEmptyLocation() {
    WroUtil.getPathInfoFromLocation(mockContextPathRequest(null), "");
  }

  @Test
  public void computePathFromSomeLocation() {
    final String result = WroUtil.getPathInfoFromLocation(mockContextPathRequest(null), "location");
    assertEquals("", result);
  }

  @Test
  public void computePathFromNestedLocation() {
    final String result = WroUtil.getPathInfoFromLocation(mockContextPathRequest(null), "/a/b/c/d");
    assertEquals("/b/c/d", result);
  }

  @Test
  public void computePathFromLocationWithContextRoot() {
    final String result = WroUtil.getPathInfoFromLocation(mockContextPathRequest("/a"), "/a/b/c/d");
    assertEquals("/b/c/d", result);
  }

  @Test
  public void computePathFromLocationWithDifferentContextRoot() {
    final String result = WroUtil.getPathInfoFromLocation(mockContextPathRequest("/z"), "/a/b/c/d");
    assertEquals("/a/b/c/d", result);
  }

  @Test
  public void computeServletPathFromLocation() {
    final String result = WroUtil.getServletPathFromLocation(mockContextPathRequest(null), "/a/b/c/d");
    assertEquals("/a", result);
  }

  /**
   * Test for several mangled header examples based on {@link http
   * ://developer.yahoo.com/blogs/ydn/posts/2010/12/pushing-beyond-gzipping/} blog post.
   */
  @Test
  public void testGzipSupport()
      throws Exception {
    HttpServletRequest request = mockRequestHeader("", "");
    assertFalse(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("Accept-Encoding", "");
    assertFalse(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("Accept-Encoding", "gzip, deflate");
    assertTrue(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("Accept-Encoding", "XYZ");
    assertFalse(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("Accept-EncodXng", "XXXXXXXXXXXXX");
    assertTrue(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("X-cept-Encoding", "gzip,deflate");
    assertTrue(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("XXXXXXXXXXXXXXX", "XXXXXXXXXXXXX");
    assertTrue(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("XXXXXXXXXXXXXXXX", "gzip, deflate");
    assertFalse(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("---------------", "-------------");
    assertTrue(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("~~~~~~~~~~~~~~~", "~~~~~~~~~~~~~");
    assertTrue(WroUtil.isGzipSupported(request));

    request = mockRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
    assertTrue(WroUtil.isGzipSupported(request));
  }

  /**
   * @param request
   * @param headerName
   * @param headerValue
   */
  private HttpServletRequest mockRequestHeader(final String headerName, final String headerValue) {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    final Enumeration<String> enumeration = Collections.enumeration(Arrays.asList(headerName));
    Mockito.when(request.getHeaderNames()).thenReturn(enumeration);
    Mockito.when(request.getHeader(headerName)).thenReturn(headerValue);
    return request;
  }

  private HttpServletRequest mockContextPathRequest(final String contextPath) {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getContextPath()).thenReturn(contextPath);
    return request;
  }

  @Test
  public void testToJsMultilineString() {
    assertEquals("[\"\\n\"].join(\"\\n\")", WroUtil.toJSMultiLineString(""));
    assertEquals("[\"alert1\\n\"].join(\"\\n\")", WroUtil.toJSMultiLineString("alert1"));
    assertEquals("[\"\",\"alert1\",\"alert2\"].join(\"\\n\")", WroUtil.toJSMultiLineString("\nalert1\nalert2"));
  }

  @Test
  public void shouldMatchUrl() {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getRequestURI()).thenReturn("wroApi/test");

    assertTrue(WroUtil.matchesUrl(request, "wroApi/test"));
  }

  @Test
  public void shouldNotMatchUrl() {
    final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getRequestURI()).thenReturn("someresource.css");

    assertFalse(WroUtil.matchesUrl(request, "wroApi/test"));
  }

  @Test
  public void shouldCreateMultiLineFromNullString() {
    assertEquals("[].join(\"\\n\")", WroUtil.toJSMultiLineString(null));
  }
}
