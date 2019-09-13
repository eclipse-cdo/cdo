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

import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.security.internal.ui.util.ActionBarsHelper;
import org.eclipse.emf.cdo.security.internal.ui.util.ObjectExistsConverter.ObjectWritableConverter;
import org.eclipse.emf.cdo.security.internal.ui.util.SecurityUIUtil;
import org.eclipse.emf.cdo.security.internal.ui.util.TableLabelProvider;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.SelectionListenerAction;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandActionDelegate;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import java.util.Collections;

/**
 * Common framework for section parts of a form that present a list of
 * similar objects.  It presents buttons for adding and deleting
 * objects in the list.  The objects presented are the contents
 * (of the appropriate type) of some {@link Directory} in the security
 * realm model.
 *
 * @author Christian W. Damus (CEA LIST)
 */
@SuppressWarnings("unchecked")
public abstract class TableSection<T extends EObject> extends AbstractSectionPart<Directory>
{
  private final Class<T> elementType;

  private final EClass elementEClass;

  private TableViewer viewer;

  public TableSection(Class<T> elementType, EClass elementEClass, EditingDomain domain, AdapterFactory adapterFactory)
  {
    super(Directory.class, SecurityPackage.Literals.DIRECTORY, domain, adapterFactory);
    this.elementType = elementType;
    this.elementEClass = elementEClass;
  }

