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
package org.eclipse.emf.cdo.transfer.ui.swt;

import org.eclipse.emf.cdo.transfer.CDOTransfer;
import org.eclipse.emf.cdo.transfer.CDOTransferMapping;
import org.eclipse.emf.cdo.transfer.ui.TransferContentProvider;
import org.eclipse.emf.cdo.transfer.ui.TransferLabelProvider;

import org.eclipse.net4j.ui.shared.SharedIcons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * A {@link Composite composite} that contains a {@link #getViewer() tree viewer} for the {@link CDOTransferMapping mappings} of a {@link #getTransfer() transfer}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferTreeComposite extends Composite
{
  private CDOTransfer transfer;

  private TreeViewer viewer;

  public TransferTreeComposite(Composite parent, int style, CDOTransfer transfer)
  {
    super(parent, style);
    this.transfer = transfer;

    setLayout(new FillLayout(SWT.VERTICAL));
    viewer = new TreeViewer(this, SWT.FULL_SELECTION);

    Tree tree = viewer.getTree();
    tree.setLinesVisible(true);
    tree.setHeaderVisible(true);

    TreeViewerColumn sourceViewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
    TreeColumn sourceColumn = sourceViewerColumn.getColumn();
    sourceColumn.setWidth(350);
    sourceColumn.setText("From " + transfer.getSourceSystem());

    TreeViewerColumn typeViewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
    TreeColumn typeColumn = typeViewerColumn.getColumn();
    typeColumn.setWidth(100);
    typeColumn.setText("Type");

    TreeViewerColumn targetViewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
    TreeColumn targetColumn = targetViewerColumn.getColumn();
    targetColumn.setWidth(450);
    targetColumn.setText("To " + transfer.getTargetSystem());

    TreeViewerColumn statusViewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
    TreeColumn statusColumn = statusViewerColumn.getColumn();
    statusColumn.setWidth(100);
    statusColumn.setText("Status");

    viewer.setAutoExpandLevel(2);
    viewer.setContentProvider(new TransferContentProvider());
    viewer.setLabelProvider(new TransferLabelProvider(transfer));
    viewer.setInput(transfer.getRootMapping());

    MenuManager manager = new MenuManager();
    Menu menu = manager.createContextMenu(tree);
    manager.addMenuListener(new IMenuListener()
    {
      @Override
      public void menuAboutToShow(IMenuManager manager)
      {
        IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
        CDOTransferMapping mapping = (CDOTransferMapping)selection.getFirstElement();
        manager.add(new UnmapAction(mapping));
      }
    });

    manager.setRemoveAllWhenShown(true);
    tree.setMenu(menu);
  }

  public CDOTransfer getTransfer()
  {
    return transfer;
  }

  public TreeViewer getViewer()
  {
    return viewer;
  }

  public CDOTransferMapping getSelectedMapping()
  {
    IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
    return (CDOTransferMapping)selection.getFirstElement();
  }

  @Override
  public boolean setFocus()
  {
    return viewer.getTree().setFocus();
  }

  /**
   * An {@link Action action} to {@link CDOTransferMapping#unmap() unmap} a {@link CDOTransferMapping transfer mapping}.
   *
   * @author Eike Stepper
   */
  public static class UnmapAction extends Action
  {
    private CDOTransferMapping mapping;

    public UnmapAction(CDOTransferMapping mapping)
    {
      super("Unmap", SharedIcons.getDescriptor(SharedIcons.ETOOL_DELETE));
      this.mapping = mapping;
    }

    public CDOTransferMapping getMapping()
    {
      return mapping;
    }

    @Override
    public void run()
    {
      mapping.unmap();
    }
  }
}
