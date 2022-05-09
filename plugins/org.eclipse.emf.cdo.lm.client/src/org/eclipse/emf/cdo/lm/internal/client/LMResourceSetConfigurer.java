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

import org.eclipse.emf.cdo.common.util.ResourceSetConfigurer;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 */
public class LMResourceSetConfigurer implements ResourceSetConfigurer
{
  public static final String TYPE = "lm";

  @Override
  public LMResourceSetConfiguration configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container)
  {
    if (context instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)context;

      IAssemblyDescriptor descriptor = IAssemblyManager.INSTANCE.getDescriptor(checkout);
      if (descriptor != null)
      {
        return ((AssemblyDescriptor)descriptor).addResourceSet(resourceSet);
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends ResourceSetConfigurer.Factory
  {
    public Factory()
    {
      super(TYPE);
    }

    @Override
    public LMResourceSetConfigurer create(String description) throws ProductCreationException
    {
      return new LMResourceSetConfigurer();
    }
  }
}
