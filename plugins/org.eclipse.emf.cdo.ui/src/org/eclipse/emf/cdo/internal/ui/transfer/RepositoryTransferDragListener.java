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
package org.eclipse.emf.cdo.internal.ui.transfer;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewRegistry;

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.ui.dnd.DNDDragListener;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.PluginTransferData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class RepositoryTransferDragListener extends DNDDragListener<Object>
{
  private static final Transfer[] TRANSFERS = { PluginTransfer.getInstance() };

  protected RepositoryTransferDragListener(StructuredViewer viewer)
  {
    super(TRANSFERS, viewer);
  }

  @Override
  protected Object getObject(IStructuredSelection selection)
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
    Transfer[] transfers = dragListener.getTransfers();
    viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT, transfers, dragListener);
    return dragListener;
  }
}
