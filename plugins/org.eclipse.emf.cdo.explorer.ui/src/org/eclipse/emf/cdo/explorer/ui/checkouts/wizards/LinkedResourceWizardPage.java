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

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutContentProvider;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LinkedResourceWizardPage extends CheckoutNodeWizardPage
{
  private static final String CONTAINER_MESSAGE = "Select the workspace container of the new link";

  private static final String NAME_MESSAGE = "Enter the name of the new link";

  private static final IWorkspaceRoot ROOT = ResourcesPlugin.getWorkspace().getRoot();

  private TreeViewer workspaceViewer;

  private IContainer targetContainer;

  private Text nameText;

  private String name;

  public LinkedResourceWizardPage(IStructuredSelection selection)
  {
    super("LinkPage", "Linked Resource", selection);
    targetContainer = getContainer(selection);
  }

  public final IContainer getTargetContainer()
  {
    return targetContainer;
  }

  @Override
  public final String getName()
  {
    return name;
  }

  @Override
  protected void doCreateControl(Composite parent)
  {
    setImageDescriptor(OM.getImageDescriptor("icons/wiz/new_link.gif"));
    setMessage("Enter the name of the " + title.toLowerCase() + ".");

    super.doCreateControl(parent);

    Label workspaceLabel = new Label(parent, SWT.NONE);
    workspaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    workspaceLabel.setText(CONTAINER_MESSAGE + ":");

    workspaceViewer = new TreeViewer(parent, SWT.BORDER);
    workspaceViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    workspaceViewer.setContentProvider(new ContainerContentProvider());
    workspaceViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
    workspaceViewer.setComparator(new ViewerComparator());
    workspaceViewer.setUseHashlookup(true);
    workspaceViewer.addSelectionChangedListener(event -> containerSelectionChanged(getContainer(event.getStructuredSelection())));
    workspaceViewer.addDoubleClickListener(event -> {
      Object item = workspaceViewer.getStructuredSelection().getFirstElement();
      if (item != null)
      {
        if (workspaceViewer.getExpandedState(item))
        {
          workspaceViewer.collapseToLevel(item, 1);
        }
        else
        {
          workspaceViewer.expandToLevel(item, 1);
        }
      }
    });

    workspaceViewer.setInput(ROOT);
    workspaceViewer.expandToLevel(1);

    if (targetContainer != null)
    {
      UIUtil.asyncExec(parent.getDisplay(), () -> workspaceViewer.setSelection(new StructuredSelection(targetContainer), true));
    }

    Label nameLabel = new Label(parent, SWT.NONE);
    nameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    nameLabel.setText(NAME_MESSAGE + ":");

    nameText = new Text(parent, SWT.BORDER);
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    nameText.addModifyListener(e -> {
      name = nameText.getText();
      validate();
    });
  }

  @Override
  protected void nodeSelectionChanged(Object oldNode, Object newNode)
  {
    updateName();
  }

  private void containerSelectionChanged(IContainer container)
  {
    targetContainer = container;
    validate();

    // fire an event so the parent can update its controls
    // if (listener != null)
    // {
    // Event changeEvent = new Event();
    // changeEvent.type = SWT.Selection;
    // changeEvent.widget = this;
    // listener.handleEvent(changeEvent);
    // }
  }

  private void updateName()
  {
    String newName = null;

    Object node = getNode();
    if (node != null)
    {
      if (node instanceof CDOCheckout)
      {
        newName = ((CDOCheckout)node).getLabel();
      }
      else
      {
        newName = ((CDOResourceNode)node).getName();
      }
    }

    nameText.setText(StringUtil.safe(newName));
  }

  @Override
  protected String getNodeMessage()
  {
    return "Select the node to be linked into the workspace";
  }

  @Override
  protected TreeViewer createNodeViewer(Composite container)
  {
    return CDOCheckoutContentProvider.createTreeViewer(container, child -> true);
  }

  @Override
  protected void deferredInit()
  {
    super.deferredInit();
    updateName();
  }

  @Override
  protected void doValidate() throws Exception
  {
    super.doValidate();

    if (targetContainer == null)
    {
      throw new Exception(CONTAINER_MESSAGE + ".");
    }

    if (name.length() == 0)
    {
      throw new Exception("Enter a name for the new link.");
    }

    IResource existingResource = targetContainer.findMember(name);
    if (existingResource != null && existingResource.exists())
    {
      throw new Exception("Name is not unique within the selected workspace container.");
    }
  }

  private static IContainer getContainer(IStructuredSelection selection)
  {
    Object element = selection.getFirstElement();
    if (element instanceof IContainer)
    {
      return (IContainer)element;
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ContainerContentProvider implements ITreeContentProvider
  {
    public ContainerContentProvider()
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public Object[] getChildren(Object element)
    {
      if (element instanceof IContainer)
      {
        IContainer container = (IContainer)element;
        if (isValid(container))
        {
          try
          {
            List<IResource> children = new ArrayList<>();
            for (IResource member : container.members())
            {
              if (member.getType() != IResource.FILE && isValid(member))
              {
                children.add(member);
              }
            }

            return children.toArray();
          }
          catch (CoreException ex)
          {
            // This should never happen because we call #isAccessible before invoking #members.
          }
        }
      }

      return new Object[0];
    }

    @Override
    public Object[] getElements(Object element)
    {
      return getChildren(element);
    }

    @Override
    public Object getParent(Object element)
    {
      if (element instanceof IProject)
      {
        return ROOT;
      }

      if (element instanceof IResource)
      {
        return ((IResource)element).getParent();
      }

      return null;
    }

    @Override
    public boolean hasChildren(Object element)
    {
      return getChildren(element).length > 0;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    private boolean isValid(IResource resource)
    {
      return resource.isAccessible() && !resource.isLinked() && !resource.isVirtual();
    }
  }
}
