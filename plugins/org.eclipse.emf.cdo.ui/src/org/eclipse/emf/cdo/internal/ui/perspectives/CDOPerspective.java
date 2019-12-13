/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.perspectives;

import org.eclipse.emf.cdo.internal.ui.views.CDORemoteSessionsView;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.internal.ui.views.CDOWatchListView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

/**
 * @author Victor Roldan Betancort
 */
public class CDOPerspective implements IPerspectiveFactory
{
  public static final String ID = "org.eclipse.emf.cdo.ui.CDOExplorerPerspective"; //$NON-NLS-1$

  private IPageLayout pageLayout;

  public CDOPerspective()
  {
  }

  public IPageLayout getPageLayout()
  {
    return pageLayout;
  }

  @Override
  public void createInitialLayout(IPageLayout pageLayout)
  {
    this.pageLayout = pageLayout;
    addViews();
    addPerspectiveShortcuts();
    addViewShortcuts();
  }

  protected void addViews()
  {
    IFolderLayout navigationPane = pageLayout.createFolder("navigationPane", IPageLayout.LEFT, 0.30f, //$NON-NLS-1$
        pageLayout.getEditorArea());
    navigationPane.addView(IPageLayout.ID_PROJECT_EXPLORER);

    IFolderLayout sessionsPane = pageLayout.createFolder("sessionsPane", IPageLayout.BOTTOM, 0.70f, //$NON-NLS-1$
        IPageLayout.ID_PROJECT_EXPLORER);
    sessionsPane.addView(CDOSessionsView.ID);

    IFolderLayout propertiesPane = pageLayout.createFolder("propertiesPane", IPageLayout.BOTTOM, 0.70f, //$NON-NLS-1$
        pageLayout.getEditorArea());
    propertiesPane.addView(IPageLayout.ID_PROP_SHEET);
    propertiesPane.addView(CDOWatchListView.ID);
    propertiesPane.addView(CDORemoteSessionsView.ID);

    IFolderLayout outlinePane = pageLayout.createFolder("outlinePane", IPageLayout.RIGHT, 0.70f, //$NON-NLS-1$
        pageLayout.getEditorArea());
    outlinePane.addView(IPageLayout.ID_OUTLINE);
  }

  protected void addViewShortcuts()
  {
    pageLayout.addShowViewShortcut(CDOSessionsView.ID);
    pageLayout.addShowViewShortcut(CDOWatchListView.ID);
    pageLayout.addShowViewShortcut(CDORemoteSessionsView.ID);
    pageLayout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    pageLayout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
    pageLayout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
  }

  protected void addPerspectiveShortcuts()
  {
    pageLayout.addPerspectiveShortcut(ID);
  }

  static public boolean isCurrent()
  {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId().equals(CDOPerspective.ID);
  }
}
