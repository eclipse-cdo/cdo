/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
