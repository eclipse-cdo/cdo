/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterFilesystemPackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterGeneratedPackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterWorkspacePackagesAction;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;
import org.eclipse.emf.cdo.util.CDOPackageType;
import org.eclipse.emf.cdo.util.CDOPackageTypeRegistry;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class PackageManagerDialog extends TitleAreaDialog
{
  private static final int REGISTER_GENERATED_PACKAGES_ID = IDialogConstants.CLIENT_ID + 1;

  private static final int REGISTER_WORKSPACE_PACKAGES_ID = IDialogConstants.CLIENT_ID + 2;

  private static final int REGISTER_FILESYSTEM_PACKAGES_ID = IDialogConstants.CLIENT_ID + 3;

  private static final String TITLE = "CDO Package Manager";

  private static final String EMPTY = "";

  private IWorkbenchPage page;

  private CDOSession session;

  private TableViewer viewer;

  public PackageManagerDialog(IWorkbenchPage page, CDOSession session)
  {
    super(new Shell(page.getWorkbenchWindow().getShell()));
    this.page = page;
    this.session = session;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(TITLE);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = (Composite)super.createDialogArea(parent);
    setTitle(CDOItemProvider.getSessionLabel(session));
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    viewer = new TableViewer(composite, SWT.NONE);
    Table table = viewer.getTable();

    table.setHeaderVisible(true);
    table.setLayoutData(UIUtil.createGridData());
    addColumn(table, "Package", 400, SWT.LEFT);
    addColumn(table, "Registry", 80, SWT.CENTER);
    addColumn(table, "Repository", 80, SWT.CENTER);

    viewer.setContentProvider(new ContentProvider());
    viewer.setLabelProvider(new LabelProvider());
    viewer.setInput(session);

    return composite;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, REGISTER_GENERATED_PACKAGES_ID, "Generated...", false);
    createButton(parent, REGISTER_WORKSPACE_PACKAGES_ID, "Workspace...", false);
    createButton(parent, REGISTER_FILESYSTEM_PACKAGES_ID, "Filesystem...", false);
    createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
  }

  @Override
  protected void buttonPressed(int buttonId)
  {
    switch (buttonId)
    {
    case REGISTER_GENERATED_PACKAGES_ID:
      new RegisterGeneratedPackagesAction(page, session)
      {
        @Override
        protected void postRegistration(List<EPackage> ePackages)
        {
          refreshViewer();
        }
      }.run();
      break;

    case REGISTER_WORKSPACE_PACKAGES_ID:
      new RegisterWorkspacePackagesAction(page, session)
      {
        @Override
        protected void postRegistration(List<EPackage> ePackages)
        {
          refreshViewer();
        }
      }.run();
      break;

    case REGISTER_FILESYSTEM_PACKAGES_ID:
      new RegisterFilesystemPackagesAction(page, session)
      {
        @Override
        protected void postRegistration(List<EPackage> ePackages)
        {
          refreshViewer();
        }
      }.run();
      break;

    case IDialogConstants.CLOSE_ID:
      close();
      break;
    }
  }

  protected Image getContentIcon(Content content)
  {
    return null;
  }

  protected String getEPackageText(Object ePackage)
  {
    if (ePackage == EcorePackage.eINSTANCE)
    {
      return "ECORE";
    }

    if (ePackage.getClass() == EPackageImpl.class)
    {
      return "DYNAMIC";
    }

    String uri = EMPTY;
    if (ePackage instanceof EPackage.Descriptor)
    {
      CDOPackageRegistry registry = session.getPackageRegistry();
      for (Map.Entry<String, Object> entry : registry.entrySet())
      {
        if (entry.getValue() == ePackage)
        {
          uri = entry.getKey();
          break;
        }
      }
    }
    else
    {
      uri = ((EPackage)ePackage).getNsURI();
    }

    CDOPackageType packageType = CDOPackageTypeRegistry.INSTANCE.get(uri);
    if (packageType == null)
    {
      return "?";
    }

    return packageType.toString();
  }

  protected String getCDOPackageText(CDOPackage cdoPackage)
  {
    if (cdoPackage.isSystem())
    {
      return "SYSTEM";
    }

    if (!cdoPackage.isPersistent())
    {
      return EMPTY;
    }

    return cdoPackage.isDynamic() ? "DYNAMIC" : "STATIC";
  }

  private void addColumn(Table table, String title, int width, int alignment)
  {
    TableColumn column = new TableColumn(table, alignment);
    column.setText(title);
    column.setWidth(width);
  }

  protected void refreshViewer()
  {
    page.getWorkbenchWindow().getShell().getDisplay().syncExec(new Runnable()
    {
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
  public class LabelProvider extends BaseLabelProvider implements ITableLabelProvider
  {
    public LabelProvider()
    {
    }

    public String getColumnText(Object element, int columnIndex)
    {
      if (element instanceof Content)
      {
        Content content = (Content)element;
        switch (columnIndex)
        {
        case 0:
          return content.getPackageURI();
        case 1:
          return content.getEPackage() == null ? EMPTY : getEPackageText(content.getEPackage());
        case 2:
          return content.getCDOPackage() == null ? EMPTY : getCDOPackageText(content.getCDOPackage());
        }
      }

      return element.toString();
    }

    public Image getColumnImage(Object element, int columnIndex)
    {
      if (element instanceof Content)
      {
        Content content = (Content)element;
        if (columnIndex == 0)
        {
          return getContentIcon(content);
        }
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ContentProvider implements IStructuredContentProvider
  {
    private static final Object[] NO_ELEMENTS = {};

    private CDOSession session;

    public ContentProvider()
    {
    }

    public void dispose()
    {
    }

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

    public Object[] getElements(Object inputElement)
    {
      if (inputElement != session)
      {
        return NO_ELEMENTS;
      }

      Map<String, Content> map = new HashMap<String, Content>();
      for (Entry<String, Object> entry : session.getPackageRegistry().entrySet())
      {
        String packageURI = entry.getKey();
        Content content = new Content(packageURI);
        map.put(packageURI, content);
        content.setEPackage(entry.getValue());
      }

      for (CDOPackage cdoPackage : session.getPackageManager().getPackages())
      {
        String packageURI = cdoPackage.getPackageURI();
        Content content = map.get(packageURI);
        if (content == null)
        {
          content = new Content(packageURI);
          map.put(packageURI, content);
        }

        content.setCDOPackage(cdoPackage);
      }

      ArrayList<Content> list = new ArrayList<Content>(map.values());
      Collections.sort(list);
      return list.toArray(new Content[list.size()]);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Content implements Comparable<Content>
  {
    private String packageURI;

    private Object ePackage;

    private CDOPackage cdoPackage;

    public Content(String packageURI)
    {
      this.packageURI = packageURI;
    }

    public String getPackageURI()
    {
      return packageURI;
    }

    public Object getEPackage()
    {
      return ePackage;
    }

    public void setEPackage(Object ePackage)
    {
      this.ePackage = ePackage;
    }

    public CDOPackage getCDOPackage()
    {
      return cdoPackage;
    }

    public void setCDOPackage(CDOPackage cdoPackage)
    {
      this.cdoPackage = cdoPackage;
    }

    public int compareTo(Content content)
    {
      return packageURI.compareTo(content.packageURI);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
      {
        return true;
      }

      if (obj instanceof Content)
      {
        Content that = (Content)obj;
        return ObjectUtil.equals(packageURI, that.packageURI);
      }

      return false;
    }

    @Override
    public int hashCode()
    {
      return ObjectUtil.hashCode(packageURI);
    }

    @Override
    public String toString()
    {
      return packageURI;
    }
  }
}
