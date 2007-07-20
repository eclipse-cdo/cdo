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

import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

/**
 * @author Eike Stepper
 */
public final class CDOStore implements EStore
{
  // @Singleton
  public static final CDOStore INSTANCE = new CDOStore();

  private final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOStore.class);

  private CDOStore()
  {
  }

  public InternalEObject getContainer(InternalEObject eObject)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("getContainer({0})", cdoObject);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    CDOID id = revision.getContainerID();
    return (InternalEObject)((CDOViewImpl)cdoObject.cdoView()).getCDOObject_IfPossible(id);
  }

  public int getContainingFeatureID(InternalEObject eObject)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("getContainingFeatureID({0})", cdoObject);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.getContainingFeatureID();
  }

  public void setContainer(InternalEObject eObject, InternalEObject newContainer, int newContainerFeatureID)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("setContainer({0}, {1}, {2})", cdoObject, newContainer, newContainerFeatureID);
    }

    CDOID containerID = (CDOID)((CDOViewImpl)cdoObject.cdoView()).getCDOID_IfPossible(newContainer);

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
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("get({0}, {1}, {2})", cdoObject, cdoFeature, index);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    Object result = revision.get(cdoFeature, index);
    if (cdoFeature.isReference())
    {
      result = ((CDOViewImpl)cdoObject.cdoView()).getCDOObject_IfPossible(result);
    }

    return result;
  }

  public boolean isSet(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
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
    InternalCDOObject cdoObject = getCDOObject(eObject);
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
    InternalCDOObject cdoObject = getCDOObject(eObject);
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
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("contains({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).getCDOID_IfPossible(value);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.contains(cdoFeature, value);
  }

  public int indexOf(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("indexOf({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).getCDOID_IfPossible(value);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.indexOf(cdoFeature, value);
  }

  public int lastIndexOf(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("lastIndexOf({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).getCDOID_IfPossible(value);
    }

    CDORevisionImpl revision = getRevisionForReading(cdoObject);
    return revision.lastIndexOf(cdoFeature, value);
  }

  public int hashCode(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
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
    InternalCDOObject cdoObject = getCDOObject(eObject);
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
        result[i] = ((CDOViewImpl)cdoObject.cdoView()).getCDOObject_IfPossible(result[i]);
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
    InternalCDOObject cdoObject = getCDOObject(eObject);
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

      value = ((CDOViewImpl)cdoObject.cdoView()).getCDOID_IfPossible(value);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    Object result = revision.set(cdoFeature, index, value);
    if (cdoFeature.isReference())
    {
      result = ((CDOViewImpl)cdoObject.cdoView()).getCDOObject_IfPossible(result);
    }

    return result;
  }

  public void unset(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
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
    InternalCDOObject cdoObject = getCDOObject(eObject);
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

      value = ((CDOViewImpl)cdoObject.cdoView()).getCDOID_IfPossible(value);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    revision.add(cdoFeature, index, value);
  }

  public Object remove(InternalEObject eObject, EStructuralFeature eFeature, int index)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("remove({0}, {1}, {2})", cdoObject, cdoFeature, index);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    Object result = revision.remove(cdoFeature, index);
    if (cdoFeature.isReference())
    {
      result = ((CDOViewImpl)cdoObject.cdoView()).getCDOObject_IfPossible(result);
    }

    return result;
  }

  public void clear(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
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
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeatureImpl cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("move({0}, {1}, {2}, {3})", cdoObject, cdoFeature, target, source);
    }

    CDORevisionImpl revision = getRevisionForWriting(cdoObject);
    Object result = revision.move(cdoFeature, target, source);
    if (cdoFeature.isReference())
    {
      result = ((CDOViewImpl)cdoObject.cdoView()).getCDOObject_IfPossible(result);
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

  public static InternalCDOObject getCDOObject(Object object)
  {
    if (object instanceof InternalCDOObject)
    {
      return (InternalCDOObject)object;
    }

    if (object instanceof InternalEObject)
    {
      return CDOAdapterImpl.getOrCreate((InternalEObject)object);
    }

    throw new ImplementationError("Neither InternalCDOObject nor InternalEObject: " + object.getClass().getName());
  }

  public static CDOFeatureImpl getCDOFeature(InternalCDOObject cdoObject, EStructuralFeature eFeature)
  {
    CDOViewImpl view = (CDOViewImpl)cdoObject.cdoView();
    if (view == null)
    {
      throw new IllegalStateException("view == null");
    }

    CDOSessionPackageManager packageManager = view.getSession().getPackageManager();
    return ModelUtil.getCDOFeature(eFeature, packageManager);
  }

  private static CDORevisionImpl getRevisionForReading(InternalCDOObject cdoObject)
  {
    CDOStateMachine.INSTANCE.read(cdoObject);
    return getRevision(cdoObject);
  }

  private static CDORevisionImpl getRevisionForWriting(InternalCDOObject cdoObject)
  {
    CDOStateMachine.INSTANCE.write(cdoObject);
    return getRevision(cdoObject);
  }

  private static CDORevisionImpl getRevision(InternalCDOObject cdoObject)
  {
    CDORevisionImpl revision = (CDORevisionImpl)cdoObject.cdoRevision();
    if (revision == null)
    {
      throw new IllegalStateException("revision == null");
    }

    return revision;
  }

  private static void handleContainmentAdd(InternalCDOObject cdoObject, CDOFeatureImpl cdoFeature, Object value)
  {
    InternalCDOObject container = cdoObject;
    InternalCDOObject contained = getCDOObject(value);
    CDOViewImpl containerView = (CDOViewImpl)container.cdoView();
    CDOViewImpl containedView = (CDOViewImpl)contained.cdoView();
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
