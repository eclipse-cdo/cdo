/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.GenUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;

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

  private InternalCDORevision revision;

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

  public InternalCDORevision cdoRevision()
  {
    return revision;
  }

  public CDOClass cdoClass()
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

  public void cdoReload()
  {
    CDOStateMachine.INSTANCE.reload(this);
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

    // TODO Detect duplicate cdoInternalSetState() calls
    return null;
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision);
    }

    this.revision = (InternalCDORevision)revision;
  }

  public void cdoInternalSetView(CDOView view)
  {
    CDOViewImpl impl = (CDOViewImpl)view;
    if (this instanceof CDOResourceImpl)
    {
      ((CDOResourceImpl)this).cdoSetView(impl);
    }

    eSetStore(impl.getStore());
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
      TRACER.format("Populating revision for {0}", this);
    }

    CDOViewImpl view = cdoView();
    revision.setContainerID(eContainer == null ? CDOID.NULL : ((CDOObjectImpl)eContainer).cdoID());
    revision.setContainingFeatureID(eContainerFeatureID);

    if (eSettings == null)
    {
      eSettings();
    }

    EClass eClass = eClass();
    for (int i = 0; i < eClass.getFeatureCount(); i++)
    {
      EStructuralFeature eFeature = cdoInternalDynamicFeature(i);
      if (!eFeature.isTransient())
      {
        populateRevisionFeature(view, revision, eFeature, eSettings, i);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void populateRevisionFeature(CDOViewImpl view, InternalCDORevision revision, EStructuralFeature eFeature,
      Object[] eSettings, int i)
  {
    CDOFeature cdoFeature = ModelUtil.getCDOFeature(eFeature, view.getSession().getPackageManager());
    if (TRACER.isEnabled())
    {
      TRACER.format("Populating feature {0}", cdoFeature);
    }

    Object setting = eSettings[i];
    if (setting == null)
    {
      setting = eFeature.getDefaultValue();
    }

    if (cdoFeature.isMany())
    {
      if (setting != null)
      {
        int index = 0;
        EList<Object> list = (EList<Object>)setting;
        for (Object value : list)
        {
          if (cdoFeature.isReference())
          {
            value = view.convertObjectToID(value);
          }

          revision.add(cdoFeature, index++, value);
        }
      }
    }
    else
    {
      if (cdoFeature.isReference())
      {
        setting = view.convertObjectToID(setting);
      }
      else
      {
        if (cdoFeature.getType() == CDOType.CUSTOM)
        {
          setting = EcoreUtil.convertToString((EDataType)eFeature.getEType(), setting);
        }
        else if (setting == null && GenUtil.isPrimitiveType(eFeature.getEType()))
        {
          setting = eFeature.getDefaultValue();
        }
      }

      revision.set(cdoFeature, 0, setting);
    }

    if (eSettings != null)
    {
      eSettings[i] = null;
    }
  }

  public void cdoInternalPostDetach()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating revision for {0}", this);
    }

    CDOViewImpl view = cdoView();
    eContainer = null;
    eContainerFeatureID = 0;

    if (eSettings == null)
    {
      eSettings();
    }

    EClass eClass = eClass();
    for (int i = 0; i < eClass.getFeatureCount(); i++)
    {
      Object setting = eSettings[i];
      if (setting != null)
      {
        EStructuralFeature eFeature = cdoInternalDynamicFeature(i);
        if (!eFeature.isTransient())
        {
          depopulateRevisionFeature(view, revision, eFeature, eSettings, i);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void depopulateRevisionFeature(CDOViewImpl view, InternalCDORevision revision, EStructuralFeature eFeature,
      Object[] eSettings, int i)
  {
    CDOFeature cdoFeature = ModelUtil.getCDOFeature(eFeature, view.getSession().getPackageManager());
    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating feature {0}", cdoFeature);
    }

    boolean isReference = cdoFeature.isReference();
    if (cdoFeature.isMany())
    {
      eSettings[i] = null;
      EList<Object> setting = (EList<Object>)super.dynamicGet(eFeature.getFeatureID());
      EList<Object> list = (EList<Object>)revision.getList(cdoFeature);
      for (Object value : list)
      {
        if (isReference)
        {
          value = view.getObject((CDOID)value, true);
        }

        setting.add(value);
      }
    }
    else
    {
      Object value = revision.getValue(cdoFeature);
      if (isReference)
      {
        value = view.getObject((CDOID)value, true);
      }
      else if (cdoFeature.getType() == CDOType.CUSTOM)
      {
        value = EcoreUtil.createFromString((EDataType)eFeature.getEType(), (String)value);
      }

      eSettings[i] = value;
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
  protected EList<?> createList(final EStructuralFeature eStructuralFeature)
  {
    final EClassifier eType = eStructuralFeature.getEType();
    if (eType.getInstanceClassName() == "java.util.Map$Entry")
    {
      class EStoreEcoreEMap extends EcoreEMap<Object, Object>
      {
        private static final long serialVersionUID = 1L;

        public EStoreEcoreEMap()
        {
          super((EClass)eType, eType.getInstanceClass(), null);
          delegateEList = new CDOStoreEList<BasicEMap.Entry<Object, Object>>(eStructuralFeature)
          {
            private static final long serialVersionUID = 1L;

            @Override
            protected void didAdd(int index, BasicEMap.Entry<Object, Object> newObject)
            {
              EStoreEcoreEMap.this.doPut(newObject);
            }

            @Override
            protected void didSet(int index, BasicEMap.Entry<Object, Object> newObject,
                BasicEMap.Entry<Object, Object> oldObject)
            {
              didRemove(index, oldObject);
              didAdd(index, newObject);
            }

            @Override
            protected void didRemove(int index, BasicEMap.Entry<Object, Object> oldObject)
            {
              EStoreEcoreEMap.this.doRemove(oldObject);
            }

            @Override
            protected void didClear(int size, Object[] oldObjects)
            {
              EStoreEcoreEMap.this.doClear();
            }

            @Override
            protected void didMove(int index, BasicEMap.Entry<Object, Object> movedObject, int oldIndex)
            {
              EStoreEcoreEMap.this.doMove(movedObject);
            }
          };

          size = delegateEList.size();
        }
      }

      return new EStoreEcoreEMap();
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
      super.eBasicSetContainer(newContainer, newContainerFeatureID);
    }
    else
    {
      CDOResource newResource = null;
      if (newContainer instanceof CDOObject)
      {
        newResource = ((CDOObject)newContainer).cdoResource();
      }

      // Delegate to CDOStore
      getStore().setContainer(this, newResource, newContainer, newContainerFeatureID);

      resource = (CDOResourceImpl)newResource;
      if (newContainer instanceof Resource.Internal)
      {
        eSetDirectResource((Resource.Internal)newContainer);
      }
    }
  }

  @Override
  public Resource eResource()
  {
    Resource resource = cdoResource();
    if (resource == null && FSMUtil.isTransient(this))
    {
      resource = super.eResource();
    }

    return resource;
  }

  /**
   * Specializing the behaviour of {@link #equals(Object)} is not permitted as per {@link EObject} specification.
   */
  @Override
  public final boolean equals(Object obj)
  {
    return super.equals(obj);
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

  static CDOClass getCDOClass(InternalCDOObject cdoObject)
  {
    CDOViewImpl view = (CDOViewImpl)cdoObject.cdoView();
    CDOSessionPackageManagerImpl packageManager = view.getSession().getPackageManager();
    return ModelUtil.getCDOClass(cdoObject.eClass(), packageManager);
  }

  static CDOViewImpl getCDOView(InternalCDOObject cdoObject)
  {
    CDOResource resource = cdoObject.cdoResource();
    return resource != null ? (CDOViewImpl)cdoObject.cdoResource().cdoView() : null;
  }

  private CDOStore getStore()
  {
    return cdoView().getStore();
  }

  /**
   * TODO Remove this when EMF has fixed http://bugs.eclipse.org/197487
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

      Object[] data = getStore().toArray(owner, eStructuralFeature);
      return new EcoreEList.UnmodifiableEList<E>(owner, eStructuralFeature, data.length, data);
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
   * TODO Remove this when EMF has fixed http://bugs.eclipse.org/197487
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

      Object[] data = getStore().toArray(owner, eStructuralFeature);
      return new EcoreEList.UnmodifiableEList<FeatureMap.Entry>(owner, eStructuralFeature, data.length, data);
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
