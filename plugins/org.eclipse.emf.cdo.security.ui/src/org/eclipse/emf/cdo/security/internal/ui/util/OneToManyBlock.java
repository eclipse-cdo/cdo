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
package org.eclipse.emf.cdo.security.internal.ui.util;

import static org.eclipse.emf.cdo.security.internal.ui.util.SecurityUIUtil.applyTypeFilter;
import static org.eclipse.emf.cdo.security.internal.ui.util.SecurityUIUtil.getViewerFilter;

import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.security.internal.ui.util.ObjectExistsConverter.ObjectWritableConverter;
import org.eclipse.emf.cdo.security.provider.SecurityEditPlugin;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.ui.actions.SelectionListenerAction;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.IdentityCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.celleditor.FeatureEditorDialog;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * An encapsulation of a block of controls in a form that edits a one-to-many
 * reference in the security model.
 *
 * @author Christian W. Damus (CEA LIST)
 */
@SuppressWarnings("unchecked")
public class OneToManyBlock
{
  private final EditingDomain domain;

  private final AdapterFactory adapterFactory;

  private final DataBindingContext context;

  private final IObservableValue<Object> input;

  private final IOneToManyConfiguration configuration;

  private final IFilter supportedContentFilter;

  private IObservableList<Object> value;

  private TableViewer viewer;

  private INewObjectConfigurator newObjectConfigurator;

  private IActionBars editorActionBars;

  public OneToManyBlock(IManagedForm managedForm, DataBindingContext context, EditingDomain domain, AdapterFactory adapterFactory, EReference reference)
  {
    this(context, domain, adapterFactory, new OneToManyConfiguration(managedForm, reference));
  }

  public OneToManyBlock(IManagedForm managedForm, DataBindingContext context, EditingDomain domain, AdapterFactory adapterFactory, EReference reference,
      EClass itemType)
  {
    this(context, domain, adapterFactory, new OneToManyConfiguration(managedForm, reference, itemType));
  }

  public OneToManyBlock(DataBindingContext context, EditingDomain domain, AdapterFactory adapterFactory, IOneToManyConfiguration blockConfig)
  {
    this.context = context;
    this.domain = domain;
    this.adapterFactory = adapterFactory;
    configuration = blockConfig;
    input = new WritableValue<Object>(context.getValidationRealm());
    supportedContentFilter = SecurityUIUtil.getSupportedElementFilter(configuration.getItemType());
  }

  protected IOneToManyConfiguration getConfiguration()
  {
    return configuration;
  }

  protected boolean isTable()
  {
    return false;
  }

  public void setEditorActionBars(IActionBars actionBars)
  {
    editorActionBars = actionBars;
  }

