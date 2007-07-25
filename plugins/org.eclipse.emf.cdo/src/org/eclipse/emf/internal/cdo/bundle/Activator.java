package org.eclipse.emf.internal.cdo.bundle;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;

import org.osgi.framework.BundleContext;

public final class Activator extends EMFPlugin
{
  // @Singleton
  public static final Activator INSTANCE = new Activator();

  private static Implementation plugin;

  public Activator()
  {
    super(new ResourceLocator[] {});
  }

  @Override
  public ResourceLocator getPluginResourceLocator()
  {
    return plugin;
  }

  public static Implementation getPlugin()
  {
    return plugin;
  }

  public static class Implementation extends EclipsePlugin
  {
    public Implementation()
    {
      plugin = this;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
      super.start(context);
      OM.BUNDLE.setBundleContext(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
      plugin = null;
      OM.BUNDLE.setBundleContext(null);
      super.stop(context);
    }
  }
}
