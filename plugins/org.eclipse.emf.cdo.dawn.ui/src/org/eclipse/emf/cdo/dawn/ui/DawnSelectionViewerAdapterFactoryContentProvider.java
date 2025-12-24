/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
