/*
 * Copyright (c) 2012, 2013, 2015, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.repository.RepositoryTransferSystem;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider;
import org.eclipse.emf.cdo.transfer.spi.ui.TransferUIProvider.Factory;
import org.eclipse.emf.cdo.transfer.ui.TransferDialog;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewRegistry;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.IDropActionDelegate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RepositoryPluginDropAdapter implements IDropActionDelegate
{
  public static final String DROP_ACTION_ID = "org.eclipse.emf.cdo.ui.RepositoryPluginDropAdapter";

  private TransferUIProvider[] uiProviders;

  public RepositoryPluginDropAdapter()
  {
    uiProviders = getUIProviders();
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
  public boolean run(Object source, Object target)
  {
    List<CDOResourceNode> nodes = getResourceNodes(source);
    if (ObjectUtil.isEmpty(nodes))
    {
      return false;
    }

    CDOView view = nodes.get(0).cdoView();
    CDOTransferSystem sourceSystem = new RepositoryTransferSystem(view);

    List<CDOTransferElement> sourceElements = new ArrayList<>(nodes.size());
    for (CDOResourceNode node : nodes)
    {
      sourceElements.add(sourceSystem.getElement(node.getPath()));
    }

    CDOTransferElement targetElement = getTargetElement(target);
    if (targetElement == null || !targetElement.isDirectory())
    {
      return false;
    }

    Shell shell = UIUtil.getShell();
    return TransferDialog.open(shell, sourceElements, targetElement);
  }

  protected CDOTransferElement getTargetElement(Object target)
  {
    for (TransferUIProvider uiProvider : uiProviders)
    {
      CDOTransferElement targetElement = uiProvider.convertTransferTarget(target);
      if (targetElement != null)
      {
        return targetElement;
      }
    }

    return null;
  }

  protected List<CDOResourceNode> getResourceNodes(Object source)
  {
    try
    {
      ByteArrayInputStream bais = new ByteArrayInputStream((byte[])source);
      ExtendedDataInputStream in = new ExtendedDataInputStream(bais);

      int viewID = in.readInt();
      CDOView view = CDOViewRegistry.INSTANCE.getView(viewID);
      if (view == null)
      {
        return null;
      }

      List<CDOResourceNode> nodes = new ArrayList<>();
      for (;;)
      {
        String path = in.readString();
        if (path == null)
        {
          break;
        }

        CDOResourceNode node = view.getResourceNode(path);
        if (node != null)
        {
          nodes.add(node);
        }
      }

      return nodes;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }
}
