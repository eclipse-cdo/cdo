/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.DelegatingEcoreEList;
import org.eclipse.emf.ecore.util.DelegatingFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Eike Stepper
 */
public class CDOObjectImpl extends EStoreEObjectImpl implements InternalCDOObject
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOObjectImpl.class);

  private CDOID id;

  private CDOState state;

  private CDOResourceImpl resource;

  private CDORevisionImpl revision;

  public CDOObjectImpl()
  {
    state = CDOState.TRANSIENT;
    eContainer = null;
  }

  public CDOID cdoID()
  {
    return id;
  }

  public CDOState cdoState()
  {
    return state;
  }

  public CDORevisionImpl cdoRevision()
  {
    return revision;
  }

  public CDOClassImpl cdoClass()
  {
    return getCDOClass(this);
  }

  public CDOViewImpl cdoView()
  {
    return getCDOView(this);
  }

  public CDOResourceImpl cdoResource()
  {
    if (this instanceof CDOResourceImpl)
    {
      resource = (CDOResourceImpl)this;
    }

    return resource;
  }

  public void cdoInternalSetID(CDOID id)
  {
    if (id == null)
    {
      throw new IllegalArgumentException("id == null");
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id);
    }

    this.id = id;
  }

  public CDOState cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      try
      {
        return this.state;
      }
      finally
      {
        this.state = state;
      }
    }
    else
    {
      // TODO Detect duplicate cdoInternalSetState() calls
      return null;
    }
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision);
    }

    this.revision = (CDORevisionImpl)revision;
  }

  public void cdoInternalSetView(CDOView view)
  {
    if (this instanceof CDOResourceImpl)
    {
      ((CDOResourceImpl)this).cdoSetView((CDOViewImpl)view);
    }

    eSetStore(cdoView().getStore());
  }

  public void cdoInternalSetResource(CDOResource resource)
  {
    if (this instanceof CDOResourceImpl)
    {
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resource: {0}", resource);
    }

    this.resource = (CDOResourceImpl)resource;
  }

  public void cdoInternalPostLoad()
  {
    // Do nothing
  }

  public void cdoInternalPostAttach()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Finalizing revision for {0}", this);
    }

    CDOViewImpl view = cdoView();
    revision.setVersion(1);
    revision.setContainerID(eContainer == null ? CDOID.NULL : ((CDOObjectImpl)eContainer).cdoID());
    revision.setContainingFeature(eContainerFeatureID);

    if (eSettings != null)
    {
      EClass eClass = eClass();
      for (int i = 0; i < eClass.getFeatureCount(); i++)
      {
        Object setting = eSettings[i];
        if (setting != null)
        {
          EStructuralFeature eFeature = cdoInternalDynamicFeature(i);
          if (!eFeature.isTransient())
          {
            finalizeRevisionFeature(view, revision, i, setting, eFeature, eSettings);
          }
        }
      }
    }
  }

  public void cdoInternalPreCommit()
  {
    // Do nothing
  }

  public InternalEObject cdoInternalInstance()
  {
    return this;
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    return eDynamicFeature(dynamicFeatureID);
  }

  @Override
  protected FeatureMap createFeatureMap(EStructuralFeature eStructuralFeature)
  {
    return new CDOStoreFeatureMap(eStructuralFeature);
  }

  @Override
  protected EList<?> createList(EStructuralFeature eStructuralFeature)
  {
    EClassifier eType = eStructuralFeature.getEType();
    if (eType.getInstanceClassName() == "java.util.Map$Entry")
    {
      return new EcoreEMap<Object, Object>((EClass)eType, eType.getInstanceClass(),
          new CDOStoreEList<BasicEMap.Entry<Object, Object>>(eStructuralFeature));
    }

    return new CDOStoreEList<Object>(eStructuralFeature);
  }

  @Override
  protected void eInitializeContainer()
  {
    throw new ImplementationError();
  }

  @Override
  protected void eSetDirectResource(Internal resource)
  {
    super.eSetDirectResource(resource);
    if (resource instanceof CDOResourceImpl)
    {
      this.resource = (CDOResourceImpl)resource;
    }
  }

  /**
   * Don't cache non-transient features in this CDOObject's {@link #eSettings()}.
   */
  @Override
  protected boolean eIsCaching()
  {
    return false;
  }

  @Override
  public Object dynamicGet(int dynamicFeatureID)
  {
    if (FSMUtil.isTransient(this))
    {
      if (eSettings == null)
      {
        return null;
      }

      return eSettings[dynamicFeatureID];
    }

    // Delegate to CDOStore
    return super.dynamicGet(dynamicFeatureID);
  }

  @Override
  public boolean eIsSet(EStructuralFeature feature)
  {
    if (FSMUtil.isTransient(this))
    {
      // TODO What about defaultValues != null?
      if (eSettings == null)
      {
        return false;
      }

      return eSettings[eDynamicFeatureID(feature)] != null;
    }

    // Delegate to CDOStore
    return super.eIsSet(feature);
  }

  @Override
  public void dynamicSet(int dynamicFeatureID, Object value)
  {
    if (FSMUtil.isTransient(this))
    {
      eSettings(); // Important to create eSettings array if necessary
      eSettings[dynamicFeatureID] = value;
    }
    else
    {
      // Delegate to CDOStore
      super.dynamicSet(dynamicFeatureID, value);
    }
  }

  @Override
  public void dynamicUnset(int dynamicFeatureID)
  {
    if (FSMUtil.isTransient(this))
    {
      if (eSettings != null)
      {
        eSettings[dynamicFeatureID] = null;
      }
    }
    else
    {
      // Delegate to CDOStore
      super.dynamicUnset(dynamicFeatureID);
    }
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    InternalEObject container;
    if (FSMUtil.isTransient(this))
    {
      container = eContainer;
    }
    else
    {
      // Delegate to CDOStore
      container = getStore().getContainer(this);
    }

    if (container instanceof CDOResource)
    {
      return null;
    }

    return container;
  }

  @Override
  public int eContainerFeatureID()
  {
    if (FSMUtil.isTransient(this))
    {
      return eContainerFeatureID;
    }

    // Delegate to CDOStore
    return getStore().getContainingFeatureID(this);
  }

  @Override
  protected void eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting container: {0}, featureID={1}", newContainer, newContainerFeatureID);
    }

    if (FSMUtil.isTransient(this))
    {
      eContainer = newContainer;
      eContainerFeatureID = newContainerFeatureID;
    }
    else
    {
      // Delegate to CDOStore
      getStore().setContainer(this, newContainer, newContainerFeatureID);
    }
  }

  @Override
  public Resource eResource()
  {
    return cdoResource();
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj == this;
  }

  @Override
  public String toString()
  {
    if (id == null)
    {
      return eClass().getName() + "?";
    }

    return eClass().getName() + "@" + id;
  }

  static CDOClassImpl getCDOClass(InternalCDOObject cdoObject)
  {
    CDOViewImpl view = (CDOViewImpl)cdoObject.cdoView();
    CDOSessionPackageManager packageManager = view.getSession().getPackageManager();
    return ModelUtil.getCDOClass(cdoObject.eClass(), packageManager);
  }

  static CDOViewImpl getCDOView(InternalCDOObject cdoObject)
  {
    CDOResource resource = cdoObject.cdoResource();
    return resource != null ? (CDOViewImpl)cdoObject.cdoResource().cdoView() : null;
  }

  static void finalizeCDORevision(InternalCDOObject cdoObject, EObject eContainer, int eContainerFeatureID,
      Object[] eSettings)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Finalizing revision for {0}", cdoObject);
    }

    CDOViewImpl view = (CDOViewImpl)cdoObject.cdoView();
    CDORevisionImpl revision = (CDORevisionImpl)cdoObject.cdoRevision();
    revision.setVersion(1);
    revision.setContainerID(eContainer == null ? CDOID.NULL : ((CDOObjectImpl)eContainer).cdoID());
    revision.setContainingFeature(eContainerFeatureID);

    if (eSettings != null)
    {
      EClass eClass = cdoObject.eClass();
      for (int i = 0; i < eClass.getFeatureCount(); i++)
      {
        Object setting = eSettings[i];
        if (setting != null)
        {
          EStructuralFeature eFeature = cdoObject.cdoInternalDynamicFeature(i);
          if (!eFeature.isTransient())
          {
            finalizeRevisionFeature(view, revision, i, setting, eFeature, eSettings);
          }
        }
      }
    }
  }

  private static void finalizeRevisionFeature(CDOViewImpl view, CDORevisionImpl revision, int i, Object setting,
      EStructuralFeature eFeature, Object[] eSettings)
  {
    CDOFeatureImpl cdoFeature = ModelUtil.getCDOFeature(eFeature, view.getSession().getPackageManager());
    if (TRACER.isEnabled())
    {
      TRACER.format("Finalizing feature {0}", cdoFeature);
    }

    boolean isReference = cdoFeature.isReference();
    if (cdoFeature.isMany())
    {
      int index = 0;
      EList<Object> list = (EList<Object>)setting;
      for (Object value : list)
      {
        if (isReference)
        {
          value = view.convertObjectToID(value);
        }

        revision.add(cdoFeature, index++, value);
      }
    }
    else
    {
      if (isReference)
      {
        setting = view.convertObjectToID(setting);
      }

      revision.set(cdoFeature, 0, setting);
    }

    if (eSettings != null)
    {
      eSettings[i] = null;
    }
  }

  private CDOStore getStore()
  {
    return cdoView().getStore();
  }

  /**
   * TODO Remove this when EMF has fixed https://bugs.eclipse.org/bugs/show_bug.cgi?id=197487
   * 
   * @author Eike Stepper
   */
  public class CDOStoreEList<E> extends DelegatingEcoreEList.Dynamic<E>
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreEList(EStructuralFeature eStructuralFeature)
    {
      super(CDOObjectImpl.this, eStructuralFeature);
    }

    @Override
    protected boolean hasProxies()
    {
      return true;
    }

    @Override
    protected List<E> delegateList()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    @Override
    protected void delegateAdd(int index, Object object)
    {
      getStore().add(owner, eStructuralFeature, index, object);
    }

    @Override
    protected void delegateAdd(Object object)
    {
      delegateAdd(delegateSize(), object);
    }

    @Override
    protected List<E> delegateBasicList()
    {
      int size = delegateSize();
      if (size == 0)
      {
        return ECollections.emptyEList();
      }
      else
      {
        Object[] data = getStore().toArray(owner, eStructuralFeature);
        return new EcoreEList.UnmodifiableEList<E>(owner, eStructuralFeature, data.length, data);
      }
    }

    @Override
    protected void delegateClear()
    {
      getStore().clear(owner, eStructuralFeature);
    }

    @Override
    protected boolean delegateContains(Object object)
    {
      return getStore().contains(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateContainsAll(Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (!delegateContains(o))
        {
          return false;
        }
      }
      return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateGet(int index)
    {
      return (E)getStore().get(owner, eStructuralFeature, index);
    }

    @Override
    protected int delegateHashCode()
    {
      return getStore().hashCode(owner, eStructuralFeature);
    }

    @Override
    protected int delegateIndexOf(Object object)
    {
      return getStore().indexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateIsEmpty()
    {
      return getStore().isEmpty(owner, eStructuralFeature);
    }

    @Override
    protected Iterator<E> delegateIterator()
    {
      return iterator();
    }

    @Override
    protected int delegateLastIndexOf(Object object)
    {
      return getStore().lastIndexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected ListIterator<E> delegateListIterator()
    {
      return listIterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateRemove(int index)
    {
      return (E)getStore().remove(owner, eStructuralFeature, index);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateSet(int index, E object)
    {
      return (E)getStore().set(owner, eStructuralFeature, index, object);
    }

    @Override
    protected int delegateSize()
    {
      return getStore().size(owner, eStructuralFeature);
    }

    @Override
    protected Object[] delegateToArray()
    {
      return getStore().toArray(owner, eStructuralFeature);
    }

    @Override
    protected <T> T[] delegateToArray(T[] array)
    {
      return getStore().toArray(owner, eStructuralFeature, array);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected E delegateMove(int targetIndex, int sourceIndex)
    {
      return (E)getStore().move(owner, eStructuralFeature, targetIndex, sourceIndex);
    }

    @Override
    protected boolean delegateEquals(Object object)
    {
      if (object == this)
      {
        return true;
      }

      if (!(object instanceof List))
      {
        return false;
      }

      List<?> list = (List<?>)object;
      if (list.size() != delegateSize())
      {
        return false;
      }

      for (ListIterator<?> i = list.listIterator(); i.hasNext();)
      {
        Object element = i.next();
        if (element == null ? get(i.previousIndex()) != null : !element.equals(get(i.previousIndex())))
        {
          return false;
        }
      }

      return true;
    }

    @Override
    protected String delegateToString()
    {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");
      for (int i = 0, size = size(); i < size;)
      {
        Object value = delegateGet(i);
        stringBuffer.append(String.valueOf(value));
        if (++i < size)
        {
          stringBuffer.append(", ");
        }
      }
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }

  /**
   * TODO Remove this when EMF has fixed https://bugs.eclipse.org/bugs/show_bug.cgi?id=197487
   * 
   * @author Eike Stepper
   */
  public class CDOStoreFeatureMap extends DelegatingFeatureMap
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreFeatureMap(EStructuralFeature eStructuralFeature)
    {
      super(CDOObjectImpl.this, eStructuralFeature);
    }

    @Override
    protected boolean hasProxies()
    {
      return true;
    }

    @Override
    protected List<FeatureMap.Entry> delegateList()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    @Override
    protected void delegateAdd(int index, Entry object)
    {
      getStore().add(owner, eStructuralFeature, index, object);
    }

    @Override
    protected void delegateAdd(Entry object)
    {
      delegateAdd(delegateSize(), object);
    }

    @Override
    protected List<FeatureMap.Entry> delegateBasicList()
    {
      int size = delegateSize();
      if (size == 0)
      {
        return ECollections.emptyEList();
      }
      else
      {
        Object[] data = getStore().toArray(owner, eStructuralFeature);
        return new EcoreEList.UnmodifiableEList<FeatureMap.Entry>(owner, eStructuralFeature, data.length, data);
      }
    }

    @Override
    protected void delegateClear()
    {
      getStore().clear(owner, eStructuralFeature);
    }

    @Override
    protected boolean delegateContains(Object object)
    {
      return getStore().contains(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateContainsAll(Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (!delegateContains(o))
        {
          return false;
        }
      }
      return true;
    }

    @Override
    protected Entry delegateGet(int index)
    {
      return (Entry)getStore().get(owner, eStructuralFeature, index);
    }

    @Override
    protected int delegateHashCode()
    {
      return getStore().hashCode(owner, eStructuralFeature);
    }

    @Override
    protected int delegateIndexOf(Object object)
    {
      return getStore().indexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateIsEmpty()
    {
      return getStore().isEmpty(owner, eStructuralFeature);
    }

    @Override
    protected Iterator<FeatureMap.Entry> delegateIterator()
    {
      return iterator();
    }

    @Override
    protected int delegateLastIndexOf(Object object)
    {
      return getStore().lastIndexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected ListIterator<FeatureMap.Entry> delegateListIterator()
    {
      return listIterator();
    }

    @Override
    protected Entry delegateRemove(int index)
    {
      return (Entry)getStore().remove(owner, eStructuralFeature, index);
    }

    @Override
    protected Entry delegateSet(int index, Entry object)
    {
      return (Entry)getStore().set(owner, eStructuralFeature, index, object);
    }

    @Override
    protected int delegateSize()
    {
      return getStore().size(owner, eStructuralFeature);
    }

    @Override
    protected Object[] delegateToArray()
    {
      return getStore().toArray(owner, eStructuralFeature);
    }

    @Override
    protected <T> T[] delegateToArray(T[] array)
    {
      return getStore().toArray(owner, eStructuralFeature, array);
    }

    @Override
    protected Entry delegateMove(int targetIndex, int sourceIndex)
    {
      return (Entry)getStore().move(owner, eStructuralFeature, targetIndex, sourceIndex);
    }

    @Override
    protected String delegateToString()
    {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");
      for (int i = 0, size = size(); i < size;)
      {
        Object value = delegateGet(i);
        stringBuffer.append(String.valueOf(value));
        if (++i < size)
        {
          stringBuffer.append(", ");
        }
      }
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }
}
