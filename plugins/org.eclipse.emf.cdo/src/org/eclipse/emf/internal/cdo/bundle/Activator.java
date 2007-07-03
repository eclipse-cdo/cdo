package org.eclipse.emf.internal.cdo.bundle;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.internal.cdo.util.EMFUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;

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

  public static class Implementation extends EclipsePlugin implements IRegistryChangeListener
  {
    public Implementation()
    {
      plugin = this;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
      super.start(context);
      CDO.BUNDLE.setBundleContext(context);
      EMFUtil.addModelInfos();
      IExtensionRegistry registry = Platform.getExtensionRegistry();
      IConfigurationElement[] elements = registry.getConfigurationElementsFor(CDO.BUNDLE_ID,
          CDO.PERSISTENT_PACKAGE_EXT_POINT);
      for (IConfigurationElement element : elements)
      {
        String uri = element.getAttribute("uri");
        // TODO Don't load EPackages eagerly
        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(uri);
        if (ePackage != null)
        {
          EMFUtil.getCDOPackage(ePackage);
        }
        else
        {
          CDO.LOG.warn("Ecore package not found: " + uri);
        }
      }

      registry.addRegistryChangeListener(this, CDO.BUNDLE_ID);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
      IExtensionRegistry registry = Platform.getExtensionRegistry();
      registry.removeRegistryChangeListener(this);
      // TODO Clear CDOPackageManager
      EMFUtil.removeModelInfos();
      plugin = null;
      CDO.BUNDLE.setBundleContext(null);
      super.stop(context);
    }

    public void registryChanged(IRegistryChangeEvent event)
    {
    }
  }
}
