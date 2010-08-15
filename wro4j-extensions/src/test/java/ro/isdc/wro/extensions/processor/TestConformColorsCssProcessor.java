/*
 * Copyright (c) 2010. All rights reserved.
 */
package ro.isdc.wro.extensions.processor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.extensions.AbstractWroTest;
import ro.isdc.wro.extensions.processor.css.ConformColorsCssProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.test.util.ResourceProcessor;


/**
 * TestConformColorsCssProcessor.
 *
 * @author Alex Objelean
 * @created Created on Aug 15, 2010
 */
public class TestConformColorsCssProcessor extends AbstractWroTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestConformColorsCssProcessor.class);
  private ResourcePostProcessor processor;

  @Before
  public void setUp() {
    processor = new ConformColorsCssProcessor();
  }

  @Test
  public void testColorTransformer()
    throws IOException {
    LOG.debug("testMixins");
    compareProcessedResourceContents("classpath:ro/isdc/wro/extensions/processor/conformColors-input.css",
      "classpath:ro/isdc/wro/extensions/processor/conformColors-output.css", new ResourceProcessor() {
        public void process(final Reader reader, final Writer writer)
          throws IOException {
          processor.process(reader, writer);
        }
      });
  }
}