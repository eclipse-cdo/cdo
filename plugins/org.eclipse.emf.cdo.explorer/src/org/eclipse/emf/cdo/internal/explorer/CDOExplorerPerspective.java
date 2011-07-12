/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.explorer;

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
public class CDOExplorerPerspective implements IPerspectiveFactory
{
  public static final String ID = "org.eclipse.emf.cdo.explorer.perspective"; //$NON-NLS-1$

  private IPageLayout pageLayout;

  public CDOExplorerPerspective()
  {
  }

  public IPageLayout getPageLayout()
  {
    return pageLayout;
  }

  public void createInitialLayout(IPageLayout pageLayout)
  {
    this.pageLayout = pageLayout;
    addViews();
    addPerspectiveShortcuts();
    addViewShortcuts();
  }

  protected void addViews()
  {
    IFolderLayout BrowsingFolderLayout0 = pageLayout.createFolder("cdoFolderLayout0", IPageLayout.LEFT, 0.20f, //$NON-NLS-1$
        pageLayout.getEditorArea());
    BrowsingFolderLayout0.addView(IPageLayout.ID_PROJECT_EXPLORER);

    IFolderLayout BrowsingFolderLayout1 = pageLayout.createFolder("cdoFolderLayout1", IPageLayout.BOTTOM, 0.70f, //$NON-NLS-1$
        IPageLayout.ID_PROJECT_EXPLORER);
    BrowsingFolderLayout1.addView(CDOSessionsView.ID);

    IFolderLayout BrowsingFolderLayout2 = pageLayout.createFolder("cdoFolderLayout3", IPageLayout.BOTTOM, 0.70f, //$NON-NLS-1$
        pageLayout.getEditorArea());
    BrowsingFolderLayout2.addView(IPageLayout.ID_PROP_SHEET);
    BrowsingFolderLayout2.addView(CDOWatchListView.ID);
    BrowsingFolderLayout2.addView(CDORemoteSessionsView.ID);

    IFolderLayout BrowsingFolderLayout3 = pageLayout.createFolder("cdoFolderLayout2", IPageLayout.RIGHT, 0.8f, //$NON-NLS-1$
        pageLayout.getEditorArea());
    BrowsingFolderLayout3.addView(IPageLayout.ID_OUTLINE);
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
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId()
        .equals(CDOExplorerPerspective.ID);
  }
}
