/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui;

import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public class DawnSelectionViewerAdapterFactoryContentProvider extends AdapterFactoryContentProvider
{
  private final CDOResource cdoResource;

  public DawnSelectionViewerAdapterFactoryContentProvider(AdapterFactory adapterFactory, CDOResource cdoResource)
  {
    super(adapterFactory);
    this.cdoResource = cdoResource;
  }

  @Override
  public Object[] getElements(Object object)
  {
    Object[] ret = new Object[1];
    ret[0] = cdoResource;
    return ret;
  }
}
