package ro.isdc.wro.model.resource.processor.decorator;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.SupportedResourceType;
import ro.isdc.wro.model.resource.processor.ImportAware;
import ro.isdc.wro.model.resource.processor.MinimizeAware;
import ro.isdc.wro.model.resource.processor.ResourceProcessor;
import ro.isdc.wro.model.resource.processor.SupportAware;
import ro.isdc.wro.model.resource.processor.SupportedResourceTypeAware;
import ro.isdc.wro.util.AbstractDecorator;
import ro.isdc.wro.util.LazyInitializer;


/**
 * Decorates a {@link LazyInitializer} which creates a processor.
 *
 * @author Alex Objelean
 * @since 1.4.6
 */
public final class LazyProcessorDecorator
    extends AbstractDecorator<LazyInitializer<ResourceProcessor>>
    implements ResourceProcessor, SupportedResourceTypeAware, MinimizeAware, SupportAware, ImportAware {
  private ProcessorDecorator processor;

  public LazyProcessorDecorator(final LazyInitializer<ResourceProcessor> processor) {
    super(processor);
  }

  private AbstractProcessorDecoratorSupport getProcessorDecorator() {
    if (processor == null) {
      processor = new ProcessorDecorator(getDecoratedObject().get());
    }
    return processor;
  }

  /**
   * {@inheritDoc}
   */
  public void process(final Resource resource, final Reader reader, final Writer writer)
      throws IOException {
    getProcessorDecorator().process(resource, reader, writer);
  }

  /**
   * {@inheritDoc}
   */
  public SupportedResourceType getSupportedResourceType() {
    return getProcessorDecorator().getSupportedResourceType();
  }

  /**
   * {@inheritDoc}
   */
  public boolean isMinimize() {
    return getProcessorDecorator().isMinimize();
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSupported() {
    return getProcessorDecorator().isSupported();
  }

  /**
   * {@inheritDoc}
   */
  public boolean isImportAware() {
    return getProcessorDecorator().isImportAware();
  }

  @Override
  public String toString() {
    return getProcessorDecorator().toString();
  }
}
