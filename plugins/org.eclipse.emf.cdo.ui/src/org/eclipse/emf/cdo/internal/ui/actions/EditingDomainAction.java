/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Eike Stepper
 * @ADDED
 */
public abstract class EditingDomainAction extends LongRunningAction
{
  protected EditingDomain editingDomain;

  public EditingDomainAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public EditingDomainAction(String text, int style)
  {
    super(text, style);
  }

  public EditingDomainAction(String text, String toolTipText, ImageDescriptor image)
  {
    super(text, toolTipText, image);
  }

  public EditingDomainAction(String text, String toolTipText)
  {
    super(text, toolTipText);
  }

  public EditingDomainAction(String text)
  {
    super(text);
  }

  public EditingDomain getEditingDomain()
  {
    return editingDomain;
  }

  public void setEditingDomain(EditingDomain editingDomain)
  {
    this.editingDomain = editingDomain;
  }

  public void setActiveWorkbenchPart(IWorkbenchPart workbenchPart)
  {
    setPage(workbenchPart == null ? null : workbenchPart.getSite().getPage());
    setEditingDomain(workbenchPart instanceof IEditingDomainProvider ? ((IEditingDomainProvider)workbenchPart).getEditingDomain() : null);
  }

  public void update()
  {
    setEnabled(editingDomain != null);
  }
}
