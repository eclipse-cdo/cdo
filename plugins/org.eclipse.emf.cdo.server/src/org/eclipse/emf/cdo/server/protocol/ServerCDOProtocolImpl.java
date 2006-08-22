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

import org.eclipse.emf.cdo.core.CDOProtocol;
import org.eclipse.emf.cdo.core.protocol.AbstractCDOProtocol;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOProtocol;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;

import org.springframework.transaction.support.TransactionTemplate;


public class ServerCDOProtocolImpl extends AbstractCDOProtocol implements ServerCDOProtocol
{
  protected Mapper mapper;

  protected TransactionTemplate transactionTemplate;

  protected ServerCDOResProtocol serverCDOResProtocol;

  public ServerCDOProtocolImpl()
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
      case ANNOUNCE_PACKAGE:
        return new AnnouncePackageIndication();

      case DESCRIBE_PACKAGE:
        return new DescribePackageIndication();

      case RESOURCE_RID:
        return new ResourceRIDIndication();

      case RESOURCE_PATH:
        return new ResourcePathIndication();

      case LOAD_RESOURCE:
        return new LoadResourceIndication();

      case LOAD_OBJECT:
        return new LoadObjectIndication();

      case COMMIT_TRANSACTION:
        return new CommitTransactionIndication();

      case QUERY_EXTENT:
        return new QueryExtentIndication();
        
      default:
        throw new ImplementationError("Invalid " + CDOProtocol.PROTOCOL_NAME + " signalId: "
            + signalId);
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

  public ServerCDOResProtocol getServerCDOResProtocol()
  {
    return serverCDOResProtocol;
  }

  public void setServerCDOResProtocol(ServerCDOResProtocol serverCDOResProtocol)
  {
    doSet("serverCDOResProtocol", serverCDOResProtocol);
  }

  protected void validate() throws ValidationException
  {
    super.validate();
    assertNotNull("mapper");
    assertNotNull("transactionTemplate");
  }
}
