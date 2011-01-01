/*******************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.editors;

import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.ui.IEditorPart;

/**
 * @author Martin Fluegge
 */
public interface IDawnEditor extends IEditorPart
{
  public String getContributorID();

  public CDOView getView();

  public void setDirty();

  /**
   * @since 1.0
   */
  public IDawnEditorSupport getDawnEditorSupport();
}
