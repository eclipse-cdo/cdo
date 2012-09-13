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
package org.eclipse.emf.cdo.transfer.internal.workspace;

import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.ui.NativeObjectLabelProvider;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider;
import org.eclipse.emf.cdo.transfer.spi.workspace.WorkspaceTransferSystem;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ResourceTransfer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class WorkspaceUIProvider implements TransferUIProvider
{
  private static final ILabelDecorator DECORATOR = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();

  public WorkspaceUIProvider()
  {
  }

  public ILabelProvider createLabelProvider(CDOTransferSystem system)
  {
    ILabelProvider delegate = new DecoratingLabelProvider(new WorkbenchLabelProvider(), DECORATOR);
    return new NativeObjectLabelProvider(delegate);
  }

  public void addSupportedTransfers(List<Transfer> transfers)
  {
    transfers.add(ResourceTransfer.getInstance());
  }

  public List<CDOTransferElement> convertTransferData(Object data)
  {
    if (data instanceof IResource[])
    {
      IResource[] resources = (IResource[])data;
      List<CDOTransferElement> result = new ArrayList<CDOTransferElement>(resources.length);
      for (int i = 0; i < resources.length; i++)
      {
        IResource resource = resources[i];
        CDOTransferElement element = WorkspaceTransferSystem.INSTANCE.getElement(resource.getFullPath());
        result.add(element);
      }

      return result;
    }

    return null;
  }

  public CDOTransferElement convertTransferTarget(Object target)
  {
    // TODO: implement WorkspaceUIProvider.convertTransferTarget(target)
    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends TransferUIProvider.Factory
  {
    public Factory()
    {
      super(WorkspaceTransferSystem.TYPE);
    }

    @Override
    public TransferUIProvider create(String description) throws ProductCreationException
    {
      return new WorkspaceUIProvider();
    }
  }
}
