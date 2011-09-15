/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public interface CDOEditorInput extends IEditorInput
{
  public CDOView getView();

  public boolean isViewOwned();

  public String getResourcePath();
}
