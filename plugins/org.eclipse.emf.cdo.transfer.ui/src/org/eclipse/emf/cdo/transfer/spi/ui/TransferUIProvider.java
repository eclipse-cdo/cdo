/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.spi.ui;

import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.ui.TransferDragListener;
import org.eclipse.emf.cdo.transfer.ui.TransferDropAdapter;
import org.eclipse.emf.cdo.transfer.ui.TransferLabelProvider;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Transfer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracts the {@link CDOTransferSystem transfer system}-specific aspects that are needed by a generic transfer user interface.
 * <p>
 * Abstracted functionalities include:
 * <ul>
 * <li>{@link TransferLabelProvider Providing labels} for {@link CDOTransferElement transfer elements}, see {@link #createLabelProvider(CDOTransferSystem) createLabelProvider()}.
 * <li>Creating transfers for {@link TransferDragListener drag operations}, see {@link #addSupportedTransfers(List) addSupportedTransfers()}, {@link #convertSelection(IStructuredSelection) convertSelection()}.
 * <li>Creating transfers for {@link TransferDropAdapter drop operations}, see {@link #addSupportedTransfers(List) addSupportedTransfers()}, {@link #convertTransferData(Object) convertTransferData()}, {@link #convertTransferTarget(Object) convertTransferTarget()}.
 * </ul>
 *
 * @author Eike Stepper
 * @since 4.2
 */
public interface TransferUIProvider
{
  public ILabelProvider createLabelProvider(CDOTransferSystem system);

  public void addSupportedTransfers(List<Transfer> transfers);

  public List<CDOTransferElement> convertTransferData(Object data);

  public CDOTransferElement convertTransferTarget(Object target);

  public Object convertSelection(IStructuredSelection selection);

  /**
   * Creates {@link TransferUIProvider} instances.
   *
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.transfer.ui.providers"; //$NON-NLS-1$

    protected Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract TransferUIProvider create(String description) throws ProductCreationException;

    public static TransferUIProvider get(IManagedContainer container, String type)
    {
      return (TransferUIProvider)container.getElement(PRODUCT_GROUP, type, null);
    }

    public static TransferUIProvider[] getAll(IManagedContainer container)
    {
      List<TransferUIProvider> uiProviders = new ArrayList<>();
      for (String type : container.getFactoryTypes(PRODUCT_GROUP))
      {
        uiProviders.add((TransferUIProvider)container.getElement(PRODUCT_GROUP, type, null));
      }

      return uiProviders.toArray(new TransferUIProvider[uiProviders.size()]);
    }
  }
}
