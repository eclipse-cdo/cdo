/*
 * Copyright (c) 2010-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *     Christian W. Damus (CEA) - bug 404043: contents of views not shown in Dawn Explorer
 */
package org.eclipse.emf.cdo.dawn.ui.views;

import org.eclipse.emf.cdo.dawn.ui.helper.EditorDescriptionHelper;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.view.CDOView;

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
    Object[] result = super.getChildren(element);

    if (result.length > 0 && result[0] instanceof CDOView)
    {
      // filter the views to show only our view
      CDOView ourView = null;
      for (int i = 0; i < result.length; i++)
      {
        if (result[i] == dawnExplorer.getView())
        {
          ourView = (CDOView)result[i];
          break;
        }
      }

      if (ourView != null)
      {
        result = new Object[] { ourView };
      } // otherwise, we're showing something totally else, so don't worry about it

      return result;
    }

    return result;
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
