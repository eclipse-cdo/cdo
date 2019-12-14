/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 399306
 */
package org.eclipse.net4j.util.ui.security;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.BaseDialog;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CredentialsDialog extends BaseDialog<Viewer>
{
  private static final String TITLE = Messages.getString("CredentialsDialog_0"); //$NON-NLS-1$

  private static final String MESSAGE = Messages.getString("CredentialsDialog_1"); //$NON-NLS-1$

  private static final int WIDTH = 400;

  private static final int HEIGHT = 225;

  private final String realm;

  private final List<String> users;

  private Control userIDControl;

  private Text passwordControl;

  private IPasswordCredentials credentials;

  public CredentialsDialog(Shell shell)
  {
    this(shell, null);
  }

  /**
   * @since 3.3
   */
  public CredentialsDialog(Shell shell, String realm)
  {
    this(shell, realm, TITLE, MESSAGE);
  }

  /**
   * @since 3.4
   */
  public CredentialsDialog(Shell shell, String realm, String title, String message)
  {
    super(shell, DEFAULT_SHELL_STYLE | SWT.APPLICATION_MODAL, title, message, OM.Activator.INSTANCE.getDialogSettings(),
        OM.getImageDescriptor("icons/credentials_wiz.gif")); //$NON-NLS-1$
    this.realm = realm;
    users = loadUsers();
  }

  /**
   * @since 3.3
   */
  public final String getRealm()
  {
    return realm;
  }

  public IPasswordCredentials getCredentials()
  {
    return credentials;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    configureShell(newShell, WIDTH, HEIGHT);
  }

  /**
   * @since 3.4
   */
  protected void configureShell(Shell newShell, int width, int height)
  {
    Composite parent = newShell.getParent();
    if (parent != null)
    {
      Rectangle bounds = parent.getBounds();

      int x = bounds.x + (bounds.width >> 1) - (width >> 1);
      int y = bounds.y + (bounds.height >> 1) - (height >> 1);

      newShell.setBounds(x, y, width, height);
    }
    else
    {
      newShell.setSize(width, height);
    }
  }

  @Override
  protected void createUI(Composite parent)
  {
    createCredentialsArea(parent);
  }

  /**
   * @since 3.4
   */
  protected Composite createCredentialsArea(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout(2, false));

    new Label(composite, SWT.NONE).setText(Messages.getString("CredentialsDialog_2")); //$NON-NLS-1$
    userIDControl = createUserIDControl(composite);
    userIDControl.setLayoutData(UIUtil.createGridData(true, false));

    new Label(composite, SWT.NONE).setText(Messages.getString("CredentialsDialog_3")); //$NON-NLS-1$
    passwordControl = new Text(composite, SWT.BORDER | SWT.PASSWORD);
    passwordControl.setLayoutData(UIUtil.createGridData(true, false));

    if (userIDControl instanceof Combo)
    {
      passwordControl.setFocus();
    }

    return composite;
  }

  /**
   * @since 3.4
   */
  protected Control createUserIDControl(Composite composite)
  {
    if (users.isEmpty())
    {
      return new Text(composite, SWT.BORDER);
    }

    Combo combo = new Combo(composite, SWT.BORDER);
    combo.setItems(users.toArray(new String[users.size()]));
    combo.setText(users.get(0));

    return combo;
  }

  @Override
  protected void okPressed()
  {
    String userID;
    if (userIDControl instanceof Combo)
    {
      userID = ((Combo)userIDControl).getText();
    }
    else
    {
      userID = ((Text)userIDControl).getText();
    }

    String password = passwordControl.getText();
    credentials = createCredentials(userID, password.toCharArray());

    users.remove(userID);
    users.add(0, userID);
    saveUsers(users);

    super.okPressed();
  }

  /**
   * @since 3.4
   */
  protected IPasswordCredentials createCredentials(String userID, char[] password)
  {
    return new PasswordCredentials(userID, password);
  }

  /**
   * @since 3.4
   */
  protected List<String> loadUsers()
  {
    List<String> result = new ArrayList<>();

    IDialogSettings settings = getUsersSection();
    String key = getRealmKey();

    String[] users = settings.getArray(key);
    if (users != null && users.length != 0)
    {
      result.addAll(Arrays.asList(users));
    }

    return result;
  }

  /**
   * @since 3.4
   */
  protected void saveUsers(List<String> users)
  {
    IDialogSettings settings = getUsersSection();
    String key = getRealmKey();

    settings.put(key, users.toArray(new String[users.size()]));
  }

  private String getRealmKey()
  {
    String key = "realm";
    if (realm != null)
    {
      key += realm;
    }

    return key;
  }

  private IDialogSettings getUsersSection()
  {
    return getDialogSettings("users");
  }
}
