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
package org.eclipse.emf.cdo.transfer.internal.ui;

import org.eclipse.emf.cdo.spi.transfer.FileSystemTransferSystem;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class FileSystemUIProvider implements TransferUIProvider
{
  public FileSystemUIProvider()
  {
  }

  @Override
  public ILabelProvider createLabelProvider(CDOTransferSystem system)
  {
    return new LabelProvider()
    {
      @Override
      public Image getImage(Object element)
      {
        if (element instanceof File)
        {
          File file = (File)element;
          if (file.isDirectory())
          {
            return SharedIcons.getImage(SharedIcons.OBJ_RESOURCE_FOLDER);
          }

          return SharedIcons.getImage(SharedIcons.OBJ_FILE_RESOURCE);
        }

        return super.getImage(element);
      }
    };
  }

  @Override
  public void addSupportedTransfers(List<Transfer> transfers)
  {
    transfers.add(FileTransfer.getInstance());
  }

  @Override
  public List<CDOTransferElement> convertTransferData(Object data)
  {
    if (data instanceof String[])
    {
      String[] paths = (String[])data;
      List<CDOTransferElement> result = new ArrayList<>(paths.length);
      for (int i = 0; i < paths.length; i++)
      {
        String path = paths[i];
        CDOTransferElement element = FileSystemTransferSystem.INSTANCE.getElement(path);
        result.add(element);
      }

      return result;
    }

    return null;
  }

  @Override
  public CDOTransferElement convertTransferTarget(Object target)
  {
    // System.out.println(target.getClass().getName());
    // if (target instanceof String)
    // {
    // String path = (String)target;
    // }

    // TODO: implement FileSystemUIProvider.convertTransferTarget(target)
    return null;
  }

  @Override
  public Object convertSelection(IStructuredSelection selection)
  {
    // TODO: implement FileSystemUIProvider.convertSelection(selection)
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
      super(FileSystemTransferSystem.TYPE);
    }

    @Override
    public TransferUIProvider create(String description) throws ProductCreationException
    {
      return new FileSystemUIProvider();
    }
  }
}
