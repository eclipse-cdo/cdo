/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server.rap;

import org.eclipse.emf.cdo.examples.server.DemoConfiguration;
import org.eclipse.emf.cdo.examples.server.DemoConfiguration.Mode;
import org.eclipse.emf.cdo.examples.server.DemoServer;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Random;

/**
 * @author Eike Stepper
 */
public class NewRepositoryDialog extends TitleAreaDialog
{
  private IWorkbenchPage page;

  private Image wizban;

  private Calculator calculator = new Calculator();

  private Text resultText;

  private Label modeLabel;

  private Combo modeCombo;

  private DemoConfiguration.Mode mode;

  public NewRepositoryDialog(IWorkbenchPage page, Image wizban)
  {
    super(new Shell(page.getWorkbenchWindow().getShell()));
    this.page = page;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.TITLE | SWT.RESIZE);
    this.wizban = wizban;
  }

  public IWorkbenchPage getPage()
  {
    return page;
  }

  public Mode getMode()
  {
    return mode;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("New Repository");
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle("Create a new CDO model repository instance for demo purposes.\nIt will automatically be destroyed after "
        + DemoServer.MAX_IDLE_MINUTES + " minutes of inactivity.");
    setTitleImage(wizban);

    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.horizontalSpacing = 10;
    gridLayout.marginWidth = 10;
    gridLayout.marginHeight = 10;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(gridLayout);

    Label resultLabel = new Label(composite, SWT.NONE);
    resultLabel.setText(calculator.toString() + " =");

    resultText = new Text(composite, SWT.BORDER);
    resultText.setTextLimit(5);
    resultText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    resultText.setFocus();

    modeLabel = new Label(composite, SWT.NONE);
    modeLabel.setText("Mode:");

    modeCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
    for (Mode mode : DemoConfiguration.Mode.values())
    {
      modeCombo.add(mode.toString());
    }

    modeCombo.select(0);

    resultText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent event)
      {
        boolean smartEnough = resultText.getText().equals(Integer.toString(calculator.getResult()));
        setEnablement(smartEnough);
      }
    });

    return composite;
  }

  @Override
  protected Control createButtonBar(Composite parent)
  {
    Control buttonBar = super.createButtonBar(parent);
    setEnablement(false);
    return buttonBar;
  }

  private void setEnablement(boolean enable)
  {
    modeLabel.setVisible(enable);
    modeCombo.setVisible(enable);

    Button okButton = getButton(IDialogConstants.OK_ID);
    if (okButton != null)
    {
      okButton.setEnabled(enable);
    }
  }

  @Override
  public boolean close()
  {
    mode = DemoConfiguration.Mode.valueOf(modeCombo.getText());
    return super.close();
  }

  /**
   * @author Eike Stepper
   */
  private static final class Calculator
  {
    private int op1;

    private int op2;

    private Operator op;

    private Calculator()
    {
      Random random = new Random(System.currentTimeMillis());
      op1 = random.nextInt(10) + 1;
      op2 = random.nextInt(10) + 1;
      Operator[] operators = Operator.values();
      op = operators[random.nextInt(operators.length)];
    }

    public int getResult()
    {
      return op.solve(op1, op2);
    }

    @Override
    public String toString()
    {
      return Integer.toString(op1) + " " + op.getSymbol() + " " + op2;
    }

    /**
     * @author Eike Stepper
     */
    public enum Operator
    {
      PLUS("+")
      {
        @Override
        public int solve(int op1, int op2)
        {
          return op1 + op2;
        }
      },

      MINUS("-")
      {
        @Override
        public int solve(int op1, int op2)
        {
          return op1 - op2;
        }
      },

      MULTIPLY("*")
      {
        @Override
        public int solve(int op1, int op2)
        {
          return op1 * op2;
        }
      };

      private String symbol;

      private Operator(String symbol)
      {
        this.symbol = symbol;
      }

      public String getSymbol()
      {
        return symbol;
      }

      public abstract int solve(int op1, int op2);
    }
  }
}
