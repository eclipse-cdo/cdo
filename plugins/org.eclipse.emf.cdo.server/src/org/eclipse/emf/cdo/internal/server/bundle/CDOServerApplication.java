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
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.net4j.internal.util.om.OSGiBundle;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import org.osgi.framework.BundleException;

/**
 * @author Eike Stepper
 */
public class CDOServerApplication implements IApplication
{
  public CDOServerApplication()
  {
  }

  public Object start(IApplicationContext context) throws Exception
  {
    return EXIT_OK;
  }

  public void stop()
  {
    try
    {
      ((OSGiBundle)OM.BUNDLE).getBundleContext().getBundle().stop();
    }
    catch (BundleException ex)
    {
      OM.LOG.error(ex);
    }
  }
}
