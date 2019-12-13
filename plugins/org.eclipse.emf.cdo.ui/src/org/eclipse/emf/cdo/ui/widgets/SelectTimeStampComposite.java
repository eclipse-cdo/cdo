/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.widgets;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenAuditDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.ValidationContext;
import org.eclipse.net4j.util.ui.ValidationParticipant;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.text.ParseException;

/**
 * UI widget allowing users to indicate {@link org.eclipse.emf.cdo.common.branch.CDOBranchPoint timestamp} of a
 * particular historical state of a {@link org.eclipse.emf.cdo.common.branch.CDOBranch branch}
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class SelectTimeStampComposite extends Composite implements ValidationParticipant
{
  private ValidationContext validationContext;

  private CDOBranch branch;

  private long timeStamp;

  private Composite pointGroup;

  private Button headRadio;

  private Button baseRadio;

  private Text baseText;

  private Button timeRadio;

  private Text timeText;

  private Button timeBrowseButton;

  /**
   * @since 4.4
   */
  public SelectTimeStampComposite(Composite parent, int style)
  {
    this(parent, style, null, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public SelectTimeStampComposite(Composite parent, int style, CDOBranch branch, long timeStamp)
  {
    super(parent, style);
    setLayout(new FillLayout());

    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;

    pointGroup = new Composite(this, SWT.NONE);
    pointGroup.setLayout(layout);

    baseRadio = new Button(pointGroup, SWT.RADIO);
    baseRadio.setText(Messages.getString("BranchSelectionDialog.2")); //$NON-NLS-1$
    baseRadio.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        if (baseRadio.getSelection())
        {
          setTimeStamp(SelectTimeStampComposite.this.branch.getBase().getTimeStamp());
        }
      }
    });

    baseText = new Text(pointGroup, SWT.BORDER);
    baseText.setLayoutData(createTimeGridData());
    baseText.setEnabled(false);
    new Label(pointGroup, SWT.NONE);

    timeRadio = new Button(pointGroup, SWT.RADIO);
    timeRadio.setText(Messages.getString("BranchSelectionDialog.3")); //$NON-NLS-1$
    timeRadio.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        if (timeRadio.getSelection())
        {
          parseTime();
        }
      }
    });

    timeText = new Text(pointGroup, SWT.BORDER);
    timeText.setLayoutData(createTimeGridData());
    timeText.setText(CDOCommonUtil.formatTimeStamp(timeStamp > CDOBranchPoint.UNSPECIFIED_DATE ? timeStamp : System.currentTimeMillis()));
    timeText.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        parseTime();
      }
    });

    timeText.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusGained(FocusEvent e)
      {
        selectRadio(timeRadio);
      }
    });

    timeBrowseButton = new Button(pointGroup, SWT.NONE);
    timeBrowseButton.setImage(SharedIcons.getImage(SharedIcons.ETOOL_CLOCK));
    timeBrowseButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        selectRadio(timeRadio);
        OpenAuditDialog dialog = new OpenAuditDialog(UIUtil.getActiveWorkbenchPage());
        if (dialog.open() == IDialogConstants.OK_ID)
        {
          setTimeStamp(dialog.getTimeStamp());
        }
      }
    });

    headRadio = new Button(pointGroup, SWT.RADIO);
    headRadio.setText(Messages.getString("BranchSelectionDialog.1")); //$NON-NLS-1$
    headRadio.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        if (headRadio.getSelection())
        {
          setTimeStamp(CDOBranchPoint.UNSPECIFIED_DATE);
        }
      }
    });

    new Label(pointGroup, SWT.NONE);
    new Label(pointGroup, SWT.NONE);

    this.timeStamp = timeStamp;
    setBranch(branch);
  }

  @Override
  public ValidationContext getValidationContext()
  {
    return validationContext;
  }

  @Override
  public void setValidationContext(ValidationContext validationContext)
  {
    this.validationContext = validationContext;
  }

  public CDOBranch getBranch()
  {
    return branch;
  }

  public void setBranch(CDOBranch branch)
  {
    this.branch = branch;
    headRadio.setEnabled(branch != null);
    baseRadio.setEnabled(branch != null);
    baseText.setText(branch != null ? CDOCommonUtil.formatTimeStamp(branch.getBase().getTimeStamp()) : "");
    setTimeStamp(timeStamp);
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    long oldTimeStamp = this.timeStamp;
    this.timeStamp = timeStamp;
    if (branch != null && (timeStamp == CDOBranchPoint.INVALID_DATE || timeStamp == CDOBranchPoint.UNSPECIFIED_DATE))
    {
      selectRadio(headRadio);
    }
    else if (branch != null && timeStamp == branch.getBase().getTimeStamp())
    {
      selectRadio(baseRadio);
    }
    else
    {
      selectRadio(timeRadio);
      // String text = CDOCommonUtil
      // .formatTimeStamp(timeStamp == CDOBranchPoint.INVALID_DATE || timeStamp == CDOBranchPoint.UNSPECIFIED_DATE
      // ? System.currentTimeMillis() : timeStamp);
      // if (!timeText.getText().equals(text))
      // {
      // timeText.setText(text);
      // }
    }

    if (oldTimeStamp != timeStamp)
    {
      timeStampChanged(timeStamp);
    }
  }

  protected void timeStampChanged(long timeStamp)
  {
  }

  public Button getHeadRadio()
  {
    return headRadio;
  }

  public Button getBaseRadio()
  {
    return baseRadio;
  }

  public Text getBaseText()
  {
    return baseText;
  }

  public Button getTimeRadio()
  {
    return timeRadio;
  }

  public Text getTimeText()
  {
    return timeText;
  }

  public Button getTimeBrowseButton()
  {
    return timeBrowseButton;
  }

  private GridData createTimeGridData()
  {
    GridData gd2 = UIUtil.createGridData(false, false);
    gd2.widthHint = 160;
    return gd2;
  }

  private void parseTime()
  {
    try
    {
      String text = timeText.getText();
      if (StringUtil.isEmpty(text))
      {
        if (validationContext != null)
        {
          validationContext.setValidationError(timeText, "Please enter a valid time stamp.");
        }

        return;
      }

      long timeStamp = CDOCommonUtil.parseTimeStamp(text);
      setTimeStamp(timeStamp);

      if (validationContext != null)
      {
        String message = null;

        if (branch != null && timeStamp != CDOBranchPoint.UNSPECIFIED_DATE && timeStamp != CDOBranchPoint.INVALID_DATE)
        {
          long repositoryCreation = branch.getBranchManager().getMainBranch().getBase().getTimeStamp();
          if (timeStamp < repositoryCreation)
          {
            message = "The repository did not exist before " + CDOCommonUtil.formatTimeStamp(repositoryCreation) + ".";
          }
        }

        validationContext.setValidationError(timeText, message);
      }
    }
    catch (ParseException ex)
    {
      if (validationContext != null)
      {
        validationContext.setValidationError(timeText, "Invalid time stamp.");
      }
    }
  }

  private void selectRadio(Button button)
  {
    headRadio.setSelection(button == headRadio);
    baseRadio.setSelection(button == baseRadio);
    timeRadio.setSelection(button == timeRadio);
  }
}
