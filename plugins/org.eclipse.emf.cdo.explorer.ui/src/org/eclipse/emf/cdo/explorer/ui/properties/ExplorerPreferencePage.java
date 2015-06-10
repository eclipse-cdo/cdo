/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
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
    repositoryTimeout = new PrefIntegerAndDisable(composite, SWT.BORDER, OM.PREF_REPOSITORY_TIMEOUT_MINUTES,
        OM.PREF_REPOSITORY_TIMEOUT_DISABLED)
    {
      @Override
      protected GridData createTextLayoutData()
      {
        return UIUtil.createGridData(true, false);
      }
    };

    repositoryTimeout.setLayoutData(UIUtil.createGridData(true, false));

    initValues();
    return composite;
  }

  protected void initValues()
  {
    repositoryTimeout.loadPreferences();
  }

  @Override
  public boolean performOk()
  {
    repositoryTimeout.savePreferences();
    return super.performOk();
  }
}
