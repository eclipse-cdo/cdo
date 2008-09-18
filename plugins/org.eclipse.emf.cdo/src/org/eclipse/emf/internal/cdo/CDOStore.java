/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Eike Stepper & Simon McDuff - http://bugs.eclipse.org/204890 
 *    Simon McDuff - http://bugs.eclipse.org/246705
 *    Simon McDuff - http://bugs.eclipse.org/246622
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOClearFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOContainerFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOUnsetFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.GenUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * CDORevision needs to follow these rules:<br>
 * - Keep CDOID only when the object (!isNew && !isTransient) // Only when CDOID will not changed.<br>
 * - Keep EObject for external reference, new, transient and that until commit time.<br>
 * It is important since these objects could changed and we need to keep a reference to {@link EObject} until the end.
 * It is the reason why {@link CDOStore} always call {@link CDOViewImpl#convertObjectToID(Object, boolean)} with true.
 * 
 * @author Eike Stepper
 */
public final class CDOStore implements EStore
{
  private final ContextTracer TRACER = new ContextTracer(OM.DEBUG_STORE, CDOStore.class);

  private CDOViewImpl view;

  // Used for optimization. Multiple call to CDStore will be sent like size and than add.
  private EStructuralFeature lastLookupEFeature;

  private CDOFeature lastLookupCDOFeature;

  private Object lock = new Object();

  public CDOStore(CDOViewImpl view)
  {
    this.view = view;
  }

  public CDOViewImpl getView()
  {
    return view;
  }

  /**
   * @since 2.0
   */
  public void setContainer(InternalEObject eObject, CDOResource newResource, InternalEObject newEContainer,
      int newContainerFeatureID)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("setContainer({0}, {1}, {2}, {3})", cdoObject, newResource, newEContainer, newContainerFeatureID);
    }
    Object newContainerID = newEContainer == null ? CDOID.NULL : ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(
        newEContainer, true);
    CDOID newResourceID = newResource == null ? CDOID.NULL : newResource.cdoID();

    CDOFeatureDelta delta = new CDOContainerFeatureDeltaImpl(newResourceID, newContainerID, newContainerFeatureID);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    revision.setResourceID(newResourceID);
    revision.setContainerID(newContainerID);
    revision.setContainingFeatureID(newContainerFeatureID);
  }

  public InternalEObject getContainer(InternalEObject eObject)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("getContainer({0})", cdoObject);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);

    return (InternalEObject)((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(revision.getContainerID());
  }

  public int getContainingFeatureID(InternalEObject eObject)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("getContainingFeatureID({0})", cdoObject);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.getContainingFeatureID();
  }

  @Deprecated
  public EStructuralFeature getContainingFeature(InternalEObject eObject)
  {
    throw new UnsupportedOperationException("Use getContainingFeatureID() instead");
  }

  public Object get(InternalEObject eObject, EStructuralFeature eFeature, int index)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("get({0}, {1}, {2})", cdoObject, cdoFeature, index);
    }

    view.getFeatureAnalyzer().preTraverseFeature(cdoObject, cdoFeature, index);
    InternalCDORevision revision = getRevisionForReading(cdoObject);
    Object value = revision.get(cdoFeature, index);
    if (cdoFeature.isReference())
    {
      if (value instanceof CDOReferenceProxy)
      {
        value = ((CDOReferenceProxy)value).resolve();
      }

      if (cdoFeature.isMany() && value instanceof CDOID)
      {
        CDOID id = (CDOID)value;
        loadAhead(revision, cdoFeature, id, index);
      }

      value = view.convertIDToObject(value);
    }
    else if (cdoFeature.getType() == CDOType.CUSTOM)
    {
      value = EcoreUtil.createFromString((EDataType)eFeature.getEType(), (String)value);
    }

    view.getFeatureAnalyzer().postTraverseFeature(cdoObject, cdoFeature, index, value);
    return value;
  }

  public boolean isSet(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("isSet({0}, {1})", cdoObject, cdoFeature);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.isSet(cdoFeature);
  }

  public int size(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("size({0}, {1})", cdoObject, cdoFeature);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.size(cdoFeature);
  }

  public boolean isEmpty(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("isEmpty({0}, {1})", cdoObject, cdoFeature);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.isEmpty(cdoFeature);
  }

  public boolean contains(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("contains({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value, true);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.contains(cdoFeature, value);
  }

  public int indexOf(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("indexOf({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value, true);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.indexOf(cdoFeature, value);
  }

  public int lastIndexOf(InternalEObject eObject, EStructuralFeature eFeature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("lastIndexOf({0}, {1}, {2})", cdoObject, cdoFeature, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value, true);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.lastIndexOf(cdoFeature, value);
  }

  public int hashCode(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("hashCode({0}, {1})", cdoObject, cdoFeature);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.hashCode(cdoFeature);
  }

  public Object[] toArray(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("toArray({0}, {1})", cdoObject, cdoFeature);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    Object[] result = revision.toArray(cdoFeature);
    if (cdoFeature.isReference())
    {
      for (int i = 0; i < result.length; i++)
      {
        if (result[i] instanceof CDOReferenceProxy)
        {
          result[i] = ((CDOReferenceProxy)result[i]).resolve();
        }

        result[i] = ((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(result[i]);
      }
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public <T> T[] toArray(InternalEObject eObject, EStructuralFeature eFeature, T[] a)
  {
    Object[] array = toArray(eObject, eFeature);
    int size = array.length;

    if (a.length < size)
    {
      a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    }

    System.arraycopy(array, 0, a, 0, size);
    if (a.length > size)
    {
      a[size] = null;
    }

    return a;
  }

  public Object set(InternalEObject eObject, EStructuralFeature eFeature, int index, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("set({0}, {1}, {2}, {3})", cdoObject, cdoFeature, index, value);
    }

    if (cdoFeature.getType() == CDOType.CUSTOM)
    {
      value = EcoreUtil.convertToString((EDataType)eFeature.getEType(), value);
    }
    else if (value == null && GenUtil.isPrimitiveType(eFeature.getEType()))
    {
      value = eFeature.getDefaultValue();
    }

    CDOFeatureDelta delta = new CDOSetFeatureDeltaImpl(cdoFeature, index, value);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    if (cdoFeature.isReference())
    {
      Object oldValue = revision.get(cdoFeature, index);
      if (oldValue instanceof CDOReferenceProxy)
      {
        ((CDOReferenceProxy)oldValue).resolve();
      }
      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value, true);
    }

    Object oldValue = revision.set(cdoFeature, index, value);
    if (cdoFeature.isReference())
    {
      oldValue = ((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(oldValue);
    }

    return oldValue;
  }

  public void unset(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("unset({0}, {1})", cdoObject, cdoFeature);
    }

    CDOFeatureDelta delta = new CDOUnsetFeatureDeltaImpl(cdoFeature);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    // TODO Handle containment remove!!!
    revision.unset(cdoFeature);
  }

  public void add(InternalEObject eObject, EStructuralFeature eFeature, int index, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("add({0}, {1}, {2}, {3})", cdoObject, cdoFeature, index, value);
    }

    if (cdoFeature.isReference())
    {
      value = ((CDOViewImpl)cdoObject.cdoView()).convertObjectToID(value, true);
    }

    CDOFeatureDelta delta = new CDOAddFeatureDeltaImpl(cdoFeature, index, value);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    revision.add(cdoFeature, index, value);
  }

  public Object remove(InternalEObject eObject, EStructuralFeature eFeature, int index)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("remove({0}, {1}, {2})", cdoObject, cdoFeature, index);
    }

    CDOFeatureDelta delta = new CDORemoveFeatureDeltaImpl(cdoFeature, index);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    Object result = revision.remove(cdoFeature, index);
    if (cdoFeature.isReference())
    {
      if (result instanceof CDOReferenceProxy)
      {
        result = ((CDOReferenceProxy)result).resolve();
      }

      result = ((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(result);
    }

    return result;
  }

  public void clear(InternalEObject eObject, EStructuralFeature eFeature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("clear({0}, {1})", cdoObject, cdoFeature);
    }

    CDOFeatureDelta delta = new CDOClearFeatureDeltaImpl(cdoFeature);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    // TODO Handle containment remove!!!
    revision.clear(cdoFeature);
  }

  public Object move(InternalEObject eObject, EStructuralFeature eFeature, int target, int source)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    CDOFeature cdoFeature = getCDOFeature(cdoObject, eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("move({0}, {1}, {2}, {3})", cdoObject, cdoFeature, target, source);
    }

    CDOFeatureDelta delta = new CDOMoveFeatureDeltaImpl(cdoFeature, target, source);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    Object result = revision.move(cdoFeature, target, source);
    if (cdoFeature.isReference())
    {
      if (result instanceof CDOReferenceProxy)
      {
        result = ((CDOReferenceProxy)result).resolve();
      }

      result = ((CDOViewImpl)cdoObject.cdoView()).convertIDToObject(result);
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
    return MessageFormat.format("CDOStore[{0}]", view);
  }

  private InternalCDOObject getCDOObject(Object object)
  {
    return FSMUtil.adapt(object, view);
  }

  private CDOFeature getCDOFeature(InternalCDOObject cdoObject, EStructuralFeature eFeature)
  {
    synchronized (lock)
    {
      if (eFeature == lastLookupEFeature)
      {
        return lastLookupCDOFeature;
      }
    }

    CDOViewImpl view = (CDOViewImpl)cdoObject.cdoView();
    if (view == null)
    {
      throw new IllegalStateException("view == null");
    }

    CDOSessionPackageManagerImpl packageManager = view.getSession().getPackageManager();
    CDOFeature cdoFeature = ModelUtil.getCDOFeature(eFeature, packageManager);

    synchronized (lock)
    {
      lastLookupEFeature = eFeature;
      lastLookupCDOFeature = cdoFeature;
    }

    return cdoFeature;
  }

  private void loadAhead(InternalCDORevision revision, CDOFeature cdoFeature, CDOID id, int index)
  {
    CDOSessionImpl session = view.getSession();
    CDORevisionManagerImpl revisionManager = session.getRevisionManager();

    int chunkSize = view.getLoadRevisionCollectionChunkSize();
    if (chunkSize > 1 && !revisionManager.containsRevision(id))
    {
      MoveableList<Object> list = revision.getList(cdoFeature);
      int fromIndex = index;
      int toIndex = Math.min(index + chunkSize, list.size()) - 1;

      Set<CDOID> notRegistered = new HashSet<CDOID>();
      for (int i = fromIndex; i <= toIndex; i++)
      {
        Object element = list.get(i);
        if (element instanceof CDOID)
        {
          CDOID idElement = (CDOID)element;
          if (!idElement.isTemporary())
          {
            if (!revisionManager.containsRevision(idElement))
            {
              if (!notRegistered.contains(idElement))
              {
                notRegistered.add(idElement);
              }
            }
          }
        }
      }

      if (!notRegistered.isEmpty())
      {
        int referenceChunk = session.getReferenceChunkSize();
        revisionManager.getRevisions(notRegistered, referenceChunk);
      }
    }
  }

  private static InternalCDORevision getRevisionForReading(InternalCDOObject cdoObject)
  {
    CDOStateMachine.INSTANCE.read(cdoObject);
    return getRevision(cdoObject);
  }

  private static InternalCDORevision getRevisionForWriting(InternalCDOObject cdoObject, CDOFeatureDelta delta)
  {
    CDOStateMachine.INSTANCE.write(cdoObject, delta);
    return getRevision(cdoObject);
  }

  private static InternalCDORevision getRevision(InternalCDOObject cdoObject)
  {
    InternalCDORevision revision = (InternalCDORevision)cdoObject.cdoRevision();
    if (revision == null)
    {
      throw new IllegalStateException("revision == null");
    }

    return revision;
  }
}
