/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CheckoutLabelPage extends CheckoutWizardPage
{
  private final CheckoutPropertiesHolder holder = new CheckoutPropertiesHolder();

  private String label;

  public CheckoutLabelPage()
  {
    super("Checkout Label", "Enter the label of the new checkout.");
  }

  public final String getLabel()
  {
    return label;
  }

  public final void setLabel(String label)
  {
    if (!ObjectUtil.equals(this.label, label))
    {
      log("Setting label to " + label);
      this.label = label;

      if (holder.labelText != null && !holder.labelText.getText().equals(label))
      {
        holder.labelText.setText(StringUtil.safe(label));
      }
    }
  }

  @Override
  protected Layout createCompositeLayout()
  {
    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    return gridLayout;
  }

  @Override
  protected void createUI(Composite parent)
  {
    holder.createUI(parent);
    holder.labelText.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        setLabel(holder.labelText.getText());
        validate();
      }
    });
  }

  @Override
  protected void repositoryChanged(CDORepository repository)
  {
    clearLabel();
    super.repositoryChanged(repository);
  }

  @Override
  protected void typeChanged(String type)
  {
    clearLabel();
    super.typeChanged(type);
  }

  @Override
  protected void branchPointChanged(int branchID, long timeStamp)
  {
    clearLabel();
    super.branchPointChanged(branchID, timeStamp);
  }

  @Override
  protected void rootObjectChanged(CDOID rootID)
  {
    clearLabel();
    super.rootObjectChanged(rootID);
  }

  @Override
  protected void pageActivated()
  {
    CheckoutWizard wizard = getWizard();
    String type = wizard.getTypePage().getType();
    CDOBranchPoint branchPoint = wizard.getBranchPointPage().getBranchPoint();
    String rootLabel = wizard.getRootObjectPage().getRootObjectText();

    if (StringUtil.isEmpty(label))
    {
      String label = StringUtil.capAll(type.replace('-', ' ')) + " Checkout";
      setLabel(CDOExplorerUtil.getCheckoutManager().getUniqueLabel(label));
    }

    holder.typeLabel.setText(type);
    holder.branchLabel.setText(branchPoint.getBranch().getPathName());
    holder.timeLabel.setText(CDOCommonUtil.formatTimeStamp(branchPoint.getTimeStamp()));
    holder.rootLabel.setText(rootLabel);
    holder.repositoryLabel.setText(wizard.getRepositoryPage().getRepository().getLabel());

    holder.labelText.setFocus();
    holder.labelText.selectAll();

    super.pageActivated();
  }

  @Override
  protected boolean doValidate() throws ValidationProblem
  {
    if (StringUtil.isEmpty(label))
    {
      return false;
    }

    for (int i = 0; i < label.length(); i++)
    {
      char c = label.charAt(i);
      for (int j = 0; j < AbstractElement.ILLEGAL_LABEL_CHARACTERS.length(); j++)
      {
        if (c == AbstractElement.ILLEGAL_LABEL_CHARACTERS.charAt(j))
        {
          throw new ValidationProblem("Invalid character: " + AbstractElement.ILLEGAL_LABEL_CHARACTERS.substring(j, 1));
        }
      }
    }

    if (CDOExplorerUtil.getCheckoutManager().getCheckoutByLabel(label) != null)
    {
      throw new ValidationProblem("Label is not unique.");
    }

    return true;
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    properties.setProperty(CDOCheckoutImpl.PROP_LABEL, label);
  }

  private void clearLabel()
  {
    label = null;
    if (holder.labelText != null)
    {
      holder.labelText.setText("");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class CheckoutPropertiesHolder
  {
    public Text labelText;

    public Label typeLabel;

    public Label branchLabel;

    public Label timeLabel;

    public Label rootLabel;

    public Label repositoryLabel;

    public CheckoutPropertiesHolder()
    {
    }

    public void createUI(Composite parent)
    {
      new Label(parent, SWT.NONE).setText("Label:");
      labelText = createLabelText(parent);
      labelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      new Label(parent, SWT.NONE).setText("Type:");
      typeLabel = createTypeLabel(parent);
      typeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      new Label(parent, SWT.NONE).setText("Branch:");
      branchLabel = createBranchLabel(parent);
      branchLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      new Label(parent, SWT.NONE).setText("Time:");
      timeLabel = createTimeLabel(parent);
      timeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      new Label(parent, SWT.NONE).setText("Root:");
      rootLabel = createRootLabel(parent);
      rootLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      new Label(parent, SWT.NONE).setText("Repository:");
      repositoryLabel = createRepositoryLabel(parent);
      repositoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    protected Text createLabelText(Composite parent)
    {
      return new Text(parent, SWT.BORDER);
    }

    protected Label createTypeLabel(Composite parent)
    {
      return new Label(parent, SWT.NONE);
    }

    protected Label createBranchLabel(Composite parent)
    {
      return new Label(parent, SWT.NONE);
    }

    protected Label createTimeLabel(Composite parent)
    {
      return new Label(parent, SWT.NONE);
    }

    protected Label createRootLabel(Composite parent)
    {
      return new Label(parent, SWT.NONE);
    }

    protected Label createRepositoryLabel(Composite parent)
    {
      return new Label(parent, SWT.NONE);
    }
  }
}
