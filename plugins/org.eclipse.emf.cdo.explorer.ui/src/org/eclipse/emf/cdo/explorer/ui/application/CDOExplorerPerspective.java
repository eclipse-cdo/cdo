/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.explorer.ui.application;

import org.eclipse.emf.cdo.explorer.ui.checkouts.actions.ShowInActionProvider;
import org.eclipse.emf.cdo.explorer.ui.repositories.CDORepositoriesView;
import org.eclipse.emf.cdo.internal.ui.views.CDORemoteSessionsView;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.internal.ui.views.CDOTimeMachineView;
import org.eclipse.emf.cdo.internal.ui.views.CDOWatchListView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.PlatformUI;

/**
 * @author Victor Roldan Betancort
 */
public class CDOExplorerPerspective implements IPerspectiveFactory
{
  public static final String ID = "org.eclipse.emf.cdo.explorer.CDOExplorerPerspective"; //$NON-NLS-1$

  private static final String CHECKOUT_AREA = "checkoutArea";

  private static final String AUDITING_AREA = "auditingArea";

  private static final String REPOSITORY_AREA = "repositoryArea";

  private static final String PROPERTIES_AREA = "propertiesArea";

  private static final String OUTLINE_AREA = "outlineArea";

  private IPageLayout pageLayout;

  public CDOExplorerPerspective()
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
    IFolderLayout checkoutArea = pageLayout.createFolder(CHECKOUT_AREA, IPageLayout.LEFT, 0.30f, pageLayout.getEditorArea());
    checkoutArea.addView(IPageLayout.ID_PROJECT_EXPLORER);

    IFolderLayout repositoryArea = pageLayout.createFolder(REPOSITORY_AREA, IPageLayout.BOTTOM, 0.70f, CHECKOUT_AREA);
    repositoryArea.addView(CDORepositoriesView.ID);

    IFolderLayout propertiesArea = pageLayout.createFolder(PROPERTIES_AREA, IPageLayout.BOTTOM, 0.70f, pageLayout.getEditorArea());
    propertiesArea.addView(IPageLayout.ID_PROP_SHEET);
    propertiesArea.addView(ShowInActionProvider.HISTORY_VIEW_ID);
    propertiesArea.addView(CDOWatchListView.ID);
    propertiesArea.addView(CDORemoteSessionsView.ID);

    IFolderLayout outlineArea = pageLayout.createFolder(OUTLINE_AREA, IPageLayout.RIGHT, 0.70f, pageLayout.getEditorArea());
    outlineArea.addView(IPageLayout.ID_OUTLINE);

    IPlaceholderFolderLayout auditingArea = pageLayout.createPlaceholderFolder(AUDITING_AREA, IPageLayout.BOTTOM, 0.84f, pageLayout.getEditorArea());
    auditingArea.addPlaceholder(CDOTimeMachineView.ID);
  }

  protected void addViewShortcuts()
  {
    pageLayout.addShowViewShortcut(CDORemoteSessionsView.ID);
    pageLayout.addShowViewShortcut(CDORepositoriesView.ID);
    pageLayout.addShowViewShortcut(CDOSessionsView.ID);
    pageLayout.addShowViewShortcut(CDOTimeMachineView.ID);
    pageLayout.addShowViewShortcut(CDOWatchListView.ID);
    pageLayout.addShowViewShortcut(ShowInActionProvider.HISTORY_VIEW_ID);
    pageLayout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    pageLayout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
    pageLayout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
  }

  protected void addPerspectiveShortcuts()
  {
    pageLayout.addPerspectiveShortcut(ID);
  }

  static public boolean isCurrent()
  {
    try
    {
      return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId().equals(CDOExplorerPerspective.ID);
    }
    catch (Throwable ex)
    {
      return false;
    }
  }
}