  @Override
  protected void createContents(Composite parent, FormToolkit toolkit)
  {
    parent.setLayout(new GridLayout());
    Table table = toolkit.createTable(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    viewer = new TableViewer(table);

    viewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
    viewer.setLabelProvider(new TableLabelProvider(getAdapterFactory()));
    addFilters(viewer);
    forwardSelection(viewer);

    getContext().bindValue(ViewerProperties.<StructuredViewer, Object> input().observe(viewer), getValue());

    configureDragSupport(viewer);
    configureDropSupport(viewer);
  }

  @Override
  public void setFocus()
  {
    if (viewer != null)
    {
      viewer.getControl().setFocus();
    }
    else
    {
      super.setFocus();
    }
  }

  protected void addFilters(TableViewer viewer)
  {
    SecurityUIUtil.applyTypeFilter(viewer, elementEClass);
    SecurityUIUtil.applySupportedElementFilter(viewer, elementEClass);
  }

  @Override
  public boolean setFormInput(Object input)
  {
    if (elementType.isInstance(input))
    {
      viewer.setSelection(new StructuredSelection(input), true);
      return true;
    }
    else if (input instanceof Directory && input == getDirectory(((Directory)input).getRealm()))
    {
      // It's my directory
      boolean result = super.setFormInput(input);
      checkForUnsupportedModelContent();
      return result;
    }
    else if (input instanceof Realm)
    {
      return setFormInput(getDirectory((Realm)input));
    }

    return false;
  }

  protected Directory getDirectory(Realm realm)
  {
    return SecurityUIUtil.getDirectory(realm, elementEClass);
  }

  @Override
  protected void createActionToolbar(Section section, FormToolkit toolkit)
  {
    ToolBarManager mgr = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = mgr.createControl(section);
    toolbar.setCursor(section.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

    mgr.add(createAddNewAction());

    IAction deleteAction = createDeleteAction();
    mgr.add(deleteAction);
    if (deleteAction instanceof ISelectionChangedListener)
    {
      ISelectionChangedListener scl = (ISelectionChangedListener)deleteAction;
      viewer.addSelectionChangedListener(scl);
      scl.selectionChanged(new SelectionChangedEvent(viewer, viewer.getSelection()));
    }

    mgr.update(true);
    section.setTextClient(toolbar);

    ActionBarsHelper actionBarsHelper = new ActionBarsHelper(getEditorActionBars());
    ActionBarsHelper globalAction = actionBarsHelper.addGlobalAction(ActionFactory.DELETE.getId(), deleteAction);
    globalAction.install(viewer);
  }

  protected IAction createAddNewAction()
  {
    Command dummy = createCreateNewCommand();
    ImageDescriptor image = null;

    if (dummy instanceof CommandActionDelegate)
    {
      image = ExtendedImageRegistry.getInstance().getImageDescriptor(((CommandActionDelegate)dummy).getImage());
    }

    IAction result = new Action(dummy.getLabel(), image)
    {
      @Override
      public void run()
      {
        final Command command = createCreateNewCommand();
        if (command.canExecute())
        {
          getEditingDomain().getCommandStack().execute(command);
          viewer.getControl().getDisplay().asyncExec(new Runnable()
          {
            public void run()
            {
              viewer.getControl().setFocus();
              viewer.setSelection(new StructuredSelection(command.getResult().toArray()));
            }
          });
        }
      }
    };

    getContext().bindValue(PojoProperties.value("enabled").observe(getContext().getValidationRealm(), result), //$NON-NLS-1$
        getValue(), null, ObjectWritableConverter.createUpdateValueStrategy());

    return result;
  }

  protected Command createCreateNewCommand()
  {
    Object input = viewer.getInput();
    Directory parent = input instanceof Directory ? (Directory)input : SecurityFactory.eINSTANCE.createDirectory();
    Object child = EcoreUtil.create(elementEClass);
    CommandParameter param = new CommandParameter(parent, SecurityPackage.Literals.DIRECTORY__ITEMS, child);
    return CreateChildCommand.create(getEditingDomain(), parent, param, Collections.singleton(parent));
  }

  protected IAction createDeleteAction()
  {
    Command dummy = createDeleteCommand(EcoreUtil.create(elementEClass));

    return new SelectionListenerAction<EObject>(dummy.getLabel(), SharedIcons.getDescriptor("etool16/delete.gif")) //$NON-NLS-1$
    {
      @Override
      public void run()
      {
        Command delete = createDeleteCommand(getSelectedObject());
        if (delete.canExecute())
        {
          getEditingDomain().getCommandStack().execute(delete);
        }
      }

      @Override
      protected boolean updateSelection(IStructuredSelection selection)
      {
        return super.updateSelection(selection) && SecurityUIUtil.isEditable(getInput());
      }

      @Override
      protected Class<EObject> getType()
      {
        return EObject.class;
      }
    };
  }

  protected Command createDeleteCommand(EObject toDelete)
  {
    return DeleteCommand.create(getEditingDomain(), toDelete);
  }

  private void forwardSelection(StructuredViewer viewer)
  {
    viewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        IManagedForm form = getManagedForm();
        if (form != null)
        {
          form.fireSelectionChanged(TableSection.this, event.getSelection());
        }
      }
    });
  }

  protected void configureDragSupport(TableViewer viewer)
  {
    UIUtil.addDragSupport(viewer);
  }

  protected void configureDropSupport(final TableViewer viewer)
  {
    viewer.addDropSupport(DND.DROP_LINK | DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] { LocalSelectionTransfer.getTransfer() }, new ViewerDropAdapter(viewer)
    {
      {
        // We don't want it to look like you can insert new elements, only drop onto existing elements
        setFeedbackEnabled(false);
      }

      @Override
      public boolean validateDrop(Object target, int operation, TransferData transferType)
      {
        boolean result = false;

        if (target instanceof EObject && LocalSelectionTransfer.getTransfer().isSupportedType(transferType))
        {
          EObject objectToDrop = getObjectToDrop(transferType);
          if (objectToDrop != null)
          {
            result = getDropReference((EObject)target, objectToDrop) != null;

            if (result && (getCurrentEvent().operations | DND.DROP_COPY) != 0)
            {
              overrideOperation(DND.DROP_COPY);
            }
          }
        }

        return result;
      }

      @Override
      public boolean performDrop(Object data)
      {
        IStructuredSelection selection = (IStructuredSelection)data;
        EObject objectToDrop = UIUtil.getElement(selection, EObject.class);
        EObject target = (EObject)getCurrentTarget();

        Command command = AddCommand.create(getEditingDomain(), target, getDropReference(target, objectToDrop), selection.toList());

        boolean result = execute(command);
        if (result)
        {
          viewer.getControl().setFocus();
          viewer.setSelection(new StructuredSelection(target));
        }

        return result;
      }

      private EObject getObjectToDrop(TransferData transferType)
      {
        return UIUtil.getElement(LocalSelectionTransfer.getTransfer().getSelection(), EObject.class);
      }
    });
  }

  protected EReference getDropReference(EObject target, EObject objectToDrop)
  {
    return null;
  }

  protected boolean execute(Command command)
  {
    boolean result = command.canExecute();
    if (result)
    {
      getEditingDomain().getCommandStack().execute(command);
    }

    return result;
  }

  protected void checkForUnsupportedModelContent()
  {
    if (getInput() == null)
    {
      getManagedForm().getMessageManager().addMessage(this, Messages.TableSection_2, null, IStatus.WARNING, viewer.getControl());
    }
    else
    {
      // Anything not matching filters?
      if (viewer.getTable().getItemCount() < getInput().getItems().size())
      {
        getManagedForm().getMessageManager().addMessage(this, Messages.TableSection_3, null, IStatus.WARNING, viewer.getControl());
      }
    }
  }
}
