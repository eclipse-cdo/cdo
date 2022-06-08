/*
 * Copyright (c) 2013, 2016, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Details page for selected {@link User} master objects.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class UserDetailsPage extends AbstractDetailsPage<User>
{
  public UserDetailsPage(EditingDomain domain, AdapterFactory adapterFactory)
  {
    super(User.class, SecurityPackage.Literals.USER, domain, adapterFactory);
  }

  @Override
  protected void createContents(Composite parent, FormToolkit toolkit)
  {
    super.createContents(parent, toolkit);

    text(parent, toolkit, Messages.UserDetailsPage_0, SecurityPackage.Literals.ASSIGNEE__ID);

    text(parent, toolkit, Messages.UserDetailsPage_1, SecurityPackage.Literals.USER__FIRST_NAME);
    text(parent, toolkit, Messages.UserDetailsPage_2, SecurityPackage.Literals.USER__LAST_NAME);
    text(parent, toolkit, Messages.UserDetailsPage_4, SecurityPackage.Literals.USER__EMAIL);
    text(parent, toolkit, Messages.UserDetailsPage_3, SecurityPackage.Literals.USER__LABEL);

    space(parent, toolkit);

    button(parent, toolkit, Messages.UserDetailsPage_9, new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        resetPassword(getInput());
      }
    });

    space(parent, toolkit);

    Control accessOverrideControl = combo(parent, toolkit, Messages.UserDetailsPage_6, SecurityPackage.Literals.USER__DEFAULT_ACCESS_OVERRIDE).getControl();
    accessOverrideControl.setToolTipText(Messages.UserDetailsPage_10);

    space(parent, toolkit);

    oneToMany(parent, toolkit, Messages.UserDetailsPage_7, SecurityPackage.Literals.USER__GROUPS);

    oneToMany(parent, toolkit, Messages.UserDetailsPage_8, SecurityPackage.Literals.ASSIGNEE__ROLES);
  }

  private void resetPassword(User user)
  {
    if (user == null)
    {
      return;
    }

    CDOView view = user.cdoView();
    if (view.isClosed())
    {
      return;
    }

    if (FSMUtil.isNew(user))
    {
      Shell shell = Display.getCurrent().getActiveShell();
      if (!MessageDialog.openQuestion(shell, Messages.UserDetailsPage_titleSaveNeeded, Messages.UserDetailsPage_messageSaveNeeded))
      {
        return;
      }

      try
      {
        ((CDOTransaction)view).commit(); // The view must be a transaction because the user is NEW.
        getManagedForm().dirtyStateChanged();
      }
      catch (CommitException ex)
      {
        StatusAdapter status = new StatusAdapter(new Status(IStatus.ERROR, OM.BUNDLE_ID, Messages.CDOSecurityFormEditor_0, ex));
        status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, Messages.CDOSecurityFormEditor_1);
        status.setProperty(IStatusAdapterConstants.TIMESTAMP_PROPERTY, System.currentTimeMillis());
        StatusManager.getManager().handle(status, StatusManager.SHOW);
        return;
      }
    }

    new Job(Messages.UserDetailsPage_9)
    {
      @Override
      public IStatus run(IProgressMonitor monitor)
      {
        if (!view.isClosed())
        {
          InternalCDOSession session = (InternalCDOSession)view.getSession();
          if (!session.isClosed())
          {
            session.resetCredentials(user.getId());
          }
        }

        return Status.OK_STATUS;
      }
    }.schedule();
  }
}
