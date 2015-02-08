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
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.core.runtime.IAdapterFactory;

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
        final AbstractElement element = (AbstractElement)adaptableObject;
        return createRenameContext(element);
      }
      else if (adaptableObject instanceof CDOBranch)
      {
        final CDOBranch branch = (CDOBranch)adaptableObject;
        return createRenameContext(branch);
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
}
