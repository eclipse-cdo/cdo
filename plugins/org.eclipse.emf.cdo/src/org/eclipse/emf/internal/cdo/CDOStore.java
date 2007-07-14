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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.internal.cdo.bundle.CDO;
import org.eclipse.emf.internal.cdo.util.EMFUtil;

/**
 * @author Eike Stepper
 */
public final class CDOStore implements EStore
{
  // @Singleton
  public static final CDOStore INSTANCE = new CDOStore();

  private static final ContextTracer TRACER = new ContextTracer(CDO.DEBUG_OBJECT, CDOStore.class);

  private CDOStore()
  {
  }

  public InternalEObject getContainer(InternalEObject eObject)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    if (TRACER.isEnabled())
    {
      TRACER.format("getContainer({0})", cdoObject);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    CDOID id = revision.getContainerID();
    return (InternalEObject)convertToObject(cdoObject.cdoView(), id);
  }

  public int getContainingFeatureID(InternalEObject eObject)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    if (TRACER.isEnabled())
    {
      TRACER.format("getContainingFeatureID({0})", cdoObject);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.getContainingFeatureID();
  }

  public void setContainer(InternalEObject eObject, InternalEObject newContainer, int newContainerFeatureID)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    if (TRACER.isEnabled())
    {
      TRACER.format("setContainer({0}, {1}, {2})", cdoObject, newContainer, newContainerFeatureID);
    }

