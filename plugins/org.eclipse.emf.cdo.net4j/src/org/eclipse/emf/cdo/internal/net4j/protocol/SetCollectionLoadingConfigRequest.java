/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig;

import java.io.IOException;

/**
 * Replaces the complete collection-loading configuration of a server session.
 *
 * @author Eike Stepper
 * @since 4.38
 */
public class SetCollectionLoadingConfigRequest extends CDOClientRequest<CDOCollectionLoadingConfig>
{
  private final CDOCollectionLoadingConfig config;

  public SetCollectionLoadingConfigRequest(CDOClientProtocol protocol, CDOCollectionLoadingConfig config)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SET_COLLECTION_LOADING_CONFIG);
    this.config = config;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (config == null)
    {
      out.writeBoolean(false);
    }
    else
    {
      config.write(out);
    }
  }

  @Override
  protected CDOCollectionLoadingConfig confirming(CDODataInput in) throws IOException
  {
    in.readBoolean();
    return in.readBoolean() ? CDOCollectionLoadingConfig.read(in) : null;
  }
}
