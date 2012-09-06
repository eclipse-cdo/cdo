/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.repository.internal.ui;

import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.repository.RepositoryTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.ui.NativeObjectLabelProvider;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.ILabelProvider;

/**
 * @author Eike Stepper
 */
public class RepositoryUIProvider implements TransferUIProvider
{
  public RepositoryUIProvider()
  {
  }

  public ILabelProvider createLabelProvider(CDOTransferSystem system)
  {
    ILabelProvider delegate = new CDOItemProvider(null);
    return new NativeObjectLabelProvider(delegate);
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends TransferUIProvider.Factory
  {
    public Factory()
    {
      super(RepositoryTransferSystem.TYPE);
    }

    @Override
    public TransferUIProvider create(String description) throws ProductCreationException
    {
      return new RepositoryUIProvider();
    }
  }
}
