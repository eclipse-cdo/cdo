/*
 * Copyright (c) 2015, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.net4j.util.AbstractSupport;

/**
 * @author Eike Stepper
 * @since 4.5
 * @noextend This class is not intended to be subclassed by clients.
 * @deprecated As of 4.15 use {@link org.eclipse.emf.cdo.common.util.Support}.
 */
@Deprecated
public abstract class Support extends AbstractSupport
{
  public static final Support PROPERTIES = new Support()
  {
    @Override
    protected boolean determineAvailability() throws Throwable
    {
      return org.eclipse.emf.cdo.common.util.Support.UI_PROPERTIES.isAvailable();
    }
  };

  public static final Support HISTORY = new Support()
  {
    @Override
    protected boolean determineAvailability() throws Throwable
    {
      return org.eclipse.emf.cdo.common.util.Support.UI_HISTORY.isAvailable();
    }
  };

  public static final Support COMPARE = new Support()
  {
    @Override
    protected boolean determineAvailability() throws Throwable
    {
      return org.eclipse.emf.cdo.common.util.Support.UI_COMPARE.isAvailable();
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
      return org.eclipse.emf.cdo.common.util.Support.SERVER_SECURITY.isAvailable();
    }
  };

  private Support()
  {
  }

  @Override
  protected abstract boolean determineAvailability() throws Throwable;
}
