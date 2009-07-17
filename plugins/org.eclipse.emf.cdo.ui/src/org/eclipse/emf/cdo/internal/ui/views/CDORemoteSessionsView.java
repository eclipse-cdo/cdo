/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Eike Stepper
 */
public class CDORemoteSessionsView extends ContainerView.Default<CDORemoteSessionManager>
{
  private static CDORemoteSessionsView instance;

  private ISelectionListener selectionListener = new ISelectionListener()
  {
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
      if (part != instance)
      {
        Object object = UIUtil.getElementIfOne(selection);
        CDOSession session = CDOUtil.getSession(object);
        if (session != null)
        {
          setContainer(session.getRemoteSessionManager());
        }
      }
    }
  };

  public CDORemoteSessionsView()
  {
  }

  @Override
  public void dispose()
  {
    getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(selectionListener);
    instance = null;
    super.dispose();
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Control control = super.createUI(parent);
    getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(selectionListener);
    instance = this;
    return control;
  }

  public static boolean hasInstance()
  {
    return instance != null;
  }

  public static CDORemoteSessionsView getInstance()
  {
    CheckUtil.checkState(instance, "instance");
    return instance;
  }
}
