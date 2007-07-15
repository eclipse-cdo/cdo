/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.util.factory.Factory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Eike Stepper
 */
public class CDOSessionFactory extends Factory<CDOSession>
{
  public static final String SESSION_GROUP = OM.BUNDLE_ID + ".sessions";

  public CDOSessionFactory()
  {
    super(SESSION_GROUP, CDOProtocolConstants.PROTOCOL_NAME);
  }

  public CDOSession create(String description)
  {
    CDOSessionImpl session = new CDOSessionImpl(null);
    session.setRepositoryName(getRepositoryName(description));
    return session;
  }

  public static String getRepositoryName(String description)
  {
    URI uri = URI.createURI(description);
    IPath path = new Path(uri.path());
    return path.segment(0);
  }
}
