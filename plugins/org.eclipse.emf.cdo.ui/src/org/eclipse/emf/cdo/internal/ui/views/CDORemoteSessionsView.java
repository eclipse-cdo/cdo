/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDORemoteSessionsView extends ContainerView.Default<CDORemoteSessionManager>
{
  public static final String ID = "org.eclipse.emf.cdo.ui.CDORemoteSessionsView"; //$NON-NLS-1$

  private static final String TYPE_TEXT_MESSAGE = "org.eclipse.emf.cdo.ui.TextMessage"; //$NON-NLS-1$

  private ISelectionListener selectionListener = new ISelectionListener()
  {
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
      if (part != CDORemoteSessionsView.this)
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

  private IListener containerListener = new CDORemoteSessionManager.EventAdapter()
  {
    @Override
    protected void onLocalSubscriptionChanged(boolean subscribed)
    {
      getViewer().getControl().setEnabled(subscribed);
    }

    @Override
    protected void onSubscribed(CDORemoteSession remoteSession)
    {
      refreshElement(remoteSession, true);
    }

    @Override
    protected void onUnsubscribed(CDORemoteSession remoteSession)
    {
      refreshElement(remoteSession, true);
    }

    @Override
    protected void onMessageReceived(final CDORemoteSession remoteSession, final CDORemoteSessionMessage message)
    {
      if (TYPE_TEXT_MESSAGE.equals(message.getType()))
      {
        try
        {
          getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              try
              {
                MessageDialog.openInformation(getShell(), MessageFormat.format(Messages.getString("CDORemoteSessionsView_0"), remoteSession), //$NON-NLS-1$
                    new String(message.getData()));
              }
              catch (RuntimeException ignore)
              {
                // ignore
              }
            }
          });
        }
        catch (RuntimeException ignore)
        {
          // ignore
        }
      }
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      setContainer(null);
    }
  };

  public CDORemoteSessionsView()
  {
  }

  @Override
  public void dispose()
  {
    getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(selectionListener);
    super.dispose();
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Control control = super.createUI(parent);
    getViewer().getControl().setEnabled(false);
    getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(selectionListener);
    return control;
  }

  @Override
  protected IListener getContainerListener()
  {
    return containerListener;
  }

  @Override
  protected Color getElementForeground(Object element)
  {
    if (element instanceof CDORemoteSession)
    {
      CDORemoteSession remoteSession = (CDORemoteSession)element;
      if (!remoteSession.isSubscribed())
      {
        return getDisplay().getSystemColor(SWT.COLOR_GRAY);
      }
    }

    return null;
  }

  @Override
  protected void doubleClicked(Object element)
  {
    if (element instanceof CDORemoteSession)
    {
      CDORemoteSession remoteSession = (CDORemoteSession)element;
      if (remoteSession.isSubscribed())
      {
        InputDialog dlg = new InputDialog(getShell(), MessageFormat.format(Messages.getString("CDORemoteSessionsView_1"), remoteSession), //$NON-NLS-1$
            Messages.getString("CDORemoteSessionsView_2"), "", null); //$NON-NLS-1$ //$NON-NLS-2$
        if (dlg.open() == InputDialog.OK)
        {
          String message = dlg.getValue();
          remoteSession.sendMessage(new CDORemoteSessionMessage(TYPE_TEXT_MESSAGE, message.getBytes()));
        }

        return;
      }
    }

    super.doubleClicked(element);
  }
}
