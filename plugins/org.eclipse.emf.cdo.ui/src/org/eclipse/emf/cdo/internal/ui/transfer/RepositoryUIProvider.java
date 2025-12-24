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
package org.eclipse.emf.cdo.internal.ui.transfer;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.repository.RepositoryTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.ui.NativeObjectLabelProvider;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Transfer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RepositoryUIProvider implements TransferUIProvider
{
  public static RepositoryUIProvider INSTANCE;

  public RepositoryUIProvider()
  {
    INSTANCE = this;
  }

  @Override
  public ILabelProvider createLabelProvider(CDOTransferSystem system)
  {
    ILabelProvider delegate = new CDOItemProvider(null);
    return new NativeObjectLabelProvider(delegate);
  }

  @Override
  public void addSupportedTransfers(List<Transfer> transfers)
  {
    // TODO: implement RepositoryUIProvider.addSupportedTransfers(transfers)
  }

  @Override
  public List<CDOTransferElement> convertTransferData(Object data)
  {
    // TODO: implement RepositoryUIProvider.convertTransferData(data)
    return null;
  }

  @Override
  public CDOTransferElement convertTransferTarget(Object target)
  {
    if (target instanceof CDOTransaction)
    {
      CDOTransferSystem system = new RepositoryTransferSystem((CDOTransaction)target);
      return system.getElement("");
    }

    if (target instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)target;
      String path = folder.getPath();

      CDOView view = folder.cdoView();
      CDOTransferSystem system = new RepositoryTransferSystem(view);
      return system.getElement(path);
    }

    return null;
  }

  @Override
  public Object convertSelection(IStructuredSelection selection)
  {
    List<CDOResourceNode> result = new ArrayList<>();
    for (Iterator<?> it = selection.iterator(); it.hasNext();)
    {
      Object object = it.next();
      if (object instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)object;
        result.add(node);
      }
    }

    return result.toArray(new CDOResourceNode[result.size()]);
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName();
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
