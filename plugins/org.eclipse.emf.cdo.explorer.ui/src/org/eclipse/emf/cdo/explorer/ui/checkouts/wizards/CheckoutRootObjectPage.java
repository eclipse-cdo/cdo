/*
 * Copyright (c) 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutViewerSorter;
import org.eclipse.emf.cdo.explorer.ui.repositories.CDORepositoryItemProvider;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CheckoutRootObjectPage extends CheckoutWizardPage
{
  private static final IContainer<Object> LOADING_INPUT = ContainerItemProvider.createSlowInput("Loading...");

  private CDOID rootID;

  private String rootObjectText;

  private TreeViewer treeViewer;

  private CDOView view;

  private CDORepository repository;

  public CheckoutRootObjectPage()
  {
    super("Root Object", "Select the root object of the new checkout.");
  }

  public final CDOID getRootID()
  {
    return rootID;
  }

  public final void setRootID(CDOID rootID)
  {
    if (this.rootID != rootID)
    {
      log("Setting root id to " + rootID);
      this.rootID = rootID;
      rootObjectChanged(rootID);
    }
  }

  public String getRootObjectText()
  {
    if (rootObjectText == null)
    {
      return repository.getLabel();
    }

    return rootObjectText;
  }

  @Override
  public void dispose()
  {
    closeView();
    super.dispose();
  }

  @Override
  protected void createUI(Composite parent)
  {
    final CDOItemProvider itemProvider = new CDOItemProvider(null)
    {
      private final ComposedAdapterFactory adapterFactory = CDOEditor.createAdapterFactory(true);

      private final ITreeContentProvider contentProvider = new AdapterFactoryContentProvider(adapterFactory);

      private final ILabelProvider labelProvider = new AdapterFactoryLabelProvider(adapterFactory);

      @Override
      public void dispose()
      {
        labelProvider.dispose();
        contentProvider.dispose();
        adapterFactory.dispose();
        super.dispose();
      }

      @Override
      public boolean hasChildren(Object element)
      {
        if (view != null)
        {
          CDOResource rootResource = view.getRootResource();
          if (element == repository)
          {
            return true;
          }

          if (element == rootResource)
          {
            return !rootResource.getContents().isEmpty();
          }
        }

        if (element instanceof EObject)
        {
          return contentProvider.hasChildren(element);
        }

        return super.hasChildren(element);
      }

      @Override
      public Object[] getChildren(Object element)
      {
        if (view != null)
        {
          CDOResource rootResource = view.getRootResource();
          if (element == repository)
          {
            return new Object[] { rootResource };
          }

          if (element == rootResource)
          {
            return rootResource.getContents().toArray();
          }
        }

        if (element instanceof EObject)
        {
          return contentProvider.getChildren(element);
        }

        return super.getChildren(element);
      }

      @Override
      public Object getParent(Object element)
      {
        if (view != null)
        {
          CDOResource rootResource = view.getRootResource();
          if (element == rootResource)
          {
            return repository;
          }
        }

        if (element instanceof EObject)
        {
          return contentProvider.getParent(element);
        }

        return super.getParent(element);
      }

      @Override
      public Image getImage(Object element)
      {
        if (view != null)
        {
          if (element == view.getRootResource())
          {
            return CDORepositoryItemProvider.REPOSITORY_IMAGE;
          }
        }

        if (element instanceof EObject)
        {
          return labelProvider.getImage(element);
        }

        return super.getImage(element);
      }

      @Override
      public String getText(Object element)
      {
        if (view != null)
        {
          if (element == view.getRootResource())
          {
            return repository.getLabel();
          }
        }

        if (element instanceof EObject)
        {
          return labelProvider.getText(element);
        }

        return super.getText(element);
      }

      @Override
      public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
      {
        // Do nothing.
      }
    };

    treeViewer = new TreeViewer(parent, SWT.BORDER);
    treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    treeViewer.setContentProvider(itemProvider);
    treeViewer.setLabelProvider(itemProvider);
    treeViewer.setComparator(new CDOCheckoutViewerSorter());
    treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
        Object element = selection.getFirstElement();
        if (element instanceof EObject)
        {
          EObject eObject = (EObject)element;
          CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
          rootID = cdoObject.cdoID();

          if (cdoObject != view.getRootResource())
          {
            rootObjectText = getText(itemProvider, cdoObject);
          }
        }

        validate();
      }

      private String getText(final CDOItemProvider itemProvider, EObject object)
      {
        String text = itemProvider.getText(object);
        for (int i = 0; i < AbstractElement.ILLEGAL_LABEL_CHARACTERS.length(); i++)
        {
          text = text.replace(AbstractElement.ILLEGAL_LABEL_CHARACTERS.charAt(i), '.');
        }

        Object parent = itemProvider.getParent(object);
        if (parent instanceof EObject)
        {
          text = getText(itemProvider, (EObject)parent) + "." + text;
        }

        return text;
      }
    });

    treeViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        showNextPage();
      }
    });

    treeViewer.setInput(LOADING_INPUT);
  }

  @Override
  protected boolean doValidate() throws ValidationProblem
  {
    return true;
  }

  @Override
  protected void repositoryChanged(CDORepository repository)
  {
    this.repository = repository;
    closeView();
    super.repositoryChanged(repository);
  }

  @Override
  protected void branchPointChanged(int branchID, long timeStamp)
  {
    closeView();
    super.branchPointChanged(branchID, timeStamp);
  }

  @Override
  protected void pageActivated()
  {
    if (view == null)
    {
      CDOSession session = repository.getSession();
      CDOBranchPoint branchPoint = getWizard().getBranchPointPage().getBranchPoint();

      log("Opening view to " + repository);
      view = session.openView(branchPoint);
      treeViewer.setInput(repository);
      rootObjectText = null;

      getShell().getDisplay().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          CDOResource rootResource = view.getRootResource();
          treeViewer.setSelection(new StructuredSelection(rootResource));
          treeViewer.expandToLevel(rootResource, 1);
        }
      });
    }
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    if (rootID != null)
    {
      properties.setProperty(CDOCheckoutImpl.PROP_ROOT_ID, CDOExplorerUtil.getCDOIDString(rootID));
    }
  }

  private void closeView()
  {
    if (view != null)
    {
      // Prevent the item provider to use the view while it's closing.
      CDOView oldView = view;
      view = null;

      if (treeViewer != null)
      {
        treeViewer.setSelection(StructuredSelection.EMPTY);
        treeViewer.setInput(LOADING_INPUT);
      }

      log("Closing view to " + repository);
      oldView.close();
    }
  }
}
