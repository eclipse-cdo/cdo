/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.views;

import org.eclipse.emf.cdo.dawn.ui.helper.EditorDescriptionHelper;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Martin Fluegge
 */
public class DawnItemProvider extends CDOItemProvider
{
  private final DawnExplorer dawnExplorer;

  public DawnItemProvider(IWorkbenchPage page, DawnExplorer dawnExplorer, IElementFilter rootElementFilter)
  {
    super(page, rootElementFilter);
    this.dawnExplorer = dawnExplorer;
  }

  @Override
  public Object[] getChildren(Object element)
  {
    // if (element instanceof CDOView)
    // {
    // return ((CDOView)element).getRootResource().getContents().toArray();
    // }

    if (element instanceof CDOResourceFolder)
    {
      return ((CDOResourceFolder)element).getNodes().toArray();
    }

    if (element instanceof CDOSession)
    {
      CDOSession session = (CDOSession)element;
      Object[] child = new Object[1];
      child[0] = session.getView(dawnExplorer.getView().getViewID());
      return child;
    }

    return super.getChildren(element);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof CDOResource)
    {
      CDOResource resource = (CDOResource)obj;
      Image img = EditorDescriptionHelper.getImageForEditor(resource.getName());
      if (img != null)
      {
        return img;
      }
    }

    return super.getImage(obj);
  }
}
