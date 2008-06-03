/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision.delta;

/**
 * @author Simon McDuff
 */
public interface CDOFeatureDeltaVisitor
{
  public void visit(CDOMoveFeatureDelta delta);

  public void visit(CDOAddFeatureDelta delta);

  public void visit(CDORemoveFeatureDelta delta);

  public void visit(CDOSetFeatureDelta delta);

  public void visit(CDOUnsetFeatureDelta delta);

  public void visit(CDOListFeatureDelta delta);

  public void visit(CDOClearFeatureDelta delta);

  public void visit(CDOContainerFeatureDelta delta);
}
