/*
 * Copyright (c) 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.team.history;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.team.ui.history.IHistoryPageSource;

/**
 * @author Eike Stepper
 */
public class CDOHistoryAdapterFactory implements IAdapterFactory
{
  private static final Class<IHistoryPageSource> CLASS_IHISTORYPAGESOURCE = IHistoryPageSource.class;

  private static final Class<?>[] ADAPTER_TYPES = { CLASS_IHISTORYPAGESOURCE };

  public CDOHistoryAdapterFactory()
  {
  }

  public Class<?>[] getAdapterList()
  {
    return ADAPTER_TYPES;
  }

  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType)
  {
    if (adapterType == CLASS_IHISTORYPAGESOURCE)
    {
      return (T)CDOHistoryPageSource.INSTANCE;
    }

    return null;
  }
}
