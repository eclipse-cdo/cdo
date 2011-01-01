/*******************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.ui.views;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
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
    if (element instanceof CDOResourceFolder)
    {
      return ((CDOResourceFolder)element).getNodes().toArray();
    }

    if (element instanceof CDOSession)
    {
      CDOSession session = (CDOSession)element;
      Object[] child = new Object[1];
      child[0] = session.getViews()[0];// .getView(dawnExplorer.getView().getViewID());
      return child;
    }

    return super.getChildren(element);
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
