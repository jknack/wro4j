/**
 * Copyright@2011 wro4j
 */
package ro.isdc.wro.runner;

import java.io.File;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.extensions.processor.css.CssLintProcessor;
import ro.isdc.wro.extensions.processor.css.YUICssCompressorProcessor;
import ro.isdc.wro.extensions.processor.js.JsHintProcessor;
import ro.isdc.wro.extensions.processor.support.csslint.CssLintException;
import ro.isdc.wro.extensions.processor.support.linter.LinterException;
import ro.isdc.wro.model.resource.processor.impl.css.CssMinProcessor;
import ro.isdc.wro.model.resource.processor.impl.css.CssUrlRewritingProcessor;
import ro.isdc.wro.model.resource.processor.impl.css.CssVariablesProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;
import ro.isdc.wro.model.resource.support.AbstractConfigurableMultipleStrategy;


/**
 * @author Alex Objelean
 */
public class TestWro4jCommandLineRunner {
  private static final Logger LOG = LoggerFactory.getLogger(TestWro4jCommandLineRunner.class);
  private File destinationFolder;

  @Before
  public void setUp() {
    destinationFolder = new File(FileUtils.getTempDirectory(), "wroTemp-" + new Date().getTime());
    destinationFolder.mkdir();
  }

  @After
  public void tearDown() {
    FileUtils.deleteQuietly(destinationFolder);
  }

  @Test
  public void cannotProcessWrongArgument()
      throws Exception {
    try {
      final String[] args = new String[] {
        "-wrongArgument"
      };
      invokeRunner(args);
      Assert.fail("Should have failed!");
    } catch (final Exception e) {
      Assert.assertEquals(CmdLineException.class, e.getCause().getClass());
    }
  }

  @Test
  public void cannotProcessNoArguments()
      throws Exception {
    try {
      invokeRunner("".split(" "));
    } catch (final Exception e) {
      Assert.assertEquals(CmdLineException.class, e.getCause().getClass());
    }
  }

  @Test
  public void processCorrectArguments()
      throws Exception {
    final String contextFolder = new File(getClass().getResource("").getFile()).getAbsolutePath();
    final String wroFile = contextFolder + File.separator + "wro.xml";
    final String[] args = String.format("--wroFile %s --contextFolder %s -m ", new Object[] {
      wroFile, contextFolder
    }).split(" ");
    invokeRunner(args);
  }

  private void invokeRunner(final String[] args)
      throws Exception {
    new Wro4jCommandLineRunner() {
      {
        {
          setDestinationFolder(destinationFolder);
        }
      }

      @Override
      protected void onRunnerException(final Exception e) {
        throw WroRuntimeException.wrap(e);
      }
    }.doMain(args);
  }

  @Test
  public void shouldApplyCssUrlRewriterProperly()
      throws Exception {
    final String contextFolder = new File(getClass().getResource("").getFile()).getAbsolutePath();

    final String wroFile = contextFolder + File.separator + "wro.xml";
    LOG.debug("wroFile: {}", wroFile);
    final String processorsList = AbstractConfigurableMultipleStrategy
        .createItemsAsString(CssUrlRewritingProcessor.ALIAS);
    final String[] args = String.format("--wroFile %s --contextFolder %s -m --preProcessors " + processorsList,
        new Object[] {
          wroFile, contextFolder
        }).split(" ");
    invokeRunner(args);
  }

  @Test
  public void shouldUseMultiplePreProcessors()
      throws Exception {
    invokeMultipleProcessors("--preProcessors");
  }

  @Test
  public void shouldUseMultiplePostProcessors()
      throws Exception {
    invokeMultipleProcessors("--postProcessors");
  }

  private void invokeMultipleProcessors(final String processorsType)
      throws Exception {
    final String contextFolder = new File(getClass().getResource("").getFile()).getAbsolutePath();
    final String wroFile = contextFolder + File.separator + "wro.xml";
    LOG.debug("wroFile: {}", wroFile);
    final String processorsList = AbstractConfigurableMultipleStrategy.createItemsAsString(CssMinProcessor.ALIAS,
        JSMinProcessor.ALIAS, CssVariablesProcessor.ALIAS);
    final String[] args = String.format(
        "--wroFile %s --contextFolder %s --destinationFolder %s -m %s " + processorsList, new Object[] {
          wroFile, contextFolder, destinationFolder.getAbsolutePath(), processorsType
        }).split(" ");
    invokeRunner(args);
  }

  @Test(expected = CssLintException.class)
  public void shouldApplyCssLint()
      throws Exception {
    final String contextFolder = new File(getClass().getResource("").getFile()).getAbsolutePath();
    final String wroFile = contextFolder + File.separator + "wro.xml";

    final String[] args = String.format(
        "--wroFile %s --contextFolder %s --destinationFolder %s -m -c " + CssLintProcessor.ALIAS, new Object[] {
          wroFile, contextFolder, destinationFolder.getAbsolutePath()
        }).split(" ");
    invokeRunner(args);
  }

  @Test
  public void shouldApplyYuiCssMinAsPostProcessor()
      throws Exception {
    final String contextFolder = new File(getClass().getResource("").getFile()).getAbsolutePath();
    final String wroFile = contextFolder + File.separator + "wro.xml";

    final String[] args = String
        .format(
            "--wroFile %s --contextFolder %s --destinationFolder %s -m --postProcessors "
                + YUICssCompressorProcessor.ALIAS, new Object[] {
              wroFile, contextFolder, destinationFolder.getAbsolutePath()
            }).split(" ");
    invokeRunner(args);
  }

  @Test(expected = LinterException.class)
  public void shouldApplyJsHint()
      throws Exception {
    final String contextFolder = new File(getClass().getResource("").getFile()).getAbsolutePath();
    final String wroFile = contextFolder + File.separator + "wro.xml";

    final String[] args = String.format(
        "--wroFile %s --contextFolder %s --destinationFolder %s -m -c " + JsHintProcessor.ALIAS, new Object[] {
          wroFile, contextFolder, destinationFolder.getAbsolutePath()
        }).split(" ");
    invokeRunner(args);
  }

  @Test
  public void shouldProcessTestWroXml()
      throws Exception {
    final String contextFolder = new File(getClass().getResource("").getFile()).getAbsolutePath();
    final String wroFile = contextFolder + File.separator + "wro.xml";

    LOG.debug(wroFile);
    final String[] args = String.format("-m --wroFile %s --contextFolder %s --destinationFolder %s", new Object[] {
      wroFile, contextFolder, destinationFolder.getAbsolutePath()
    }).split(" ");
    invokeRunner(args);
  }

  @Test
  public void shouldAcceptGroovyDSLUsingSmartModelFactory() {
    final File contextFolderFile = new File(getClass().getResource("").getFile(), "dsl");
    final String contextFolder = contextFolderFile.getAbsolutePath();

    final String[] args = String.format("-m --contextFolder %s --destinationFolder %s", new Object[] {
      contextFolder, destinationFolder.getAbsolutePath()
    }).split(" ");

    // invoke runner
    new Wro4jCommandLineRunner() {
      {
        {
          setDestinationFolder(destinationFolder);
        }
      }

      @Override
      protected File newDefaultWroFile() {
        return new File(contextFolderFile, "wro.xml");
      }

      @Override
      protected void onRunnerException(final Exception e) {
        LOG.error("Exception occured: ", e.getCause());
        throw new RuntimeException(e);
      }
    }.doMain(args);
  }
}
