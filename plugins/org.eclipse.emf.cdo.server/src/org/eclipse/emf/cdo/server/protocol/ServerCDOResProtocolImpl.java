/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.Indication;
import org.eclipse.net4j.spring.ValidationException;
import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.cdo.core.protocol.AbstractCDOResProtocol;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;

import org.springframework.transaction.support.TransactionTemplate;


public class ServerCDOResProtocolImpl extends AbstractCDOResProtocol implements
    ServerCDOResProtocol
{
  protected Mapper mapper;

  protected TransactionTemplate transactionTemplate;

  public ServerCDOResProtocolImpl()
  {
  }

  public int getType()
  {
    return SERVER;
  }

  public Indication createIndication(short signalId)
  {
    switch (signalId)
    {
      case QUERY_ALL_RESOURCES:
        return new QueryAllResourcesIndication();

      default:
        throw new ImplementationError("Invalid " + PROTOCOL_NAME + " signalId: " + signalId);
    }
  }

  public Mapper getMapper()
  {
    return mapper;
  }

  public void setMapper(Mapper mapper)
  {
    doSet("mapper", mapper);
  }

  public TransactionTemplate getTransactionTemplate()
  {
    return transactionTemplate;
  }

  public void setTransactionTemplate(TransactionTemplate transactionTemplate)
  {
    doSet("transactionTemplate", transactionTemplate);
  }

  protected void validate() throws ValidationException
  {
    super.validate();
    assertNotNull("mapper");
    assertNotNull("transactionTemplate");
  }
}
