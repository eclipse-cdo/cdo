/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import java.util.Date;

/**
 * @author Eike Stepper
 * @since 2.0
 */
//Under development
@Deprecated
@SuppressWarnings("deprecation")
public class DateSpinner extends Composite implements SelectionListener
{
  public static final Date MIN_DATE = new Date(0);

  private Date minDate = MIN_DATE;

  private Date maxDate = new Date();

  private Date date = maxDate;

  private Spinner yearSpinner;

  private Spinner monthSpinner;

  private Spinner daySpinner;

  public DateSpinner(Composite parent, int spinnerStyle)
  {
    super(parent, SWT.NONE);
    setLayout(new FillLayout(SWT.HORIZONTAL));

    yearSpinner = new Spinner(this, spinnerStyle);
    yearSpinner.addSelectionListener(this);
    yearSpinner.setTextLimit(4);
    yearSpinner.setMinimum(minDate.getYear() + 1900);
    yearSpinner.setMaximum(maxDate.getYear() + 1900);
    yearSpinner.setSelection(date.getYear() + 1900);

    monthSpinner = new Spinner(this, spinnerStyle);
    monthSpinner.addSelectionListener(this);
    monthSpinner.addMouseListener(new MouseListener()
    {
      public void mouseDoubleClick(MouseEvent e)
      {
        System.out.println("mouseDoubleClick: " + e);
      }

      public void mouseDown(MouseEvent e)
      {
        System.out.println("mouseDown: " + e);
      }

      public void mouseUp(MouseEvent e)
      {
        System.out.println("mouseUp: " + e);
      }
    });
    monthSpinner.setTextLimit(2);
    monthSpinner.setMinimum(minDate.getMonth() + 1);
    monthSpinner.setMaximum(maxDate.getMonth() + 1);
    monthSpinner.setSelection(date.getMonth() + 1);

    daySpinner = new Spinner(this, spinnerStyle);
    daySpinner.addSelectionListener(this);
    daySpinner.setTextLimit(2);
    daySpinner.setMinimum(minDate.getDate());
    daySpinner.setMaximum(maxDate.getDate());
    daySpinner.setSelection(date.getDate());
  }

  public Date getMinDate()
  {
    return minDate;
  }

  public void setMinDate(Date minDate)
  {
    if (minDate.before(MIN_DATE))
    {
      throw new IllegalArgumentException("minDate");
    }

    if (this.minDate != minDate)
    {
      this.minDate = minDate;
      yearSpinner.setMinimum(minDate.getYear() + 1900);
      monthSpinner.setMinimum(minDate.getMonth() + 1);
      daySpinner.setMinimum(minDate.getDate());
    }
  }

  public Date getMaxDate()
  {
    return maxDate;
  }

  public void setMaxDate(Date maxDate)
  {
    if (maxDate.after(new Date()))
    {
      throw new IllegalArgumentException("maxDate");
    }

    if (this.maxDate != maxDate)
    {
      this.maxDate = maxDate;
      yearSpinner.setMaximum(maxDate.getYear() + 1900);
      monthSpinner.setMaximum(maxDate.getMonth() + 1);
      daySpinner.setMaximum(maxDate.getDate());
    }
  }

  public Date getDate()
  {
    return date;
  }

  public void setDate(Date date)
  {
    if (date.before(minDate))
    {
      throw new IllegalArgumentException("date");
    }

    if (date.after(maxDate))
    {
      throw new IllegalArgumentException("date");
    }

    if (this.date != date)
    {
      this.date = date;
      yearSpinner.setSelection(date.getYear() + 1900);
      monthSpinner.setSelection(date.getMonth());
      daySpinner.setSelection(date.getDay());
    }
  }

  public void widgetSelected(SelectionEvent e)
  {
    System.out.println("widgetSelected: " + e);
    int year = yearSpinner.getSelection() - 1900;
    if (date.getYear() != year)
    {
      date.setYear(year);
    }

    int month = monthSpinner.getSelection() - 1;
    if (date.getMonth() != month)
    {
      date.setMonth(month);
    }

    int day = daySpinner.getSelection();
    if (date.getDate() != day)
    {
      date.setDate(day);
    }
  }

  public void widgetDefaultSelected(SelectionEvent e)
  {
    System.out.println("widgetDefaultSelected: " + e);
  }

  public static void main(String[] args)
  {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());

    DateSpinner spinner = new DateSpinner(shell, SWT.NONE);

    // scale.setSize(200, 64);
    // scale.setMaximum(40);
    // scale.setPageIncrement(5);

    shell.open();
    while (!shell.isDisposed())
    {
      if (!display.readAndDispatch())
      {
        display.sleep();
      }
    }

    display.dispose();
  }
}
