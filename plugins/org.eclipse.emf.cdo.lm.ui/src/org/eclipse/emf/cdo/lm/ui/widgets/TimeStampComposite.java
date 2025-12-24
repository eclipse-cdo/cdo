/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.widgets;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.lm.internal.client.TimeStamp;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.collection.ConcurrentArray;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import java.util.Objects;

/**
 * @author Eike Stepper
 */
public class TimeStampComposite extends Composite
{
  private ConcurrentArray<ModifyListener> listeners = new ConcurrentArray<>()
  {
    @Override
    protected ModifyListener[] newArray(int length)
    {
      return new ModifyListener[length];
    }
  };

  private final Text text;

  private final long minTimeStamp;

  private final long maxTimeStamp;

  private long timeStamp = CDOBranchPoint.INVALID_DATE;

  private String error;

  private boolean settingValue;

  private static TimeStampSelectorContributor[] selectors = { new TimeStampSelectorContributor()
  {
    @Override
    public boolean contributeTimeStampSelector(TimeStampComposite composite, Object context)
    {
      Button button = new Button(composite, SWT.PUSH);
      button.setLayoutData(GridDataFactory.fillDefaults().create());
      button.setText("Now");
      button.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          composite.setTimeStamp(System.currentTimeMillis());
        }
      });

      return true;
    }
  } };

  public TimeStampComposite(Composite parent, int style, long minTimeStamp, long maxTimeStamp, Object context)
  {
    super(parent, style);

    this.minTimeStamp = minTimeStamp;
    this.maxTimeStamp = maxTimeStamp;

    text = new Text(this, SWT.BORDER);
    text.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());
    text.addModifyListener(e -> {
      if (!settingValue)
      {
        long oldValue = timeStamp;
        String oldError = error;
        validate();
        notifyModifyListeners(oldValue, oldError);
      }
    });

    text.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusLost(FocusEvent e)
      {
        if (timeStamp != CDOBranchPoint.INVALID_DATE)
        {
          String string = TimeStamp.toString(timeStamp);
          String oldString = text.getText();
          if (!Objects.equals(string, oldString))
          {
            try
            {
              settingValue = true;
              text.setText(string);
            }
            finally
            {
              settingValue = false;
            }
          }
        }
      }
    });

    int columns = 1;
    if (context != null)
    {
      for (int i = 0; i < selectors.length; i++)
      {
        TimeStampSelectorContributor selector = selectors[i];
        if (selector.contributeTimeStampSelector(this, context))
        {
          ++columns;
        }
      }
    }

    setLayout(GridLayoutFactory.fillDefaults().numColumns(columns).create());
  }

  public void addModifyListener(ModifyListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    listeners.add(listener);
  }

  public void removeModifyListener(ModifyListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    listeners.remove(listener);
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    if (this.timeStamp != timeStamp)
    {
      long oldValue = this.timeStamp;
      String oldError = error;

      this.timeStamp = timeStamp;
      validateValue();

      if (!text.isDisposed())
      {
        try
        {
          settingValue = true;
          text.setText(TimeStamp.toString(timeStamp));
        }
        finally
        {
          settingValue = false;
        }
      }

      notifyModifyListeners(oldValue, oldError);
    }
  }

  public String getError()
  {
    return error;
  }

  protected void validate()
  {
    String string = text.getText();
    error = null;

    try
    {
      timeStamp = TimeStamp.parseTimeStamp(string);
    }
    catch (Exception ex)
    {
      timeStamp = CDOBranchPoint.INVALID_DATE;
      error = ex.getMessage();
      return;
    }

    validateValue();
  }

  private void validateValue()
  {
    error = null;

    if (minTimeStamp != CDOBranchPoint.UNSPECIFIED_DATE && timeStamp < minTimeStamp)
    {
      timeStamp = CDOBranchPoint.INVALID_DATE;
      error = "Time stamp must not be smaller than " + TimeStamp.toString(minTimeStamp);
      return;
    }

    if (maxTimeStamp != CDOBranchPoint.UNSPECIFIED_DATE && timeStamp > maxTimeStamp)
    {
      timeStamp = CDOBranchPoint.INVALID_DATE;
      error = "Time stamp must not be greater than " + TimeStamp.toString(maxTimeStamp);
      return;
    }
  }

  private void notifyModifyListeners(long oldValue, String oldError)
  {
    if (timeStamp != oldValue || !Objects.equals(error, oldError))
    {
      ModifyListener[] array = listeners.get();

      for (int i = 0; i < array.length; i++)
      {
        try
        {
          ModifyListener listener = array[i];
          if (listener != null)
          {
            listener.modifyTimeStamp(this, timeStamp, error);
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface ModifyListener
  {
    public void modifyTimeStamp(TimeStampComposite control, long timeStamp, String error);
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface TimeStampSelectorContributor
  {
    public boolean contributeTimeStampSelector(TimeStampComposite composite, Object context);
  }
}
