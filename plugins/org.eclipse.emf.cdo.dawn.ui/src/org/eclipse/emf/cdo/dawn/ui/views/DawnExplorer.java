/*
 * Copyright (c) 2010-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.views;

import org.eclipse.emf.cdo.dawn.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.ui.helper.EditorDescriptionHelper;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;

/**
 * @author Martin Fluegge
 */
public class DawnExplorer extends CDOSessionsView
{
  private static final int DEFAULT_SLEEP_INTERVAL = 5000;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnExplorer.class);

  public static final String ID = "org.eclipse.emf.cdo.dawn.ui.views.DawnExplorer";

  private CDOView view;

  public CDOView getView()
  {
    return view;
  }

  /**
   * The constructor.
   */
  public DawnExplorer()
  {
    boolean initialize = initialize();

    if (!initialize)
    {
      Thread thread = new Thread(new Runnable()
      {
        @Override
        public void run()
        {
          while (!initialize())
          {
            try
            {
              Thread.sleep(DEFAULT_SLEEP_INTERVAL);
            }
            catch (InterruptedException ex)
            {
              ex.printStackTrace();
            }
          }
        }
      });
      thread.start();
    }
  }

  /**
   * Initializes the view of the DawnExplorer
   */
  private boolean initialize()
  {
    try
    {
      CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(), PreferenceConstants.getServerName());
      CDOSession session = CDOConnectionUtil.instance.openSession();
      view = CDOConnectionUtil.instance.openView(session);
    }
    catch (ConnectorException ex)
    {
      return false;
    }
    return true;
  }

  @Override
  public void dispose()
  {
    // actually no one else should use this view
    view.close();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new DawnItemProvider(getSite().getPage(), this, new IElementFilter()
    {
      @Override
      public boolean filter(Object element)
      {
        return element instanceof CDOSession;
      }
    });
  }

  @Override
  protected void hookDoubleClick()
  {
    getViewer().addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        Object obj = ((IStructuredSelection)event.getSelection()).getFirstElement();
        if (obj instanceof CDOResource)
        {
          CDOResource resource = (CDOResource)obj;

          if (TRACER.isEnabled())
          {
            TRACER.format("Opening CDOResource {0} ", resource); //$NON-NLS-1$
          }

          String editorID = EditorDescriptionHelper.getEditorIdForDawnEditor(resource.getName());

          if (editorID != null && !editorID.equals(""))
          {
            try
            {
              DawnEditorInput editorInput = new DawnEditorInput(resource.getURI());
              DawnExplorer.this.getSite().getPage().openEditor(editorInput, editorID);
            }
            catch (PartInitException e)
            {
              e.printStackTrace();
            }
          }
          else
          {
            CDOTransaction transaction = view.getSession().openTransaction();
            CDOEditorInput editorInput = CDOEditorUtil.createCDOEditorInput(transaction, ((CDOResource)obj).getPath(), true);
            try
            {
              DawnExplorer.this.getSite().getPage().openEditor(editorInput, CDOEditorUtil.getEditorID());
            }
            catch (PartInitException e)
            {
              e.printStackTrace();
            }
          }
        }
      }
    });
  }
}
