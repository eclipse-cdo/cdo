/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import org.eclipse.emf.cdo.security.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.security.internal.ui.util.ObjectExistsConverter.ObjectWritableConverter;
import org.eclipse.emf.cdo.security.internal.ui.util.OneToManyBlock;
import org.eclipse.emf.cdo.security.internal.ui.util.OneToManyTableBlock;
import org.eclipse.emf.cdo.security.provider.SecurityEditPlugin;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * Common framework for the right-hand side object details pages of the
 * master/detail form.
 *
 * @author Christian W. Damus (CEA LIST)
 */
@SuppressWarnings("unchecked")
public abstract class AbstractDetailsPage<T extends EObject> extends AbstractSectionPart<T> implements IDetailsPage
{
  public AbstractDetailsPage(Class<T> elementType, EClass elementEClass, EditingDomain domain, AdapterFactory adapterFactory)
  {
    super(elementType, elementEClass, domain, adapterFactory);
  }

  @Override
  protected String getTitle()
  {
    return NLS.bind(Messages.AbstractDetailsPage_0, SecurityEditPlugin.INSTANCE.getString(String.format("_UI_%s_type", getInputEClass().getName()))); //$NON-NLS-1$
  }

  @Override
  public void createContents(Composite parent)
  {
    GridLayout layout = new GridLayout();
    parent.setLayout(layout);

    super.createContents(parent);
  }

