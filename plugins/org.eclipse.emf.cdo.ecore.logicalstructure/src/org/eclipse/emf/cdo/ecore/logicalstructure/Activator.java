/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.logicalstructure;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

/**
 * @author Eike Stepper
 */
public class Activator extends Plugin
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.ecore.logicalstructure";

  private static final IExtensionPoint EXTENSION_POINT = Platform.getExtensionRegistry()
      .getExtensionPoint(PLUGIN_ID + ".containerValues");

  private static Activator INSTANCE;

  public Activator()
  {
    INSTANCE = this;
  }

  public static void error(Throwable t)
  {
    IStatus status = getStatus(t);
    INSTANCE.getLog().log(status);
  }

  public static IStatus getStatus(Throwable t)
  {
    if (t instanceof CoreException)
    {
      return ((CoreException)t).getStatus();
    }

    return new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t);
  }

  public static IConfigurationElement[] getContainerValues()
  {
    return EXTENSION_POINT.getConfigurationElements();
  }
}
