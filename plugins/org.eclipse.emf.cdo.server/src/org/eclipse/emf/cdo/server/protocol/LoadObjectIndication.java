/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;

import org.eclipse.emf.cdo.core.CdoProtocol;
import org.eclipse.emf.cdo.server.CdoServerProtocol;
import org.eclipse.emf.cdo.server.Mapper;


public class LoadObjectIndication extends AbstractIndicationWithResponse
{
  private long oid;

  public short getSignalId()
  {
    return CdoProtocol.LOAD_OBJECT;
  }

  public void indicate()
  {
    oid = receiveLong();
  }

  public void respond()
  {
    Mapper mapper = ((CdoServerProtocol) getProtocol()).getMapper();
    mapper.transmitObject(getChannel(), oid);
  }
}
