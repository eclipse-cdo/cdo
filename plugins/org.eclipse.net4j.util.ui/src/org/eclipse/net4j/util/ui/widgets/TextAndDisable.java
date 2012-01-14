/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TextAndDisable extends Composite implements SelectionListener, ModifyListener
{
  private Text text;

  private Button disabled;

  private String disabledValue;

  public TextAndDisable(Composite parent, int textStyle, String disabledValue)
  {
    super(parent, SWT.NONE);
    this.disabledValue = disabledValue;

    GridLayout grid = new GridLayout(2, false);
    grid.marginHeight = 0;
    grid.marginWidth = 0;
    setLayout(grid);

    text = createText(textStyle);
    text.setLayoutData(createTextLayoutData());
    text.addModifyListener(this);

    disabled = createButton();
    disabled.setText(Messages.getString("TextAndDisable.0")); //$NON-NLS-1$
    disabled.addSelectionListener(this);
    disabled.setLayoutData(UIUtil.createGridData(false, false));
  }

  public Text getText()
  {
    return text;
  }

  public Button getButton()
  {
    return disabled;
  }

  public boolean isDisabled()
  {
    return disabled.getSelection();
  }

  public void setDisabled(boolean disabled)
  {
    this.disabled.setSelection(disabled);
    widgetSelected(null);
  }

  public String getValue()
  {
    return text.getText();
  }

  public void setValue(String value)
  {
    text.setText(value);
    setDisabled(ObjectUtil.equals(value, disabledValue));
  }

  public void widgetDefaultSelected(SelectionEvent e)
  {
    widgetSelected(e);
  }

  public void widgetSelected(SelectionEvent e)
  {
    if (isDisabled())
    {
      text.setText(disabledValue);
      text.setEnabled(false);
    }
    else
    {
      text.setEnabled(true);
    }
  }

  public void modifyText(ModifyEvent e)
  {
  }

  protected Text createText(int textStyle)
  {
    return new Text(this, textStyle);
  }

  protected GridData createTextLayoutData()
  {
    GridData gd = new GridData();
    gd.widthHint = 32;
    return gd;
  }

  protected Button createButton()
  {
    return new Button(this, SWT.CHECK);
  }
}
