/*
 * Copyright (c) 2007-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.IHistory;
import org.eclipse.net4j.util.collection.IHistoryChangeEvent;
import org.eclipse.net4j.util.collection.IHistoryElement;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
public class HistoryText
{
  private IHistory<String> history;

  private CCombo combo;

  private Method droppedMethod;

  private IListener historyListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof IHistoryChangeEvent)
      {
        historyChanged();
      }
    }
  };

  public HistoryText(Composite parent, int style, IHistory<String> history)
  {
    this.history = history;
    history.addListener(historyListener);

    combo = new CCombo(parent, style);
    combo.setLayoutData(UIUtil.createGridData());
    combo.addDisposeListener(new DisposeListener()
    {
      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        HistoryText.this.history.removeListener(historyListener);
      }
    });

    // TODO Can't get traversal working when keyListener is added ;-(
    // combo.addKeyListener(new KeyAdapter()
    // {
    // @Override
    // public void keyPressed(KeyEvent event)
    // {
    // if (event.character == SWT.DEL && event.stateMask == 0 && isDropped())
    // {
    // int index = combo.getSelectionIndex();
    // if (index != -1)
    // {
    // HistoryText.this.history.remove(index);
    // }
    // }
    // }
    // });

    try
    {
      droppedMethod = combo.getClass().getDeclaredMethod("isDropped", ReflectUtil.NO_PARAMETERS); //$NON-NLS-1$
      droppedMethod.setAccessible(true);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    historyChanged();
  }

  public IHistory<String> getHistory()
  {
    return history;
  }

  public CCombo getCombo()
  {
    return combo;
  }

  public void append(String string)
  {
    combo.setText(combo.getText() + string);
  }

  public int getCharCount()
  {
    return combo.getText().length();
  }

  public int getLineCount()
  {
    return StringUtil.occurrences(combo.getText(), getLineDelimiter()) + 1;
  }

  public String getLineDelimiter()
  {
    return Text.DELIMITER;
  }

  public int getLineHeight()
  {
    return combo.getTextHeight();
  }

  public String getText(boolean addHistory)
  {
    String text = combo.getText();
    if (addHistory)
    {
      history.add(text);
    }

    return text;
  }

  public String getText()
  {
    return getText(false);
  }

  public void setText(String string)
  {
    combo.setText(string);
  }

  public boolean setFocus()
  {
    return combo.setFocus();
  }

  public boolean isDropped()
  {
    if (droppedMethod != null)
    {
      try
      {
        return (Boolean)droppedMethod.invoke(combo, ReflectUtil.NO_ARGUMENTS);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    return false;
  }

  protected void historyChanged()
  {
    if (combo.isDisposed())
    {
      return;
    }

    combo.removeAll();
    for (IHistoryElement<String> element : history)
    {
      combo.add(element.getData());
    }

    String mostRecent = history.getMostRecent();
    if (mostRecent != null)
    {
      setText(mostRecent);
    }
  }

  /**
   * @since 3.0
   */
  public void addListener(int evenType, Listener listener)
  {
    combo.addListener(evenType, listener);
  }

  /**
   * @since 3.0
   */
  public void removeListener(int evenType, Listener listener)
  {
    combo.removeListener(evenType, listener);
  }
}
