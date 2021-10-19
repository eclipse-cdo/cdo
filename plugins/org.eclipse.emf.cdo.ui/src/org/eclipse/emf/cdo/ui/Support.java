/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.common.CommonPlugin;

/**
 * @author Eike Stepper
 * @since 4.5
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class Support
{
  public static final Support PROPERTIES = new Support()
  {
    @Override
    protected boolean determineAvailability() throws Throwable
    {
      return CommonPlugin.loadClass("org.eclipse.ui.views", "org.eclipse.ui.views.properties.PropertySheet") != null;
    }
  };

  public static final Support HISTORY = new Support()
  {
    @Override
    protected boolean determineAvailability() throws Throwable
    {
      return CommonPlugin.loadClass("org.eclipse.emf.cdo.ui.team", "org.eclipse.emf.cdo.ui.internal.team.history.CDOHistoryPage") != null;
    }
  };

  public static final Support COMPARE = new Support()
  {
    @Override
    protected boolean determineAvailability() throws Throwable
    {
      return CommonPlugin.loadClass("org.eclipse.emf.cdo.ui.compare", "org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil") != null;
    }
  };

  /**
   * @since 4.11
   */
  public static final Support SERVER_SECURITY = new Support()
  {
    @Override
    protected boolean determineAvailability() throws Throwable
    {
      return CommonPlugin.loadClass("org.eclipse.emf.cdo.server.security", "org.eclipse.emf.cdo.server.security.SecurityManagerUtil") != null;
    }
  };

  private Boolean available;

  private Support()
  {
  }

  public boolean isAvailable()
  {
    if (available == null)
    {
      try
      {
        available = determineAvailability();
      }
      catch (Throwable ex)
      {
        available = false;
      }
    }

    return available;
  }

  protected abstract boolean determineAvailability() throws Throwable;
}
