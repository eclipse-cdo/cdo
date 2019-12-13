/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutContentProvider;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class NewWizardPage extends WizardPage
{
  private final String resourceType;

  private final String title;

  private TreeViewer parentViewer;

  private boolean parentRevealed;

  private Object parent;

  private Text nameText;

  private boolean nameModified;

  private String name;

  public NewWizardPage(String resourceType, String title, IStructuredSelection selection)
  {
    super("NewPage");
    this.resourceType = resourceType;
    this.title = title;
    parent = selection.getFirstElement();
  }

  @Override
  public NewWizard getWizard()
  {
    return (NewWizard)super.getWizard();
  }

  public final Object getParent()
  {
    return parent;
  }

  @Override
  public final String getName()
  {
    return name;
  }

  @Override
  public void createControl(Composite parentControl)
  {
    setTitle(title);
    setImageDescriptor(OM.getImageDescriptor("icons/wiz/new_" + resourceType + ".gif"));
    setMessage("Enter the name of the " + title.toLowerCase() + ".");

    Composite container = new Composite(parentControl, SWT.NULL);
    GridLayout containerGridLayout = new GridLayout();
    containerGridLayout.numColumns = 2;
    container.setLayout(containerGridLayout);
    setControl(container);

    Label parentLabel = new Label(container, SWT.NONE);
    parentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    parentLabel.setText("Select the parent folder:");

    parentViewer = CDOCheckoutContentProvider.createTreeViewer(container);
    parentViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    parentViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        if (parentRevealed)
        {
          IStructuredSelection selection = (IStructuredSelection)event.getSelection();
          parent = selection.getFirstElement();
        }

        validate();
      }
    });

    Label nameLabel = new Label(container, SWT.NONE);
    nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    nameLabel.setText("Name:");

    nameText = new Text(container, SWT.BORDER);
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    nameText.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        nameModified = true;
        validate();
      }
    });

    getShell().getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          getShell().setText(title);
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }

        revealParent();
        validate();

        nameText.setFocus();
        nameText.selectAll();
      }
    });
  }

  private void revealParent()
  {
    if (parent != null)
    {
      List<Object> segments = new ArrayList<Object>();
      fillSegments(segments, parent);

      for (int i = 0; i < segments.size() - 1; i++)
      {
        Object segment = segments.get(i);
        parentViewer.setExpandedState(segment, true);
      }

      parentRevealed = true;
      parentViewer.setSelection(new StructuredSelection(parent), true);
    }
  }

  private void fillSegments(List<Object> segments, Object node)
  {
    if (node instanceof CDOCheckout)
    {
      segments.add(node);
      return;
    }

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

  private void validate()
  {
    try
    {
      String name = nameText.getText();
      if (!nameModified)
      {
        name = getUniqueName();
        nameText.setText(name);
      }
      else
      {
        if (name.length() == 0)
        {
          // throw new Exception("Name is empty.");
        }

        if (!isUnique(name))
        {
          throw new Exception("Name is not unique within the parent folder.");
        }

        this.name = name;
      }

      setErrorMessage(null);
      setPageComplete(true);
    }
    catch (Exception ex)
    {
      setErrorMessage(ex.getMessage());
      setPageComplete(false);
    }
  }

  private EList<EObject> getChildrenOfParent()
  {
    if (parent instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)parent;
      return checkout.getRootObject().eContents();
    }

    if (parent instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)parent;
      return folder.eContents();
    }

    return ECollections.emptyEList();
  }

  private String getUniqueName()
  {
    Set<String> names = new HashSet<String>();
    for (EObject eObject : getChildrenOfParent())
    {
      if (eObject instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)eObject;
        String name = node.getName();
        if (name.startsWith(resourceType))
        {
          names.add(name);
        }
      }
    }

    for (int i = 1; i < Integer.MAX_VALUE; i++)
    {
      String name = resourceType + i;
      if (!names.contains(name))
      {
        return name;
      }
    }

    throw new IllegalStateException("Too many children");
  }

  private boolean isUnique(String name)
  {
    for (EObject eObject : getChildrenOfParent())
    {
      if (eObject instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)eObject;
        if (ObjectUtil.equals(node.getName(), name))
        {
          return false;
        }
      }
    }

    return true;
  }
}
