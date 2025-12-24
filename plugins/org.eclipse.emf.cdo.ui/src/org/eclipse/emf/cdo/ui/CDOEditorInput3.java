/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An extension to {@link CDOEditorInput} that provides long-running {@link CDOView} opening.
 *
 * @author Eike Stepper
 * @since 4.8
 * @see org.eclipse.ui.IEditorInput
 * @see org.eclipse.emf.cdo.ui.CDOEditorUtil
 */
public interface CDOEditorInput3 extends CDOEditorInput
{
  /**
   * Opens the CDOView associated with this CDOEditorInput
   */
  public CDOView openView(IProgressMonitor progressMonitor);
}
