/*
 * Copyright (c) 2010-2012, 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *     Christian W. Damus (CEA) - bug 436036
 */
package org.eclipse.emf.cdo.dawn.ui.views;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.views.IElementFilter;

/**
 * @author Martin Fluegge
 * @param <CONTAINER>
 */
public class DawnWizardPageItemProvider<CONTAINER extends IContainer<Object>> extends CDOItemProvider
{

  public DawnWizardPageItemProvider(IElementFilter iElementFilter)
  {
    super(null, iElementFilter);
  }

  @Override
  public Object[] getChildren(Object element)
  {
    Object[] result = super.getChildren(element);

    if (result.length > 0 && result[0] instanceof CDOView)
    {
      // filter the views to show only the first view
      if (result.length > 1)
      {
        result = new Object[] { result[0] };
      }
    }

    return result;
  }

  @Override
  public boolean hasChildren(Object element)
  {
    if (element instanceof CDOView)
    {
      return ((CDOView)element).getRootResource().getContents().size() > 0;
    }

    if (element instanceof CDOResourceFolder)
    {
      return ((CDOResourceFolder)element).getNodes().size() > 0;
    }

    return super.hasChildren(element);
  }

  @Override
  public Object getParent(Object element)
  {
    if (element instanceof CDOResourceNode)
    {
      CDOResourceNode node = (CDOResourceNode)element;
      CDOResourceNode parent = (CDOResourceNode)node.eContainer();
      if (parent.isRoot())
      {
        return parent.cdoView();
      }

      return parent;
    }

    return super.getParent(element);
  }
}
