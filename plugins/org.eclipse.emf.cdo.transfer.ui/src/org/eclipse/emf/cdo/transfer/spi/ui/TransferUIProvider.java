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
package org.eclipse.emf.cdo.transfer.spi.ui;

import org.eclipse.emf.cdo.transfer.CDOTransferSystem;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.ILabelProvider;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public interface TransferUIProvider
{
  public static final TransferUIProvider CONTAINER = new TransferUIProvider()
  {
    public ILabelProvider createLabelProvider(CDOTransferSystem system)
    {
      TransferUIProvider provider = Factory.get(system.getType());
      return provider.createLabelProvider(system);
    }
  };

  public ILabelProvider createLabelProvider(CDOTransferSystem system);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.transfer.ui.providers";

    protected Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    public abstract TransferUIProvider create(String description) throws ProductCreationException;

    public static TransferUIProvider get(String type)
    {
      return (TransferUIProvider)IPluginContainer.INSTANCE.getElement(PRODUCT_GROUP, type, null);
    }
  }
}
