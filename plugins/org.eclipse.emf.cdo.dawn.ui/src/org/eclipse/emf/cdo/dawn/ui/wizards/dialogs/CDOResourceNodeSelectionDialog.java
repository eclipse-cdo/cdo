/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - bug 247226: Transparently support legacy models
 */
package org.eclipse.emf.cdo.dawn.ui.wizards.dialogs;

import org.eclipse.emf.cdo.dawn.ui.composites.CDOResourceNodeChooserComposite;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @since 1.0
 * @author Martin Fluegge
 */
public class CDOResourceNodeSelectionDialog extends Dialog
{
  private final CDOView view;

  private CDOResourceNodeChooserComposite chooserComposite;

  private boolean showResources = false;

  private URI uri;

  public CDOResourceNodeSelectionDialog(Shell parentShell, CDOView view)
  {
    super(parentShell);
    this.view = view;
  }

  public CDOResourceNodeSelectionDialog(Shell parentShell, CDOView view, boolean showResources)
  {
    super(parentShell);
    this.view = view;
    this.showResources = showResources;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Control parentControl = super.createDialogArea(parent);
    chooserComposite = new CDOResourceNodeChooserComposite((Composite)parentControl, SWT.NONE, "ecore", view);

    chooserComposite.showResources(showResources);
    chooserComposite.createAutomaticResourceName();
    GridData gd = new GridData(GridData.FILL_BOTH);
    chooserComposite.setLayoutData(gd);

    return parentControl;
  }

  @Override
  protected Control createButtonBar(Composite parent)
  {
    Control buttonBar = super.createButtonBar(parent);
    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
    buttonBar.setLayoutData(gd);
    getShell().pack();
    return buttonBar;
  }

  @Override
  protected void initializeBounds()
  {
    getShell().setSize(300, 400);
  }

  public URI getResults()
  {
    return uri;
  }

  @Override
  public boolean close()
  {
    uri = chooserComposite.getURI();
    return super.close();
  }
}
