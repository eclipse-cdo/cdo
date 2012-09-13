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
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferMapping;
import org.eclipse.emf.cdo.transfer.CDOTransferType;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.StructuredContentProvider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
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
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferDetailsComposite extends Composite implements IListener
{
  private static final String UP = "..";

  private static final Path UP_PATH = new Path(UP);

  private CDOTransfer transfer;

  private CDOTransferMapping mapping;

  private Text sourcePath;

  private Text targetPath;

  private Combo transferType;

  private Text status;

  private Text relativePath;

  private Combo resolution;

  private ListViewer unmappedModels;

  public TransferDetailsComposite(Composite parent, int style, final CDOTransfer transfer)
  {
    super(parent, style);
    this.transfer = transfer;
    this.transfer.addListener(this);

    GridLayout gl_composite = new GridLayout(4, false);
    gl_composite.marginWidth = 10;
    setLayout(gl_composite);

    Label sourcePathLabel = new Label(this, SWT.NONE);
    sourcePathLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    sourcePathLabel.setText("Source Path:");

    sourcePath = new Text(this, SWT.BORDER | SWT.READ_ONLY);
    sourcePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label transferTypeLabel = new Label(this, SWT.NONE);
    transferTypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    transferTypeLabel.setText("Type:");

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
          CDOTransferType type = CDOTransferType.REGISTRY.get(text);
          mapping.setTransferType(type);
        }
      }
    });

    Set<CDOTransferType> usedTransferTypes = transfer.getUsedTransferTypes();
    usedTransferTypes.addAll(CDOTransferType.STANDARD_TYPES);
    usedTransferTypes.addAll(CDOTransferType.STANDARD_TYPES);

    List<CDOTransferType> transferTypes = new ArrayList<CDOTransferType>(usedTransferTypes);
    Collections.sort(transferTypes);

    for (CDOTransferType type : transferTypes)
    {
      transferType.add(type.toString());
    }

    Label targetPathLabel = new Label(this, SWT.NONE);
    targetPathLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    targetPathLabel.setText("Target Path:");

    targetPath = new Text(this, SWT.BORDER | SWT.READ_ONLY);
    targetPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label statusLabel = new Label(this, SWT.NONE);
    statusLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    statusLabel.setText("Status:");

    status = new Text(this, SWT.BORDER | SWT.READ_ONLY);
    status.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

    Label relativePathLabel = new Label(this, SWT.NONE);
    relativePathLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    relativePathLabel.setText("Relative Path:");

    GridData gd_pathPane = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gd_pathPane.heightHint = 27;

    GridLayout pathPaneLayout = new GridLayout(4, false);
    pathPaneLayout.marginWidth = 0;
    pathPaneLayout.marginHeight = 0;

    Composite pathPane = new Composite(this, SWT.NONE);
    pathPane.setLayoutData(gd_pathPane);
    pathPane.setLayout(pathPaneLayout);

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

    Button leftButton = new Button(pathPane, SWT.NONE);
    leftButton.setBounds(0, 0, 75, 25);
    leftButton.setText("<");
    leftButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        IPath path = new Path(relativePath.getText());
        if (path.isEmpty() || UP.equals(path.segment(0)))
        {
          path = UP_PATH.append(path);
        }
        else
        {
          path = path.removeFirstSegments(1);
        }

        mapping.setRelativePath(path);
      }
    });

    Button rightButton = new Button(pathPane, SWT.NONE);
    rightButton.setBounds(0, 0, 75, 25);
    rightButton.setText(">");
    rightButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        IPath path = new Path(relativePath.getText());
        if (UP.equals(path.segment(0)))
        {
          path = path.removeFirstSegments(1);
        }
        else if (path.isEmpty())
        {
          path = new Path(mapping.getSource().getName());
        }
        else
        {
          path = new Path("folder").append(path);
        }

        mapping.setRelativePath(path);
      }
    });

    Button renameButton = new Button(pathPane, SWT.NONE);
    renameButton.setText("+");
    renameButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        String sourceName = mapping.getSource().getName();

        IPath path = new Path(relativePath.getText());
        if (path.isEmpty() || UP.equals(path.segment(0)))
        {
          path = new Path(sourceName);
        }

        String name = path.lastSegment();
        int i = 1;
        while (transfer.getTargetSystem().getElement(path) != null) // TODO This condition is still wrong
        {
          path = path.removeLastSegments(1).append(name + i);
        }

        mapping.setRelativePath(path);
      }
    });

    Label resolutionLabel = new Label(this, SWT.NONE);
    resolutionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    resolutionLabel.setText("Resolution:");
    resolutionLabel.setVisible(false);

    resolution = new Combo(this, SWT.NONE);
    resolution.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    resolution.setVisible(false);

    Label unmappedModelsLabel = new Label(this, SWT.NONE);
    unmappedModelsLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
    unmappedModelsLabel.setText("Unmapped Models:");

    unmappedModels = new ListViewer(this, SWT.BORDER);
    org.eclipse.swt.widgets.List list = unmappedModels.getList();
    list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    unmappedModels.setContentProvider(new UnmappedModelsContentProvider());
    unmappedModels.setLabelProvider(new UnmappedModelsLabelProvider());
    unmappedModels.setInput(transfer);

    GridLayout transformationButtonsPaneLayout = new GridLayout(1, false);
    transformationButtonsPaneLayout.marginWidth = 0;
    transformationButtonsPaneLayout.marginHeight = 0;

    Composite transformationButtonsPane = new Composite(this, SWT.NONE);
    transformationButtonsPane.setLayout(transformationButtonsPaneLayout);
    transformationButtonsPane.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));

    Button mapSource = new Button(transformationButtonsPane, SWT.NONE);
    mapSource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    mapSource.setBounds(0, 0, 75, 25);
    mapSource.setText("Map From Source");
    mapSource.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        IStructuredSelection selection = (IStructuredSelection)unmappedModels.getSelection();
        Resource resource = (Resource)selection.getFirstElement();

        URI uri = resource.getURI();
        CDOTransferElement element = transfer.getSourceSystem().getElement(uri);
        transfer.map(element);
      }
    });

    Button replaceTarget = new Button(transformationButtonsPane, SWT.NONE);
    replaceTarget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    replaceTarget.setBounds(0, 0, 75, 25);
    replaceTarget.setText("Replace With Target");

    Button keepAsIs = new Button(transformationButtonsPane, SWT.NONE);
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

  public CDOTransfer getTransfer()
  {
    return transfer;
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

  public Text getSourcePath()
  {
    return sourcePath;
  }

  public Text getTargetPath()
  {
    return targetPath;
  }

  public Combo getTransferType()
  {
    return transferType;
  }

  public Text getStatus()
  {
    return status;
  }

  public Text getRelativePath()
  {
    return relativePath;
  }

  public Combo getResolution()
  {
    return resolution;
  }

  public ListViewer getUnmappedModels()
  {
    return unmappedModels;
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
      CDOTransfer.TransferTypeChangedEvent e = (CDOTransfer.TransferTypeChangedEvent)event;
      final String value = e.getNewType().toString();
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          if (!ObjectUtil.equals(value, transferType.getText()))
          {
            transferType.setText(value);
          }

          unmappedModels.refresh();
        }
      });
    }
    else if (event instanceof CDOTransfer.RelativePathChangedEvent)
    {
      CDOTransfer.RelativePathChangedEvent e = (CDOTransfer.RelativePathChangedEvent)event;
      final String value = e.getNewPath().toString();
      if (!ObjectUtil.equals(value, relativePath.getText()))
      {
        getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            relativePath.setText(value);
            status.setText(mapping.getStatus().toString());
          }
        });
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class UnmappedModelsContentProvider extends StructuredContentProvider<CDOTransfer>
  {
    public Object[] getElements(Object inputElement)
    {
      CDOTransfer transfer = getInput();
      Set<Resource> resources = transfer.getModelTransferContext().resolve();
      return resources.toArray(new Resource[resources.size()]);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class UnmappedModelsLabelProvider extends LabelProvider
  {
    @Override
    public String getText(Object element)
    {
      if (element instanceof Resource)
      {
        Resource resource = (Resource)element;
        return resource.getURI().toString();
      }

      return super.getText(element);
    }
  }
}
