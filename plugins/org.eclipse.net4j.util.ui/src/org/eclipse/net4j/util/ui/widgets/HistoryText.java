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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
public class HistoryText extends Composite
{
  private IHistory<String> history;

  private CCombo combo;

  private Method droppedMethod;

  private IListener historyListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof IHistoryChangeEvent)
      {
        historyChanged();
      }
    }
  };

  public HistoryText(Composite parent, int comboStyle, IHistory<String> history)
  {
    super(parent, SWT.NONE);
    setLayout(UIUtil.createGridLayout(1));

    combo = createCombo(comboStyle);
    combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    try
    {
      droppedMethod = combo.getClass().getDeclaredMethod("isDropped", ReflectUtil.NO_PARAMETERS);
      droppedMethod.setAccessible(true);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    this.history = history;
    historyChanged();
    history.addListener(historyListener);
  }

  public IHistory<String> getHistory()
  {
    return history;
  }

  public CCombo getCombo()
  {
    return combo;
  }

  @Override
  public void dispose()
  {
    history.removeListener(historyListener);
    super.dispose();
  }

  public void addModifyListener(ModifyListener listener)
  {
    combo.addModifyListener(listener);
  }

  public void addSelectionListener(SelectionListener listener)
  {
    combo.addSelectionListener(listener);
  }

  public void addVerifyListener(VerifyListener listener)
  {
    combo.addVerifyListener(listener);
  }

  public void append(String string)
  {
    combo.setText(combo.getText() + string);
  }

  public void clearSelection()
  {
    combo.clearSelection();
  }

  public void copy()
  {
    combo.copy();
  }

  public void cut()
  {
    combo.cut();
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

  public String getText()
  {
    return combo.getText();
  }

  public int getTextLimit()
  {
    return combo.getTextLimit();
  }

  public void paste()
  {
    combo.paste();
  }

  public void removeModifyListener(ModifyListener listener)
  {
    combo.removeModifyListener(listener);
  }

  public void removeSelectionListener(SelectionListener listener)
  {
    combo.removeSelectionListener(listener);
  }

  public void removeVerifyListener(VerifyListener listener)
  {
    combo.removeVerifyListener(listener);
  }

  public void setText(String string)
  {
    combo.setText(string);
  }

  public void setTextLimit(int limit)
  {
    combo.setTextLimit(limit);
  }

  @Override
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
    if (isDisposed())
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

  protected CCombo createCombo(int style)
  {
    final CCombo combo = new CCombo(this, style);
    combo.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent event)
      {
        if (event.character == SWT.DEL && event.stateMask == 0 && isDropped())
        {
          int index = combo.getSelectionIndex();
          if (index != -1)
          {
            history.remove(index);
          }
        }
      }
    });

    return combo;
  }
}
