/*
 * Copyright (c) 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.team.history;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.Support;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;

import org.eclipse.net4j.util.ui.widgets.AbstractDialog;
import org.eclipse.net4j.util.ui.widgets.DoubleClickButtonAdapter;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class DropConfirmationDialog extends AbstractDialog
{
  private static final String TITLE = "New Commit";

  private final CDOBranchPoint sourcePoint;

  private final CDOBranch targetBranch;

  private Operation operation;

  public DropConfirmationDialog(Shell parentShell, CDOBranchPoint sourcePoint, CDOBranch targetBranch)
  {
    super(parentShell);
    this.sourcePoint = sourcePoint;
    this.targetBranch = targetBranch;
  }

  public CDOBranchPoint getSourcePoint()
  {
    return sourcePoint;
  }

  public CDOBranch getTargetBranch()
  {
    return targetBranch;
  }

  public Operation getOperation()
  {
    return operation;
  }

  public void setOperation(Operation operation)
  {
    this.operation = operation;
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(450, 250);
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(TITLE);
  }

  @Override
  protected void createUI(Composite container)
  {
    setTitle(TITLE);
    setMessage("Select the content of the new commit in branch " + targetBranch.getPathName() + ".");

    GridLayout containerGridLayout = (GridLayout)container.getLayout();
    containerGridLayout.numColumns = 3;

    GridDataFactory grab = GridDataFactory.fillDefaults().grab(true, false);
    new Label(container, SWT.NONE).setLayoutData(grab.create());

    Button revertToButton = new Button(container, SWT.RADIO);
    revertToButton.setText(formatOperation(Operation.REVERT_TO));
    new OperationAdapter(revertToButton, Operation.REVERT_TO);

    new Label(container, SWT.NONE).setLayoutData(grab.create());
    new Label(container, SWT.NONE).setLayoutData(grab.create());

    Button mergeFromButton = new Button(container, SWT.RADIO);
    mergeFromButton.setText(formatOperation(Operation.MERGE_FROM));
    new OperationAdapter(mergeFromButton, Operation.MERGE_FROM);

    new Label(container, SWT.NONE).setLayoutData(grab.create());

    if (!Support.UI_COMPARE.isAvailable())
    {
      mergeFromButton.setEnabled(false);
      operation = Operation.REVERT_TO;
    }

    if (operation == Operation.MERGE_FROM)
    {
      mergeFromButton.setSelection(true);
    }
    else
    {
      revertToButton.setSelection(true);
      operation = Operation.REVERT_TO;
    }
  }

  private String formatOperation(Operation operation)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(operation.getLabel());
    builder.append(' ');
    CDOTransactionCommentator.appendBranchPoint(builder, sourcePoint);
    return builder.toString();
  }

  /**
   * @author Eike Stepper
   */
  public enum Operation
  {
    REVERT_TO("Revert to"), MERGE_FROM("Merge from");

    private String label;

    private Operation(String label)
    {
      this.label = label;
    }

    public String getLabel()
    {
      return label;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class OperationAdapter extends DoubleClickButtonAdapter
  {
    private final Operation operation;

    public OperationAdapter(Button button, Operation operation)
    {
      super(button);
      this.operation = operation;
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
      DropConfirmationDialog.this.operation = operation;
    }

    @Override
    public void widgetDoubleClicked(SelectionEvent e)
    {
      close();
    }
  }
}
