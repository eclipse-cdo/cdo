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
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.emf.cdo.CDOElement.StateProvider;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.util.CDORenameContext;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutState;
import org.eclipse.emf.cdo.explorer.ui.repositories.CDORepositoryItemProvider;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutManagerImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * @since 4.4
 */
public class ExplorerUIAdapterFactory implements IAdapterFactory
{
  private static final Class<CDORenameContext> CLASS_EXPLORER_RENAME_CONTEXT = CDORenameContext.class;

  private static final Class<StateProvider> CLASS_STATE_PROVIDER = StateProvider.class;

  private static final Class<IWorkbenchAdapter> CLASS_WORKBENCH_ADAPTER = IWorkbenchAdapter.class;

  private static final Class<?>[] CLASSES = { CLASS_EXPLORER_RENAME_CONTEXT, CLASS_STATE_PROVIDER, CLASS_WORKBENCH_ADAPTER };

  public ExplorerUIAdapterFactory()
  {
  }

  @Override
  public Class<?>[] getAdapterList()
  {
    return CLASSES;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType)
  {
    if (adapterType == CLASS_EXPLORER_RENAME_CONTEXT)
    {
      if (adaptableObject instanceof AbstractElement)
      {
        AbstractElement element = (AbstractElement)adaptableObject;
        return (T)createRenameContext(element);
      }

      if (adaptableObject instanceof CDOBranch)
      {
        CDOBranch branch = (CDOBranch)adaptableObject;
        return (T)createRenameContext(branch);
      }

      if (adaptableObject instanceof CDOResourceNode)
      {
        CDOResourceNode resourceNode = (CDOResourceNode)adaptableObject;
        CDOCheckout checkout = CDOExplorerUtil.getCheckout(resourceNode);
        if (checkout != null && !checkout.isReadOnly())
        {
          return (T)createRenameContext(resourceNode);
        }
      }
    }
    else if (adapterType == CLASS_STATE_PROVIDER)
    {
      if (adaptableObject instanceof EObject)
      {
        EObject eObject = (EObject)adaptableObject;
        CDOCheckout checkout = CDOExplorerUtil.getCheckout(eObject);
        if (checkout != null)
        {
          return (T)checkout;
        }
      }
    }
    else if (adapterType == CLASS_WORKBENCH_ADAPTER)
    {
      if (adaptableObject instanceof AbstractElement)
      {
        return (T)ExplorerWorkbenchAdapter.INSTANCE;
      }
    }

    return null;
  }

  private Object createRenameContext(final AbstractElement element)
  {
    return new CDORenameContext()
    {
      @Override
      public String getType()
      {
        String type = StringUtil.capAll(element.getType());
        if (element instanceof CDORepository)
        {
          type += " Repository";
        }
        else if (element instanceof CDOCheckout)
        {
          type += " Checkout";
        }

        return type;
      }

      @Override
      public String getName()
      {
        return element.getLabel();
      }

      @Override
      public void setName(String name)
      {
        element.setLabel(name);
      }

      @Override
      public String validateName(String name)
      {
        return element.validateLabel(name);
      }
    };
  }

  private Object createRenameContext(final CDOBranch branch)
  {
    return new CDORenameContext()
    {
      @Override
      public String getType()
      {
        return "Branch";
      }

      @Override
      public String getName()
      {
        return branch.getName();
      }

      @Override
      public void setName(String name)
      {
        branch.setName(name);
      }

      @Override
      public String validateName(String name)
      {
        if (StringUtil.isEmpty(name))
        {
          return "Branch name is empty.";
        }

        if (name.equals(getName()))
        {
          return null;
        }

        CDOBranch baseBranch = branch.getBase().getBranch();
        if (baseBranch.getBranch(name) != null)
        {
          return "Branch name is not unique within the base branch.";
        }

        return null;
      }
    };
  }

