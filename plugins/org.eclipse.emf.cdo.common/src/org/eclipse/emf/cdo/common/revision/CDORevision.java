/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - delta support
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public interface CDORevision
{
  public static final long UNSPECIFIED_DATE = 0;

  public static final int UNCHUNKED = -1;

  public CDORevisionResolver getRevisionResolver();

  public CDOClass getCDOClass();

  public CDOID getID();

  public int getVersion();

  public long getCreated();

  public long getRevised();

  public boolean isCurrent();

  public boolean isValid(long timeStamp);

  public boolean isTransactional();

  public boolean isResource();

  public CDORevisionData getData();

  public CDORevisionDelta compare(CDORevision origin);

  public void merge(CDORevisionDelta delta);

  public void write(ExtendedDataOutput out, CDOIDProvider idProvider, int referenceChunk) throws IOException;
}
