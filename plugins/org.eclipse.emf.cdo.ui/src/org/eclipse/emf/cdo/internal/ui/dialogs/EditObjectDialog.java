/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.internal.ui.CDOPropertyAdapterFactory;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.ui.view.swt.DefaultReferenceService;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContextFactory;
import org.eclipse.emf.ecp.view.spi.provider.ViewProviderHelper;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class EditObjectDialog extends TitleAreaDialog
{
  private final ComposedAdapterFactory adapterFactory;

  private final EObject eObject;

  public EditObjectDialog(Shell parentShell, ComposedAdapterFactory adapterFactory, EObject eObject)
  {
    super(parentShell);
    this.adapterFactory = adapterFactory;
    this.eObject = eObject;

    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    String typeText = CDOPropertyAdapterFactory.getTypeText(adapterFactory, eObject);
    String title = "Edit " + typeText;

    getShell().setText(title);
    setTitle(title);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_EDIT));
    setMessage("Edit the " + typeText.toLowerCase() + " and press OK to commit or Cancel to rollback your changes.");

    Composite area = (Composite)super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    GridLayout containerGridLayout = new GridLayout(2, false);
    containerGridLayout.marginWidth = 10;
    containerGridLayout.marginHeight = 10;
    container.setLayout(containerGridLayout);

    try
    {
      ViewModelContext viewModelContext = ViewModelContextFactory.INSTANCE
          .createViewModelContext(ViewProviderHelper.getView(eObject, null), eObject, new DefaultReferenceService());

      ECPSWTViewRenderer.INSTANCE.render(container, viewModelContext);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Error ex)
    {
      throw ex;
    }
    catch (Throwable ex)
    {
      throw new RuntimeException(ex);
    }

    return area;
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(600, 500);
  }
}
