/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalCDORevisionResolver extends CDORevisionResolver
{
  public List<CDORevision> getCachedRevisions();

  public boolean addCachedRevision(InternalCDORevision revision);

  public void removeCachedRevision(CDOID id, int version);

  public void clearCache();

  public void revisedRevision(CDOID id, long timeStamp);

  public void revisedRevisionByVersion(CDOID id, int version, long timeStamp);
}
