/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class LongIDStore extends Store
{
  /**
   * @since 3.0
   */
  public static final Set<ObjectType> OBJECT_ID_TYPES = Collections.singleton(CDOID.ObjectType.LONG);

  @ExcludeFromDump
  private transient AtomicLong lastObjectID = new AtomicLong();

  public LongIDStore(String type, Set<ChangeFormat> supportedChangeFormats,
      Set<RevisionTemporality> supportedRevisionTemporalities, Set<RevisionParallelism> supportedRevisionParallelisms)
  {
    super(type, OBJECT_ID_TYPES, supportedChangeFormats, supportedRevisionTemporalities, supportedRevisionParallelisms);
  }

  public long getLastObjectID()
  {
    return lastObjectID.get();
  }

  public void setLastObjectID(long lastObjectID)
  {
    this.lastObjectID.set(lastObjectID);
  }

  public CDOID getNextCDOID()
  {
    return CDOIDUtil.createLong(lastObjectID.incrementAndGet());
  }
}
