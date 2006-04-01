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


import org.eclipse.net4j.core.Indication;
import org.eclipse.net4j.spring.ValidationException;
import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.cdo.core.CdoProtocol;
import org.eclipse.emf.cdo.core.protocol.AbstractCdoProtocol;
import org.eclipse.emf.cdo.server.CdoServerProtocol;
import org.eclipse.emf.cdo.server.Mapper;

import org.springframework.transaction.support.TransactionTemplate;


public class CdoServerProtocolImpl extends AbstractCdoProtocol implements CdoServerProtocol
{
  protected Mapper mapper;

  protected TransactionTemplate transactionTemplate;

  public CdoServerProtocolImpl()
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
        return new ResourceRidIndication();

      case RESOURCE_PATH:
        return new ResourcePathIndication();

      case LOAD_RESOURCE:
        return new LoadResourceIndication();

      case LOAD_OBJECT:
        return new LoadObjectIndication();

      case COMMIT_TRANSACTION:
        return new CommitTransactionIndication();

      default:
        throw new ImplementationError("Invalid " + CdoProtocol.PROTOCOL_NAME + " signalId: "
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

  protected void validate() throws ValidationException
  {
    super.validate();
    assertNotNull("mapper");
    assertNotNull("transactionTemplate");
  }
}