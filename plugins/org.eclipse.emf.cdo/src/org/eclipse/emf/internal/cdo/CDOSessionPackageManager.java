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
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.protocol.util.TransportException;

import org.eclipse.emf.internal.cdo.protocol.LoadPackageRequest;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class CDOSessionPackageManager extends CDOPackageManagerImpl
{
  private CDOSessionImpl session;

  public CDOSessionPackageManager(CDOSessionImpl session)
  {
    this.session = session;
    ModelUtil.addModelInfos(this);
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public void addPackageProxies(Collection<String> packageURIs)
  {
    for (String packageURI : packageURIs)
    {
      CDOPackageImpl proxy = new CDOPackageImpl(this, packageURI);
      addPackage(proxy);
    }
  }

  @Override
  protected void resolve(CDOPackageImpl cdoPackage)
  {
    try
    {
      new LoadPackageRequest(session.getChannel(), cdoPackage).send();
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }
}
