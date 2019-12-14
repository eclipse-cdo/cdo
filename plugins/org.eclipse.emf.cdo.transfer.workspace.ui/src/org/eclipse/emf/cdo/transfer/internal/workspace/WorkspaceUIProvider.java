/*
 * Copyright (c) 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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

  @Override
  public ILabelProvider createLabelProvider(CDOTransferSystem system)
  {
    ILabelProvider delegate = new DecoratingLabelProvider(new WorkbenchLabelProvider(), DECORATOR);
    return new NativeObjectLabelProvider(delegate);
  }

  @Override
  public void addSupportedTransfers(List<Transfer> transfers)
  {
    transfers.add(ResourceTransfer.getInstance());
  }

  @Override
  public List<CDOTransferElement> convertTransferData(Object data)
  {
    if (data instanceof IResource[])
    {
      IResource[] resources = (IResource[])data;
      List<CDOTransferElement> result = new ArrayList<>(resources.length);
      for (int i = 0; i < resources.length; i++)
      {
        IResource resource = resources[i];
        IPath path = resource.getFullPath();
        CDOTransferElement element = WorkspaceTransferSystem.INSTANCE.getElement(path);
        result.add(element);
      }

      return result;
    }

    return null;
  }

  @Override
  public CDOTransferElement convertTransferTarget(Object target)
  {
    if (target instanceof IResource)
    {
      IResource resource = (IResource)target;
      IPath path = resource.getFullPath();
      return WorkspaceTransferSystem.INSTANCE.getElement(path);
    }

    return null;
  }

  @Override
  public Object convertSelection(IStructuredSelection selection)
  {
    // TODO: implement WorkspaceUIProvider.convertSelection(selection)
    return null;
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
      super(WorkspaceTransferSystem.TYPE);
    }

    @Override
    public TransferUIProvider create(String description) throws ProductCreationException
    {
      return new WorkspaceUIProvider();
    }
  }
}
