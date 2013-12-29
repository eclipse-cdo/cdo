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
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Eike Stepper
 */
public class SetupPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
  public void init(IWorkbench workbench)
  {
  }

  @Override
  protected IPreferenceStore doGetPreferenceStore()
  {
    return Activator.getDefault().getPreferenceStore();
  }

  @Override
  protected void createFieldEditors()
  {
    Composite parent = getFieldEditorParent();

    addField(new BooleanFieldEditor(SetupConstants.PREF_SKIP_STARTUP_TASKS,
        "Skip automatic task execution at startup time", parent));

    addField(new BooleanFieldEditor(SetupConstants.PREF_LOG_UNNEEDED_TASKS,
        "Show also tasks in progress dialog that are not executed", parent));
  }
}
