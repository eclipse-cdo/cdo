/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.util;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import java.text.MessageFormat;

/**
 * A dialog cell editor in which the text field is editable: the user
 * can directly edit the value without opening the dialog.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public abstract class EditableDialogCellEditor extends DialogCellEditor
{
  private Text text;

  public EditableDialogCellEditor()
  {
  }

  public EditableDialogCellEditor(Composite parent)
  {
    super(parent);
  }

  public EditableDialogCellEditor(Composite parent, int style)
  {
    super(parent, style);
  }

  @Override
  protected Control createContents(Composite cell)
  {
    text = new Text(cell, SWT.SINGLE);
    text.setFont(cell.getFont());
    text.setBackground(cell.getBackground());

    text.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetDefaultSelected(SelectionEvent e)
      {
        String newValue = text.getText();

        if (isCorrect(newValue))
        {
          markDirty();
          doSetValue(newValue);
        }
        else
        {
          setErrorMessage(MessageFormat.format(getErrorMessage(), newValue.toString()));
        }

        fireApplyEditorValue();
        deactivate();
      }
    });

    return text;
  }

  @Override
  protected void updateContents(Object value)
  {
    text.setText(value == null ? "" : value.toString()); //$NON-NLS-1$
  }

  @Override
  public void setFocus()
  {
    text.setFocus();
  }
}
