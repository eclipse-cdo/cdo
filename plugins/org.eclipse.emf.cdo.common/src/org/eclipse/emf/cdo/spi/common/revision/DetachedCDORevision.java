/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.ecore.EClass;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class DetachedCDORevision extends SyntheticCDORevision
{
  private int version;

  private long timeStamp;

  public DetachedCDORevision(EClass eClass, CDOID id, CDOBranch branch, int version, long timeStamp)
  {
    super(eClass, id, branch);
    this.version = version;
    this.timeStamp = timeStamp;
  }

  @Override
  public final int getVersion()
  {
    return version;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public long getRevised()
  {
    return UNSPECIFIED_DATE;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("DetachedCDORevision[{0}:{1}v{2}]", getID(), getBranch().getID(), version);
  }
}
