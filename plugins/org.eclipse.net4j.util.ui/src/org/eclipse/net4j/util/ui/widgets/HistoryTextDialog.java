/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.util.collection.IHistory;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * TODO extend BaseDialog
 *
 * @author Eike Stepper
 */
public class HistoryTextDialog extends InputDialog
{
  private static final String EMPTY = ""; //$NON-NLS-1$

  private IHistory<String> history;

  private String value = EMPTY;

  private HistoryText historyText;

  public HistoryTextDialog(Shell parentShell, String dialogTitle, String dialogMessage, IHistory<String> history, IInputValidator validator)
  {
    super(parentShell, dialogTitle, dialogMessage, history.getMostRecent(), validator);
    this.history = history;
    value = super.getValue();
  }

  public IHistory<String> getHistory()
  {
    return history;
  }

  public HistoryText getHistoryText()
  {
    return historyText;
  }

  @Override
  public String getValue()
  {
    return value;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = (Composite)super.createDialogArea(parent);
    Text text = getText();
    text.setVisible(false);
    text.setEnabled(false);

    historyText = new HistoryText(composite, SWT.BORDER, history);
    historyText.getCombo().moveAbove(text);
    historyText.getCombo().setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
    historyText.getCombo().addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        validateInput();
      }
    });

    text.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusGained(FocusEvent e)
      {
        historyText.setFocus();
      }
    });

    composite.getShell().layout(true);
    return composite;
  }

  @Override
  protected void buttonPressed(int buttonId)
  {
    if (IDialogConstants.OK_ID == buttonId)
    {
      value = historyText.getText();
      okPressed();
    }
    else if (IDialogConstants.CANCEL_ID == buttonId)
    {
      value = null;
      cancelPressed();
    }
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    super.createButtonsForButtonBar(parent);
    historyText.setFocus();
    if (value != null)
    {
      historyText.setText(value);
    }
  }

  @Override
  protected void validateInput()
  {
    String errorMessage = null;
    if (getValidator() != null)
    {
      errorMessage = getValidator().isValid(historyText.getText());
    }

    // Bug 16256: important not to treat "" (blank error) the same as null
    // (no error)
    setErrorMessage(errorMessage);
  }
}
