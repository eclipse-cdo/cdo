/*
 * Copyright (c) 2007-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.BaseDialog;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class SelectPackageDialog extends BaseDialog<CheckboxTableViewer>
{
  private static final Set<String> NO_URIS = Collections.emptySet();

  private Set<String> excludedURIs = new HashSet<>();

  private Set<String> checkedURIs = new HashSet<>();

  public SelectPackageDialog(Shell shell, String title, String message, Set<String> excludedURIs)
  {
    super(shell, DEFAULT_SHELL_STYLE | SWT.APPLICATION_MODAL, title, message, OM.Activator.INSTANCE.getDialogSettings());
    this.excludedURIs = excludedURIs;
  }

  public SelectPackageDialog(Shell shell, String title, String message)
  {
    this(shell, title, message, NO_URIS);
  }

  public Set<String> getCheckedURIs()
  {
    return checkedURIs;
  }

  @Override
  protected void createUI(Composite parent)
  {
    CheckboxTableViewer viewer = CheckboxTableViewer.newCheckList(parent, SWT.SINGLE | SWT.BORDER);
    viewer.getTable().setLayoutData(UIUtil.createGridData());
    viewer.setContentProvider(new PackageContentProvider());
    viewer.setLabelProvider(new PackageLabelProvider());
    viewer.setInput(EPackage.Registry.INSTANCE);

    String[] uris = OM.PREF_HISTORY_SELECT_PACKAGES.getValue();
    if (uris != null)
    {
      viewer.setCheckedElements(uris);
    }

    setCurrentViewer(viewer);
  }

  @Override
  protected void okPressed()
  {
    Object[] checkedElements = getCurrentViewer().getCheckedElements();
    for (Object checkedElement : checkedElements)
    {
      checkedURIs.add((String)checkedElement);
    }

    OM.PREF_HISTORY_SELECT_PACKAGES.setValue(checkedURIs.toArray(new String[checkedURIs.size()]));
    super.okPressed();
  }

  /**
   * @author Eike Stepper
   */
  private class PackageContentProvider implements IStructuredContentProvider
  {
    public PackageContentProvider()
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
      Set<String> uris = new HashSet<>(EPackage.Registry.INSTANCE.keySet());
      uris.removeAll(excludedURIs);

      List<String> elements = new ArrayList<>(uris);
      Collections.sort(elements);
      return elements.toArray();
    }
  }

  /**
   * @author Eike Stepper
   */
  private class PackageLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider
  {
    private final Color red = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);

    public PackageLabelProvider()
    {
    }

    @Override
    public String getText(Object element)
    {
      return element.toString();
    }

    @Override
    public Image getImage(Object element)
    {
      if (element instanceof String)
      {
        Type type = CDOPackageTypeRegistry.INSTANCE.lookup((String)element);
        switch (type)
        {
        case LEGACY:
          return SharedIcons.getImage(SharedIcons.OBJ_EPACKAGE_LEGACY);

        case NATIVE:
          return SharedIcons.getImage(SharedIcons.OBJ_EPACKAGE_NATIVE);

        case DYNAMIC:
          return SharedIcons.getImage(SharedIcons.OBJ_EPACKAGE_DYNAMIC);

        case UNKNOWN:
          return SharedIcons.getImage(SharedIcons.OBJ_EPACKAGE_UNKNOWN);
        }
      }

      return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex)
    {
      return getText(element);
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex)
    {
      return getImage(element);
    }

    @Override
    public Color getBackground(Object element, int columnIndex)
    {
      return null;
    }

    @Override
    public Color getForeground(Object element, int columnIndex)
    {
      if (EcorePackage.eINSTANCE.getNsURI().equals(element))
      {
        return red;
      }

      return null;
    }
  }
}
