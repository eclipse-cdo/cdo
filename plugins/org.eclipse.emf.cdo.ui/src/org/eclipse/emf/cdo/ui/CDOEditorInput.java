/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.ui.IEditorInput;

/**
 * An specialized {@link org.eclipse.ui.IEditorInput IEditorInput} for the CDO editor. A <code>CDOEditorInput</code> is
 * associated with a {@link org.eclipse.emf.cdo.eresource.CDOResource CDOResource}, accessed through a
 * {@link org.eclipse.emf.cdo.view.CDOView CDOView} instance.
 * <p>
 * For related operations, see
 * {@link org.eclipse.emf.cdo.ui.CDOEditorUtil#createCDOEditorInput(CDOView, String, boolean) CDOEditorUtil}
 *
 * @author Victor Roldan Betancort
 * @since 2.0
 * @see org.eclipse.ui.IEditorInput
 * @see org.eclipse.emf.cdo.ui.CDOEditorUtil
 * @see org.eclipse.emf.cdo.view.CDOView
 */
public interface CDOEditorInput extends IEditorInput
{
  /**
   * Returns the CDOView associated with this CDOEditorInput
   */
  public CDOView getView();

  /**
   * Returns true if the CDO editor instance is responsible for the underlying org.eclipse.emf.cdo.view.CDOView
   */
  public boolean isViewOwned();

  /**
   * Returns the path to the
   */
  public String getResourcePath();
}
