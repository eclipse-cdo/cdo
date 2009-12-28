/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - bug 230832
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class SetPassiveUpdateRequest extends SyncRevisionsRequest
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, SetPassiveUpdateRequest.class);

  private boolean passiveUpdateEnabled;

  public SetPassiveUpdateRequest(CDOClientProtocol protocol, Map<CDOID, CDOIDAndVersion> idAndVersions,
      int referenceChunk, boolean passiveUpdateEnabled)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_PASSIVE_UPDATE, idAndVersions, referenceChunk);
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Turning " + (passiveUpdateEnabled ? "on" : "off") + " passive update"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    super.requesting(out);
    out.writeBoolean(passiveUpdateEnabled);
  }
}
