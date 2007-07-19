/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

/**
 * @author Eike Stepper
 */
public interface InternalCDOObject extends CDOObject
{
  public void setID(CDOID id);

  public void setResource(CDOResource resource);

  public void setAdapter(CDOViewImpl view);

  public void setState(CDOState prepared_attach);

  public void setRevision(CDORevision revision);

  public CDORevisionImpl copyRevision();

  public void finalizeRevision();
}
