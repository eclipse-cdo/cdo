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
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.notify.impl.NotifyingListImpl;
import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.InternalEList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Resource</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getResourceSet <em>Resource Set</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getURI <em>URI</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getContents <em>Contents</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#isModified <em>Modified</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#isLoaded <em>Loaded</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#isTrackingModification <em>Tracking Modification</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getErrors <em>Errors</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getWarnings <em>Warnings</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getTimeStamp <em>Time Stamp</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getPath <em>Path</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CDOResourceImpl extends CDOObjectImpl implements CDOResource
{
  /**
   * @ADDED
   */
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOResourceImpl.class);

  /**
   * @ADDED
   */
  private CDOViewImpl view;

  /**
   * @ADDED
   */
  private boolean existing;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CDOResourceImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EresourcePackage.Literals.CDO_RESOURCE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ResourceSet getResourceSet()
  {
    return (ResourceSet)eGet(EresourcePackage.Literals.CDO_RESOURCE__RESOURCE_SET, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setResourceSet(ResourceSet newResourceSet)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__RESOURCE_SET, newResourceSet);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public URI getURI()
  {
    return (URI)eGet(EresourcePackage.Literals.CDO_RESOURCE__URI, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public void setURI(URI newURI)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__URI, newURI);
    basicSetPath(CDOUtil.extractResourcePath(newURI));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @SuppressWarnings("unchecked")
  public EList<EObject> getContents()
  {
    if (FSMUtil.isTransient(this))
    {
      EList<EObject> transientContents = (EList<EObject>)eSettings[EresourcePackage.CDO_RESOURCE__CONTENTS];
      if (transientContents == null)
      {
        transientContents = new TransientContents<EObject>();
        eSettings[EresourcePackage.CDO_RESOURCE__CONTENTS] = transientContents;
      }

      return transientContents;
    }

    return (EList<EObject>)eGet(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isModified()
  {
    return ((Boolean)eGet(EresourcePackage.Literals.CDO_RESOURCE__MODIFIED, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setModified(boolean newModified)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__MODIFIED, new Boolean(newModified));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isLoaded()
  {
    return ((Boolean)eGet(EresourcePackage.Literals.CDO_RESOURCE__LOADED, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setLoaded(boolean newLoaded)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__LOADED, new Boolean(newLoaded));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isTrackingModification()
  {
    return ((Boolean)eGet(EresourcePackage.Literals.CDO_RESOURCE__TRACKING_MODIFICATION, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setTrackingModification(boolean newTrackingModification)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__TRACKING_MODIFICATION, new Boolean(newTrackingModification));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Diagnostic> getErrors()
  {
    return (EList<Diagnostic>)eGet(EresourcePackage.Literals.CDO_RESOURCE__ERRORS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Diagnostic> getWarnings()
  {
    return (EList<Diagnostic>)eGet(EresourcePackage.Literals.CDO_RESOURCE__WARNINGS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public long getTimeStamp()
  {
    return ((Long)eGet(EresourcePackage.Literals.CDO_RESOURCE__TIME_STAMP, true)).longValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setTimeStamp(long newTimeStamp)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__TIME_STAMP, new Long(newTimeStamp));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getPath()
  {
    return (String)eGet(EresourcePackage.Literals.CDO_RESOURCE__PATH, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public void setPath(String newPath)
  {
    setURI(CDOUtil.createResourceURI(newPath));
  }

  /**
   * @ADDED
   * @see ResourceImpl#getAllContents()
   */
  public TreeIterator<EObject> getAllContents()
  {
    return new AbstractTreeIterator<EObject>(this, false)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public Iterator<EObject> getChildren(Object object)
      {
        return object == CDOResourceImpl.this ? CDOResourceImpl.this.getContents().iterator() : ((EObject)object)
            .eContents().iterator();
      }
    };
  }

  /**
   * @ADDED
   */
  public EObject getEObject(String uriFragment)
  {
    // TODO Implement method CDOResourceImpl.getEObject()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * @ADDED
   */
  public String getURIFragment(EObject object)
  {
    // TODO Implement method CDOResourceImpl.getURIFragment()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * @ADDED
   */
  public void load(InputStream inputStream, Map<?, ?> options) throws IOException
  {
    // Do nothing
  }

  /**
   * @ADDED
   */
  public void load(Map<?, ?> options) throws IOException
  {
    // Do nothing
  }

  /**
   * @ADDED
   */
  public void save(Map<?, ?> options) throws IOException
  {
    if (view instanceof CDOTransaction)
    {
      CDOTransaction transaction = (CDOTransaction)view;
      transaction.commit();
    }
    else
    {
      throw new IOException("CDO view is read only: " + view);
    }
  }

  /**
   * @ADDED
   */
  public void save(OutputStream outputStream, Map<?, ?> options) throws IOException
  {
    // Do nothing
  }

  /**
   * @ADDED
   */
  public void unload()
  {
    // Do nothing
  }

  /**
   * @ADDED
   */
  public void delete(Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @ADDED
   */
  public void attached(EObject object)
  {
    // InternalCDOObject legacy = getLegacyWrapper(object);
    // if (legacy.cdoState() != CDOState.CLEAN)
    // {
    // CDOStateMachine.INSTANCE.attach(legacy, this, view);
    // }
  }

  /**
   * @ADDED
   */
  public void detached(EObject object)
  {
    // InternalCDOObject legacy = getLegacyWrapper(object);
    // CDOStateMachine.INSTANCE.detach(legacy);
  }

  /**
   * @ADDED
   * @see ResourceImpl#basicSetResourceSet(ResourceSet, NotificationChain)
   */
  public NotificationChain basicSetResourceSet(ResourceSet resourceSet, NotificationChain notifications)
  {
    ResourceSet oldResourceSet = getResourceSet();
    if (oldResourceSet != null)
    {
      notifications = ((InternalEList<Resource>)oldResourceSet.getResources()).basicRemove(this, notifications);
    }

    setResourceSet(resourceSet);

    if (eNotificationRequired())
    {
      if (notifications == null)
      {
        notifications = new NotificationChainImpl(2);
      }
      notifications.add(new NotificationImpl(Notification.SET, oldResourceSet, resourceSet)
      {
        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID(Class<?> expectedClass)
        {
          return RESOURCE__RESOURCE_SET;
        }
      });
    }

    return notifications;
  }

  /**
   * @ADDED
   */
  public boolean isLoading()
  {
    // TODO Implement method CDOResourceImpl.isLoading()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * @ADDED
   */
  @Override
  public CDOState cdoState()
  {
    CDOState superState = super.cdoState();
    if (superState == CDOState.TRANSIENT && isExisting())
    {
      return CDOState.PROXY;
    }

    return superState;
  }

  /**
   * @ADDED
   */
  @Override
  public CDOViewImpl cdoView()
  {
    return view;
  }

  /**
   * @ADDED
   */
  public void cdoSetView(CDOViewImpl view)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting view: {0}", view);
    }

    this.view = view;
  }

  /**
   * @ADDED
   */
  public boolean isExisting()
  {
    return existing;
  }

  /**
   * @ADDED
   */
  void setExisting(boolean existing)
  {
    this.existing = existing;
  }

  /**
   * @ADDED
   */
  @Override
  protected EList<?> createList(EStructuralFeature eStructuralFeature)
  {
    if (eStructuralFeature == EresourcePackage.eINSTANCE.getCDOResource_Contents())
    {
      return new PersistentContents(eStructuralFeature);
    }

    return super.createList(eStructuralFeature);
  }

  /**
   * @ADDED
   */
  private void basicSetPath(String newPath)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__PATH, newPath);
  }

  /**
   * @ADDED
   * @author Eike Stepper
   */
  protected class PersistentContents extends CDOStoreEList<Object>
  {
    private static final long serialVersionUID = 1L;

    public PersistentContents(EStructuralFeature eStructuralFeature)
    {
      super(eStructuralFeature);
      if (!cdoView().hasUniqueResourceContents())
      {
        kind &= ~IS_UNIQUE;
      }
    }
  }

  /**
   * TODO Change superclass to NotifyingInternalEListImpl when EMF 2.3 is out of maintenance
   * 
   * @ADDED
   * @author Eike Stepper
   */
  protected class TransientContents<E extends Object & EObject> extends NotifyingListImpl<E> implements
      InternalEList<E>
  {
    private static final long serialVersionUID = 1L;

    public TransientContents()
    {
    }

    public TransientContents(Collection<? extends E> collection)
    {
      super(collection);
    }

    public TransientContents(int initialCapacity)
    {
      super(initialCapacity);
    }

    @Override
    public Object getNotifier()
    {
      return CDOResourceImpl.this;
    }

    @Override
    public int getFeatureID()
    {
      return EresourcePackage.CDO_RESOURCE__CONTENTS;
    }

    @Override
    protected boolean isNotificationRequired()
    {
      return eNotificationRequired();
    }

    @Override
    protected boolean useEquals()
    {
      return false;
    }

    @Override
    protected boolean hasInverse()
    {
      return true;
    }

    @Override
    protected boolean isUnique()
    {
      return true;
    }

    @Override
    public NotificationChain inverseAdd(E object, NotificationChain notifications)
    {
      InternalEObject eObject = (InternalEObject)object;
      notifications = eObject.eSetResource(CDOResourceImpl.this, notifications);
      // CDOResourceImpl.this.attached(eObject);
      return notifications;
    }

    @Override
    public NotificationChain inverseRemove(E object, NotificationChain notifications)
    {
      InternalEObject eObject = (InternalEObject)object;
      // CDOResourceImpl.this.detached(eObject);
      return eObject.eSetResource(null, notifications);
    }

    public boolean basicContains(Object object)
    {
      return super.contains(object);
    }

    public boolean basicContainsAll(Collection<?> collection)
    {
      return super.containsAll(collection);
    }

    public int basicIndexOf(Object object)
    {
      return super.indexOf(object);
    }

    public int basicLastIndexOf(Object object)
    {
      return super.lastIndexOf(object);
    }

    public Object[] basicToArray()
    {
      return super.toArray();
    }

    public <T> T[] basicToArray(T[] array)
    {
      return super.toArray(array);
    }

    @Override
    public Iterator<E> basicIterator()
    {
      return super.basicIterator();
    }

    @Override
    public ListIterator<E> basicListIterator()
    {
      return super.basicListIterator();
    }

    @Override
    public ListIterator<E> basicListIterator(int index)
    {
      return super.basicListIterator(index);
    }

    @Override
    public List<E> basicList()
    {
      return super.basicList();
    }

    @Override
    protected Object[] newData(int capacity)
    {
      return new EObject[capacity];
    }

    @Override
    protected void didAdd(int index, E object)
    {
      super.didAdd(index, object);
      modified();
    }

    @Override
    protected void didRemove(int index, E object)
    {
      super.didRemove(index, object);
      modified();
    }

    @Override
    protected void didSet(int index, E newObject, E oldObject)
    {
      super.didSet(index, newObject, oldObject);
      modified();
    }

    @Override
    protected void didClear(int oldSize, Object[] oldData)
    {
      super.didClear(oldSize, oldData);
      // modified();
    }

    protected void modified()
    {
      if (isTrackingModification())
      {
        setModified(true);
      }
    }
  }
} // CDOResourceImpl
