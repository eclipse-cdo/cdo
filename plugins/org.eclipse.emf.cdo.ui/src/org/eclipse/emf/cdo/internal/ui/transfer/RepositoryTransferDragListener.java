/*
 * Copyright (c) 2012, 2013, 2017 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewRegistry;

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.PluginTransferData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class RepositoryTransferDragListener extends DragSourceAdapter
{
  private static final Transfer[] TRANSFERS = { PluginTransfer.getInstance(), FileTransfer.getInstance() };

  private StructuredViewer viewer;

  protected RepositoryTransferDragListener(StructuredViewer viewer)
  {
    this.viewer = viewer;
  }

  @Override
  public void dragStart(DragSourceEvent event)
  {
    IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
    event.doit = !selection.isEmpty(); // TODO Check that only resource nodes are selected?
  }

  @Override
  public void dragSetData(DragSourceEvent event)
  {
    IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
    for (Transfer transfer : TRANSFERS)
    {
      if (transfer.isSupportedType(event.dataType))
      {
        event.data = getObject(selection, transfer);
        break;
      }
    }
  }

  protected Object getObject(IStructuredSelection selection, Transfer transfer)
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ExtendedDataOutputStream out = new ExtendedDataOutputStream(baos);

      CDOView view = null;
      for (Iterator<?> it = selection.iterator(); it.hasNext();)
      {
        Object object = it.next();
        if (object instanceof CDOResourceNode)
        {
          CDOResourceNode node = (CDOResourceNode)object;
          CDOView nodeView = node.cdoView();
          if (view == null)
          {
            view = nodeView;

            int viewID = CDOViewRegistry.INSTANCE.getID(view);
            out.writeInt(viewID);
          }
          else if (view != nodeView)
          {
            continue;
          }

          out.writeString(node.getPath());
        }
      }

      out.writeString(null);
      byte[] data = baos.toByteArray();
      return new PluginTransferData(RepositoryPluginDropAdapter.DROP_ACTION_ID, data);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static RepositoryTransferDragListener support(StructuredViewer viewer)
  {
    RepositoryTransferDragListener dragListener = new RepositoryTransferDragListener(viewer);
    viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT, TRANSFERS, dragListener);
    return dragListener;
  }
}
