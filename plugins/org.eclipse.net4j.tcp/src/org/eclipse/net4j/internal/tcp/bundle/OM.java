/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 * 
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.net4j.tcp"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMTracer DEBUG_SSLBUFFER = DEBUG.tracer("sslbuffer"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  /**
   * @since 3.1
   */
  public static final String DEFAULT_PORT_PROPERTY = BUNDLE_ID + ".port";

  /**
   * @since 3.1
   */
  public static final int DEFAULT_PORT = 2036;

  public static int getDefaultPort()
  {
    try
    {
      String property = BUNDLE.getPlatform().getProperty(DEFAULT_PORT_PROPERTY);
      if (property != null)
      {
        return Integer.valueOf(property);
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    return DEFAULT_PORT;
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
