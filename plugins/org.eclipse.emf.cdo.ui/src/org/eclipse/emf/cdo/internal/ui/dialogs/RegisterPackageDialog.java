/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.internal.util.collection.PreferenceHistory;
import org.eclipse.net4j.util.collection.IHistory;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class RegisterPackageDialog extends TitleAreaDialog
{
  public static final IHistory<String> HISTORY = new PreferenceHistory(OM.PREF_HISTORY_REGISTER_PACKAGE);

  private static final String TITLE = "Register Package";

  private CDOSession session;

  public RegisterPackageDialog(Shell parentShell, CDOSession session)
  {
    super(parentShell);
    this.session = session;
    setShellStyle(getShellStyle() | SWT.TITLE | SWT.RESIZE);
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(TITLE);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = (Composite)super.createDialogArea(parent);
    setTitle(TITLE);

    IPackageProvider[] packageProviders = getPackageProviders();
    Composite packageProviderArea = new Composite(composite, SWT.NONE);
    packageProviderArea.setLayout(new GridLayout(packageProviders.length, false));
    packageProviderArea.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    for (final IPackageProvider packageProvider : packageProviders)
    {
      Button button = new Button(packageProviderArea, SWT.PUSH);
      button.setText(packageProvider.getTitle());
      button.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          EPackage ePackage = packageProvider.getPackage(session, e);
          if (ePackage != null)
          {
            session.getPackageRegistry().putEPackage(ePackage);
          }
        }
      });
    }

    Composite grid = new Composite(composite, SWT.NONE);
    grid.setLayout(new GridLayout(2, false));

    return composite;
  }

  protected IPackageProvider[] getPackageProviders()
  {
    return new IPackageProvider[0];
  }

  protected void registerPackage()
  {
    // RegisterCDOPackageAction action = new RegisterCDOPackageAction(page,
    // session);
    // action.run();
  }
}
