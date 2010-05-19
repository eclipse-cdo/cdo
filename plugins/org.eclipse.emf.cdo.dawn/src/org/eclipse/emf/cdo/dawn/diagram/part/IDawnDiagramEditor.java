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
package org.eclipse.emf.cdo.dawn.diagram.part;

import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Martin Fluegge
 */
public interface IDawnDiagramEditor
{

  public String getContributorID();

  public CDOView getView();

  public void setDirty();
}
