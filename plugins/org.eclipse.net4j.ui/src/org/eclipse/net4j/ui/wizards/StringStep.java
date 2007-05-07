/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class StringStep extends ValueStep<String>
{
  public StringStep(String label, String key)
  {
    super(label, key, String.class);
  }

  public StringStep(String key)
  {
    this(key, key);
  }

  @Override
  protected String validateValue(String value)
  {
    if (value == null || value.length() == 0)
    {
      return super.validateValue(null);
    }

    return null;
  }

  @Override
  protected Control createControl(Composite parent)
  {
    final Text control = new Text(parent, SWT.BORDER);
    String value = getValue();
    if (value != null)
    {
      control.setText(value);
    }

    control.setLayoutData(createLayoutData());
    control.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        setValue(control.getText());
      }
    });

    return control;
  }

  protected GridData createLayoutData()
  {
    return new GridData(SWT.FILL, SWT.TOP, true, false);
  }
}
