/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.util.ResourceSetConfigurer;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 * @since 4.26
 */
public abstract class ViewResourceSetConfigurer implements ResourceSetConfigurer
{
  public ViewResourceSetConfigurer()
  {
  }

  @Override
  public Object configureResourceSet(ResourceSet resourceSet, Object context, IManagedContainer container)
  {
    if (context instanceof CDOView)
    {
      CDOView view = (CDOView)context;
      return configureViewResourceSet(resourceSet, view);
    }

    return null;
  }

  protected abstract Object configureViewResourceSet(ResourceSet resourceSet, CDOView view);
}
