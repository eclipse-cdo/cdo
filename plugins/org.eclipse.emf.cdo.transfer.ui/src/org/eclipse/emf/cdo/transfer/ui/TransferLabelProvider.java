/*
 * Copyright (c) 2012, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transfer.CDOTransferMapping;
import org.eclipse.emf.cdo.transfer.CDOTransferMapping.Status;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.CDOTransferType;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * A reusable {@link ITableLabelProvider table label provider} for the {@link CDOTransferMapping mappings} of a {@link CDOTransfer transfer}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider
{
  public static final Color GRAY = UIUtil.initResource(display -> display.getSystemColor(SWT.COLOR_GRAY));

  public static final Color RED = UIUtil.initResource(display -> display.getSystemColor(SWT.COLOR_RED));

  private CDOTransfer transfer;

  private ILabelProvider sourceSystemLabelProvider;

  public TransferLabelProvider(CDOTransfer transfer)
  {
    this.transfer = transfer;

    CDOTransferSystem sourceSystem = transfer.getSourceSystem();
    TransferUIProvider provider = TransferUIProvider.Factory.get(getContainer(), sourceSystem.getType());
    sourceSystemLabelProvider = provider.createLabelProvider(sourceSystem);
  }

  public CDOTransfer getTransfer()
  {
    return transfer;
  }

  @Override
  public void dispose()
  {
    sourceSystemLabelProvider.dispose();
    sourceSystemLabelProvider = null;
    super.dispose();
  }

  @Override
  public String getColumnText(Object element, int columnIndex)
  {
    if (element instanceof CDOTransferMapping)
    {
      CDOTransferMapping mapping = (CDOTransferMapping)element;
      switch (columnIndex)
      {
      case 0:
        return sourceSystemLabelProvider.getText(mapping.getSource());
      case 1:
        return mapping.getTransferType().toString();
      case 2:
        return mapping.getFullPath().toString();
      case 3:
        return mapping.getStatus().toString();
      }
    }

    return null;
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex)
  {
    if (element instanceof CDOTransferMapping)
    {
      CDOTransferMapping mapping = (CDOTransferMapping)element;
      switch (columnIndex)
      {
      case 0:
        return sourceSystemLabelProvider.getImage(mapping.getSource());
      case 1:
        return null;
      case 2:
        return null;
      case 3:
        return null;
      }
    }

    return null;
  }

  @Override
  public Color getForeground(Object element, int columnIndex)
  {
    if (element instanceof CDOTransferMapping)
    {
      CDOTransferMapping mapping = (CDOTransferMapping)element;
      switch (columnIndex)
      {
      case 0:
        return null;

      case 1:
        CDOTransferType transferType = mapping.getTransferType();
        if (transferType == CDOTransferType.FOLDER)
        {
          return TransferLabelProvider.GRAY;
        }

        return null;

      case 2:
      case 3:
        Status status = mapping.getStatus();
        if (status == Status.MERGE)
        {
          return TransferLabelProvider.GRAY;
        }

        if (status == Status.CONFLICT)
        {
          return TransferLabelProvider.RED;
        }

        return null;
      }
    }

    return null;
  }

  @Override
  public Color getBackground(Object element, int columnIndex)
  {
    return null;
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
