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
package org.eclipse.emf.cdo.examples.client.offline.nodes;

import org.eclipse.emf.cdo.examples.client.offline.Application;

import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ItemProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.ExampleResourceManager;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class NodeManagerDialog extends TitleAreaDialog
{
  public static final int TYPE = 0;

  public static final int NODE = 1;

  private final NodeManager nodeManager;

  private IElement currentElement;

  private TreeViewer treeViewer;

  private Composite details;

  private StackLayout detailsStack;

  private boolean updatingDetails;

  public static final String TITLE = "CDO Offline Example";

  public NodeManagerDialog(Shell parentShell, NodeManager nodeManager)
  {
    super(parentShell);
    this.nodeManager = nodeManager;
    setHelpAvailable(false);
    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.PRIMARY_MODAL);
  }

  public NodeManager getNodeManager()
  {
    return nodeManager;
  }

  public Node getCurrentNode()
  {
    if (currentElement instanceof Node)
    {
      return (Node)currentElement;
    }

    return null;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(TITLE);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    ItemProvider<NodeManager> itemProvider = new ContainerItemProvider<NodeManager>()
    {
      @Override
      public Image getImage(Object obj)
      {
        return ((IElement)obj).getImage();
      }
    };

    setTitle("Node Manager");
    setTitleImage(ExampleResourceManager.getPluginImage(Application.PLUGIN_ID, "icons/NodeManager.gif"));
    setMessage("Select an existing node or create a new one.");

    Composite area = (Composite)super.createDialogArea(parent);
    SashForm container = new SashForm(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    Composite composite = new Composite(container, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));

    treeViewer = new TreeViewer(composite, SWT.BORDER);
    treeViewer.setLabelProvider(itemProvider);
    treeViewer.setContentProvider(itemProvider);
    treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        setCurrentElement();
      }
    });

    treeViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        if (getCurrentNode() != null)
        {
          close();
        }
      }
    });

    Tree tree = treeViewer.getTree();
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    details = new Composite(container, SWT.NONE);
    detailsStack = new StackLayout();
    details.setLayout(detailsStack);

    for (NodeType nodeType : nodeManager.getElements())
    {
      nodeType.createUI(this, TYPE);
      nodeType.createUI(this, NODE);
    }

    treeViewer.setInput(nodeManager);
    treeViewer.expandAll();
    container.setWeights(new int[] { 1, 1 });
    return area;
  }

  public Composite getDetails()
  {
    return details;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(500, 500);
  }

  private void setCurrentElement()
  {
    if (treeViewer != null)
    {
      IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
      currentElement = (IElement)selection.getFirstElement();
    }

    getButton(IDialogConstants.OK_ID).setEnabled(getCurrentNode() != null);
    if (currentElement != null)
    {
      Control control = currentElement.getDetailsControl();
      detailsStack.topControl = control;

      try
      {
        updatingDetails = true;
        currentElement.showSettings();
      }
      finally
      {
        updatingDetails = false;
      }
    }
    else
    {
      detailsStack.topControl = null;
    }

    details.layout();
  }

  public void onModify(NodeType.Property property, String value)
  {
    Properties settings = currentElement.getSettings();
    String name = property.getName();
    settings.setProperty(name, value);
  }

  public void onCreate(NodeType nodeType)
  {
    nodeManager.createNode(nodeType);
  }

  public void onDelete(NodeType nodeType)
  {
  }

  public boolean isUpdatingDetails()
  {
    return updatingDetails;
  }
}
