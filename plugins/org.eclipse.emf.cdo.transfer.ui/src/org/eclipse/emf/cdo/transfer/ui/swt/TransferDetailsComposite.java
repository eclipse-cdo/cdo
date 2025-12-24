/*
 * Copyright (c) 2012, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer.ui.swt;

import org.eclipse.emf.cdo.transfer.CDOTransfer;
import org.eclipse.emf.cdo.transfer.CDOTransfer.ModelTransferContext;
import org.eclipse.emf.cdo.transfer.CDOTransfer.ModelTransferResolution;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferMapping;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;
import org.eclipse.emf.cdo.transfer.CDOTransferType;
import org.eclipse.emf.cdo.transfer.ui.TransferDialog;
import org.eclipse.emf.cdo.transfer.ui.TransferTypeContentProvider;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.StructuredContentProvider;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link Composite composite} that lays out {@link Control controls} for the details of a single {@link CDOTransferMapping}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class TransferDetailsComposite extends Composite implements IListener
{
  private static final String UP = "..";

  private static final Path UP_PATH = new Path(UP);

  private CDOTransfer transfer;

  private CDOTransferType[] transferTypes;

  private CDOTransferMapping mapping;

  private boolean handlingMappingEvent;

  private Text sourcePath;

  private Text targetPath;

  private ComboViewer transferType;

  private Text status;

  private Text relativePath;

  private Combo resolution;

  private ListViewer unmappedModels;

  private Button mapSource;

  private Button replaceTarget;

  private Button keepAsIs;

  public TransferDetailsComposite(Composite parent, int style, final CDOTransfer transfer)
  {
    super(parent, style);
    this.transfer = transfer;
    this.transfer.addListener(this);
    initTransferTypes(transfer);

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

    transferType = new ComboViewer(this, SWT.NONE);
    transferType.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    transferType.setContentProvider(new TransferTypeContentProvider());
    transferType.setLabelProvider(new LabelProvider());
    transferType.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        if (mapping != null)
        {
          IStructuredSelection selection = (IStructuredSelection)transferType.getSelection();
          CDOTransferType type = (CDOTransferType)selection.getFirstElement();
          mapping.setTransferType(type);
        }
      }
    });

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
      @Override
      public void modifyText(ModifyEvent e)
      {
        if (mapping != null && !handlingMappingEvent)
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
    list.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
    list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    unmappedModels.setContentProvider(new UnmappedModelsContentProvider());
    unmappedModels.setLabelProvider(new UnmappedModelsLabelProvider(transfer));
    unmappedModels.setInput(transfer);
    unmappedModels.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)unmappedModels.getSelection();
        Resource resource = (Resource)selection.getFirstElement();
        updateTransformationButtons(resource);
      }
    });

    GridLayout transformationButtonsPaneLayout = new GridLayout(1, false);
    transformationButtonsPaneLayout.marginWidth = 0;
    transformationButtonsPaneLayout.marginHeight = 0;

    Composite transformationButtonsPane = new Composite(this, SWT.NONE);
    transformationButtonsPane.setLayout(transformationButtonsPaneLayout);
    transformationButtonsPane.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));

    mapSource = new Button(transformationButtonsPane, SWT.NONE);
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
        TransferDialog.initializeTransfer(transfer, Collections.singleton(element));
      }
    });

    replaceTarget = new Button(transformationButtonsPane, SWT.NONE);
    replaceTarget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    replaceTarget.setBounds(0, 0, 75, 25);
    replaceTarget.setText("Replace With Target");

    keepAsIs = new Button(transformationButtonsPane, SWT.NONE);
    keepAsIs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    keepAsIs.setBounds(0, 0, 75, 25);
    keepAsIs.setText("Keep As Is");
    keepAsIs.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        IStructuredSelection selection = (IStructuredSelection)unmappedModels.getSelection();
        Resource resource = (Resource)selection.getFirstElement();

        URI uri = resource.getURI();
        transfer.getModelTransferContext().setResolution(uri, new ModelTransferResolution()
        {
          // XXX
        });
      }
    });

    Button refresh = new Button(transformationButtonsPane, SWT.NONE);
    refresh.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    refresh.setBounds(0, 0, 75, 25);
    refresh.setText("Refresh");
    refresh.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        unmappedModels.refresh();
      }
    });

    updateTransformationButtons(null);
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
        targetPath.setText(mapping.getFullPath().makeAbsolute().toString());
        status.setText(mapping.getStatus().toString());
        relativePath.setText(mapping.getRelativePath().toString());

        CDOTransferType type = mapping.getTransferType();
        transferType.setInput(type != CDOTransferType.FOLDER ? transferTypes : CDOTransferType.FOLDER);
        transferType.setSelection(new StructuredSelection(type));
        transferType.getCombo().setEnabled(type != CDOTransferType.FOLDER);
      }
      else
      {
        sourcePath.setText(StringUtil.EMPTY);
        targetPath.setText(StringUtil.EMPTY);
        status.setText(StringUtil.EMPTY);
        relativePath.setText(StringUtil.EMPTY);

        transferType.setInput(TransferTypeContentProvider.NO_TANSFER_TYPES);
        transferType.setSelection(StructuredSelection.EMPTY);
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

  public ComboViewer getTransferType()
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

  @Override
  public void notifyEvent(final IEvent event)
  {
    if (isDisposed())
    {
      return;
    }

    try
    {
      getDisplay().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          if (!isDisposed())
          {
            try
            {
              if (event instanceof CDOTransfer.MappingEvent)
              {
                CDOTransfer.MappingEvent e = (CDOTransfer.MappingEvent)event;
                if (ObjectUtil.equals(e.getMapping(), mapping))
                {
                  notifyMappingEvent(e);
                }
              }
              else if (event instanceof CDOTransfer.UnmappedModelsEvent)
              {
                unmappedModels.refresh();
              }
            }
            catch (SWTException ex)
            {
              // Ignoredd
            }
          }
        }
      });
    }
    catch (SWTException ex)
    {
      // Ignoredd
    }
  }

  protected void notifyMappingEvent(CDOTransfer.MappingEvent event)
  {
    if (event instanceof CDOTransfer.TransferTypeChangedEvent)
    {
      CDOTransfer.TransferTypeChangedEvent e = (CDOTransfer.TransferTypeChangedEvent)event;
      final CDOTransferType newType = e.getNewType();
      Object currentType = ((IStructuredSelection)transferType.getSelection()).getFirstElement();
      if (currentType != newType)
      {
        transferType.setSelection(new StructuredSelection(newType));
      }

      unmappedModels.refresh();
    }
    else if (event instanceof CDOTransfer.RelativePathChangedEvent)
    {
      CDOTransfer.RelativePathChangedEvent e = (CDOTransfer.RelativePathChangedEvent)event;
      final String value = e.getNewPath().toString();
      if (!ObjectUtil.equals(value, relativePath.getText()))
      {
        try
        {
          handlingMappingEvent = true;
          relativePath.setText(value);
          status.setText(mapping.getStatus().toString());
        }
        finally
        {
          handlingMappingEvent = false;
        }
      }

      targetPath.setText(mapping.getFullPath().makeAbsolute().toString());
    }
  }

  protected void initTransferTypes(final CDOTransfer transfer)
  {
    Set<CDOTransferType> set = new HashSet<>(CDOTransferType.REGISTRY.values());
    set.remove(CDOTransferType.FOLDER);

    transferTypes = set.toArray(new CDOTransferType[set.size()]);
    Arrays.sort(transferTypes);
  }

  protected void updateTransformationButtons(Resource resource)
  {
    if (resource == null)
    {
      mapSource.setEnabled(false);
      replaceTarget.setEnabled(false);
      keepAsIs.setEnabled(false);
      return;
    }

    URI uri = resource.getURI();
    CDOTransferSystem sourceSystem = transfer.getSourceSystem();
    CDOTransferElement sourceElement = sourceSystem.getElement(uri);
    mapSource.setEnabled(sourceElement != null);

    ModelTransferContext context = transfer.getModelTransferContext();
    ModelTransferResolution resolution = context.getResolution(uri);
    keepAsIs.setEnabled(resolution == null); // TODO Test type of resolution
  }

  /**
   * A reusable {@link StructuredContentProvider content provider} for the {@link ModelTransferContext#getUnmappedModels() unmapped models} of a {@link CDOTransfer transfer}.
   * <p>
   * The {@link StructuredViewer#setInput(Object) input} must be an instance of {@link CDOTransfer}.
   *
   * @author Eike Stepper
   */
  public static class UnmappedModelsContentProvider extends StructuredContentProvider<CDOTransfer>
  {
    @Override
    public Object[] getElements(Object inputElement)
    {
      CDOTransfer transfer = getInput();
      ModelTransferContext context = transfer.getModelTransferContext();
      Set<Resource> resources = context.getUnmappedModels();
      return resources.toArray(new Resource[resources.size()]);
    }
  }

  /**
   * A reusable {@link LabelProvider label provider} for the {@link ModelTransferContext#getUnmappedModels() unmapped models} of a {@link CDOTransfer transfer}.
   *
   * @author Eike Stepper
   */
  public static class UnmappedModelsLabelProvider extends LabelProvider implements IColorProvider
  {
    @Deprecated
    public static final Color GRAY = null;

    @Deprecated
    public static final Color RED = null;

    private CDOTransfer transfer;

    public UnmappedModelsLabelProvider(CDOTransfer transfer)
    {
      this.transfer = transfer;
    }

    public CDOTransfer getTransfer()
    {
      return transfer;
    }

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

    @Override
    public Color getForeground(Object element)
    {
      if (element instanceof Resource)
      {
        Resource resource = (Resource)element;
        URI uri = resource.getURI();

        ModelTransferContext context = transfer.getModelTransferContext();
        ModelTransferResolution resolution = context.getResolution(uri);
        if (resolution != null) // TODO Test type of resolution
        {
          return UIUtil.grayColor();
        }
      }

      return UIUtil.redColor();
    }

    @Override
    public Color getBackground(Object element)
    {
      return null;
    }
  }
}
