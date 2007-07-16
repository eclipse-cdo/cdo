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
package org.eclipse.net4j.ui.widgets;

import org.eclipse.net4j.internal.ui.bundle.OM;
import org.eclipse.net4j.ui.UIUtil;
import org.eclipse.net4j.util.collection.HistoryUtil;
import org.eclipse.net4j.util.collection.IHistory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Eike Stepper
 */
public class HistoryView extends ViewPart
{
  public static final IHistory<String> HISTORY = HistoryUtil.createPreferenceHistory(OM.PREF_HISTORY);

  private HistoryText control;

  public HistoryView()
  {
  }

  @Override
  public void createPartControl(Composite parent)
  {
    Composite composite = UIUtil.createGridComposite(parent, 2);
    control = new HistoryText(composite, SWT.NONE, HISTORY);
    control.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    Button button = new Button(composite, SWT.PUSH);
    button.setText("Use");
    button.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
    button.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        HISTORY.add(control.getText());
      }
    });
  }

  @Override
  public void setFocus()
  {
    if (control != null)
    {
      control.setFocus();
    }
  }
}
