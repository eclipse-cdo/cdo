/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial api
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOLobUtil;

/**
 * Persists a {@link CDOBlob} in the DB.
 */
public class CDOBlobUserType extends CDOLobUserType
{
  public Class<?> returnedClass()
  {
    return CDOBlob.class;
  }

  @Override
  protected Object createLob(byte[] id, long size)
  {
    return CDOLobUtil.createBlob(id, size);
  }
}
