/**
 * Copyright Alex Objelean
 */
package ro.isdc.wro.maven.plugin;

import static junit.framework.Assert.assertTrue;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import ro.isdc.wro.util.WroUtil;


/**
 * Test the {@link JsHintMojo} class.
 * 
 * @author Alex Objelean
 */
public class TestJsHintMojo
    extends AbstractTestLinterMojo {
  /**
   * {@inheritDoc}
   */
  @Override
  protected AbstractSingleProcessorMojo newLinterMojo() {
    return new JsHintMojo() {
      @Override
      void onException(final Exception e) {
        Assert.fail("Shouldn't fail. Exception message: " + e.getMessage());
      }
    };
  }
  
  @Test
  public void usePredefOptions()
      throws Exception {
    getMojo().setOptions("predef=['YUI','window','document','OnlineOpinion','xui']");
    getMojo().setTargetGroups("undef");
    getMojo().execute();
  }
  
  @Test
  public void testMojoWithPropertiesSet()
      throws Exception {
    getMojo().setIgnoreMissingResources(true);
    getMojo().execute();
  }
  
  @Test
  public void testWroXmlWithInvalidResourcesAndIgnoreMissingResourcesTrue()
      throws Exception {
    setWroWithInvalidResources();
    getMojo().setIgnoreMissingResources(true);
    getMojo().execute();
  }
  
  @Test
  public void testResourceWithUndefVariables()
      throws Exception {
    getMojo().setTargetGroups("undef");
    getMojo().execute();
  }
  
  @Test
  public void testEmptyOptions()
      throws Exception {
    getMojo().setOptions("");
    getMojo().setTargetGroups("undef");
    getMojo().execute();
  }
  
  @Test
  public void shouldGenerateXmlReportFile()
      throws Exception {
    final File reportFile = WroUtil.createTempFile();
    try {
      ((JsHintMojo) getMojo()).setReportFile(reportFile);
      getMojo().setOptions("undef, browser");
      getMojo().setTargetGroups(null);
      getMojo().setFailNever(true);
      getMojo().setIgnoreMissingResources(true);
      getMojo().execute();
    } finally {
      //Assert that file is big enough to prove that it contains serialized errors.
      assertTrue(reportFile.length() > 1000);
      FileUtils.deleteQuietly(reportFile);
    }
  }
}
