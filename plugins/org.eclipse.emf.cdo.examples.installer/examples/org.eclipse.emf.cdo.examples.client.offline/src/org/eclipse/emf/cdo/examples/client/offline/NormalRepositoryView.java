/*
 * Copyright (c) 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ItemProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.ExampleResourceManager;

import java.net.URL;

/**
 * @author Eike Stepper
 */
public class NormalRepositoryView extends AbstractView<IRepository>
{
  public static final String ID = "org.eclipse.emf.cdo.examples.client.offline.NormalRepositoryView"; //$NON-NLS-1$

  private ItemProvider<IRepository> itemProvider;

  private TreeViewer treeViewer;

  public NormalRepositoryView()
  {
    super(IRepository.class);
  }

  @Override
  protected void createPane(Composite parent, IRepository repository)
  {
    itemProvider = new ContainerItemProvider<IRepository>()
    {
      private Image bean = ExampleResourceManager.getPluginImage(Application.PLUGIN_ID, "icons/Bean.gif");

      @Override
      public Image getImage(Object obj)
      {
        return bean;
      }

      @Override
      protected void handleElementEvent(final IEvent event)
      {
        addEvent(event);
      }
    };

    SashForm sash = new SashForm(parent, SWT.SMOOTH);

    treeViewer = new TreeViewer(sash, SWT.BORDER);
    treeViewer.setLabelProvider(itemProvider);
    treeViewer.setContentProvider(itemProvider);
    treeViewer.setInput(repository);

    new RepositoryDetails(sash, repository, treeViewer.getControl());

    sash.setWeights(new int[] { 1, 1 });
  }

  @Override
  protected void initializeToolBar(IToolBarManager toolbarManager)
  {
    super.initializeToolBar(toolbarManager);
    toolbarManager.add(new Action("Browser",
        ExampleResourceManager.getPluginImageDescriptor(Application.PLUGIN_ID, "icons/Browser.gif"))
    {
      @Override
      public void run()
      {
        CDOServerBrowser browser = Application.NODE.getObject(CDOServerBrowser.class);
        if (browser != null)
        {
          int port = browser.getPort();

          try
          {
            PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
            .openURL(new URL("http://localhost:" + port));
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      }
    });
  }

  @Override
  public void setFocus()
  {
    treeViewer.getTree().setFocus();
  }

  @Override
  public void dispose()
  {
    itemProvider.dispose();
    super.dispose();
  }
}
