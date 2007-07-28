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

import org.eclipse.net4j.ui.UIUtil;
import org.eclipse.net4j.util.om.pref.OMPreference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Eike Stepper
 */
public class PreferenceButton extends Composite
{
  private OMPreference<Boolean> preference;

  private Button button;

  public PreferenceButton(Composite parent, int buttonStyle, final OMPreference<Boolean> preference)
  {
    super(parent, SWT.NONE);
    setLayout(UIUtil.createGridLayout(1));
    this.preference = preference;

    button = createButton(buttonStyle);
    button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    button.setSelection(preference.getValue());
    button.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        preference.setValue(button.getSelection());
      }
    });
  }

  public OMPreference<Boolean> getPreference()
  {
    return preference;
  }

  public Button getButton()
  {
    return button;
  }

  protected Button createButton(int buttonStyle)
  {
    return new Button(this, buttonStyle);
  }

  public void addSelectionListener(SelectionListener listener)
  {
    button.addSelectionListener(listener);
  }

  public int getAlignment()
  {
    return button.getAlignment();
  }

  public Image getImage()
  {
    return button.getImage();
  }

  public boolean getSelection()
  {
    return button.getSelection();
  }

  public String getText()
  {
    return button.getText();
  }

  public void removeSelectionListener(SelectionListener listener)
  {
    button.removeSelectionListener(listener);
  }

  public void setAlignment(int alignment)
  {
    button.setAlignment(alignment);
  }

  public void setImage(Image image)
  {
    button.setImage(image);
  }

  public void setSelection(boolean selected)
  {
    button.setSelection(selected);
  }

  public void setText(String string)
  {
    button.setText(string);
  }
}