  public void createControl(Composite parent, FormToolkit toolkit)
  {
    final EReference reference = getConfiguration().getModelReference();
    final EClass itemType = getConfiguration().getItemType();

    parent.setLayout(new GridLayout(2, false));

    Composite tableParent;
    TableColumnLayout tableLayout = null;
    if (isTable())
    {
      tableParent = toolkit.createComposite(parent);
      tableLayout = new TableColumnLayout();
      tableParent.setLayout(tableLayout);
    }
    else
    {
      tableParent = parent;
    }

    Table table = toolkit.createTable(tableParent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
    viewer = new TableViewer(table);

    if (isTable())
    {
      tableParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      configureColumns(viewer, tableLayout);
    }
    else
    {
      table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      AdapterFactoryLabelProvider labels = new TableLabelProvider(adapterFactory);
      labels.setFireLabelUpdateNotifications(true); // Needed to support the data-binding input
      viewer.setLabelProvider(labels);
    }

    viewer.setContentProvider(new ObservableListContentProvider<Object>());
    SecurityUIUtil.applySupportedElementFilter(viewer, itemType);
    if (itemType != reference.getEReferenceType())
    {
      applyTypeFilter(viewer, itemType);
    }

    if (getConfiguration().getItemFilter() != null)
    {
      viewer.addFilter(getViewerFilter(getConfiguration().getItemFilter()));
    }

    viewer.setInput(value);
    hookUnsupportedModelContentValidation(value);

    if (!reference.isContainment())
    {
      configureDropSupport(viewer);
    }

    context.bindValue(WidgetProperties.enabled().observe(viewer.getControl()), input, null, ObjectWritableConverter.createUpdateValueStrategy());

    Composite buttons = toolkit.createComposite(parent);
    FillLayout fill = new FillLayout(SWT.VERTICAL);
    fill.spacing = 5;
    buttons.setLayout(fill);
    buttons.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

    Button newButton = null;
    Button addButton = null;
    Button removeButton = null;

    newButton = toolkit.createButton(buttons, Messages.OneToManyBlock_0, SWT.PUSH);
    if (!reference.isContainment())
    {
      addButton = toolkit.createButton(buttons, Messages.OneToManyBlock_1, SWT.PUSH);
    }

    removeButton = toolkit.createButton(buttons, Messages.OneToManyBlock_2, SWT.PUSH);

    final IObservableValue<?> selection = ViewerProperties.singleSelection().observe(viewer);

    context.bindValue(WidgetProperties.enabled().observe(newButton), input, null, ObjectWritableConverter.createUpdateValueStrategy());
    if (addButton != null)
    {
      context.bindValue(WidgetProperties.enabled().observe(addButton), input, null, ObjectWritableConverter.createUpdateValueStrategy());
    }

    context.bindValue(WidgetProperties.enabled().observe(removeButton), selection, null, ObjectWritableConverter.createUpdateValueStrategy());

    newButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        Realm realm = ((SecurityItem)input.getValue()).getRealm();

        Object owner;
        if (reference.isContainment())
        {
          owner = input.getValue();
        }
        else
        {
          owner = SecurityUIUtil.getDirectory(realm, itemType);
        }

        if (owner != null)
        {
          // Create a new object in the appropriate owner and add it to the
          // reference list if that's not the containment
          Object child = EcoreUtil.create(itemType);

          CommandParameter param;
          Command addToReference;

          if (reference.isContainment())
          {
            param = new CommandParameter(owner, reference, child);
            addToReference = IdentityCommand.INSTANCE;
          }
          else
          {
            param = new CommandParameter(owner, SecurityPackage.Literals.DIRECTORY__ITEMS, child);
            addToReference = AddCommand.create(domain, input.getValue(), reference, Collections.singleton(child));
          }

          Command command = CreateChildCommand.create(domain, owner, param, Collections.singleton(owner));
          command = command.chain(addToReference);

          if (getNewObjectConfigurator() != null)
          {
            command = command.chain(getNewObjectConfigurator().createConfigureCommand(child));
          }

          if (execute(command))
          {
            viewer.setSelection(new StructuredSelection(child));
            viewer.getControl().setFocus();
            viewer.refresh(child);
          }
        }
      }
    });

    if (addButton != null)
    {
      addButton.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          Realm realm = ((SecurityItem)input.getValue()).getRealm();
          Directory directory = SecurityUIUtil.getDirectory(realm, itemType);
          if (directory != null)
          {
            // Get the available items not already in our input's reference list
            List<?> available = new ArrayList<Object>(EcoreUtil.getObjectsByType(directory.getItems(), itemType));
            available.removeAll(value);
            SecurityUIUtil.applySupportedElementFilter(available, itemType);

            String label = NLS.bind(Messages.OneToManyBlock_3,
                SecurityEditPlugin.INSTANCE.getString(String.format("_UI_%s_%s_feature", reference.getEContainingClass().getName(), reference.getName()))); //$NON-NLS-1$

            FeatureEditorDialog dlg = new FeatureEditorDialog(viewer.getControl().getShell(), new TableLabelProvider(adapterFactory), input.getValue(),
                reference.getEContainingClass(), Collections.EMPTY_LIST, label, available, false, true, true);

            if (dlg.open() == Window.OK && !dlg.getResult().isEmpty())
            {
              Command command = AddCommand.create(domain, input.getValue(), reference, dlg.getResult());
              if (execute(command))
              {
                viewer.setSelection(new StructuredSelection(dlg.getResult()));
                viewer.getControl().setFocus();
              }
            }
          }
        }
      });
    }

    final SelectionListenerAction<EObject> removeAction = new SelectionListenerAction<EObject>(Messages.OneToManyBlock_2,
        SharedIcons.getDescriptor("etool16/delete.gif")) //$NON-NLS-1$
    {
      @Override
      public void run()
      {
        Object selected = selection.getValue();
        if (selected != null)
        {
          Command command;

          if (reference.isContainment())
          {
            command = DeleteCommand.create(domain, selection.getValue());
          }
          else
          {
            command = RemoveCommand.create(domain, input.getValue(), reference, selection.getValue());
          }

          execute(command);
        }
      }

      @Override
      protected boolean updateSelection(IStructuredSelection selection)
      {
        return super.updateSelection(selection) && SecurityUIUtil.isEditable(input.getValue());
      }

      @Override
      protected Class<EObject> getType()
      {
        return EObject.class;
      }
    };

    removeButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        if (removeAction.isEnabled())
        {
          removeAction.run();
        }
      }
    });

    viewer.addSelectionChangedListener(removeAction);

    new ActionBarsHelper(editorActionBars).addGlobalAction(ActionFactory.DELETE.getId(), removeAction).install(viewer);
  }

  public void setInput(IObservableValue<Object> input)
  {
    if (input != null)
    {
      Observables.pipe(input, this.input);
    }

    if (value != null)
    {
      if (viewer != null)
      {
        viewer.setInput(null);
      }

      value.dispose();
    }

    value = EMFEditObservables.observeDetailList(context.getValidationRealm(), domain, input, getConfiguration().getModelReference());

    if (viewer != null)
    {
      viewer.setInput(value);
      hookUnsupportedModelContentValidation(value);
    }
  }

  protected boolean execute(Command command)
  {
    boolean result = command.canExecute();

    if (result)
    {
      domain.getCommandStack().execute(command);
    }

    return result;
  }

  public void setNewObjectConfigurator(INewObjectConfigurator newObjectConfigurator)
  {
    this.newObjectConfigurator = newObjectConfigurator;
  }

  public INewObjectConfigurator getNewObjectConfigurator()
  {
    return newObjectConfigurator;
  }

  protected void configureColumns(TableViewer viewer, TableColumnLayout layout)
  {
    // Pass
  }

  private boolean canPresent(Object object)
  {
    IOneToManyConfiguration config = getConfiguration();
    boolean result = config.getItemType().isInstance(object);

    if (result && config.getItemFilter() != null)
    {
      result = config.getItemFilter().select(object);
    }

    if (result)
    {
      // Last check: cannot drop something from a different editing domain
      result = AdapterFactoryEditingDomain.getEditingDomainFor(object) == domain;
    }

    return result;
  }

  private boolean canPresentAll(ISelection selection)
  {
    boolean result = selection != null && !selection.isEmpty();

    if (result && selection instanceof IStructuredSelection)
    {
      for (Iterator<?> iter = ((IStructuredSelection)selection).iterator(); result && iter.hasNext();)
      {
        result = canPresent(iter.next());
      }
    }

    return result;
  }

  protected void configureDropSupport(final TableViewer viewer)
  {
    viewer.addDropSupport(DND.DROP_LINK | DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] { LocalSelectionTransfer.getTransfer() }, new DropTargetAdapter()
    {
      @Override
      public void dragEnter(DropTargetEvent event)
      {
        if (!canDrop(event))
        {
          // Reject this drop
          event.detail = DND.DROP_NONE;
        }
        else if ((event.operations | DND.DROP_COPY) != 0)
        {
          event.detail = DND.DROP_COPY;
        }
      }

      private boolean canDrop(DropTargetEvent event)
      {
        boolean result = false;

        if (LocalSelectionTransfer.getTransfer().isSupportedType(event.currentDataType))
        {
          result = canPresentAll(LocalSelectionTransfer.getTransfer().getSelection());
        }

        return result;
      }

      @Override
      public void dropAccept(DropTargetEvent event)
      {
        if (!canDrop(event))
        {
          // Reject this drop
          event.detail = DND.DROP_NONE;
        }
        else if ((event.operations | DND.DROP_COPY) != 0)
        {
          event.detail = DND.DROP_COPY;
        }
      }

      @Override
      public void drop(DropTargetEvent event)
      {
        if (canDrop(event))
        {
          IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection();
          Command command = AddCommand.create(domain, input.getValue(), getConfiguration().getModelReference(), selection.toList());
          if (execute(command))
          {
            viewer.setSelection(selection);
            viewer.getControl().setFocus();
          }
        }
      }
    });
  }

  protected void hookUnsupportedModelContentValidation(IObservableList<Object> observableList)
  {
    // No need to hook a listener if there is no supported-content filter to check
    if (observableList != null && supportedContentFilter != null)
    {
      observableList.addChangeListener(new IChangeListener()
      {
        public void handleChange(ChangeEvent event)
        {
          checkUnsupportedModelContent((IObservableList<Object>)event.getObservable());
        }
      });

      // Initialize the validation state
      checkUnsupportedModelContent(observableList);
    }
  }

  protected void checkUnsupportedModelContent(IObservableList<Object> observableList)
  {
    // Anything not matching the supported-content filter?
    for (Object element : observableList)
    {
      if (!supportedContentFilter.select(element))
      {
        configuration.getManagedForm().getMessageManager().addMessage(this, Messages.TableSection_3, null, IStatus.WARNING, viewer.getControl());
        return;
      }
    }

    configuration.getManagedForm().getMessageManager().removeMessage(this, viewer.getControl());
  }

  /**
   * Specification of the configuration of a one-to-many block's contents.
   *
   * @author Christian W. Damus (CEA LIST)
   */
  public static interface IOneToManyConfiguration
  {
    public IManagedForm getManagedForm();

    public EReference getModelReference();

    public EClass getItemType();

    public IFilter getItemFilter();
  }

  /**
   * Default one-to-many block configuration implementation.
   *
   * @author Christian W. Damus (CEA LIST)
   */
  public static class OneToManyConfiguration implements IOneToManyConfiguration
  {
    private final IManagedForm managedForm;

    private final EReference reference;

    private final EClass itemType;

    private final IFilter filter;

    public OneToManyConfiguration(IManagedForm managedForm, EReference reference)
    {
      this(managedForm, reference, reference.getEReferenceType(), SecurityUIUtil.getSupportedElementFilter(reference.getEReferenceType()));
    }

    public OneToManyConfiguration(IManagedForm managedForm, EReference reference, EClass itemType)
    {
      this(managedForm, reference, itemType, SecurityUIUtil.getSupportedElementFilter(itemType));
    }

    public OneToManyConfiguration(IManagedForm managedForm, EReference reference, IFilter filter)
    {
      this(managedForm, reference, reference.getEReferenceType(), filter);
    }

    public OneToManyConfiguration(IManagedForm managedForm, EReference reference, EClass itemType, IFilter filter)
    {
      this.managedForm = managedForm;
      this.reference = reference;
      this.itemType = itemType;
      this.filter = filter;
    }

    public IManagedForm getManagedForm()
    {
      return managedForm;
    }

    public EReference getModelReference()
    {
      return reference;
    }

    public EClass getItemType()
    {
      return itemType;
    }

    public IFilter getItemFilter()
    {
      return filter;
    }
  }
}
