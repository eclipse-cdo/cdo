/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.server.db.IMapping;

/**
 * @author Eike Stepper
 */
public final class ClassServerInfo extends ServerInfo
{
  private IMapping mapping;

  private ClassServerInfo(int id)
  {
    super(id);
  }

  public static ClassServerInfo setDBID(CDOClass cdoClass, int id)
  {
    ClassServerInfo serverInfo = new ClassServerInfo(id);
    cdoClass.setServerInfo(serverInfo);
    return serverInfo;
  }

  public static IMapping getMapping(CDOClass cdoClass)
  {
    return ((ClassServerInfo)cdoClass.getServerInfo()).mapping;
  }

  public static void setMapping(CDOClass cdoClass, IMapping mapping)
  {
    ((ClassServerInfo)cdoClass.getServerInfo()).mapping = mapping;
  }
}
