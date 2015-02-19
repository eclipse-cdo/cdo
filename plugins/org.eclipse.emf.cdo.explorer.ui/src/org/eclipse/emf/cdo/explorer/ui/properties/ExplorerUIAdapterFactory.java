/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
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

/**
 * @since 4.4
 */
@SuppressWarnings("rawtypes")
public class ExplorerUIAdapterFactory implements IAdapterFactory
{
  private static final Class[] CLASSES = { ExplorerRenameContext.class };

  public ExplorerUIAdapterFactory()
  {
  }

  public Class[] getAdapterList()
  {
    return CLASSES;
  }

  public Object getAdapter(Object adaptableObject, Class adapterType)
  {
    if (adapterType == CLASSES[0])
    {
      if (adaptableObject instanceof AbstractElement)
      {
        AbstractElement element = (AbstractElement)adaptableObject;
        return createRenameContext(element);
      }
      else if (adaptableObject instanceof CDOBranch)
      {
        CDOBranch branch = (CDOBranch)adaptableObject;
        return createRenameContext(branch);
      }
      else if (adaptableObject instanceof CDOResourceNode)
      {
        CDOResourceNode resourceNode = (CDOResourceNode)adaptableObject;
        return createRenameContext(resourceNode);
      }
    }

    return null;
  }

  private Object createRenameContext(final AbstractElement element)
  {
    return new ExplorerRenameContext()
    {
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

      public String getName()
      {
        return element.getLabel();
      }

      public void setName(String name)
      {
        element.setLabel(name);
      }

      public String validateName(String name)
      {
        return element.validateLabel(name);
      }
    };
  }

  private Object createRenameContext(final CDOBranch branch)
  {
    return new ExplorerRenameContext()
    {
      public String getType()
      {
        return "Branch";
      }

      public String getName()
      {
        return branch.getName();
      }

      public void setName(String name)
      {
        branch.setName(name);
      }

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
    return new ExplorerRenameContext()
    {
      public String getType()
      {
        return resourceNode instanceof CDOResourceFolder ? "Folder" : "Resource";
      }

      public String getName()
      {
        return resourceNode.getName();
      }

      public void setName(final String name)
      {
        new Job("Rename " + getType().toLowerCase())
        {
          @Override
          protected IStatus run(IProgressMonitor monitor)
          {
            CDOCheckout checkout = CDOExplorerUtil.getCheckout(resourceNode);
            CDOTransaction transaction = checkout.openTransaction();

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

            if (commitInfo != null)
            {
              checkout.getView().waitForUpdate(commitInfo.getTimeStamp());
              CDOCheckoutManagerImpl checkoutManager = (CDOCheckoutManagerImpl)CDOExplorerUtil.getCheckoutManager();
              checkoutManager.fireElementChangedEvent(resourceNode, true);
            }

            return Status.OK_STATUS;
          }
        }.schedule();
      }

      public String validateName(String name)
      {
        if (StringUtil.isEmpty(name))
        {
          return getType() + " name is empty.";
        }

        if (name.equals(getName()))
        {
          return null;
        }

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
                return getType() + " name is not unique within the root resource.";
              }
            }
          }
        }
        else if (parentFolder.getNode(name) != null)
        {
          return getType() + " name is not unique within the parent folder.";
        }

        return null;
      }
    };
  }
}
