/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOClearFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOContainerFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOUnsetFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.session.CDORevisionManagerImpl;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.CDOElementProxy;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * CDORevision needs to follow these rules:<br>
 * - Keep CDOID only when the object (!isNew && !isTransient) // Only when CDOID will not changed.<br>
 * - Keep EObject for external reference, new, transient and that until commit time.<br>
 * It is important since these objects could changed and we need to keep a reference to {@link EObject} until the end.
 * It is the reason why {@link CDOStore} always call {@link InternalCDOView#convertObjectToID(Object, boolean)} with
 * true.
 * 
 * @author Eike Stepper
 */
public final class CDOStore implements EStore
{
  private final ContextTracer TRACER = new ContextTracer(OM.DEBUG_STORE, CDOStore.class);

  private InternalCDOView view;

  // // Used for optimization. Multiple call to CDStore will be sent like size and than add.
  // private EStructuralFeature lastLookupEFeature;
  //
  // private EStructuralFeature lastLookupEStructuralFeature;
  //
  // private Object lock = new Object();

  /**
   * @since 2.0
   */
  public CDOStore(InternalCDOView view)
  {
    this.view = view;
  }

  /**
   * @since 2.0
   */
  public InternalCDOView getView()
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

    Object newContainerID = newEContainer == null ? CDOID.NULL : cdoObject.cdoView().convertObjectToID(newEContainer,
        true);
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
    return (InternalEObject)cdoObject.cdoView().convertIDToObject(revision.getContainerID());
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

  /**
   * @since 2.0
   */
  public InternalEObject getResource(InternalEObject eObject)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("getResource({0})", cdoObject);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return (InternalEObject)cdoObject.cdoView().convertIDToObject(revision.getResourceID());
  }

  @Deprecated
  public EStructuralFeature getContainingFeature(InternalEObject eObject)
  {
    throw new UnsupportedOperationException("Use getContainingFeatureID() instead");
  }

  public Object get(InternalEObject eObject, EStructuralFeature feature, int index)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("get({0}, {1}, {2})", cdoObject, feature, index);
    }

    view.getFeatureAnalyzer().preTraverseFeature(cdoObject, feature, index);
    InternalCDORevision revision = getRevisionForReading(cdoObject);

    Object value = revision.basicGet(feature, index);
    value = convertToEMF(view, eObject, revision, feature, index, value);

    view.getFeatureAnalyzer().postTraverseFeature(cdoObject, feature, index, value);
    return value;
  }

  @Deprecated
  public boolean isSet(InternalEObject eObject, EStructuralFeature feature)
  {
    // Should not be called
    throw new ImplementationError();
  }

  public int size(InternalEObject eObject, EStructuralFeature feature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("size({0}, {1})", cdoObject, feature);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.size(feature);
  }

  public boolean isEmpty(InternalEObject eObject, EStructuralFeature feature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("isEmpty({0}, {1})", cdoObject, feature);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.isEmpty(feature);
  }

  public boolean contains(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("contains({0}, {1}, {2})", cdoObject, feature, value);
    }

    if (feature instanceof EReference)
    {
      value = cdoObject.cdoView().convertObjectToID(value, true);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.contains(feature, value);
  }

  public int indexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("indexOf({0}, {1}, {2})", cdoObject, feature, value);
    }

    if (feature instanceof EReference)
    {
      value = cdoObject.cdoView().convertObjectToID(value, true);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.indexOf(feature, value);
  }

  public int lastIndexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("lastIndexOf({0}, {1}, {2})", cdoObject, feature, value);
    }

    if (feature instanceof EReference)
    {
      value = cdoObject.cdoView().convertObjectToID(value, true);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.lastIndexOf(feature, value);
  }

  public int hashCode(InternalEObject eObject, EStructuralFeature feature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("hashCode({0}, {1})", cdoObject, feature);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    return revision.hashCode(feature);
  }

  public Object[] toArray(InternalEObject eObject, EStructuralFeature feature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("toArray({0}, {1})", cdoObject, feature);
    }

    InternalCDORevision revision = getRevisionForReading(cdoObject);
    Object[] result = revision.toArray(feature);
    if (feature instanceof EReference)
    {
      for (int i = 0; i < result.length; i++)
      {
        result[i] = resolveProxy(revision, feature, i, result[i]);
        result[i] = cdoObject.cdoView().convertIDToObject(result[i]);
      }
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public <T> T[] toArray(InternalEObject eObject, EStructuralFeature feature, T[] a)
  {
    Object[] array = toArray(eObject, feature);
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

  public Object set(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("set({0}, {1}, {2}, {3})", cdoObject, feature, index, value);
    }

    value = convertToCDO(getView(), feature, value);

    CDOFeatureDelta delta = new CDOSetFeatureDeltaImpl(feature, index, value);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    if (feature instanceof EReference)
    {
      Object oldValue = revision.basicGet(feature, index);
      oldValue = resolveProxy(revision, feature, index, oldValue);
      value = cdoObject.cdoView().convertObjectToID(value, true);
    }

    Object oldValue = revision.basicSet(feature, index, value);
    oldValue = convertToEMF(cdoObject.cdoView(), eObject, revision, feature, index, oldValue);
    return oldValue;
  }

  /**
   * @since 2.0
   */
  public Object convertToEMF(InternalCDOView view, EObject eObject, InternalCDORevision revision,
      EStructuralFeature feature, int index, Object value)
  {
    if (value != null)
    {
      if (value == InternalCDORevision.NIL)
      {
        return EStoreEObjectImpl.NIL;
      }

      if (feature.isMany() && EStore.NO_INDEX != index)
      {
        value = resolveProxy(revision, feature, index, value);
        if (feature.isMany() && value instanceof CDOID)
        {
          CDOID id = (CDOID)value;
          CDOList list = revision.getList(feature);
          CDORevisionManagerImpl revisionManager = (CDORevisionManagerImpl)view.getSession().getRevisionManager();
          CDORevisionPrefetchingPolicy policy = view.options().getRevisionPrefetchingPolicy();
          Collection<CDOID> listOfIDs = policy.loadAhead(revisionManager, eObject, feature, list, index, id);
          if (!listOfIDs.isEmpty())
          {
            revisionManager.getRevisions(listOfIDs, view.getSession().options().getCollectionLoadingPolicy()
                .getInitialChunkSize());
          }
        }
      }

      if (feature instanceof EReference)
      {
        value = view.convertIDToObject(value);
      }
      else if (feature.getEType() instanceof EDataType)
      {
        EDataType eType = (EDataType)feature.getEType();
        CDOType type = CDOModelUtil.getCoreType(eType);
        if (type == null)
        {
          value = EcoreUtil.createFromString(eType, (String)value);
        }
      }
      // TODO Handle feature maps! else if (feature.getType() == CDOType.FEATURE_MAP_ENTRY)
      // {
      // EStructuralFeatureMapEntryDataTypeImpl entry = (EStructuralFeatureMapEntryDataTypeImpl)value;
      // EStructuralFeature feature = (EStructuralFeature)view.getResourceSet().getEObject(
      // URI.createURI(entry.getURI()), true);
      // Object object = view.convertIDToObject(entry.getObject());
      // value = FeatureMapUtil.createEntry(feature, object);
      // }
    }

    return value;
  }

  /**
   * @since 2.0
   */
  public Object convertToCDO(InternalCDOView view, EStructuralFeature feature, Object value)
  {
    if (value != null)
    {
      if (value == EStoreEObjectImpl.NIL)
      {
        value = InternalCDORevision.NIL;
      }
      else if (feature instanceof EReference)
      {
        value = view.convertObjectToID(value, true);
      }
      // TODO Handle feature maps! else if (feature.getType() == CDOType.FEATURE_MAP_ENTRY)
      // {
      // FeatureMap.Entry entry = (FeatureMap.Entry)value;
      // String uri = EcoreUtil.getURI(entry.getEStructuralFeature()).toString();
      // value = CDORevisionUtil.createFeatureMapEntry(uri, entry.getValue());
      // }
      else if (feature.getEType() instanceof EDataType)
      {
        EDataType eType = (EDataType)feature.getEType();
        CDOType type = CDOModelUtil.getCoreType(eType);
        if (type == null)
        {
          value = EcoreUtil.convertToString(eType, value);
        }
      }
    }

    return value;
  }

  public void unset(InternalEObject eObject, EStructuralFeature feature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("unset({0}, {1})", cdoObject, feature);
    }

    CDOFeatureDelta delta = new CDOUnsetFeatureDeltaImpl(feature);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);

    // TODO Handle containment remove!!!
    revision.set(feature, 0, null);
  }

  public void add(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("add({0}, {1}, {2}, {3})", cdoObject, feature, index, value);
    }

    value = convertToCDO(cdoObject.cdoView(), feature, value);

    CDOFeatureDelta delta = new CDOAddFeatureDeltaImpl(feature, index, value);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    revision.add(feature, index, value);
  }

  public Object remove(InternalEObject eObject, EStructuralFeature feature, int index)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("remove({0}, {1}, {2})", cdoObject, feature, index);
    }

    CDOFeatureDelta delta = new CDORemoveFeatureDeltaImpl(feature, index);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    Object result = revision.remove(feature, index);

    result = convertToEMF(cdoObject.cdoView(), eObject, revision, feature, index, result);

    return result;
  }

  public void clear(InternalEObject eObject, EStructuralFeature feature)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("clear({0}, {1})", cdoObject, feature);
    }

    CDOFeatureDelta delta = new CDOClearFeatureDeltaImpl(feature);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    // TODO Handle containment remove!!!
    revision.clear(feature);
  }

  public Object move(InternalEObject eObject, EStructuralFeature feature, int target, int source)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject);
    if (TRACER.isEnabled())
    {
      TRACER.format("move({0}, {1}, {2}, {3})", cdoObject, feature, target, source);
    }

    CDOFeatureDelta delta = new CDOMoveFeatureDeltaImpl(feature, target, source);
    InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
    Object result = revision.move(feature, target, source);

    result = convertToEMF(cdoObject.cdoView(), eObject, revision, feature, EStore.NO_INDEX, result);
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

  /**
   * @since 2.0
   */
  public Object resolveProxy(InternalCDORevision revision, EStructuralFeature feature, int index, Object value)
  {
    if (value instanceof CDOElementProxy)
    {
      value = ((CDOElementProxy)value).resolve(getView().getSession().getRevisionManager(), revision, feature, index);
    }

    return value;
  }

  // private EStructuralFeature getEStructuralFeature(InternalCDOObject cdoObject, EStructuralFeature feature)
  // {
  // synchronized (lock)
  // {
  // if (feature == lastLookupEFeature)
  // {
  // return lastLookupEStructuralFeature;
  // }
  // }
  //
  // InternalCDOView view = cdoObject.cdoView();
  // if (view == null)
  // {
  // throw new IllegalStateException("view == null");
  // }
  //
  // CDOSessionPackageManagerImpl packageManager = (CDOSessionPackageManagerImpl)view.getSession().getPackageManager();
  // EStructuralFeature feature = packageManager.getEStructuralFeature(feature);
  //
  // synchronized (lock)
  // {
  // lastLookupEFeature = feature;
  // lastLookupEStructuralFeature = feature;
  // }
  //
  // return feature;
  // }

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
    InternalCDORevision revision = cdoObject.cdoRevision();
    if (revision == null)
    {
      throw new IllegalStateException("revision == null");
    }

    return revision;
  }
}
