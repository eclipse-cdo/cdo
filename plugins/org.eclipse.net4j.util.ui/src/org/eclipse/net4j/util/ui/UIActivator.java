/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.internal.util.om.OSGiBundle;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OSGiActivator;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public class UIActivator extends AbstractUIPlugin
{
  private OMBundle omBundle;

  public UIActivator(OMBundle omBundle)
  {
    this.omBundle = omBundle;
  }

  public final OMBundle getOMBundle()
  {
    return omBundle;
  }

  @Override
  public void start(BundleContext context) throws Exception
  {
    OSGiActivator.startBundle(context, (OSGiBundle)getOMBundle());
    super.start(context);
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    super.stop(context);
    OSGiActivator.stopBundle(context, (OSGiBundle)getOMBundle());
  }
}