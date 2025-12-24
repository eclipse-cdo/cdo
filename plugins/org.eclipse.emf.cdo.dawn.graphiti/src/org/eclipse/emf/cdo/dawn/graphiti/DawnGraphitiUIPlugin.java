/*
 * Copyright (c) 2011, 2012, 2015, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * @author Martin Fluegge
 */
public class DawnGraphitiUIPlugin extends AbstractUIPlugin
{
  public static final String ID = "org.eclipse.emf.cdo.dawn.emf"; //$NON-NLS-1$

  private static DawnGraphitiUIPlugin plugin;

  public DawnGraphitiUIPlugin()
  {
  }

  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    super.stop(context);
  }

  public static DawnGraphitiUIPlugin getDefault()
  {
    return plugin;
  }
}
