/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @author Eike Stepper
 */
public final class LMAdapterFactory implements IAdapterFactory
{
  private static final Class<IAssemblyDescriptor> ASSEMBLY_DESCRIPTOR_CLASS = IAssemblyDescriptor.class;

  private static final Class<?>[] CLASSES = { ASSEMBLY_DESCRIPTOR_CLASS };

  public LMAdapterFactory()
  {
  }

  @Override
  public Class<?>[] getAdapterList()
  {
    return CLASSES;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType)
  {
    if (adapterType == ASSEMBLY_DESCRIPTOR_CLASS)
    {
      if (adaptableObject instanceof CDOCheckout)
      {
        CDOCheckout checkout = (CDOCheckout)adaptableObject;
        return (T)IAssemblyManager.INSTANCE.getDescriptor(checkout);
      }
    }

    return null;
  }
}
