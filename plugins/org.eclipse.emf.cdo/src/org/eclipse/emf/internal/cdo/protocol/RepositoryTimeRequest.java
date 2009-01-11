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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RepositoryTimeRequest extends CDOTimeRequest<RepositoryTimeResult>
{
  public RepositoryTimeRequest(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPOSITORY_TIME);
  }

  @Override
  protected RepositoryTimeResult confirming(CDODataInput in) throws IOException
  {
    super.confirming(in);
    return getRepositoryTimeResult();
  }
}
