/*
 * Copyright (c) 2015, 2016, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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

  private PrefBoolean showObjectsInExplorer;

  public ExplorerPreferencePage()
  {
    super(OM.PREFS);
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
    composite.setLayoutData(UIUtil.createGridData());

    new Label(composite, SWT.NONE).setText("Repository inactivity timeout:");
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
    rememberOpenEditors.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
    UIUtil.setIndentation(rememberOpenEditors, -1, 10);

    showObjectsInExplorer = new PrefBoolean(composite, SWT.CHECK, OM.PREF_SHOW_OBJECTS_IN_EXPLORER);
    showObjectsInExplorer.getButton().setText("Show objects in project explorer");
    showObjectsInExplorer.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());

    initValues();
    return composite;
  }

  protected void initValues()
  {
    repositoryTimeout.loadPreferences();
    rememberOpenEditors.loadPreferences();
    showObjectsInExplorer.loadPreferences();
  }

  @Override
  public boolean performOk()
  {
    repositoryTimeout.savePreferences();
    rememberOpenEditors.savePreferences();
    showObjectsInExplorer.savePreferences();
    return super.performOk();
  }
}
