/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.ui;

import org.eclipse.emf.cdo.transfer.CDOTransfer;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider.Factory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.dnd.DNDDragListener;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link DNDDragListener drag listener} that can {@link #support(StructuredViewer) support} a {@link StructuredViewer viewer} to create and perform
 * {@link CDOTransfer transfers} of the dragged elements to target {@link CDOTransferElement elements}.
 *
 * @author Eike Stepper
 */
public class TransferDragListener extends DNDDragListener<Object>
{
  private TransferUIProvider[] uiProviders;

  protected TransferDragListener(StructuredViewer viewer)
  {
    super(viewer);
    uiProviders = getUIProviders();

    List<Transfer> transfers = new ArrayList<>();
    for (int i = 0; i < uiProviders.length; i++)
    {
      TransferUIProvider uiProvider = uiProviders[i];
      uiProvider.addSupportedTransfers(transfers);
    }

    setTransfers(transfers.toArray(new Transfer[transfers.size()]));
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected TransferUIProvider[] getUIProviders()
  {
    return Factory.getAll(getContainer());
  }

  @Override
  protected Object getObject(IStructuredSelection selection)
  {
    for (int i = 0; i < uiProviders.length; i++)
    {
      TransferUIProvider uiProvider = uiProviders[i];

      Object object = uiProvider.convertSelection(selection);
      if (object != null)
      {
        return object;
        // return new File[] { new File("dummy.transfer") };
      }
    }

    return null;
  }

  @Override
  public void dragFinished(DragSourceEvent event)
  {
    // TODO Eventually implement removal for move DND
    super.dragFinished(event);
  }

  public static TransferDragListener support(StructuredViewer viewer)
  {
    TransferDragListener dragListener = new TransferDragListener(viewer);
    Transfer[] transfers = dragListener.getTransfers();
    viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT, transfers, dragListener);
    return dragListener;
  }
}
