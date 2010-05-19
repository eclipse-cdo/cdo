/*******************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.CDOAdapter;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;

/**
 * @author Martin Fluegge
 */
public abstract class AbstractDawnResoureChangeListener extends org.eclipse.emf.ecore.util.EContentAdapter implements
    CDOAdapter
{

  protected DiagramDocumentEditor editor;

  public AbstractDawnResoureChangeListener()
  {
    // TODO remove this and create the listener with the other conatructor using java reflections
    // Only for framework access. Customers should always use the constructor with the DiagramDocumentEditor parameter
  }

  public AbstractDawnResoureChangeListener(DiagramDocumentEditor editor)
  {
    this.editor = editor;
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    super.notifyChanged(notification);
  }

  public void setEditor(DiagramDocumentEditor editor)
  {
    this.editor = editor;
  }
}
