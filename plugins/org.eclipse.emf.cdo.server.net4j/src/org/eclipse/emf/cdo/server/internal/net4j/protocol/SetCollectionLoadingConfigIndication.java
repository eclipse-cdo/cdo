/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig;

import java.io.IOException;

/**
 * Installs a complete collection-loading configuration snapshot on the server session.
 *
 * @author Eike Stepper
 * @since 4.38
 */
public class SetCollectionLoadingConfigIndication extends CDOServerReadIndication
{
  private CDOCollectionLoadingConfig repositoryConfig;

  public SetCollectionLoadingConfigIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SET_COLLECTION_LOADING_CONFIG);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    CDOCollectionLoadingConfig config = CDOCollectionLoadingConfig.read(in);
    getSession().setCollectionLoadingConfig(config);
    repositoryConfig = getSession().getRepository().getCollectionLoadingConfig();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
    out.writeBoolean(repositoryConfig != null);

    if (repositoryConfig != null)
    {
      repositoryConfig.write(out);
    }
  }
}
