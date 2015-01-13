/*
 * Copyright (c) 2011-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Eike Stepper & Simon McDuff - bug 204890
 *    Simon McDuff - bug 246705
 *    Simon McDuff - bug 246622
 *    Christian W. Damus (CEA) - bug 400236: get internal instance of objects in ID conversion
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
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
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.text.MessageFormat;
import java.util.List;

/**
 * CDORevision needs to follow these rules:<br>
 * - Keep CDOID only when the object (!isNew && !isTransient) // Only when CDOID will not changed.<br>
 * - Keep EObject for external reference, new, transient and that until commit time.<br>
 * It is important since these objects could changed and we need to keep a reference to {@link EObject} until the end.
 * It is the reason why {@link CDOStoreImpl} always call {@link InternalCDOView#convertObjectToID(Object, boolean)} with
 * true.
 *
 * @author Eike Stepper
 */
public final class CDOStoreImpl implements CDOStore
{
  private final ContextTracer TRACER = new ContextTracer(OM.DEBUG_STORE, CDOStoreImpl.class);

  private InternalCDOView view;

  public CDOStoreImpl(InternalCDOView view)
  {
    this.view = view;
  }

  public InternalCDOView getView()
  {
    return view;
  }

  /**
   * @category READ
   */
  public InternalEObject getContainer(InternalEObject eObject)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("getContainer({0})", cdoObject); //$NON-NLS-1$
      }

      InternalCDORevision revision = readRevision(cdoObject);
      return (InternalEObject)convertIDToObject(view, cdoObject, EcorePackage.eINSTANCE.eContainingFeature(), -1,
          revision.getContainerID());
    }
  }

  /**
   * @category READ
   */
  public int getContainingFeatureID(InternalEObject eObject)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("getContainingFeatureID({0})", cdoObject); //$NON-NLS-1$
      }

      InternalCDORevision revision = readRevision(cdoObject);
      return revision.getContainingFeatureID();
    }
  }

  /**
   * @category READ
   */
  public InternalEObject getResource(InternalEObject eObject)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("getResource({0})", cdoObject); //$NON-NLS-1$
      }

      InternalCDORevision revision = readRevision(cdoObject);
      return (InternalEObject)convertIDToObject(view, cdoObject, EcorePackage.eINSTANCE.eContainingFeature(), -1,
          revision.getResourceID());
    }
  }

  /**
   * @category READ
   */
  public EStructuralFeature getContainingFeature(InternalEObject eObject)
  {
    throw new UnsupportedOperationException("Use getContainingFeatureID() instead"); //$NON-NLS-1$
  }

  /**
   * @category READ
   */
  public Object get(InternalEObject eObject, EStructuralFeature feature, int index)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("get({0}, {1}, {2})", cdoObject, feature, index); //$NON-NLS-1$
      }

      CDOFeatureAnalyzer featureAnalyzer = view.options().getFeatureAnalyzer();

      featureAnalyzer.preTraverseFeature(cdoObject, feature, index);
      InternalCDORevision revision = readRevision(cdoObject);

      Object value = revision.get(feature, index);
      value = convertToEMF(eObject, revision, feature, index, value);

      featureAnalyzer.postTraverseFeature(cdoObject, feature, index, value);
      return value;
    }
  }

  /**
   * @category READ
   */
  public boolean isSet(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("isSet({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = readRevision(cdoObject);
      if (feature.isMany())
      {
        CDOList list = revision.getList(feature);
        return list != null && !list.isEmpty();
      }

      Object value = revision.getValue(feature);
      if (feature.isUnsettable())
      {
        return value != null;
      }

      if (value == null)
      {
        return false;
      }

      value = convertToEMF(eObject, revision, feature, NO_INDEX, value);
      Object defaultValue = feature.getDefaultValue();
      return !ObjectUtil.equals(value, defaultValue);
    }
  }

  /**
   * @category READ
   */
  public int size(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("size({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = readRevision(cdoObject);
      return revision.size(feature);
    }
  }

  /**
   * @category READ
   */
  public boolean isEmpty(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("isEmpty({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = readRevision(cdoObject);
      return revision.isEmpty(feature);
    }
  }

  /**
   * @category READ
   */
  public boolean contains(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("contains({0}, {1}, {2})", cdoObject, feature, value); //$NON-NLS-1$
      }

      Object convertedValue = convertToCDO(cdoObject, feature, value);

      InternalCDORevision revision = readRevision(cdoObject);
      boolean result = revision.contains(feature, convertedValue);

      // Special handling of detached (TRANSIENT) objects, see bug 354395
      if (!result && value != convertedValue && value instanceof EObject)
      {
        result = revision.contains(feature, value);
      }

      return result;
    }
  }

  /**
   * @category READ
   */
  public int indexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("indexOf({0}, {1}, {2})", cdoObject, feature, value); //$NON-NLS-1$
      }

      value = convertToCDO(cdoObject, feature, value);

      InternalCDORevision revision = readRevision(cdoObject);
      return revision.indexOf(feature, value);
    }
  }

  /**
   * @category READ
   */
  public int lastIndexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("lastIndexOf({0}, {1}, {2})", cdoObject, feature, value); //$NON-NLS-1$
      }

      value = convertToCDO(cdoObject, feature, value);

      InternalCDORevision revision = readRevision(cdoObject);
      return revision.lastIndexOf(feature, value);
    }
  }

  /**
   * @category READ
   */
  public int hashCode(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("hashCode({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = readRevision(cdoObject);
      return revision.hashCode(feature);
    }
  }

  /**
   * @category READ
   */
  public Object[] toArray(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("toArray({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = readRevision(cdoObject);
      Object[] result = revision.toArray(feature);
      for (int i = 0; i < result.length; i++)
      {
        result[i] = convertToEMF(eObject, revision, feature, i, result[i]);
      }

      // // TODO Clarify feature maps
      // if (feature instanceof EReference)
      // {
      // for (int i = 0; i < result.length; i++)
      // {
      // result[i] = resolveProxy(revision, feature, i, result[i]);
      // result[i] = convertIdToObject(cdoObject.cdoView(), eObject, feature, i, result[i]);
      // }
      // }

      return result;
    }
  }

  /**
   * @category READ
   */
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(InternalEObject eObject, EStructuralFeature feature, T[] a)
  {
    synchronized (view)
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
  }

  /**
   * @category WRITE
   */
  public void setContainer(InternalEObject eObject, CDOResource newResource, InternalEObject newEContainer,
      int newContainerFeatureID)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("setContainer({0}, {1}, {2}, {3})", cdoObject, newResource, newEContainer, newContainerFeatureID); //$NON-NLS-1$
      }

      Object newContainerID = newEContainer == null ? CDOID.NULL : view.convertObjectToID(newEContainer, true);
      CDOID newResourceID = newResource == null ? CDOID.NULL : newResource.cdoID();

      CDOFeatureDelta delta = new CDOContainerFeatureDeltaImpl(newResourceID, newContainerID, newContainerFeatureID);
      writeRevision(cdoObject, delta);
    }
  }

  /**
   * @category WRITE RESULT
   */
  public Object set(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("set({0}, {1}, {2}, {3})", cdoObject, feature, index, value); //$NON-NLS-1$
      }

      value = convertToCDO(cdoObject, feature, value);

      // TODO: Use writeRevision() result!!
      InternalCDORevision oldRevision = readRevision(cdoObject);
      Object oldValue = oldRevision.get(feature, index);
      oldValue = convertToEMF(eObject, oldRevision, feature, index, oldValue);

      CDOFeatureDelta delta = new CDOSetFeatureDeltaImpl(feature, index, value, oldValue);
      writeRevision(cdoObject, delta);

      return oldValue;
    }
  }

  /**
   * @category WRITE
   */
  public void unset(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("unset({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      if (feature.isMany())
      {
        Object object = cdoObject.eGet(feature);
        if (object instanceof List<?> && !CDOUtil.isLegacyObject(cdoObject))
        {
          List<?> list = (List<?>)object;
          list.clear();
        }
      }
      else
      {
        CDOFeatureDelta delta = new CDOUnsetFeatureDeltaImpl(feature);
        writeRevision(cdoObject, delta);
      }
    }
  }

  /**
   * @category WRITE
   */
  public void add(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("add({0}, {1}, {2}, {3})", cdoObject, feature, index, value); //$NON-NLS-1$
      }

      value = convertToCDO(cdoObject, feature, value);

      CDOFeatureDelta delta = new CDOAddFeatureDeltaImpl(feature, index, value);
      writeRevision(cdoObject, delta);
    }
  }

  /**
   * @category WRITE RESULT
   */
  public Object remove(InternalEObject eObject, EStructuralFeature feature, int index)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("remove({0}, {1}, {2})", cdoObject, feature, index); //$NON-NLS-1$
      }

      Object oldValue = getOldListValue(eObject, cdoObject, feature, index);

      removeElement(cdoObject, feature, index);
      return oldValue;
    }
  }

  /**
   * @category WRITE RESULT
   */
  public Object move(InternalEObject eObject, EStructuralFeature feature, int target, int source)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("move({0}, {1}, {2}, {3})", cdoObject, feature, target, source); //$NON-NLS-1$
      }

      Object oldValue = getOldListValue(eObject, cdoObject, feature, source);

      CDOFeatureDelta delta = new CDOMoveFeatureDeltaImpl(feature, target, source);
      writeRevision(cdoObject, delta);

      return oldValue;
    }
  }

  /**
   * @category WRITE
   */
  public void clear(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("clear({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      CDOFeatureDelta delta = new CDOClearFeatureDeltaImpl(feature);
      writeRevision(cdoObject, delta);
    }
  }

  public EObject create(EClass eClass)
  {
    throw new UnsupportedOperationException("Use the generated factory to create objects"); //$NON-NLS-1$
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOStore[{0}]", view); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  public Object resolveProxy(InternalCDORevision revision, EStructuralFeature feature, int index, Object value)
  {
    synchronized (view)
    {
      if (value instanceof CDOElementProxy)
      {
        // Resolve proxy
        CDOElementProxy proxy = (CDOElementProxy)value;
        value = view.getSession().resolveElementProxy(revision, feature, index, proxy.getIndex());
      }

      return value;
    }
  }

  /**
   * @since 3.0
   */
  public Object convertToCDO(InternalCDOObject object, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      if (value != null)
      {
        if (feature instanceof EReference)
        {
          value = view.convertObjectToID(value, true);
        }
        else if (FeatureMapUtil.isFeatureMap(feature))
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)value;
          EStructuralFeature innerFeature = entry.getEStructuralFeature();
          Object innerValue = entry.getValue();
          Object convertedValue = view.convertObjectToID(innerValue);
          if (convertedValue != innerValue)
          {
            value = CDORevisionUtil.createFeatureMapEntry(innerFeature, convertedValue);
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature.getEType());
          if (type != null)
          {
            value = type.convertToCDO(feature.getEType(), value);
          }
        }
      }

      return value;
    }
  }

  /**
   * @since 2.0
   */
  public Object convertToEMF(EObject eObject, InternalCDORevision revision, EStructuralFeature feature, int index,
      Object value)
  {
    synchronized (view)
    {
      if (value != null)
      {
        if (feature.isMany())
        {
          if (index == EStore.NO_INDEX)
          {
            return value;
          }

          value = resolveProxy(revision, feature, index, value);
          if (value instanceof CDOID)
          {
            CDOID id = (CDOID)value;
            CDOList list = revision.getList(feature);
            CDORevisionPrefetchingPolicy policy = view.options().getRevisionPrefetchingPolicy();
            InternalCDORevisionManager revisionManager = view.getSession().getRevisionManager();
            List<CDOID> listOfIDs = policy.loadAhead(revisionManager, view, eObject, feature, list, index, id);
            if (!listOfIDs.isEmpty())
            {
              int initialChunkSize = view.getSession().options().getCollectionLoadingPolicy().getInitialChunkSize();
              revisionManager.getRevisions(listOfIDs, view, initialChunkSize, CDORevision.DEPTH_NONE, true);
            }
          }
        }

        if (feature instanceof EReference)
        {
          value = convertIDToObject(view, eObject, feature, index, value);
        }
        else if (FeatureMapUtil.isFeatureMap(feature))
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)value;
          EStructuralFeature innerFeature = entry.getEStructuralFeature();
          Object innerValue = entry.getValue();
          Object convertedValue = convertIDToObject(view, eObject, feature, index, innerValue);
          if (convertedValue != innerValue)
          {
            value = FeatureMapUtil.createEntry(innerFeature, convertedValue);
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature.getEType());
          if (type != null)
          {
            value = type.convertToEMF(feature.getEType(), value);
          }
        }
      }

      return value;
    }
  }

  private Object convertIDToObject(InternalCDOView view, EObject eObject, EStructuralFeature feature, int index,
      Object value)
  {
    try
    {
      value = view.convertIDToObject(value);
    }
    catch (ObjectNotFoundException ex)
    {
      if (value instanceof CDOID)
      {
        CDOStaleReferencePolicy staleReferencePolicy = view.options().getStaleReferencePolicy();
        value = staleReferencePolicy.processStaleReference(eObject, feature, index, ex.getID());
      }
    }

    return getInternalInstance(value);
  }

  private InternalCDOObject getCDOObject(Object object)
  {
    return FSMUtil.adapt(object, view);
  }

  private Object getInternalInstance(Object object)
  {
    if (object instanceof InternalCDOObject)
    {
      return ((InternalCDOObject)object).cdoInternalInstance();
    }

    return object;
  }

  private Object getOldListValue(InternalEObject eObject, InternalCDOObject cdoObject, EStructuralFeature feature,
      int index)
  {
    if (!feature.isMany())
    {
      throw new UnsupportedOperationException("Not supported for single-valued features");
    }

    // Bug 293283 / Bug 314387
    InternalCDORevision revision = readRevision(cdoObject);
    CDOList list = revision.getList(feature);
    int size = list.size();
    if (index < 0 || size <= index)
    {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    Object oldValue = revision.get(feature, index);
    oldValue = convertToEMF(eObject, revision, feature, index, oldValue);
    return oldValue;
  }

  private static InternalCDORevision readRevision(InternalCDOObject cdoObject)
  {
    InternalCDORevision revision = CDOStateMachine.INSTANCE.read(cdoObject);
    if (revision == null)
    {
      throw new IllegalStateException("revision == null");
    }

    return revision;
  }

  private static Object writeRevision(InternalCDOObject cdoObject, CDOFeatureDelta delta)
  {
    return CDOStateMachine.INSTANCE.write(cdoObject, delta);
  }

  public static void removeElement(InternalCDOObject cdoObject, EStructuralFeature feature, int index)
  {
    CDOFeatureDelta delta = new CDORemoveFeatureDeltaImpl(feature, index);
    writeRevision(cdoObject, delta);
  }
}
