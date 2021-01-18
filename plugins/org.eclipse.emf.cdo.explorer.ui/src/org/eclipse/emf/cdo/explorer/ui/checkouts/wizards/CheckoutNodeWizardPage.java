/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutContentProvider;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
public abstract class CheckoutNodeWizardPage extends WizardPage
{
  protected final String title;

  private TreeViewer nodeViewer;

  private boolean nodeRevealed;

  /**
   * Either a {@link CDOCheckout}, a {@link CDOResourceNode}, or <code>null</code>.
   */
  private Object node;

  public CheckoutNodeWizardPage(String pageName, String title, IStructuredSelection selection)
  {
    super(pageName);
    this.title = title;
    node = getNode(selection);
  }

  /**
   * Returns either a {@link CDOCheckout}, a {@link CDOResourceNode}, or <code>null</code>.
   */
  public final Object getNode()
  {
    return node;
  }

  public final TreeViewer getNodeViewer()
  {
    return nodeViewer;
  }

  @Override
  public void createControl(Composite parent)
  {
    setTitle(title);

    Composite container = new Composite(parent, SWT.NULL);
    GridLayout containerGridLayout = new GridLayout();
    containerGridLayout.numColumns = 2;
    container.setLayout(containerGridLayout);
    setControl(container);

    doCreateControl(container);

    Shell shell = getShell();
    UIUtil.asyncExec(shell.getDisplay(), () -> {
      try
      {
        shell.setText(title);
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }

      revealNode();
      validate();
      deferredInit();
    });
  }

  protected void doCreateControl(Composite parent)
  {
    Label nodeLabel = new Label(parent, SWT.NONE);
    nodeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    nodeLabel.setText(getNodeMessage() + ":");

    nodeViewer = createNodeViewer(parent);
    nodeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    nodeViewer.addSelectionChangedListener(event -> nodeSelectionChanged(event));
  }

  protected abstract String getNodeMessage();

  protected TreeViewer createNodeViewer(Composite container)
  {
    return CDOCheckoutContentProvider.createTreeViewer(container);
  }

  protected void deferredInit()
  {
  }

  protected final Pair<CDOCheckout, CDOResourceNode> getNodeInfo()
  {
    if (node instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)node;
      EObject parentNode = checkout.getRootObject();
      if (parentNode instanceof CDOResourceNode)
      {
        return Pair.create(checkout, (CDOResourceNode)parentNode);
      }
    }
    else if (node instanceof CDOResourceNode)
    {
      CDOResourceNode parentNode = (CDOResourceNode)node;
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(parentNode);
      if (checkout != null)
      {
        return Pair.create(checkout, parentNode);
      }
    }

    return null;
  }

  protected final EList<EObject> getNodeChildren()
  {
    if (node instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)node;
      return checkout.getRootObject().eContents();
    }

    if (node instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)node;
      return folder.eContents();
    }

    return ECollections.emptyEList();
  }

  protected final void validate()
  {
    try
    {
      doValidate();

      setErrorMessage(null);
      setPageComplete(true);
    }
    catch (Exception ex)
    {
      setErrorMessage(ex.getMessage());
      setPageComplete(false);
    }
  }

  protected void doValidate() throws Exception
  {
    if (node == null)
    {
      throw new Exception(getNodeMessage() + ".");
    }
  }

  private void nodeSelectionChanged(SelectionChangedEvent event)
  {
    Object oldNode = node;
    if (nodeRevealed)
    {
      IStructuredSelection selection = (IStructuredSelection)event.getSelection();
      node = getNode(selection);
    }

    if (!Objects.equals(node, oldNode))
    {
      nodeSelectionChanged(oldNode, node);
    }

    validate();
  }

  protected void nodeSelectionChanged(Object oldNode, Object newNode)
  {
  }

  private void revealNode()
  {
    if (node != null)
    {
      List<Object> segments = new ArrayList<>();
      fillSegments(segments, node);

      for (int i = 0; i < segments.size() - 1; i++)
      {
        Object segment = segments.get(i);
        nodeViewer.setExpandedState(segment, true);
      }
    }
    else
    {
      IStructuredContentProvider contentProvider = (IStructuredContentProvider)nodeViewer.getContentProvider();
      Object[] elements = contentProvider.getElements(nodeViewer.getInput());
      if (elements != null && elements.length != 0)
      {
        if (elements[0] instanceof CDOCheckout || elements[0] instanceof CDOResourceNode)
        {
          node = elements[0];
        }
      }
    }

    if (node != null)
    {
      nodeViewer.setSelection(new StructuredSelection(node), true);
    }

    nodeRevealed = true;
  }

  private void fillSegments(List<Object> segments, Object node)
  {
    if (node instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)node;

      Adapter adapter = EcoreUtil.getAdapter(folder.eAdapters(), CDOCheckout.class);
      if (adapter != null)
      {
        fillSegments(segments, adapter);
      }
      else
      {
        Object parent = folder.getFolder();
        if (parent == null)
        {
          parent = EcoreUtil.getAdapter(folder.cdoView().getRootResource().eAdapters(), CDOCheckout.class);
        }

        fillSegments(segments, parent);
      }
    }

    segments.add(node);
  }

  private static Object getNode(IStructuredSelection selection)
  {
    if (selection != null)
    {
      Object element = selection.getFirstElement();
      if (element instanceof CDOCheckout || element instanceof CDOResourceNode)
      {
        return element;
      }
    }

    return null;
  }

  protected static boolean isContainer(Object node)
  {
    return node instanceof CDOCheckout || node instanceof CDOResourceFolder;
  }
}
