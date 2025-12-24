/*
 * Copyright (c) 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryImpl;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryProperties;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.ui.security.CredentialsDialog;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class RepositoryPropertyPage extends AbstractPropertyPage<CDORepository>
{
  private static final String USER_ID_LINK_URI = "security:change_login_crendentials";

  private Link userIDLink;

  public RepositoryPropertyPage()
  {
    super(CDORepositoryProperties.INSTANCE, CDORepositoryProperties.CATEGORY_REPOSITORY, "id", "type", "label", "folder");
  }

  @Override
  protected CDORepository convertElement(IAdaptable element)
  {
    if (element instanceof CDOCheckout)
    {
      return ((CDOCheckout)element).getRepository();
    }

    return AdapterUtil.adapt(element, CDORepository.class);
  }

  @Override
  protected Control createControl(Composite parent, String name, String description, String value)
  {
    if ("folder".equals(name))
    {
      return createFileLink(parent, name, description, value);
    }

    if ("authenticating".equals(name))
    {
      Button button = new Button(parent, SWT.CHECK);
      button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
      button.setSelection(getInput().isAuthenticating());
      button.setEnabled(false);
      return button;
    }

    if ("userID".equals(name))
    {
      userIDLink = createLink(parent, value, USER_ID_LINK_URI, uri -> changeLoginCrendentials());
      userIDLink.setToolTipText("Change login credentials");
      return userIDLink;
    }

    return super.createControl(parent, name, description, value);
  }

  private void changeLoginCrendentials()
  {
    CDORepositoryImpl repository = (CDORepositoryImpl)getInput();
    String realm = repository.getURI();
    IPasswordCredentials credentials = repository.getCredentials();
    String currentUserID = credentials == null ? null : credentials.getUserID();

    ChangeCredentialsDialog dialog = new ChangeCredentialsDialog(getShell(), realm, currentUserID);
    if (dialog.open() == ChangeCredentialsDialog.OK)
    {
      List<CDOCheckout> openCheckouts = null;
      boolean connected = repository.isConnected();

      if (connected)
      {
        try
        {
          openCheckouts = new ArrayList<>();
          for (CDOCheckout checkout : repository.getCheckouts())
          {
            if (checkout.isOpen())
            {
              openCheckouts.add(checkout);
            }
          }

          repository.disconnect(true);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }

      IPasswordCredentials newCredentials = dialog.getCredentials();
      setLinkText(userIDLink, newCredentials.getUserID(), USER_ID_LINK_URI);

      repository.setCredentials(newCredentials);

      if (connected)
      {
        try
        {
          repository.connect();

          if (repository.isConnected() && openCheckouts != null)
          {
            for (CDOCheckout checkout : openCheckouts)
            {
              checkout.open();
            }
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ChangeCredentialsDialog extends CredentialsDialog
  {
    private final String currentUserID;

    public ChangeCredentialsDialog(Shell shell, String realm, String currentUserID)
    {
      super(shell, realm, "Change Login Credentials", "Enter your user ID and password for the connection.");
      this.currentUserID = currentUserID;
    }

    @Override
    protected String getInitialUserID()
    {
      if (!StringUtil.isEmpty(currentUserID))
      {
        return currentUserID;
      }

      return super.getInitialUserID();
    }

    @Override
    protected List<String> loadUsers()
    {
      Set<String> userIDs = new HashSet<>(super.loadUsers());
      if (!StringUtil.isEmpty(currentUserID))
      {
        userIDs.add(currentUserID);
      }

      List<String> result = new ArrayList<>(userIDs);
      result.sort(null);
      return result;
    }
  }
}
