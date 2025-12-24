/*
 * Copyright (c) 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti.editors;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.DiagramEditorInputFactory;
import org.eclipse.ui.IMemento;

/**
 * @author Martin Fluegge
 */
public class DawnGraphitiDiagramEditorInputFactory extends DiagramEditorInputFactory
{
  @Override
  public IAdaptable createElement(IMemento memento)
  {
    final String diagramUriString = memento.getString(DiagramEditorInput.KEY_URI);
    if (diagramUriString == null)
    {
      return null;
    }

    final String providerID = memento.getString(DiagramEditorInput.KEY_PROVIDER_ID);

    // TODO Check, if needed:
    // final TransactionalEditingDomain domain = createResourceSetAndEditingDomain();

    return new DawnGraphitiEditorInput(URI.createURI(diagramUriString), providerID);
  }
}
