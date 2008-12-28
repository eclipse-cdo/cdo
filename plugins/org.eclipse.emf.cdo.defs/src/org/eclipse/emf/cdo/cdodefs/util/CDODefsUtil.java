package org.eclipse.emf.cdo.cdodefs.util;

import org.eclipse.emf.cdo.cdodefs.CDOAuditDef;
import org.eclipse.emf.cdo.cdodefs.CDODefsFactory;
import org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef;
import org.eclipse.emf.cdo.cdodefs.CDOResourceDef;
import org.eclipse.emf.cdo.cdodefs.CDOSessionDef;
import org.eclipse.emf.cdo.cdodefs.CDOTransactionDef;
import org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef;

import org.eclipse.net4j.net4jdefs.TCPConnectorDef;

import java.util.Date;

public class CDODefsUtil
{

  public static CDOSessionDef createSessionDef(String repositoryName, CDOPackageRegistryDef cdoPackageRegistryDef,
      TCPConnectorDef tcpConnectorDef)
  {
    CDOSessionDef cdoSessionDef = CDODefsFactory.eINSTANCE.createCDOSessionDef();
    cdoSessionDef.setConnectorDef(tcpConnectorDef);
    cdoSessionDef.setRepositoryName(repositoryName);
    cdoSessionDef.setCdoPackageRegistryDef(cdoPackageRegistryDef);
    return cdoSessionDef;
  }

  public static CDOSessionDef createSessionDef(String repositoryName, CDOPackageRegistryDef cdoPackageRegistryDef,
      FailOverStrategyDef failOverStrategyDef)
  {
    CDOSessionDef cdoSessionDef = CDODefsFactory.eINSTANCE.createCDOSessionDef();
    cdoSessionDef.setFailOverStrategyDef(failOverStrategyDef);
    cdoSessionDef.setRepositoryName(repositoryName);
    cdoSessionDef.setCdoPackageRegistryDef(cdoPackageRegistryDef);
    return cdoSessionDef;
  }

  public static CDOPackageRegistryDef createEagerPackageRegistryDef()
  {
    CDOPackageRegistryDef cdoPackageRegistryDef = CDODefsFactory.eINSTANCE.createCDOEagerPackageRegistryDef();
    return cdoPackageRegistryDef;
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