  private Object createRenameContext(final CDOResourceNode resourceNode)
  {
    return new CDORenameContext()
    {
      @Override
      public String getType()
      {
        return resourceNode instanceof CDOResourceFolder ? "Folder" : "Resource";
      }

      @Override
      public String getName()
      {
        return resourceNode.getName();
      }

      @Override
      public void setName(final String name)
      {
        final String title = "Rename " + getType().toLowerCase();

        new Job(title)
        {
          @Override
          protected IStatus run(IProgressMonitor monitor)
          {
            CDOTransaction transaction;

            CDOCheckout checkout = CDOExplorerUtil.getCheckout(resourceNode);
            if (checkout != null)
            {
              transaction = checkout.openTransaction();
            }
            else
            {
              CDOView view = resourceNode.cdoView();
              transaction = view.getSession().openTransaction(view.getBranch());
            }

            CDOCommitInfo commitInfo = null;

            try
            {
              CDOResourceNode transactionalResourceNode = transaction.getObject(resourceNode);
              transactionalResourceNode.setName(name);

              commitInfo = transaction.commit();
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
            finally
            {
              transaction.close();
            }

            if (commitInfo != null && checkout != null)
            {
              if (!checkout.getView().waitForUpdate(commitInfo.getTimeStamp(), 10000))
              {
                OM.LOG.error(title + ": Did not receive an update");
                return Status.OK_STATUS;
              }

              CDOCheckoutManagerImpl checkoutManager = (CDOCheckoutManagerImpl)CDOExplorerUtil.getCheckoutManager();
              checkoutManager.fireElementChangedEvent(ElementsChangedEvent.StructuralImpact.PARENT, resourceNode);
            }

            return Status.OK_STATUS;
          }
        }.schedule();
      }

      @Override
      public String validateName(String name)
      {
        String type = getType();
        if (StringUtil.isEmpty(name))
        {
          return type + " name is empty.";
        }

        if (name.equals(getName()))
        {
          return null;
        }

        return checkUniqueName(resourceNode, name, type);
      }
    };
  }

  public static String checkUniqueName(final CDOResourceNode resourceNode, String name, String type)
  {
    CDOResourceFolder parentFolder = resourceNode.getFolder();
    if (parentFolder == null)
    {
      CDOView view = resourceNode.cdoView();
      for (EObject eObject : view.getRootResource().getContents())
      {
        if (eObject instanceof CDOResourceNode)
        {
          CDOResourceNode child = (CDOResourceNode)eObject;
          if (ObjectUtil.equals(child.getName(), name))
          {
            return type + " name is not unique within the root resource.";
          }
        }
      }
    }
    else if (parentFolder.getNode(name) != null)
    {
      return type + " name is not unique within the parent folder.";
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ExplorerWorkbenchAdapter extends WorkbenchAdapter
  {
    public static final IWorkbenchAdapter INSTANCE = new ExplorerWorkbenchAdapter();

    private static final ImageDescriptor REPOSITORY_IMAGE_DESCRIPTOR = ImageDescriptor.createFromImage(CDORepositoryItemProvider.REPOSITORY_IMAGE);

    private static final ImageDescriptor CHECKOUT_IMAGE_DESCRIPTOR = ImageDescriptor.createFromImage(CDOCheckoutState.CHECKOUT_IMAGE);

    private static final ImageDescriptor CHECKOUT_CLOSED_IMAGE_DESCRIPTOR = ImageDescriptor.createFromImage(CDOCheckoutState.CHECKOUT_CLOSED_IMAGE);

    private ExplorerWorkbenchAdapter()
    {
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object)
    {
      if (object instanceof CDORepository)
      {
        return REPOSITORY_IMAGE_DESCRIPTOR;
      }

      if (object instanceof CDOCheckout)
      {
        CDOCheckout checkout = (CDOCheckout)object;
        if (checkout.isOpen())
        {
          return CHECKOUT_IMAGE_DESCRIPTOR;
        }

        return CHECKOUT_CLOSED_IMAGE_DESCRIPTOR;
      }

      return super.getImageDescriptor(object);
    }

    @Override
    public String getLabel(Object object)
    {
      if (object instanceof CDOExplorerElement)
      {
        CDOExplorerElement element = (CDOExplorerElement)object;
        return element.getLabel();
      }

      return super.getLabel(object);
    }
  }
}
