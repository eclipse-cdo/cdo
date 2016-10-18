/*
 * Copyright (c) 2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.teneo.Constants;
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
    teneoAuditEntry.setTeneo_container_id(HibernateUtil.getInstance().convertCDOIDToString((CDOID)cdoRevision.getContainerID()));
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

  public boolean isAudited(CDOID id)
  {
    if (!getDataStore().isAuditing())
    {
      return false;
    }
    if (!(id instanceof CDOClassifierRef.Provider))
    {
      return false;
    }
    CDOClassifierRef cdoClassifierRef = ((CDOClassifierRef.Provider)id).getClassifierRef();
    final EClass eClass = HibernateUtil.getInstance().getEClass(cdoClassifierRef);
    if (eClass == null)
    {
      return false;
    }
    final EClass auditEClass = getAuditingModelElement(eClass);
    return auditEClass != null;
  }

  @Override
  protected boolean supportCustomType()
  {
    return true;
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
  public void copyContentToAuditEntry(Session session, Object object, TeneoAuditEntry auditEntry, boolean copyCollections)
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
      if (targetEFeature.getEType().getInstanceClass() != null && targetEFeature.getEType().getInstanceClass().isArray())
      {
        auditEntry.eSet(targetEFeature, Array.newInstance(targetEFeature.getEType().getInstanceClass().getComponentType(), 0));
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
                ((Collection<Object>)auditEntry.eGet(targetEFeature)).add(convertValue(sourceEFeature, targetEFeature, value));
              }
            }
          }
          else
          {
            // not set
            if (sourceEFeature.isUnsettable() && source.getValue(sourceEFeature) == null)
            {
              auditEntry.eUnset(targetEFeature);
            }
            else
            {
              auditEntry.eSet(targetEFeature, convertValue(sourceEFeature, targetEFeature, source.getValue(sourceEFeature)));
            }
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

  protected void convertEMap(Session session, InternalCDORevision source, EReference sourceEReference, TeneoAuditEntry auditEntry, EReference targetEReference)
  {
    throw new IllegalStateException("Error case: The system should not use this method when doing auditing in CDO");
  }

  protected void convertFeatureMap(Session session, InternalCDORevision source, EStructuralFeature sourceEFeature, TeneoAuditEntry auditEntry,
      EStructuralFeature targetEFeature)
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

    CDOID cdoID;
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

    // get the correct id
    if (HibernateThreadContext.isCommitContextSet())
    {
      final CommitContext commitContext = HibernateThreadContext.getCommitContext().getCommitContext();
      CDOID newID = cdoID;
      int cnt = 0;
      while (commitContext.getIDMappings().containsKey(newID))
      {
        newID = commitContext.getIDMappings().get(newID);
        cnt++;
        if (cnt > 1000)
        {
          throw new IllegalStateException("Cycle detected in id mappings " + newID + " maps to " + commitContext.getIDMappings().get(newID));
        }
      }
      cdoID = newID;
    }

    return HibernateUtil.getInstance().convertCDOIDToString(cdoID);
  }

  @Override
  public String idToString(EClass eClass, Object id)
  {
    return HibernateUtil.getInstance().convertCDOIDToString((CDOID)id);
  }

  public Object convertValue(EStructuralFeature sourceEFeature, EStructuralFeature eFeature, Object value)
  {
    if (value == CDORevisionData.NIL)
    {
      return null;
    }
    if (value instanceof EEnumLiteral)
    {
      return ((EEnumLiteral)value).getLiteral();
    }
    if (value instanceof Enumerator)
    {
      return ((Enumerator)value).getLiteral();
    }

    if (sourceEFeature.getEType() instanceof EEnum && value instanceof Integer)
    {
      final int ordinal = (Integer)value;
      final EEnum eeNum = (EEnum)sourceEFeature.getEType();
      if (eeNum.getInstanceClass() != null && eeNum.getInstanceClass().isEnum())
      {
        final Object[] constants = eeNum.getInstanceClass().getEnumConstants();
        for (Object constant : constants)
        {
          if (constant instanceof Enumerator)
          {
            final Enumerator enumerator = (Enumerator)constant;
            if (enumerator.getValue() == ordinal)
            {
              return enumerator.getLiteral();
            }
          }
        }
        return ((Enum<?>)constants[ordinal]).name();
      }
      return eeNum.getEEnumLiteral((Integer)value).getLiteral();
    }

    return super.convertValue(eFeature, value);
  }

  // map all enums as string
  @Override
  protected EStructuralFeature createEAttribute(EAttribute eAttribute)
  {
    final EStructuralFeature eFeature = super.createEAttribute(eAttribute);
    if (eFeature.getEType() instanceof EEnum)
    {
      eFeature.setEType(EcorePackage.eINSTANCE.getEString());
      // add a dummy teneo.jpa to prevent anyone of accidentally mapping
      // enumerated in a different way
      // set in the source efeature will be found there
      if (eAttribute.getEAnnotation(Constants.ANNOTATION_SOURCE_TENEO_JPA_AUDITING) == null)
      {
        final EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
        eAnnotation.setSource(Constants.ANNOTATION_SOURCE_TENEO_JPA_AUDITING);
        eAnnotation.getDetails().put(Constants.ANNOTATION_KEY_VALUE, "");
        eAttribute.getEAnnotations().add(eAnnotation);
      }
      return eFeature;
    }

    if (isCustomType(eAttribute.getEAttributeType()))
    {
      if (!isTeneoAnnotated(eAttribute))
      {
        eFeature.setEType(EcorePackage.eINSTANCE.getEString());
      }
    }

    return eFeature;
  }

  private boolean isTeneoAnnotated(EAttribute eAttribute)
  {
    return eAttribute.getEAnnotation(Constants.ANNOTATION_SOURCE_TENEO_JPA) != null || eAttribute.getEAnnotation(Constants.ANNOTATION_SOURCE_TENEO_JPA) != null
        || eAttribute.getEAttributeType().getEAnnotation(Constants.ANNOTATION_SOURCE_TENEO_JPA_AUDITING) != null
        || eAttribute.getEAttributeType().getEAnnotation(Constants.ANNOTATION_SOURCE_TENEO_JPA) != null;
  }

}
