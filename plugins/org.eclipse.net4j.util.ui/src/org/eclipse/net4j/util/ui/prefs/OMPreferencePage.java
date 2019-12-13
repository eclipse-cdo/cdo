/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.prefs;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.om.pref.OMPreferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Eike Stepper
 */
public abstract class OMPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
  private IWorkbench workbench;

  private OMPreferenceStore preferenceStore;

  private SelectionListener selectionListener = new SelectionListener()
  {
    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
      dialogChanged();
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
      dialogChanged();
    }
  };

  private ModifyListener modifyListener = new ModifyListener()
  {
    @Override
    public void modifyText(ModifyEvent e)
    {
      dialogChanged();
    }
  };

  public OMPreferencePage(OMPreferences preferences)
  {
    preferenceStore = new OMPreferenceStore(preferences);
  }

  public OMPreferences getPreferences()
  {
    return preferenceStore.getPreferences();
  }

  public IWorkbench getWorkbench()
  {
    return workbench;
  }

  @Override
  public void init(IWorkbench workbench)
  {
    this.workbench = workbench;
  }

  @Override
  protected IPreferenceStore doGetPreferenceStore()
  {
    return preferenceStore;
  }

  protected final SelectionListener getSelectionListener()
  {
    return selectionListener;
  }

  protected final ModifyListener getModifyListener()
  {
    return modifyListener;
  }

  protected void dialogChanged()
  {
  }

  @Override
  protected final Control createContents(Composite parent)
  {
    try
    {
      Control control = createUI(parent);
      dialogChanged();
      addListeners(control);
      return control;
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
  }

  protected void addListeners(Control control)
  {
    if (control instanceof Text)
    {
      Text c = (Text)control;
      c.addModifyListener(modifyListener);
    }

    if (control instanceof Combo)
    {
      Combo c = (Combo)control;
      c.addModifyListener(modifyListener);
      c.addSelectionListener(selectionListener);
    }

    if (control instanceof CCombo)
    {
      CCombo c = (CCombo)control;
      c.addModifyListener(modifyListener);
      c.addSelectionListener(selectionListener);
    }

    if (control instanceof List)
    {
      List c = (List)control;
      c.addSelectionListener(selectionListener);
    }

    if (control instanceof DateTime)
    {
      DateTime c = (DateTime)control;
      c.addSelectionListener(selectionListener);
    }

    if (control instanceof Table)
    {
      Table c = (Table)control;
      c.addSelectionListener(selectionListener);
    }

    if (control instanceof Tree)
    {
      Table c = (Table)control;
      c.addSelectionListener(selectionListener);
    }

    if (control instanceof Button)
    {
      Button c = (Button)control;
      c.addSelectionListener(selectionListener);
    }

    if (control instanceof Composite)
    {
      Composite c = (Composite)control;
      for (Control child : c.getChildren())
      {
        addListeners(child);
      }
    }
  }

  protected abstract Control createUI(Composite parent);
}
