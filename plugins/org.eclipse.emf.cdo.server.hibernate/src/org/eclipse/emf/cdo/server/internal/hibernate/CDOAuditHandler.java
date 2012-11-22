/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - moved cdopackage handler to other class, changed configuration
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.teneo.hibernate.auditing.AuditHandler;
import org.eclipse.emf.teneo.hibernate.auditing.model.teneoauditing.TeneoAuditEntry;

import org.hibernate.Session;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * Overrides the {@link AuditHandler} to implement CDO
 * specific audit handling.
 * 
 * @author Martin Taal
 */
public class CDOAuditHandler extends AuditHandler
{

  @Override
  public void setContainerInfo(Session session, TeneoAuditEntry teneoAuditEntry, Object object)
  {
    final InternalCDORevision cdoRevision = (InternalCDORevision)object;

    teneoAuditEntry.setTeneo_container_feature_id(cdoRevision.getContainingFeatureID());
    teneoAuditEntry.setTeneo_container_id(HibernateUtil.getInstance().convertCDOIDToString(
        (CDOID)cdoRevision.getContainerID()));
  }

  @Override
  public boolean isAudited(Object entity)
  {
    if (!(entity instanceof CDORevision))
    {
      // TODO: support featuremap entries
      return false;
    }
    if (!getDataStore().isAuditing())
    {
      return false;
    }
    final CDORevision cdoRevision = (CDORevision)entity;
    final EClass auditEClass = getAuditingModelElement(cdoRevision.getEClass());
    return auditEClass != null;
  }

  @Override
  public EClass getEClass(Object o)
  {
    if (o instanceof EObject)
    {
      return ((EObject)o).eClass();
    }
    return ((CDORevision)o).getEClass();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void copyContentToAuditEntry(Session session, Object object, TeneoAuditEntry auditEntry,
      boolean copyCollections)
  {
    final InternalCDORevision source = (InternalCDORevision)object;
    final EClass sourceEClass = source.getEClass();
    final EClass targetEClass = auditEntry.eClass();
    for (EStructuralFeature targetEFeature : targetEClass.getEAllStructuralFeatures())
    {

      if (!copyCollections && targetEFeature.isMany())
      {
        continue;
      }
      // part of a featuremap
      if (ExtendedMetaData.INSTANCE.getGroup(targetEFeature) != null)
      {
        continue;
      }

      // initialize with new arrays always to prevent hibernate from complaining if the
      // same array is re-used accross entities
      if (targetEFeature.getEType().getInstanceClass() != null
          && targetEFeature.getEType().getInstanceClass().isArray())
      {
        auditEntry.eSet(targetEFeature,
            Array.newInstance(targetEFeature.getEType().getInstanceClass().getComponentType(), 0));
      }

      final EStructuralFeature sourceEFeature = sourceEClass.getEStructuralFeature(targetEFeature.getName());
      if (sourceEFeature != null)
      {
        if (targetEFeature instanceof EAttribute && sourceEFeature instanceof EReference)
        {
          if (sourceEFeature.isMany())
          {
            for (Object value : (Collection<?>)source.getList(sourceEFeature))
            {
              final String idAsString = entityToIdString(session, value);
              ((Collection<Object>)auditEntry.eGet(targetEFeature)).add(idAsString);
            }
          }
          else
          {
            final String idAsString = entityToIdString(session, source.getValue(sourceEFeature));
            auditEntry.eSet(targetEFeature, idAsString);
          }
        }
        else
        {
          if (sourceEFeature.isMany())
          {
            if (FeatureMapUtil.isFeatureMap(sourceEFeature))
            {
              convertFeatureMap(session, source, sourceEFeature, auditEntry, targetEFeature);
            }
            else
            {
              for (Object value : (Collection<?>)source.getList(sourceEFeature))
              {
                ((Collection<Object>)auditEntry.eGet(targetEFeature)).add(convertValue(targetEFeature, value));
              }
            }
          }
          else
          {
            auditEntry.eSet(targetEFeature, convertValue(targetEFeature, source.getValue(sourceEFeature)));
          }
        }
      }
    }
  }

  @Override
  protected EStructuralFeature createEMapFeature(EStructuralFeature sourceEFeature, EClass eMapEntryEClass)
  {
    return createEReferenceAttribute((EReference)sourceEFeature);
  }

  protected void convertEMap(Session session, InternalCDORevision source, EReference sourceEReference,
      TeneoAuditEntry auditEntry, EReference targetEReference)
  {
    throw new IllegalStateException("Error case: The system should not use this method when doing auditing in CDO");
  }

  protected void convertFeatureMap(Session session, InternalCDORevision source, EStructuralFeature sourceEFeature,
      TeneoAuditEntry auditEntry, EStructuralFeature targetEFeature)
  {
    super.convertFeatureMap(session, source.getList(sourceEFeature), sourceEFeature, auditEntry, targetEFeature);
  }

  @Override
  public String entityToIdString(Session session, Object entity)
  {
    if (entity == null || entity == InternalCDORevision.NIL)
    {
      return null;
    }

    final CDOID cdoID;
    if (entity instanceof CDOID)
    {
      cdoID = (CDOID)entity;
    }
    else
    {
      final CDORevision cdoRevision = (CDORevision)entity;
      cdoID = cdoRevision.getID();
    }
    if (cdoID == CDOID.NULL)
    {
      return null;
    }

    return HibernateUtil.getInstance().convertCDOIDToString(cdoID);
  }

  @Override
  public String idToString(EClass eClass, Object id)
  {
    return HibernateUtil.getInstance().convertCDOIDToString((CDOID)id);
  }

  @Override
  public Object convertValue(EStructuralFeature eFeature, Object value)
  {
    if (value == CDORevisionData.NIL)
    {
      return null;
    }
    return super.convertValue(eFeature, value);
  }

}
