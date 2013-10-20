/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Eike Stepper
 */
public class RelengPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
  public RelengPreferencePage()
  {
    super("<taken from plugin.xml>");
    setDescription("General Release Engineering Preferences");
  }

  public void init(IWorkbench workbench)
  {
    // Do nothing
  }

  @Override
  protected Control createContents(Composite parent)
  {
    return new Composite(parent, SWT.NULL);
  }
}
