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

import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.ui.NativeObjectLabelProvider;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider;
import org.eclipse.emf.cdo.transfer.spi.workspace.WorkspaceTransferSystem;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

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
