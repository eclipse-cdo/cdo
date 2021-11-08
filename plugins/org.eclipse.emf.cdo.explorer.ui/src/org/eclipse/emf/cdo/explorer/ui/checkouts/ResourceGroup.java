/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.util.CDORenameContext;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.properties.ExplorerUIAdapterFactory;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutManagerImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Eike Stepper
 */
public class ResourceGroup extends CDOElement
{
  private String name;

  public ResourceGroup(CDOResourceNode delegate)
  {
    super(delegate);
    reset();
  }

  @Override
  public CDOResourceNode getDelegate()
  {
    return (CDOResourceNode)super.getDelegate();
  }

  @Override
  public void reset()
  {
    super.reset();
    name = getDelegate().trimExtension();
  }

  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
  {
    if (adapter == CDORenameContext.class)
    {
      CDOResourceNode delegate = getDelegate();
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(delegate);
      if (checkout != null && checkout.isOpen() && !checkout.isReadOnly())
      {
        return new CDORenameContext.WithElement()
        {
          @Override
          public Object getElement()
          {
            return delegate;
          }

          @Override
          public String getType()
          {
            return "Resource Group";
          }

          @Override
          public String getName()
          {
            return name;
          }

          @Override
          public void setName(final String name)
          {
            final String type = getType();
            final String title = "Rename " + type.toLowerCase();

            new Job(title)
            {
              @Override
              protected IStatus run(IProgressMonitor monitor)
              {
                CDOResourceNode resourceNode = getDelegate();
                CDOCheckout checkout = CDOExplorerUtil.getCheckout(resourceNode);
                CDOTransaction transaction = checkout.openTransaction();

                CDOCommitInfo commitInfo = null;

                try
                {
                  for (Object child : getChildren())
                  {
                    if (child instanceof CDOResourceNode)
                    {
                      CDOResourceNode childNode = (CDOResourceNode)child;
                      CDOResourceNode transactionalChildNode = transaction.getObject(childNode);
                      String extension = transactionalChildNode.getExtension();

                      transactionalChildNode.setName(name + "." + extension);
                    }
                  }

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
                  if (!checkout.getView().waitForUpdate(commitInfo.getTimeStamp(), 10000))
                  {
                    OM.LOG.error(title + ": Did not receive an update");
                    return Status.OK_STATUS;
                  }

                  CDOCheckoutManagerImpl checkoutManager = (CDOCheckoutManagerImpl)CDOExplorerUtil.getCheckoutManager();
                  checkoutManager.fireElementChangedEvent(ElementsChangedEvent.StructuralImpact.PARENT, ResourceGroup.this);
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

            for (Object child : getChildren())
            {
              if (child instanceof CDOResourceNode)
              {
                CDOResourceNode childNode = (CDOResourceNode)child;
                String extension = childNode.getExtension();

                String error = ExplorerUIAdapterFactory.checkUniqueName(childNode, name + "." + extension, type);
                if (error != null)
                {
                  return error;
                }
              }
            }

            return null;
          }
        };
      }
    }

    return super.getAdapter(adapter);
  }

  @Override
  public String toString(Object child)
  {
    return ((CDOResourceNode)child).getExtension();
  }

  @Override
  public String toString()
  {
    return name;
  }
}