  @Override
  protected void createContents(Composite parent, FormToolkit toolkit)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    parent.setLayout(layout);
  }

  protected void execute(Command command)
  {
    if (command.canExecute())
    {
      getEditingDomain().getCommandStack().execute(command);
    }
  }

  protected Text text(Composite parent, FormToolkit toolkit, String label, EAttribute attribute)
  {
    toolkit.createLabel(parent, label);
    Text result = toolkit.createText(createDecorationComposite(parent, toolkit, layoutData(parent, SWT.FILL, false, 1)), ""); //$NON-NLS-1$
    getContext().bindValue(observeText(result), EMFEditObservables.observeDetailValue(getRealm(), getEditingDomain(), getValue(), attribute));
    getContext().bindValue(WidgetProperties.enabled().observe(result), getValue(), null, ObjectWritableConverter.createUpdateValueStrategy(attribute));

    addRevertDecoration(result, attribute);
    return result;
  }

  private static GridData horzAlign(GridData gridData, int align, boolean grab, int span)
  {
    gridData.horizontalAlignment = align;
    gridData.grabExcessHorizontalSpace = grab;
    gridData.horizontalSpan = span;
    return gridData;
  }

  private static GridData vertAlign(GridData gridData, int align, boolean grab, int span)
  {
    gridData.verticalAlignment = align;
    gridData.grabExcessVerticalSpace = grab;
    gridData.verticalSpan = span;
    return gridData;
  }

  private static TableWrapData horzAlign(TableWrapData tableData, int align, boolean grab, int span)
  {
    int tableWrapAlign = 0;
    switch (align)
    {
    case SWT.LEFT:
      tableWrapAlign = TableWrapData.LEFT;
      break;
    case SWT.CENTER:
      tableWrapAlign = TableWrapData.CENTER;
      break;
    case SWT.RIGHT:
      tableWrapAlign = TableWrapData.RIGHT;
      break;
    case SWT.FILL:
      tableWrapAlign = TableWrapData.FILL;
      break;
    }

    tableData.align = tableWrapAlign;
    tableData.grabHorizontal = grab;
    tableData.colspan = span;
    return tableData;
  }

  private static TableWrapData vertAlign(TableWrapData tableData, int align, boolean grab, int span)
  {
    int tableWrapAlign = 0;
    switch (align)
    {
    case SWT.TOP:
      tableWrapAlign = TableWrapData.TOP;
      break;
    case SWT.CENTER:
      tableWrapAlign = TableWrapData.MIDDLE;
      break;
    case SWT.BOTTOM:
      tableWrapAlign = TableWrapData.BOTTOM;
      break;
    case SWT.FILL:
      tableWrapAlign = TableWrapData.FILL;
      break;
    }

    tableData.valign = tableWrapAlign;
    tableData.grabVertical = grab;
    tableData.rowspan = span;
    return tableData;
  }

  protected static Object layoutData(Composite parent, int horzAlign, boolean horzGrab, int horzSpan)
  {
    if (parent.getLayout() instanceof GridLayout)
    {
      return horzAlign(new GridData(), horzAlign, horzGrab, horzSpan);
    }

    return horzAlign(new TableWrapData(), horzAlign, horzGrab, horzSpan);
  }

  protected static Object layoutData(Composite parent, int horzAlign, boolean horzGrab, int horzSpan, int vertAlign, boolean vertGrab, int vertSpan)
  {
    if (parent.getLayout() instanceof GridLayout)
    {
      return vertAlign(horzAlign(new GridData(), horzAlign, horzGrab, horzSpan), vertAlign, vertGrab, vertSpan);
    }

    return vertAlign(horzAlign(new TableWrapData(), horzAlign, horzGrab, horzSpan), vertAlign, vertGrab, vertSpan);
  }

  protected static <T> T heightHint(T layoutData, int hint)
  {
    if (layoutData instanceof GridData)
    {
      ((GridData)layoutData).heightHint = hint;
    }
    else
    {
      ((TableWrapData)layoutData).heightHint = hint;
    }

    return layoutData;
  }

  protected Control space(Composite parent, FormToolkit toolkit)
  {
    Control result = toolkit.createComposite(parent);
    result.setLayoutData(heightHint(layoutData(parent, SWT.FILL, false, 2), 15));
    return result;
  }

  protected OneToManyBlock oneToMany(Composite parent, FormToolkit toolkit, String label, EReference reference)
  {
    return oneToMany(parent, toolkit, label, reference, reference.getEReferenceType());
  }

  protected OneToManyBlock oneToMany(Composite parent, FormToolkit toolkit, String label, EReference reference, EClass itemType)
  {
    return oneToMany(parent, toolkit, label, reference, itemType, null);
  }

  protected OneToManyBlock oneToMany(Composite parent, FormToolkit toolkit, String label, EReference reference, EClass itemType, IFilter itemFilter)
  {
    toolkit.createLabel(parent, label).setLayoutData(layoutData(parent, SWT.LEFT, false, 2));
    Composite blockParent = toolkit.createComposite(parent);
    blockParent.setLayoutData(layoutData(parent, SWT.FILL, true, 2, SWT.FILL, true, 1));

    OneToManyBlock result = new OneToManyBlock(getContext(), getEditingDomain(), getAdapterFactory(),
        new OneToManyBlock.OneToManyConfiguration(getManagedForm(), reference, itemType, itemFilter));

    result.setEditorActionBars(getEditorActionBars());
    result.setInput(getValue());
    result.createControl(blockParent, toolkit);
    return result;
  }

  protected OneToManyTableBlock table(Composite parent, FormToolkit toolkit, String label, OneToManyTableBlock.ITableConfiguration config)
  {
    toolkit.createLabel(parent, label).setLayoutData(layoutData(parent, SWT.LEFT, false, 2));
    Composite blockParent = toolkit.createComposite(parent);
    blockParent.setLayoutData(layoutData(parent, SWT.FILL, true, 2, SWT.FILL, true, 1));

    OneToManyTableBlock result = new OneToManyTableBlock(getContext(), getEditingDomain(), getAdapterFactory(), config);

    result.setEditorActionBars(getEditorActionBars());
    result.setInput(getValue());
    result.createControl(blockParent, toolkit);
    return result;
  }

  protected Button checkbox(Composite parent, FormToolkit toolkit, String label, EAttribute attribute)
  {
    Button result = toolkit.createButton(createDecorationComposite(parent, toolkit, layoutData(parent, SWT.LEFT, false, 2)), label, SWT.CHECK);
    getContext().bindValue(WidgetProperties.enabled().observe(result),
        EMFEditObservables.observeDetailValue(getRealm(), getEditingDomain(), getValue(), attribute));
    getContext().bindValue(WidgetProperties.enabled().observe(result), getValue(), null, ObjectWritableConverter.createUpdateValueStrategy(attribute));

    addRevertDecoration(result, attribute);
    return result;
  }

  protected Button button(Composite parent, FormToolkit toolkit, String label, SelectionListener selectionListener)
  {
    Button result = toolkit.createButton(parent, label, SWT.PUSH);
    result.setLayoutData(layoutData(parent, SWT.LEFT, false, 2));

    getContext().bindValue(WidgetProperties.enabled().observe(result), getValue(), null, ObjectWritableConverter.createUpdateValueStrategy());

    result.addSelectionListener(selectionListener);
    return result;
  }

  protected ComboViewer combo(Composite parent, FormToolkit toolkit, String label, EAttribute attribute)
  {
    toolkit.createLabel(parent, label);
    ComboViewer result = new ComboViewer(createDecorationComposite(parent, toolkit, layoutData(parent, SWT.LEFT, false, 1)), SWT.READ_ONLY | SWT.DROP_DOWN);
    result.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
    result.setContentProvider(new ArrayContentProvider());
    result.setInput(attribute.getEAttributeType().getInstanceClass().getEnumConstants());

    getContext().bindValue(ViewersObservables.observeSingleSelection(result),
        EMFEditObservables.observeDetailValue(getRealm(), getEditingDomain(), getValue(), attribute));
    getContext().bindValue(WidgetProperties.enabled().observe(result.getControl()), getValue(), null,
        ObjectWritableConverter.createUpdateValueStrategy(attribute));

    addRevertDecoration(result.getControl(), attribute);
    return result;
  }

  protected Composite createDecorationComposite(Composite parent, FormToolkit toolkit, Object layoutData)
  {
    Composite result = toolkit.createComposite(parent);
    result.setLayoutData(layoutData);

    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.marginRight = 18; // 16 for image and 2 for offset
    result.setLayout(layout);

    return result;
  }

  protected void addRevertDecoration(final Control control, final EStructuralFeature feature)
  {
    final ControlDecoration decoration = new ControlDecoration(control, SWT.RIGHT | SWT.CENTER);
    decoration.hide();
    decoration.setDescriptionText(Messages.AbstractDetailsPage_3);
    decoration.setImage(ExtendedImageRegistry.getInstance().getImage(URI.createPlatformPluginURI(OM.BUNDLE_ID + "/icons/full/elcl16/revert.gif", true))); //$NON-NLS-1$
    decoration.setMarginWidth(2);

    decoration.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        Command command = SetCommand.create(getEditingDomain(), getInput(), feature, SetCommand.UNSET_VALUE);
        if (command.canExecute())
        {
          getEditingDomain().getCommandStack().execute(command);
        }
      }
    });

    control.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

    // Show the decoration when the user hovers over the control and auto-hide after leaving the control
    MouseTrackListener showHideListener = new MouseTrackAdapter()
    {
      private Runnable hideRunnable;

      @Override
      public void mouseHover(MouseEvent e)
      {
        hideRunnable = null;
        if (control.isEnabled())
        {
          decoration.show();
        }
      }

      @Override
      public void mouseExit(MouseEvent e)
      {
        if (control.isEnabled())
        {
          hideRunnable = new Runnable()
          {

            public void run()
            {
              if (hideRunnable == this)
              {
                hideRunnable = null;
                decoration.hide();
              }
            }
          };

          control.getDisplay().timerExec(1000, hideRunnable);
        }
      }
    };

    control.addMouseTrackListener(showHideListener);
    control.getParent().addMouseTrackListener(showHideListener);
  }
}
