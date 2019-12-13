/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.ui;

import org.eclipse.emf.cdo.transfer.CDOTransfer;
import org.eclipse.emf.cdo.transfer.CDOTransferMapping;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * A reusable {@link ITreeContentProvider tree content provider} for the {@link CDOTransferMapping mappings} of a {@link CDOTransfer transfer}.
 * <p>
 * The {@link StructuredViewer#setInput(Object) input} must be an instance of {@link CDOTransferMapping}, e.g., the return value of {@link CDOTransfer#getRootMapping()}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferContentProvider implements ITreeContentProvider, IListener
{
  private TreeViewer viewer;

  private CDOTransferMapping input;

  public TransferContentProvider()
  {
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (viewer == null)
    {
      return;
    }

    if (event instanceof CDOTransfer.MappingEvent)
    {
      CDOTransfer.MappingEvent e = (CDOTransfer.MappingEvent)event;
      CDOTransferMapping mapping = e.getMapping();

      if (e.hasTreeImpact())
      {
        UIUtil.refreshElement(viewer, mapping, true);
      }
      else
      {
        UIUtil.refreshElement(viewer, mapping, true); // TODO Just update labels
      }
    }
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    this.viewer = (TreeViewer)viewer;

    // boolean refreshViewer = false;
    if (input != null)
    {
      input.getTransfer().removeListener(this);
      // refreshViewer = true;
    }

    if (newInput instanceof CDOTransferMapping)
    {
      input = (CDOTransferMapping)newInput;
      input.getTransfer().addListener(this);
    }
    else
    {
      if (newInput == null)
      {
        input = null;
      }
      else
      {
        throw new IllegalArgumentException("Not a transfer mapping: " + newInput);
      }
    }
  }

  @Override
  public void dispose()
  {
    if (input != null)
    {
      input.getTransfer().removeListener(this);
      input = null;
    }
  }

  @Override
  public boolean hasChildren(Object element)
  {
    return getChildren(element).length != 0;
  }

  @Override
  public Object[] getChildren(Object element)
  {
    if (element instanceof CDOTransferMapping)
    {
      CDOTransferMapping mapping = (CDOTransferMapping)element;
      return mapping.getChildren();
    }

    return CDOTransferMapping.NO_CHILDREN;
  }

  @Override
  public Object[] getElements(Object element)
  {
    return getChildren(element);
  }

  @Override
  public Object getParent(Object element)
  {
    if (element == input)
    {
      return null;
    }

    if (element instanceof CDOTransferMapping)
    {
      CDOTransferMapping mapping = (CDOTransferMapping)element;
      return mapping.getParent();
    }

    return null;
  }
}
