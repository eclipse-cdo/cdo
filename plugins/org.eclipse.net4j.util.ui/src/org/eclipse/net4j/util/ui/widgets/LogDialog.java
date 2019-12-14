/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LogDialog extends BaseDialog<Viewer>
{
  private StringBuilder log = new StringBuilder();

  private StyledText text;

  private TextStyle textStyle;

  private List<StyleRange> styleRanges = new ArrayList<>();

  private StyleRange currentStyleRange = new StyleRange();

  private StyleRange lastStyleRange;

  private Font font;

  public LogDialog(Shell parentShell, int shellStyle, String title, String message, IDialogSettings settings)
  {
    super(parentShell, shellStyle, title, message, settings);
  }

  public LogDialog(Shell parentShell, String title, String message, IDialogSettings settings)
  {
    this(parentShell, DEFAULT_SHELL_STYLE, title, message, settings);
  }

  public TextStyle getTextStyle()
  {
    return textStyle;
  }

  public void setTextStyle(TextStyle textStyle)
  {
    if (textStyle == null)
    {
      throw new IllegalArgumentException("textStyle == null"); //$NON-NLS-1$
    }

    if (textStyle.equals(this.textStyle))
    {
      return;
    }

    lastStyleRange = currentStyleRange;
    this.textStyle = textStyle;

    currentStyleRange = new StyleRange();
    currentStyleRange.start = log.length();
    currentStyleRange.rise = textStyle.rise;
    currentStyleRange.background = textStyle.background;
    currentStyleRange.font = textStyle.font;
    currentStyleRange.foreground = textStyle.foreground;
    currentStyleRange.metrics = textStyle.metrics;
    currentStyleRange.strikeout = textStyle.strikeout;
    currentStyleRange.underline = textStyle.underline;
  }

  public void append(String text)
  {
    checkStyleRange();
    log.append(text);
    currentStyleRange.length += text.length();
  }

  public void append(Throwable t)
  {
    checkStyleRange();
    String text = IOUtil.toString(t);
    log.append(text);
    currentStyleRange.length += text.length();
  }

  @Override
  public boolean close()
  {
    font.dispose();
    return super.close();
  }

  @Override
  protected void createUI(Composite parent)
  {
    GridLayout grid = new GridLayout();
    grid.marginTop = 6;
    grid.marginLeft = 6;
    grid.marginRight = 6;
    grid.marginBottom = 6;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(grid);
    composite.setLayoutData(UIUtil.createGridData());

    font = new Font(getShell().getDisplay(), "Courier New", 9, SWT.NORMAL); //$NON-NLS-1$
    checkStyleRange();
    lastStyleRange = currentStyleRange;
    checkStyleRange();

    text = new StyledText(composite, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    text.setLayoutData(UIUtil.createGridData());
    text.setText(log.toString());
    text.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
    text.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
    text.setFont(font);
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
  }

  private void checkStyleRange()
  {
    if (lastStyleRange != null)
    {
      styleRanges.add(lastStyleRange);
      lastStyleRange = null;
    }
  }
}