    CDOID containerID = (CDOID)convertToID(cdoObject.cdoView(), newContainer);

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    revision.setContainerID(containerID);
    revision.setContainingFeature(newContainerFeatureID);
  }

  public EStructuralFeature getContainingFeature(InternalEObject eObject)
  {
    throw new UnsupportedOperationException("Use getContainingFeatureID() instead");
  }

  public Object get(InternalEObject eObject, EStructuralFeature eFeature, int index)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("get({0}, {1}, {2})", cdoObject, cdoFeature, index);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    Object result = revision.get(cdoFeature, index);
    if (cdoFeature.isReference())
    {
      result = convertToObject(cdoObject.cdoView(), result);
    }

    return result;
  }

  public boolean isSet(InternalEObject eObject, EStructuralFeature eFeature)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("isSet({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.isSet(cdoFeature);
  }

  public int size(InternalEObject eObject, EStructuralFeature eFeature)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("size({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.size(cdoFeature);
  }

  public boolean isEmpty(InternalEObject eObject, EStructuralFeature eFeature)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("isEmpty({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.isEmpty(cdoFeature);
  }

  public boolean contains(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("contains({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = convertToID(cdoObject.cdoView(), value);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.contains(cdoFeature, value);
  }

  public int indexOf(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("indexOf({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = convertToID(cdoObject.cdoView(), value);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.indexOf(cdoFeature, value);
  }

  public int lastIndexOf(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("lastIndexOf({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = convertToID(cdoObject.cdoView(), value);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.lastIndexOf(cdoFeature, value);
  }

  public int hashCode(InternalEObject eObject, EStructuralFeature eFeature)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("hashCode({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.hashCode(cdoFeature);
  }

  public Object[] toArray(InternalEObject eObject, EStructuralFeature eFeature)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("toArray({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    Object[] result = revision.toArray(cdoFeature);
    if (cdoFeature.isReference())
    {
      for (int i = 0; i < result.length; i++)
      {
        result[i] = convertToObject(cdoObject.cdoView(), result[i]);
      }
    }

    return result;
  }

  public <T> T[] toArray(InternalEObject eObject, EStructuralFeature eFeature, T[] array)
  {
    // TODO Implement method CDOStore.toArray()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public Object set(InternalEObject eObject, EStructuralFeature eFeature, int index, Object value)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("set({0}, {1}, {2}, {3})", cdoObject, cdoFeature, index, value);
    }

    if (cdoFeature.isReference())
    {
      if (cdoFeature.isContainment())
      {
        handleContainmentAdd(cdoObject, cdoFeature, value);
      }

      value = convertToID(cdoObject.cdoView(), value);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    Object result = revision.set(cdoFeature, index, value);
    if (cdoFeature.isReference())
    {
      result = convertToObject(cdoObject.cdoView(), result);
    }

    return result;
  }

  public void unset(InternalEObject eObject, EStructuralFeature eFeature)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("unset({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    revision.unset(cdoFeature);
  }

  public void add(InternalEObject eObject, EStructuralFeature eFeature, int index, Object value)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("add({0}, {1}, {2}, {3})", cdoObject, cdoFeature, index, value);
    }

    if (cdoFeature.isReference())
    {
      if (cdoFeature.isContainment())
      {
        handleContainmentAdd(cdoObject, cdoFeature, value);
      }

      value = convertToID(cdoObject.cdoView(), value);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    revision.add(cdoFeature, index, value);
  }

  public Object remove(InternalEObject eObject, EStructuralFeature eFeature, int index)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("remove({0}, {1}, {2})", cdoObject, cdoFeature, index);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    Object result = revision.remove(cdoFeature, index);
    if (cdoFeature.isReference())
    {
      result = convertToObject(cdoObject.cdoView(), result);
    }

    return result;
  }

  public void clear(InternalEObject eObject, EStructuralFeature eFeature)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("clear({0}, {1})", cdoObject, cdoFeature);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    revision.clear(cdoFeature);
  }

  public Object move(InternalEObject eObject, EStructuralFeature eFeature, int target, int source)
  {
    CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("move({0}, {1}, {2}, {3})", cdoObject, cdoFeature, target, source);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    Object result = revision.move(cdoFeature, target, source);
    if (cdoFeature.isReference())
    {
      result = convertToObject(cdoObject.cdoView(), result);
    }

    return result;
  }

  public EObject create(EClass eClass)
  {
    throw new UnsupportedOperationException("Use the generated factory to create objects");
  }

  @Override
  public String toString()
  {
    return "PersistentStore";
  }

  public static Object convertToID(CDOView adapter, Object potentialObject)
  {
    if (potentialObject instanceof CDOObject)
    {
      CDOObject object = (CDOObject)potentialObject;
      if (object.cdoView() == adapter)
      {
        return object.cdoID();
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Dangling reference: {0}", potentialObject);
    }

    return potentialObject;
  }

  /**
   * TODO Move to adapter
   */
  public static Object convertToObject(CDOView adapter, Object potentialID)
  {
    if (potentialID instanceof CDOID)
    {
      if (potentialID == CDOID.NULL)
      {
        return null;
      }

      CDOID id = (CDOID)potentialID;
      CDOObject result = ((CDOViewImpl)adapter).lookupObject(id);
      if (result == null)
      {
        throw new ImplementationError("ID not registered: " + id);
      }

      return result;
    }

    return potentialID;
  }

  private static CDOFeatureImpl getCDOFeature(CDOObjectImpl cdoObject, EStructuralFeature eFeature)
  {
    CDOViewImpl view = cdoObject.cdoView();
    if (view == null)
    {
      throw new IllegalStateException("view == null");
    }

    CDOPackageManagerImpl packageManager = view.getSession().getPackageManager();
    return EMFUtil.getCDOFeature(eFeature, packageManager);
  }

  private static CDORevisionImpl getRevisionForReading(CDOObjectImpl cdoObject)
  {
    CDOStateMachine.INSTANCE.read(cdoObject);
    return getRevision(cdoObject);
  }

  private static CDORevisionImpl getRevisionForWriting(CDOObjectImpl cdoObject)
  {
    CDOStateMachine.INSTANCE.write(cdoObject);
    return getRevision(cdoObject);
  }

  private static CDORevisionImpl getRevision(CDOObjectImpl cdoObject)
  {
    CDORevisionImpl revision = cdoObject.cdoRevision();
    if (revision == null)
    {
      throw new IllegalStateException("revision == null");
    }

    return revision;
  }

  private static void handleContainmentAdd(CDOObjectImpl cdoObject, CDOFeatureImpl cdoFeature, Object value)
  {
    CDOObjectImpl container = cdoObject;
    CDOObjectImpl contained = (CDOObjectImpl)value;
    CDOViewImpl containerView = container.cdoView();
    CDOViewImpl containedView = contained.cdoView();
    if (containedView != containerView)
    {
      if (containedView != null)
      {
        CDOStateMachine.INSTANCE.detach(contained, contained.cdoResource(), containedView);
      }

      CDOStateMachine.INSTANCE.attach(contained, container.cdoResource(), containerView);
    }
  }
}
