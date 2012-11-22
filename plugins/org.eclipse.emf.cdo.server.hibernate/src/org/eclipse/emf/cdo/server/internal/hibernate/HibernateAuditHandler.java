/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDLong;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.teneo.hibernate.HbDataStore;
import org.eclipse.emf.teneo.hibernate.auditing.AuditHandler;
import org.eclipse.emf.teneo.hibernate.auditing.AuditVersionProvider;
import org.eclipse.emf.teneo.hibernate.auditing.model.teneoauditing.TeneoAuditEntry;
import org.eclipse.emf.teneo.hibernate.auditing.model.teneoauditing.TeneoAuditKind;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods to support auditing in the hibernate store.
 * An internal class.
 * 
 * @author Martin Taal
 */
public class HibernateAuditHandler
{
  private HibernateStore hibernateStore;

  private HbDataStore cdoDataStore;

  public AuditHandler getAuditHandler()
  {
    return cdoDataStore.getAuditHandler();
  }

  public InternalCDORevision readRevision(Session session, CDOID id, long timeStamp)
  {
    final CDOClassifierRef classifierRef = CDOIDUtil.getClassifierRef(id);
    if (classifierRef == null)
    {
      throw new IllegalArgumentException("This CDOID type of " + id + " is not supported by this store."); //$NON-NLS-1$ //$NON-NLS-2$
    }
    final EClass eClass = HibernateUtil.getInstance().getEClass(classifierRef);
    final CDORevision revision = getCDORevision(session, eClass, id, timeStamp);
    return (InternalCDORevision)revision;
  }

  public CDORevision getCDORevision(Session session, EClass eClass, Object id, long timeStamp)
  {
    AuditVersionProvider auditVersionProvider = cdoDataStore.getAuditVersionProvider();
    auditVersionProvider.setSession(session);
    final TeneoAuditEntry auditEntry = auditVersionProvider.getAuditEntry(eClass, id, timeStamp);
    if (auditEntry.getTeneo_audit_kind() == TeneoAuditKind.DELETE)
    {
      return null;
    }
    return getCDORevision(session, auditEntry);
  }

  public CDORevision getCDORevision(Session session, TeneoAuditEntry teneoAuditEntry)
  {
    final CDORevision revision = convertAuditEntryToCDORevision(teneoAuditEntry);
    return revision;
  }

  protected CDORevision convertAuditEntryToCDORevision(TeneoAuditEntry teneoAuditEntry)
  {
    final HibernateStoreAccessor storeAccessor = HibernateThreadContext.getCurrentStoreAccessor();
    final EClass domainEClass = getAuditHandler().getModelElement(teneoAuditEntry.eClass());
    final CDOID cdoID = HibernateUtil.getInstance().convertStringToCDOID(teneoAuditEntry.getTeneo_object_id());
    final InternalCDORevision revision = hibernateStore.createRevision(domainEClass, cdoID);
    revision.setBranchPoint(storeAccessor.getStore().getMainBranchHead().getBranch()
        .getPoint(teneoAuditEntry.getTeneo_start()));
    if (teneoAuditEntry.getTeneo_end() > 0)
    {
      revision.setRevised(teneoAuditEntry.getTeneo_end());
    }
    convertContent(teneoAuditEntry, revision);
    return revision;
  }

  protected void convertContent(TeneoAuditEntry auditEntry, InternalCDORevision cdoRevision)
  {
    final EClass auditingEClass = auditEntry.eClass();
    for (EStructuralFeature targetEFeature : cdoRevision.getEClass().getEAllStructuralFeatures())
    {
      final EStructuralFeature sourceEFeature = auditingEClass.getEStructuralFeature(targetEFeature.getName());
      if (sourceEFeature == null)
      {
        continue;
      }
      if (!auditEntry.eIsSet(sourceEFeature) && !sourceEFeature.isMany())
      {
        continue;
      }
      if (sourceEFeature.isMany())
      {
        for (Object value : (Collection<?>)auditEntry.eGet(sourceEFeature))
        {
          cdoRevision.getList(targetEFeature).add(convertValue(targetEFeature, value));
        }
      }
      else
      {
        final Object value = auditEntry.eGet(sourceEFeature);
        cdoRevision.setValue(targetEFeature, convertValue(targetEFeature, value));
      }
    }

    if (auditEntry.getTeneo_container_id() != null)
    {
      cdoRevision.setContainerID(HibernateUtil.getInstance().convertStringToCDOID(auditEntry.getTeneo_container_id()));
      cdoRevision.setContainingFeatureID(auditEntry.getTeneo_container_feature_id());
    }
  }

  private Object convertValue(EStructuralFeature targetEFeature, Object value)
  {
    if (value == null)
    {
      return null;
    }
    if (targetEFeature instanceof EReference)
    {
      final CDOID cdoID = value instanceof CDOID ? (CDOID)value : HibernateUtil.getInstance().convertStringToCDOID(
          (String)value);
      return cdoID;
    }
    if (value instanceof Enumerator)
    {
      return ((Enumerator)value).getValue();
    }
    if (value instanceof EEnumLiteral)
    {
      return ((EEnumLiteral)value).getValue();
    }
    return value;
  }

  public List<?> getCDOResources(Session session, CDOID folderId, long timeStamp)
  {
    final AuditHandler auditHandler = getAuditHandler();
    final EClass auditEClass = auditHandler.getAuditingModelElement(EresourcePackage.eINSTANCE.getCDOResourceNode());
    final String entityName = cdoDataStore.toEntityName(auditEClass);
    return getCDOResources(session, folderId, timeStamp, entityName);
  }

  public List<?> getCDOResources(Session session, CDOID folderId, long timeStamp, String entityName)
  {
    final AuditHandler auditHandler = getAuditHandler();
    String idStr = null;
    if (folderId != null)
    {
      idStr = auditHandler.idToString(EresourcePackage.eINSTANCE.getCDOResourceFolder(),
          ((AbstractCDOIDLong)folderId).getLongValue());
    }

    final String qryStr = "select e from " + entityName + " e where e.folder"
        + (idStr == null ? " is null " : "=:folder")
        + " and (e.teneo_end=-1 or e.teneo_end>:end) and e.teneo_start<=:start";
    final Query qry = session.createQuery(qryStr);
    if (idStr != null)
    {
      qry.setParameter("folder", idStr);
    }
    qry.setParameter("start", timeStamp);
    qry.setParameter("end", timeStamp);
    return qry.list();
  }

  public List<CDORevision> handleRevisionsByEClass(Session session, EClass eClass, CDORevisionHandler handler,
      long timeStamp)
  {
    AuditVersionProvider auditVersionProvider = cdoDataStore.getAuditVersionProvider();
    auditVersionProvider.setSession(session);
    final List<TeneoAuditEntry> teneoAuditEntries = auditVersionProvider.getAllAuditEntries(eClass, timeStamp);
    final List<CDORevision> revisions = new ArrayList<CDORevision>();
    for (TeneoAuditEntry teneoAuditEntry : teneoAuditEntries)
    {
      final CDORevision cdoRevision = getCDORevision(session, teneoAuditEntry);
      handler.handleRevision(cdoRevision);
    }
    return revisions;
  }

  public HbDataStore getCdoDataStore()
  {
    return cdoDataStore;
  }

  public void setCdoDataStore(HbDataStore cdoDataStore)
  {
    this.cdoDataStore = cdoDataStore;
  }

  public HibernateStore getHibernateStore()
  {
    return hibernateStore;
  }

  public void setHibernateStore(HibernateStore hibernateStore)
  {
    this.hibernateStore = hibernateStore;
  }
}
