/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.internal.ui.dialogs.RepositoryResourceSelectionDialog;
import org.eclipse.emf.cdo.ui.CDOLoadResourceProvider;
import org.eclipse.emf.cdo.ui.CDOLoadResourceProvider.ImageProvider;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RepositoryLoadResourceProvider implements CDOLoadResourceProvider, ImageProvider
{
  public RepositoryLoadResourceProvider()
  {
  }

  @Override
  public String getButtonText(ResourceSet resourceSet)
  {
    int viewCount = 0;

    CDOViewSet viewSet = CDOUtil.getViewSet(resourceSet);
    if (viewSet != null)
    {
      for (CDOView view : viewSet.getViews())
      {
        if (!view.isClosed())
        {
          if (++viewCount > 1)
          {
            break;
          }
        }
      }
    }

    return viewCount == 1 ? "&Repository..." : "&Repositories...";
  }

  @Override
  public Image getButtonImage(ResourceSet resourceSet)
  {
    return SharedIcons.getImage(SharedIcons.OBJ_REPO);
  }

  @Override
  public boolean canHandle(ResourceSet resourceSet)
  {
    CDOViewSet viewSet = CDOUtil.getViewSet(resourceSet);
    if (viewSet != null)
    {
      for (CDOView view : viewSet.getViews())
      {
        if (!view.isClosed())
        {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public List<URI> browseResources(ResourceSet resourceSet, Shell shell, boolean multi)
  {
    List<CDOView> views = new ArrayList<>();

    CDOViewSet viewSet = CDOUtil.getViewSet(resourceSet);
    if (viewSet != null)
    {
      for (CDOView view : viewSet.getViews())
      {
        if (!view.isClosed())
        {
          views.add(view);
        }
      }
    }

    RepositoryResourceSelectionDialog dialog = new RepositoryResourceSelectionDialog(shell, multi, views);
    if (dialog.open() == RepositoryResourceSelectionDialog.OK)
    {
      return new ArrayList<>(dialog.getURIs());
    }

    return null;
  }
}
