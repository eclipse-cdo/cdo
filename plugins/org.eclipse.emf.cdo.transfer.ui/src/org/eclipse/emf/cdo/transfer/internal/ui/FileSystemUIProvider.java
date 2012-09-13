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
package org.eclipse.emf.cdo.transfer.internal.ui;

import org.eclipse.emf.cdo.spi.transfer.FileSystemTransferSystem;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class FileSystemUIProvider implements TransferUIProvider
{
  public FileSystemUIProvider()
  {
  }

  public ILabelProvider createLabelProvider(CDOTransferSystem system)
  {
    // Return null to fall back to default labels
    return null;
  }

  public void addSupportedTransfers(List<Transfer> transfers)
  {
    transfers.add(FileTransfer.getInstance());
  }

  public List<CDOTransferElement> convertTransferData(Object data)
  {
    // TODO: implement FileSystemUIProvider.convertTransferData(data)
    return null;
  }

  public CDOTransferElement convertTransferTarget(Object target)
  {
    // TODO: implement FileSystemUIProvider.convertTransferTarget(target)
    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends TransferUIProvider.Factory
  {
    public Factory()
    {
      super(FileSystemTransferSystem.TYPE);
    }

    @Override
    public TransferUIProvider create(String description) throws ProductCreationException
    {
      return new FileSystemUIProvider();
    }
  }
}
