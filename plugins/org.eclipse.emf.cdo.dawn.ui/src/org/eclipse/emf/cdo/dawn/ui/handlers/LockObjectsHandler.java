/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.handlers;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public class LockObjectsHandler extends SelectionHandler
{
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    ((IDawnEditor)DawnEditorHelper.getActiveEditor()).getDawnEditorSupport().lockObjects(getSelectedObjects(event));

    return null;
  }
}
