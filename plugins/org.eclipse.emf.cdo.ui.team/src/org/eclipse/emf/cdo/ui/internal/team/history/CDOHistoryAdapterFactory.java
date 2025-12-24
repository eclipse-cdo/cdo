/*
 * Copyright (c) 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.team.history;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
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

  @Override
  public Class<?>[] getAdapterList()
  {
    return ADAPTER_TYPES;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType)
  {
    if (adapterType == CLASS_IHISTORYPAGESOURCE)
    {
      return (T)CDOHistoryPageSource.INSTANCE;
    }

    return null;
  }

  public static void load()
  {
    EObject dummy = EcoreFactory.eINSTANCE.createEObject();
    Platform.getAdapterManager().loadAdapter(dummy, CLASS_IHISTORYPAGESOURCE.getName());
  }
}
