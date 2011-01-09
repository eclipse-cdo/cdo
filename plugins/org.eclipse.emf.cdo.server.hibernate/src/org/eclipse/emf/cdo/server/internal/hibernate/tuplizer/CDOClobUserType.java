/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobUtil;

/**
 * Persists a {@link CDOClob} in the DB.
 */
public class CDOClobUserType extends CDOLobUserType
{
  public Class<?> returnedClass()
  {
    return CDOClob.class;
  }

  @Override
  protected Object createLob(byte[] id, long size)
  {
    return CDOLobUtil.createClob(id, size);
  }
}
