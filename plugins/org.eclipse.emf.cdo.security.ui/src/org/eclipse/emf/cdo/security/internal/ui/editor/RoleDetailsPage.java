/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import static org.eclipse.emf.cdo.security.internal.ui.util.SecurityUIUtil.getTypeFilter;
import static org.eclipse.emf.cdo.security.internal.ui.util.SecurityUIUtil.getViewerFilter;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.ResourceFilter;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.internal.ui.dialogs.FilterTreeSelectionDialog;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.security.internal.ui.util.EditableDialogCellEditor;
import org.eclipse.emf.cdo.security.internal.ui.util.INewObjectConfigurator;
import org.eclipse.emf.cdo.security.internal.ui.util.OneToManyTableBlock;
import org.eclipse.emf.cdo.security.provider.SecurityEditPlugin;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.util.Collections;

/**
 * The details page for {@link Role} master selections.  Includes a table
 * that provides in-line editing of resource-based permissions.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class RoleDetailsPage extends AbstractDetailsPage<Role>
{
  public RoleDetailsPage(EditingDomain domain, AdapterFactory adapterFactory)
  {
    super(Role.class, SecurityPackage.Literals.ROLE, domain, adapterFactory);
  }

  @Override
  protected void createContents(Composite parent, FormToolkit toolkit)
  {
    super.createContents(parent, toolkit);

    text(parent, toolkit, Messages.RoleDetailsPage_0, SecurityPackage.Literals.ROLE__ID);

    space(parent, toolkit);

    OneToManyTableBlock perms = table(parent, toolkit, Messages.RoleDetailsPage_1, createTableConfiguration());
    perms.setNewObjectConfigurator(createNewPermissionConfigurator());

    space(parent, toolkit);

    oneToMany(parent, toolkit, Messages.RoleDetailsPage_2, SecurityPackage.Literals.ROLE__ASSIGNEES, SecurityPackage.Literals.GROUP);

    oneToMany(parent, toolkit, Messages.RoleDetailsPage_3, SecurityPackage.Literals.ROLE__ASSIGNEES, SecurityPackage.Literals.USER);
  }

  protected INewObjectConfigurator createNewPermissionConfigurator()
  {
    return new INewObjectConfigurator()
    {

      @Override
      public Command createConfigureCommand(Object newObject)
      {
        ResourceFilter filter = SecurityFactory.eINSTANCE.createResourceFilter("/home/${user}", PatternStyle.TREE); //$NON-NLS-1$
        Command result = CreateChildCommand.create(getEditingDomain(), newObject,
            new CommandParameter(newObject, SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, filter), Collections.singleton(newObject));
        result = result.chain(SetCommand.create(getEditingDomain(), newObject, SecurityPackage.Literals.PERMISSION__ACCESS, Access.WRITE));
        return result;
      }
    };
  }

  protected OneToManyTableBlock.ITableConfiguration createTableConfiguration()
  {
    return new OneToManyTableBlock.TableConfiguration(getManagedForm(), SecurityPackage.Literals.ROLE__PERMISSIONS, SecurityPackage.Literals.FILTER_PERMISSION)
    {
      private static final int COL_ACCESS = 0;

      private static final int COL_PATTERN_STYLE = 1;

      private static final int COL_PATH = 2;

      private final String[] columnTitles = { Messages.RoleDetailsPage_5, Messages.RoleDetailsPage_6, Messages.RoleDetailsPage_7 };

      private final CellEditor[] cellEditors = new CellEditor[3];

      @Override
      public String[] getColumnTitles()
      {
        return columnTitles;
      }

      @Override
      public int getColumnWeight(int index)
      {
        switch (index)
        {
        case COL_ACCESS:
          return 15;
        case COL_PATTERN_STYLE:
          return 15;
        case COL_PATH:
          return 70;
        default:
          throw new IllegalArgumentException("index"); //$NON-NLS-1$
        }
      }

      @Override
      public int getColumnMinimumSize(int index)
      {
        switch (index)
        {
        case COL_ACCESS:
          return 30;
        case COL_PATTERN_STYLE:
          return 30;
        case COL_PATH:
          return 120;
        default:
          throw new IllegalArgumentException("index"); //$NON-NLS-1$
        }
      }

      @Override
      public boolean isColumnResizable(int index)
      {
        switch (index)
        {
        case COL_ACCESS:
          return false;
        case COL_PATTERN_STYLE:
          return false;
        case COL_PATH:
          return true;
        default:
          throw new IllegalArgumentException("index"); //$NON-NLS-1$
        }
      }

      @Override
      public CellLabelProvider getLabelProvider(TableViewer viewer, final int columnIndex)
      {
        return new ColumnLabelProvider()
        {
          @Override
          public String getText(Object element)
          {
            final FilterPermission perm = (FilterPermission)element;
            final ResourceFilter filter = perm.getFilters().isEmpty() ? null : (ResourceFilter)perm.getFilters().get(0);

            switch (columnIndex)
            {
            case COL_ACCESS:
              return SecurityEditPlugin.INSTANCE.getString(String.format("_UI_Access_%s_literal", perm.getAccess())); //$NON-NLS-1$
            case COL_PATTERN_STYLE:
            {
              String result = "=="; //$NON-NLS-1$
              if (filter != null && filter.getPatternStyle() != null)
              {
                switch (filter.getPatternStyle())
                {
                case EXACT:
                  break;
                case TREE:
                  result = ">="; //$NON-NLS-1$
                  break;
                default:
                  result = "~="; //$NON-NLS-1$
                  break;
                }
              }
              return result;
            }
            case COL_PATH:
              return filter == null ? "" : filter.getPath(); //$NON-NLS-1$
            default:
              throw new IllegalArgumentException("columnIndex"); //$NON-NLS-1$
            }
          }
        };
      }

      @Override
      public boolean canEdit(TableViewer viewer, Object element, int columnIndex)
      {
        return true;
      }

      @Override
      public void setValue(TableViewer viewer, Object element, int columnIndex, Object value)
      {
        final FilterPermission perm = (FilterPermission)element;
        final ResourceFilter filter = (ResourceFilter)perm.getFilters().get(0);

        switch (columnIndex)
        {
        case COL_ACCESS:
          if (perm.getAccess() != value)
          {
            execute(SetCommand.create(getEditingDomain(), perm, SecurityPackage.Literals.PERMISSION__ACCESS, value));
            viewer.refresh(element);
          }
          break;
        case COL_PATTERN_STYLE:
          if (filter.getPatternStyle() != value)
          {
            execute(SetCommand.create(getEditingDomain(), filter, SecurityPackage.Literals.RESOURCE_FILTER__PATTERN_STYLE, value));
            viewer.refresh(element);
          }
          break;
        case COL_PATH:
          if (!ObjectUtil.equals(filter.getPath(), value))
          {
            execute(SetCommand.create(getEditingDomain(), filter, SecurityPackage.Literals.RESOURCE_FILTER__PATH, value));
            viewer.refresh(element);
          }
          break;
        default:
          throw new IllegalArgumentException("columnIndex"); //$NON-NLS-1$
        }
      }

      @Override
      public Object getValue(TableViewer viewer, Object element, int columnIndex)
      {
        final FilterPermission perm = (FilterPermission)element;
        final ResourceFilter filter = (ResourceFilter)perm.getFilters().get(0);

        switch (columnIndex)
        {
        case COL_ACCESS:
          return perm.getAccess();
        case COL_PATTERN_STYLE:
          return filter.getPatternStyle();
        case COL_PATH:
          return filter.getPath();
        default:
          throw new IllegalArgumentException("columnIndex"); //$NON-NLS-1$
        }
      }

      @Override
      public CellEditor getCellEditor(final TableViewer viewer, int columnIndex)
      {
        CellEditor result = cellEditors[columnIndex];
        if (result == null)
        {
          result = createCellEditor(viewer, columnIndex);
          cellEditors[columnIndex] = result;
        }

        return result;
      }

      protected CellEditor createCellEditor(TableViewer viewer, int columnIndex)
      {
        Composite parent = (Composite)viewer.getControl();

        switch (columnIndex)
        {
        case COL_ACCESS:
        {
          ComboBoxViewerCellEditor result = new ComboBoxViewerCellEditor(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
          result.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
          result.setContentProvider(new ArrayContentProvider());
          result.setInput(Access.VALUES);
          return result;
        }
        case COL_PATTERN_STYLE:
        {
          ComboBoxViewerCellEditor result = new ComboBoxViewerCellEditor(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
          result.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
          result.setContentProvider(new ArrayContentProvider());
          result.setInput(PatternStyle.VALUES);
          return result;
        }
        case COL_PATH:
          return new EditableDialogCellEditor(parent)
          {

            @Override
            protected Object openDialogBox(Control cellEditorWindow)
            {
              final CDOView view = getInput().cdoView();
              @SuppressWarnings({ "rawtypes", "unchecked" })
              CDOItemProvider provider = new CDOItemProvider(null)
              {
                private boolean connected;

                {
                  // Connect the input now, because the dialog will try to access the content provider before it has
                  // been set into the tree viewer
                  connectInput((IContainer)view);
                }

                @Override
                protected void connectInput(IContainer<Object> input)
                {
                  if (!connected)
                  {
                    super.connectInput(input);
                    connected = true;
                  }
                }

                @Override
                protected void disconnectInput(IContainer<Object> input)
                {
                  if (connected)
                  {
                    connected = false;
                    super.disconnectInput(input);
                  }
                }
              };

              FilterTreeSelectionDialog dlg = new FilterTreeSelectionDialog(cellEditorWindow.getShell(), provider, provider);

              dlg.setAllowMultiple(false);
              dlg.setMessage(Messages.RoleDetailsPage_4);
              dlg.setTitle(Messages.RoleDetailsPage_8);
              dlg.setDoubleClickSelects(true);
              dlg.addFilter(getViewerFilter(getTypeFilter(EresourcePackage.Literals.CDO_RESOURCE_NODE)));
              dlg.setBlockOnOpen(true);

              String current = (String)getValue();

              dlg.setInput(view);
              if (current != null && view.hasResource(current))
              {
                dlg.setInitialSelection(view.getResourceNode(current));
              }

              String result = null;

              if (dlg.open() == Window.OK)
              {
                CDOResourceNode node = (CDOResourceNode)dlg.getFirstResult();
                if (node != null)
                {
                  result = node.getPath();
                }
              }

              return result;
            }
          };
        default:
          throw new IllegalArgumentException("columnIndex"); //$NON-NLS-1$
        }
      }
    };
  }
}
