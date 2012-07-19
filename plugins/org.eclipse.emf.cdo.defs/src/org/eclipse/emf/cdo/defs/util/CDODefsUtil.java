/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs.util;

import org.eclipse.emf.cdo.defs.CDOAuditDef;
import org.eclipse.emf.cdo.defs.CDODefsFactory;
import org.eclipse.emf.cdo.defs.CDOPackageRegistryDef;
import org.eclipse.emf.cdo.defs.CDOResourceDef;
import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.defs.CDOTransactionDef;

import org.eclipse.net4j.defs.TCPConnectorDef;

import java.util.Date;

/**
 * @author Andre Dietisheim
 */
public class CDODefsUtil
{
  public static CDOSessionDef createSessionDef(String repositoryName, CDOPackageRegistryDef ePackageRegistryDef,
      TCPConnectorDef tcpConnectorDef)
  {
    CDOSessionDef cdoSessionDef = CDODefsFactory.eINSTANCE.createCDOSessionDef();
    cdoSessionDef.setConnectorDef(tcpConnectorDef);
    cdoSessionDef.setRepositoryName(repositoryName);
    cdoSessionDef.setCdoPackageRegistryDef(ePackageRegistryDef);
    return cdoSessionDef;
  }

  /**
   * @since 4.0
   */
  public static CDOSessionDef createSessionDef(String repositoryName, CDOPackageRegistryDef ePackageRegistryDef)
  {
    CDOSessionDef cdoSessionDef = CDODefsFactory.eINSTANCE.createCDOSessionDef();
    cdoSessionDef.setRepositoryName(repositoryName);
    cdoSessionDef.setCdoPackageRegistryDef(ePackageRegistryDef);
    return cdoSessionDef;
  }

  public static CDOPackageRegistryDef createEagerPackageRegistryDef()
  {
    CDOPackageRegistryDef ePackageRegistryDef = CDODefsFactory.eINSTANCE.createCDOEagerPackageRegistryDef();
    return ePackageRegistryDef;
  }

  public static CDOPackageRegistryDef createLazyPackageRegistryDef()
  {
    CDOPackageRegistryDef ePackageRegistryDef = CDODefsFactory.eINSTANCE.createCDOLazyPackageRegistryDef();
    return ePackageRegistryDef;
  }

  public static CDOAuditDef createCDOAuditDef(CDOSessionDef cdoSessionDef)
  {
    CDOAuditDef cdoAuditDef = CDODefsFactory.eINSTANCE.createCDOAuditDef();
    cdoAuditDef.setCdoSessionDef(cdoSessionDef);
    cdoAuditDef.setTimeStamp(new Date());
    return cdoAuditDef;
  }

  public static CDOTransactionDef createCDOTransactionDef(String host, String repositoryName,
      TCPConnectorDef tcpConnectorDef)
  {
    CDOSessionDef sessionDef = createSessionDef(repositoryName, //
        createEagerPackageRegistryDef(), //
        tcpConnectorDef);

    return createCDOTransactionDef(sessionDef);
  }

  public static CDOTransactionDef createCDOTransactionDef(CDOSessionDef cdoSessionDef)
  {

    CDOTransactionDef cdoTransactionDef = CDODefsFactory.eINSTANCE.createCDOTransactionDef();
    cdoTransactionDef.setCdoSessionDef(cdoSessionDef);
    return cdoTransactionDef;
  }

  public static CDOResourceDef createCDOResourceDef(CDOTransactionDef cdoTransactionDef)
  {

    CDOResourceDef cdoResourceDef = CDODefsFactory.eINSTANCE.createCDOResourceDef();
    cdoResourceDef.setCdoTransaction(cdoTransactionDef);
    return cdoResourceDef;
  }

}
