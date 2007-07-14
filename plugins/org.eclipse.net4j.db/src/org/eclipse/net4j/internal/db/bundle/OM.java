/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db.bundle;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.internal.db.DBAdapterDescriptor;
import org.eclipse.net4j.internal.db.DBAdapterRegistry;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * @author Eike Stepper
 */
public final class OM
{
  public static final String BUNDLE_ID = "org.eclipse.net4j.db"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMTracer DEBUG_SQL = DEBUG.tracer("sql"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final String EXT_POINT = "dbAdapters";

  private OM()
  {
  }

  public static void start() throws Exception
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(BUNDLE_ID, EXT_POINT);
    for (final IConfigurationElement element : elements)
    {
      if ("dbAdapter".equals(element.getName()))
      {
        DBAdapterDescriptor descriptor = new DBAdapterDescriptor(element.getAttribute("name"))
        {
          @Override
          public IDBAdapter createDBAdapter()
          {
            try
            {
              return (IDBAdapter)element.createExecutableExtension("class");
            }
            catch (CoreException ex)
            {
              OM.LOG.error(ex);
              return null;
            }
          }
        };

        DBAdapterRegistry.INSTANCE.addDescriptor(descriptor);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator
  {
    public Activator()
    {
      super(BUNDLE);
    }
  }
}
