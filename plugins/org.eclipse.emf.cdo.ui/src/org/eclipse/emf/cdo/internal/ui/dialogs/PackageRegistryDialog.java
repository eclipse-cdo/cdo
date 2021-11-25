/*
 * Copyright (c) 2009-2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterFilesystemPackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterGeneratedPackagesAction;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.CustomizeableComposite;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPage;

import javax.swing.text.AbstractDocument.Content;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class PackageRegistryDialog extends TitleAreaDialog
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.ui.packageRegistryDialogCustomizers";

  private static final String TITLE = Messages.getString("PackageRegistryDialog.0"); //$NON-NLS-1$

  private IWorkbenchPage page;

  private CDOSession session;

  private TableViewer viewer;

  public PackageRegistryDialog(IWorkbenchPage page, CDOSession session)
  {
    super(page.getWorkbenchWindow().getShell());
    this.page = page;
    this.session = session;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public IWorkbenchPage getPage()
  {
    return page;
  }

  public CDOSession getSession()
  {
    return session;
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(660, 500);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(TITLE);
    setTitle(session.toString());
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    Composite composite = (Composite)super.createDialogArea(parent);

    viewer = new TableViewer(composite, SWT.NONE);
    Table table = viewer.getTable();

    table.setHeaderVisible(true);
    table.setLayoutData(UIUtil.createGridData());
    addColumn(table, Messages.getString("PackageRegistryDialog.1"), 400, SWT.LEFT); //$NON-NLS-1$
    addColumn(table, Messages.getString("PackageRegistryDialog.2"), 75, SWT.CENTER); //$NON-NLS-1$
    addColumn(table, Messages.getString("PackageRegistryDialog.3"), 75, SWT.CENTER); //$NON-NLS-1$
    addColumn(table, Messages.getString("PackageRegistryDialog.4"), 75, SWT.CENTER); //$NON-NLS-1$

    viewer.setContentProvider(new EPackageContentProvider());
    viewer.setLabelProvider(new EPackageLabelProvider());
    viewer.setInput(session);

    return composite;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    Button button = createButton(parent, -1, Messages.getString("PackageRegistryDialog.5"), false); //$NON-NLS-1$
    button.setEnabled(isGlobalPackageAvaliable());
    button.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        new RegisterGeneratedPackagesAction(page, session)
        {
          @Override
          protected void postRegistration(List<EPackage> ePackages)
          {
            refreshViewer();
          }
        }.run();
      }
    });

    createButton(parent, -1, Messages.getString("PackageRegistryDialog.7"), false) //$NON-NLS-1$
        .addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            new RegisterFilesystemPackagesAction(page, session)
            {
              @Override
              protected void postRegistration(List<EPackage> ePackages)
              {
                refreshViewer();
              }
            }.run();
          }
        });

    CustomizeableComposite.customize(parent, IPluginContainer.INSTANCE, PRODUCT_GROUP, this);

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
  }

  /**
   * Expose this method publicly.
   */
  @Override
  public Button createButton(Composite parent, int id, String label, boolean defaultButton)
  {
    return super.createButton(parent, id, label, defaultButton);
  }

  private boolean isGlobalPackageAvaliable()
  {
    Set<String> uris = new HashSet<>(EPackage.Registry.INSTANCE.keySet());
    uris.removeAll(session.getPackageRegistry().keySet());
    return !uris.isEmpty();
  }

  private void addColumn(Table table, String title, int width, int alignment)
  {
    TableColumn column = new TableColumn(table, alignment);
    column.setText(title);
    column.setWidth(width);
  }

  protected Image getContentIcon(Content content)
  {
    return null;
  }

  public void refreshViewer()
  {
    page.getWorkbenchWindow().getShell().getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          viewer.refresh();
        }
        catch (RuntimeException ignore)
        {
        }
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  public class EPackageLabelProvider extends BaseLabelProvider implements ITableLabelProvider, IColorProvider
  {
    public EPackageLabelProvider()
    {
    }

    @Override
    public String getColumnText(Object element, int columnIndex)
    {
      @SuppressWarnings("unchecked")
      Map.Entry<String, Object> entry = (Map.Entry<String, Object>)element;
      CDOPackageInfo packageInfo = CDOModelUtil.getPackageInfo(entry.getValue(), session.getPackageRegistry());
      if (packageInfo != null)
      {
        switch (columnIndex)
        {
        case 0:
          return packageInfo.getPackageURI();

        case 1:
          return packageInfo.getPackageUnit().getState().toString();

        case 2:
          if (packageInfo.getPackageUnit().getType() == Type.UNKNOWN)
          {
            return Messages.getString("PackageRegistryDialog.8"); //$NON-NLS-1$
          }

          return packageInfo.getPackageUnit().getType().toString();

        case 3:
          return packageInfo.getPackageUnit().getOriginalType().toString();
        }
      }

      switch (columnIndex)
      {
      case 0:
        return entry.getKey();

      default:
        return ""; //$NON-NLS-1$
      }
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex)
    {
      if (columnIndex == 0)
      {
        @SuppressWarnings("unchecked")
        Map.Entry<String, Object> entry = (Map.Entry<String, Object>)element;
        CDOPackageInfo packageInfo = CDOModelUtil.getPackageInfo(entry.getValue(), session.getPackageRegistry());
        if (packageInfo != null)
        {
          switch (packageInfo.getPackageUnit().getType())
          {
          case LEGACY:
            return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_LEGACY).createImage();

          case NATIVE:
            return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_NATIVE).createImage();

          case DYNAMIC:
            return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_DYNAMIC).createImage();
          }
        }

        return SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_UNKNOWN).createImage();
      }

      return null;
    }

    @Override
    public Color getBackground(Object element)
    {
      return null;
    }

    @Override
    public Color getForeground(Object element)
    {
      @SuppressWarnings("unchecked")
      Map.Entry<String, Object> entry = (Map.Entry<String, Object>)element;
      CDOPackageInfo packageInfo = CDOModelUtil.getPackageInfo(entry.getValue(), session.getPackageRegistry());
      if (packageInfo != null)
      {
        return null;
      }

      return UIUtil.grayColor();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class EPackageContentProvider implements IStructuredContentProvider
  {
    private static final Object[] NO_ELEMENTS = {};

    private CDOSession session;

    public EPackageContentProvider()
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      if (newInput instanceof CDOSession)
      {
        if (!ObjectUtil.equals(session, newInput))
        {
          session = (CDOSession)newInput;
        }
      }
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
      if (inputElement != session)
      {
        return NO_ELEMENTS;
      }

      return EMFUtil.getSortedRegistryEntries(session.getPackageRegistry());
    }
  }
}
