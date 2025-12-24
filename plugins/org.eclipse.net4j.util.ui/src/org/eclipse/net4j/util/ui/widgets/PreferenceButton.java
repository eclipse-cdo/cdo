/*
 * Copyright (c) 2007, 2008, 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.util.om.pref.OMPreference;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Eike Stepper
 */
public class PreferenceButton
{
  private OMPreference<Boolean> preference;

  private Button button;

  public PreferenceButton(Composite parent, int style, String text, final OMPreference<Boolean> preference)
  {
    this.preference = preference;

    button = new Button(parent, style);
    button.setText(text);
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

  public int getAlignment()
  {
    return button.getAlignment();
  }

  public Image getImage()
  {
    return button.getImage();
  }

  public boolean getSelection(boolean setPreference)
  {
    boolean selection = button.getSelection();
    if (setPreference)
    {
      preference.setValue(selection);
    }

    return selection;
  }

  public boolean getSelection()
  {
    return getSelection(false);
  }

  public String getText()
  {
    return button.getText();
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

  public boolean setFocus()
  {
    return button.setFocus();
  }

  /**
   * @since 3.0
   */
  public void addListener(int evenType, Listener listener)
  {
    button.addListener(evenType, listener);
  }

  /**
   * @since 3.0
   */
  public void removeListener(int evenType, Listener listener)
  {
    button.removeListener(evenType, listener);
  }
}
