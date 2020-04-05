/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.prefs.OMPreferencePage;
import org.eclipse.net4j.util.ui.prefs.PrefBoolean;
import org.eclipse.net4j.util.ui.prefs.PrefIntegerAndDisable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Eike Stepper
 */
public class ExplorerPreferencePage extends OMPreferencePage
{
  private PrefIntegerAndDisable repositoryTimeout;

  private PrefBoolean rememberOpenEditors;

  public ExplorerPreferencePage()
  {
    super(OM.PREFS);
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Composite composite = UIUtil.createGridComposite(parent, 2);
    composite.setLayoutData(UIUtil.createGridData());

    new Label(composite, SWT.NONE).setText("Repository timeout:");
    repositoryTimeout = new PrefIntegerAndDisable(composite, SWT.BORDER, OM.PREF_REPOSITORY_TIMEOUT_MINUTES, OM.PREF_REPOSITORY_TIMEOUT_DISABLED)
    {
      @Override
      protected GridData createTextLayoutData()
      {
        return UIUtil.createGridData(true, false);
      }
    };

    repositoryTimeout.setLayoutData(UIUtil.createGridData(true, false));

    rememberOpenEditors = new PrefBoolean(composite, SWT.CHECK, OM.PREF_REMEMBER_OPEN_EDITORS);
    rememberOpenEditors.getButton().setText("Remember open model editors");
    rememberOpenEditors.setLayoutData(UIUtil.createGridData(false, false));
    UIUtil.setIndentation(rememberOpenEditors, -1, 10);

    initValues();
    return composite;
  }

  protected void initValues()
  {
    repositoryTimeout.loadPreferences();
    rememberOpenEditors.loadPreferences();
  }

  @Override
  public boolean performOk()
  {
    repositoryTimeout.savePreferences();
    rememberOpenEditors.savePreferences();
    return super.performOk();
  }
}
