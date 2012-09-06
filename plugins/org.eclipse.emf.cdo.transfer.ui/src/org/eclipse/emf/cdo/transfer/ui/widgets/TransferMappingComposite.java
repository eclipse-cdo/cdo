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
import org.eclipse.emf.cdo.transfer.CDOTransferType;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferMappingComposite extends Composite implements IListener
{
  private CDOTransfer transfer;

  private CDOTransferMapping mapping;

  private Text sourcePath;

  private Text targetPath;

  private Combo transferType;

  private Label status;

  private Text relativePath;

  private Combo resolution;

  public TransferMappingComposite(Composite parent, int style, CDOTransfer transfer)
  {
    super(parent, style);
    this.transfer = transfer;
    this.transfer.addListener(this);

    GridLayout gl_composite = new GridLayout(4, false);
    gl_composite.marginWidth = 10;
    setLayout(gl_composite);

    Label lblSourcePath = new Label(this, SWT.NONE);
    lblSourcePath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblSourcePath.setText("Source Path:");

    sourcePath = new Text(this, SWT.BORDER | SWT.READ_ONLY);
    sourcePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblType = new Label(this, SWT.NONE);
    lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblType.setText("Type:");

    transferType = new Combo(this, SWT.NONE);
    transferType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    transferType.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        if (mapping != null)
        {
          String text = transferType.getText();
          mapping.setTransferType(CDOTransferType.REGISTRY.get(text));
        }
      }
    });

    Set<CDOTransferType> usedTransferTypes = transfer.getUsedTransferTypes();
    usedTransferTypes.addAll(CDOTransferType.STANDARD_TYPES);

    List<CDOTransferType> transferTypes = new ArrayList<CDOTransferType>(usedTransferTypes);
    Collections.sort(transferTypes);

    for (CDOTransferType type : transferTypes)
    {
      transferType.add(type.toString());
    }

    Label lblTargetPath = new Label(this, SWT.NONE);
    lblTargetPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblTargetPath.setText("Target Path:");

    targetPath = new Text(this, SWT.BORDER | SWT.READ_ONLY);
    targetPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblStatus = new Label(this, SWT.NONE);
    lblStatus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblStatus.setText("Status:");

    status = new Label(this, SWT.NONE);
    status.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    Label lblRelativePath = new Label(this, SWT.NONE);
    lblRelativePath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblRelativePath.setText("Relative Path:");

    Composite pathPane = new Composite(this, SWT.NONE);
    pathPane.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    GridLayout gl_pathPane = new GridLayout(3, false);
    gl_pathPane.marginWidth = 0;
    gl_pathPane.marginHeight = 0;
    pathPane.setLayout(gl_pathPane);

    relativePath = new Text(pathPane, SWT.BORDER);
    relativePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    relativePath.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        if (mapping != null)
        {
          String text = relativePath.getText();
          mapping.setRelativePath(text);
        }
      }
    });

    Button btnNewButton = new Button(pathPane, SWT.NONE);
    btnNewButton.setBounds(0, 0, 75, 25);
    btnNewButton.setText("<");

    Button btnNewButton_1 = new Button(pathPane, SWT.NONE);
    btnNewButton_1.setBounds(0, 0, 75, 25);
    btnNewButton_1.setText(">");

    Label lblResolution = new Label(this, SWT.NONE);
    lblResolution.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblResolution.setText("Resolution:");

    resolution = new Combo(this, SWT.NONE);
    resolution.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    Label lblUnmappedReferences = new Label(this, SWT.NONE);
    lblUnmappedReferences.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
    lblUnmappedReferences.setText("Unmapped References:");

    TableViewer tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
    Table table = tableViewer.getTable();
    table.setLinesVisible(true);
    table.setHeaderVisible(true);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmnUri = tableViewerColumn.getColumn();
    tblclmnUri.setWidth(373);
    tblclmnUri.setText("URI");

    TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmnNewColumn = tableViewerColumn_1.getColumn();
    tblclmnNewColumn.setWidth(341);
    tblclmnNewColumn.setText("Transformation");

    Composite composite_1 = new Composite(this, SWT.NONE);
    GridLayout gl_composite_1 = new GridLayout(1, false);
    gl_composite_1.marginWidth = 0;
    gl_composite_1.marginHeight = 0;
    composite_1.setLayout(gl_composite_1);
    composite_1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));

    Button mapSource = new Button(composite_1, SWT.NONE);
    mapSource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    mapSource.setBounds(0, 0, 75, 25);
    mapSource.setText("Map From Source");

    Button replaceTarget = new Button(composite_1, SWT.NONE);
    replaceTarget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    replaceTarget.setBounds(0, 0, 75, 25);
    replaceTarget.setText("Replace With Target");

    Button keepAsIs = new Button(composite_1, SWT.NONE);
    keepAsIs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    keepAsIs.setBounds(0, 0, 75, 25);
    keepAsIs.setText("Keep As Is");
  }

  @Override
  public void dispose()
  {
    transfer.removeListener(this);
    transfer = null;
    mapping = null;
    super.dispose();
  }

  public CDOTransferMapping getMapping()
  {
    return mapping;
  }

  public void setMapping(CDOTransferMapping mapping)
  {
    if (!ObjectUtil.equals(this.mapping, mapping))
    {
      this.mapping = mapping;
      if (mapping != null)
      {
        sourcePath.setText(mapping.getSource().getPath().toString());
        targetPath.setText(mapping.getFullPath().toString());
        transferType.setText(mapping.getTransferType().toString());
        status.setText(mapping.getStatus().toString());
        relativePath.setText(mapping.getRelativePath().toString());
      }
      else
      {
        sourcePath.setText(StringUtil.EMPTY);
        targetPath.setText(StringUtil.EMPTY);
        transferType.setText(StringUtil.EMPTY);
        status.setText(StringUtil.EMPTY);
        relativePath.setText(StringUtil.EMPTY);
      }
    }
  }

  @Override
  public boolean setFocus()
  {
    return relativePath.setFocus();
  }

  public void notifyEvent(IEvent event)
  {
    if (event instanceof CDOTransfer.MappingEvent)
    {
      CDOTransfer.MappingEvent e = (CDOTransfer.MappingEvent)event;
      if (ObjectUtil.equals(e.getMapping(), mapping))
      {
        notifyMappingEvent(e);
      }
    }
  }

  protected void notifyMappingEvent(CDOTransfer.MappingEvent event)
  {
    if (event instanceof CDOTransfer.TransferTypeChangedEvent)
    {
      CDOTransfer.TransferTypeChangedEvent e2 = (CDOTransfer.TransferTypeChangedEvent)event;
      final String value = e2.getNewType().toString();
      if (!ObjectUtil.equals(value, transferType.getText()))
      {
        getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            transferType.setText(value);
          }
        });
      }
    }
    else if (event instanceof CDOTransfer.RelativePathChangedEvent)
    {
      CDOTransfer.RelativePathChangedEvent e2 = (CDOTransfer.RelativePathChangedEvent)event;
      final String value = e2.getNewPath().toString();
      if (!ObjectUtil.equals(value, relativePath.getText()))
      {
        getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            relativePath.setText(value);
          }
        });
      }
    }
  }
}
