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
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.CDOView;

/**
 * @author Eike Stepper
 */
public final class ResourceHistory
{
  public static final ResourceHistory INSTANCE = new ResourceHistory();

  private static final Entry[] NO_ENTRIES = {};

  private ResourceHistory()
  {
  }

  public Entry[] getEntries(CDOView view)
  {
    return NO_ENTRIES;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Entry
  {
    private String resourcePath;

    private Entry(String resourcePath)
    {
      this.resourcePath = resourcePath;
    }

    public String getResourcePath()
    {
      return resourcePath;
    }

    @Override
    public String toString()
    {
      return resourcePath;
    }
  }
}
