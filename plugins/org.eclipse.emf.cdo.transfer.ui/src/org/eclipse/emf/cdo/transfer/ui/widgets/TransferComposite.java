/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.ui.widgets;

import org.eclipse.emf.cdo.transfer.CDOTransfer;
import org.eclipse.emf.cdo.transfer.CDOTransferMapping;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferComposite extends SashForm implements ISelectionChangedListener
{
  private TransferTreeComposite transferTreeComposite;

  private TransferMappingComposite transferMappingComposite;

  public TransferComposite(Composite parent, CDOTransfer transfer)
  {
    super(parent, SWT.SMOOTH | SWT.VERTICAL);
    setLayout(new FillLayout());

    transferTreeComposite = new TransferTreeComposite(this, SWT.NONE, transfer);
    transferTreeComposite.getViewer().addSelectionChangedListener(this);

    transferMappingComposite = new TransferMappingComposite(this, SWT.NONE, transfer);
    selectionChanged(null);

    // Composite details = new Composite(this, SWT.NONE);
    // GridLayout gl_composite = new GridLayout(4, false);
    // gl_composite.marginWidth = 10;
    // details.setLayout(gl_composite);
    //
    // Label lblSourcePath = new Label(details, SWT.NONE);
    // lblSourcePath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    // lblSourcePath.setText("Source Path:");
    //
    // sourcePath = new Text(details, SWT.BORDER | SWT.READ_ONLY);
    // sourcePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    //
    // Label lblType = new Label(details, SWT.NONE);
    // lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    // lblType.setText("Type:");
    //
    // transferType = new Combo(details, SWT.NONE);
    // transferType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    //
    // Set<CDOTransferType> usedTransferTypes = transfer.getUsedTransferTypes();
    // usedTransferTypes.addAll(CDOTransferType.STANDARD_TYPES);
    //
    // List<CDOTransferType> transferTypes = new ArrayList<CDOTransferType>(usedTransferTypes);
    // Collections.sort(transferTypes);
    //
    // for (CDOTransferType type : transferTypes)
    // {
    // transferType.add(type.toString());
    // }
    //
    // Label lblTargetPath = new Label(details, SWT.NONE);
    // lblTargetPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    // lblTargetPath.setText("Target Path:");
    //
    // targetPath = new Text(details, SWT.BORDER | SWT.READ_ONLY);
    // targetPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    //
    // Label lblStatus = new Label(details, SWT.NONE);
    // lblStatus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    // lblStatus.setText("Status:");
    //
    // status = new Label(details, SWT.NONE);
    // status.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    //
    // Label lblRelativePath = new Label(details, SWT.NONE);
    // lblRelativePath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    // lblRelativePath.setText("Relative Path:");
    //
    // Composite pathPane = new Composite(details, SWT.NONE);
    // pathPane.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    // GridLayout gl_pathPane = new GridLayout(3, false);
    // gl_pathPane.marginWidth = 0;
    // gl_pathPane.marginHeight = 0;
    // pathPane.setLayout(gl_pathPane);
    //
    // relativePath = new Text(pathPane, SWT.BORDER);
    // relativePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    // relativePath.addModifyListener(new ModifyListener()
    // {
    // public void modifyText(ModifyEvent e)
    // {
    // String text = relativePath.getText();
    // CDOTransferMapping mapping = getSelection();
    // if (!ObjectUtil.equals(mapping.getRelativePath().toString(), text))
    // {
    // mapping.setRelativePath(text);
    // UIUtil.refreshElement(treeViewer, mapping, true);
    // }
    // }
    // });
    //
    // Button btnNewButton = new Button(pathPane, SWT.NONE);
    // btnNewButton.setBounds(0, 0, 75, 25);
    // btnNewButton.setText("<");
    //
    // Button btnNewButton_1 = new Button(pathPane, SWT.NONE);
    // btnNewButton_1.setBounds(0, 0, 75, 25);
    // btnNewButton_1.setText(">");
    //
    // Label lblResolution = new Label(details, SWT.NONE);
    // lblResolution.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    // lblResolution.setText("Resolution:");
    //
    // resolution = new Combo(details, SWT.NONE);
    // resolution.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    //
    // Label lblUnmappedReferences = new Label(details, SWT.NONE);
    // lblUnmappedReferences.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
    // lblUnmappedReferences.setText("Unmapped References:");
    //
    // TableViewer tableViewer = new TableViewer(details, SWT.BORDER | SWT.FULL_SELECTION);
    // Table table = tableViewer.getTable();
    // table.setLinesVisible(true);
    // table.setHeaderVisible(true);
    // table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    //
    // TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    // TableColumn tblclmnUri = tableViewerColumn.getColumn();
    // tblclmnUri.setWidth(373);
    // tblclmnUri.setText("URI");
    //
    // TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
    // TableColumn tblclmnNewColumn = tableViewerColumn_1.getColumn();
    // tblclmnNewColumn.setWidth(341);
    // tblclmnNewColumn.setText("Transformation");
    //
    // Composite composite_1 = new Composite(details, SWT.NONE);
    // GridLayout gl_composite_1 = new GridLayout(1, false);
    // gl_composite_1.marginWidth = 0;
    // gl_composite_1.marginHeight = 0;
    // composite_1.setLayout(gl_composite_1);
    // composite_1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
    //
    // Button mapSource = new Button(composite_1, SWT.NONE);
    // mapSource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    // mapSource.setBounds(0, 0, 75, 25);
    // mapSource.setText("Map From Source");
    //
    // Button replaceTarget = new Button(composite_1, SWT.NONE);
    // replaceTarget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    // replaceTarget.setBounds(0, 0, 75, 25);
    // replaceTarget.setText("Replace With Target");
    //
    // Button keepAsIs = new Button(composite_1, SWT.NONE);
    // keepAsIs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    // keepAsIs.setBounds(0, 0, 75, 25);
    // keepAsIs.setText("Keep As Is");

    setWeights(new int[] { 2, 1 });
  }

  public void selectionChanged(SelectionChangedEvent event)
  {
    CDOTransferMapping mapping = transferTreeComposite.getSelectedMapping();
    transferMappingComposite.setMapping(mapping);
  }

  @Override
  public boolean setFocus()
  {
    return transferTreeComposite.setFocus();
  }

  @Override
  protected void checkSubclass()
  {
    // Do nothing
  }
}
