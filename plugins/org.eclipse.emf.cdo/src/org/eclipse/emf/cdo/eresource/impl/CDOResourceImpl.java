/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

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
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Resource</b></em>'.
 * 
 * @extends Resource.Internal<!-- end-user-doc -->
 *          <p>
 *          The following features are implemented:
 *          <ul>
 *          <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getResourceSet <em>Resource Set</em>}</li>
 *          <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getURI <em>URI</em>}</li>
 *          <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getContents <em>Contents</em>}</li>
 *          <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#isModified <em>Modified</em>}</li>
 *          <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#isLoaded <em>Loaded</em>}</li>
 *          <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#isTrackingModification <em>Tracking
 *          Modification</em>}</li>
 *          <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getErrors <em>Errors</em>}</li>
 *          <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getWarnings <em>Warnings</em>}</li>
 *          <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getTimeStamp <em>Time Stamp</em>}</li>
 *          </ul>
 *          </p>
 * @generated
 */
public class CDOResourceImpl extends CDOResourceNodeImpl implements CDOResource, Resource.Internal
{
  /**
   * The default URI converter when there is no resource set.
   * 
   * @ADDED
   */
  private static URIConverter defaultURIConverter;

  /**
   * @ADDED
   */
  private boolean root;

  /**
   * @ADDED
   */
  private URI initialURI;

  /**
   * @ADDED
   */
  private boolean existing;

  /**
   * @ADDED
   */
  private boolean loading;

  /**
   * @ADDED
   */
  private boolean loaded;

  /**
   * @ADDED
   */
  private EList<Diagnostic> errors;

  /**
   * @ADDED
   */
  private EList<Diagnostic> warnings;

  /**
   * @ADDED
   * @since 2.0
   */
  public CDOResourceImpl(URI initialURI)
  {
    this.initialURI = initialURI;
  }

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
   * @generated NOT
   * @since 2.0
   */
  @Override
  public Resource.Internal eDirectResource()
  {
    if (isRoot())
    {
      return this;
    }
    return super.eDirectResource();
  }

  /**
   * @since 2.0
   */
  public boolean isRoot()
  {
    return root;
  }

  void setRoot(boolean root)
  {
    this.root = root;
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
   * <!-- begin-user-doc -->
   * 
   * @since 2.0 <!-- end-user-doc -->
   * @generated
   */
  public URI getURIGen()
  {
    return (URI)eGet(EresourcePackage.Literals.CDO_RESOURCE__URI, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public URI getURI()
  {
    if (cdoID() == null && initialURI != null)
    {
      return initialURI;
    }

    return CDOURIUtil.createResourceURI(cdoView(), getPath());
  }

  /**
   * <!-- begin-user-doc -->
   * 
   * @since 2.0 <!-- end-user-doc -->
   * @generated
   */
  public void setURIGen(URI newURI)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__URI, newURI);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public void setURI(URI newURI)
  {
    String newPath = CDOURIUtil.extractResourcePath(newURI);
    setPath(newPath);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @SuppressWarnings("unchecked")
  public EList<EObject> getContents()
  {
    return (EList<EObject>)eGet(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   * @since 2.0
   */
  @Override
  public void cdoInternalPostInvalid()
  {
    super.cdoInternalPostInvalid();
    existing = false;
    cdoView().getResourceSet().getResources().remove(this);
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
   * @generated NOT
   */
  public void setModified(boolean newModified)
  {
    boolean oldIsModified = isModified();
    eSet(EresourcePackage.Literals.CDO_RESOURCE__MODIFIED, new Boolean(newModified));
    if (eNotificationRequired())
    {
      Notification notification = new NotificationImpl(Notification.SET, oldIsModified, newModified)
      {
        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID(Class<?> expectedClass)
        {
          return RESOURCE__IS_MODIFIED;
        }
      };

      eNotify(notification);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public boolean isLoaded()
  {
    return loaded;
  }

  /**
   * @see ResourceImpl#setLoaded(boolean)
   * @ADDED
   */
  private Notification setLoaded(boolean isLoaded)
  {
    boolean oldIsLoaded = loaded;
    loaded = isLoaded;

    if (eNotificationRequired())
    {
      Notification notification = new NotificationImpl(Notification.SET, oldIsLoaded, isLoaded)
      {
        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID(Class<?> expectedClass)
        {
          return EresourcePackage.CDO_RESOURCE__LOADED;
        }
      };

      return notification;
    }
    else
    {
      return null;
    }
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
   * @generated NOT
   */
  public EList<Diagnostic> getErrors()
  {
    if (errors == null)
    {
      errors = new NotifyingListImpl<Diagnostic>()
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean isNotificationRequired()
        {
          return CDOResourceImpl.this.eNotificationRequired();
        }

        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID()
        {
          return EresourcePackage.CDO_RESOURCE__ERRORS;
        }
      };
    }

    return errors;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public EList<Diagnostic> getWarnings()
  {
    if (warnings == null)
    {
      warnings = new NotifyingListImpl<Diagnostic>()
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean isNotificationRequired()
        {
          return CDOResourceImpl.this.eNotificationRequired();
        }

        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID()
        {
          return EresourcePackage.CDO_RESOURCE__WARNINGS;
        }
      };
    }

    return warnings;
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
   * <b>Note:</b> URI from temporary objects are going to changed when we commit the CDOTransaction. Objects will not be
   * accessible from their temporary URI once CDOTransaction is committed.
   * <p>
   * <b>Note:</b> This resource is not actually used to lookup the resulting object in CDO. Only the CDOView is used for
   * this lookup! This means that this resource can be used to resolve <em>any</em> fragment with a CDOID of the
   * associated CDOView.
   * 
   * @ADDED
   */
  public EObject getEObject(String uriFragment)
  {
    // Should we return CDOResource (this ?) ?
    if (uriFragment == null)
    {
      return null;
    }

    CDOID cdoID = CDOIDUtil.read(uriFragment, cdoView().getSession().getPackageManager().getCDOIDObjectFactory());
    if (CDOIDUtil.isNull(cdoID) || cdoID.isTemporary() && !cdoView().isObjectRegistered(cdoID))
    {
      return null;
    }

    if (cdoID.isObject())
    {
      try
      {
        return cdoView().getObject(cdoID, true);
      }
      catch (ObjectNotFoundException ex)
      {
        // Do nothing
        // getEObject return null when the object cannot be resolved.
      }
    }

    // If it doesn`t match to anything we return null like ResourceImpl.getEObject
    return null;
  }

  /**
   * @ADDED
   */
  public String getURIFragment(EObject object)
  {
    // TODO if object == this ??? what we do. Is it wanted ? How we handle them ?
    InternalCDOObject internalCDOObject = FSMUtil.adapt(object, cdoView());
    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, internalCDOObject.cdoID());
    return builder.toString();
  }

  /**
   * @ADDED
   */
  public void load(InputStream inputStream, Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
    // if (inputStream instanceof CDOResourceInputStream)
    // {
    // CDOResourceInputStream stream = (CDOResourceInputStream)inputStream;
    // URI uri = stream.getURI();
    // }
    // else
    // {
    // throw new IOException("Stream not supported: " + inputStream);
    // }
  }

  /**
   * @ADDED
   */
  public void load(Map<?, ?> options) throws IOException
  {
    if (!isLoaded())
    {
      CDOViewImpl view = cdoView();
      if (!FSMUtil.isTransient(this))
      {
        CDOID id = cdoID();
        if (id == null || id != null && !view.isObjectRegistered(id))
        {
          try
          {
            view.registerProxyResource(this);
          }
          catch (Exception ex)
          {
            setExisting(false);
            cdoInternalSetState(CDOState.TRANSIENT);
            throw new IOWrappedException(ex);
          }
        }
      }

      setLoaded(true);

      // URIConverter uriConverter = getURIConverter();
      //
      // // If an input stream can't be created, ensure that the resource is still considered loaded after the failure,
      // // and do all the same processing we'd do if we actually were able to create a valid input stream.
      // //
      // InputStream inputStream = null;
      //
      // try
      // {
      // inputStream = uriConverter.createInputStream(getURI(), options);
      // }
      // catch (IOException exception)
      // {
      // Notification notification = setLoaded(true);
      // loading = true;
      // if (errors != null)
      // {
      // errors.clear();
      // }
      //
      // if (warnings != null)
      // {
      // warnings.clear();
      // }
      //
      // loading = false;
      // if (notification != null)
      // {
      // eNotify(notification);
      // }
      //
      // setModified(false);
      // throw exception;
      // }
      //
      // try
      // {
      // load(inputStream, options);
      // }
      // finally
      // {
      // inputStream.close();
      // // TODO Handle timeStamp
      // // Long timeStamp = (Long)response.get(URIConverter.RESPONSE_TIME_STAMP_PROPERTY);
      // // if (timeStamp != null)
      // // {
      // // setTimeStamp(timeStamp);
      // // }
      // }
    }
  }

  /**
   * Returns the URI converter. This typically gets the {@link ResourceSet#getURIConverter converter} from the
   * {@link #getResourceSet containing} resource set, but it calls {@link #getDefaultURIConverter} when there is no
   * containing resource set.
   * 
   * @return the URI converter.
   * @ADDED
   */
  private URIConverter getURIConverter()
  {
    return getResourceSet() == null ? getDefaultURIConverter() : getResourceSet().getURIConverter();
  }

  /**
   * Returns the default URI converter that's used when there is no resource set.
   * 
   * @return the default URI converter.
   * @see #getURIConverter
   * @ADDED
   */
  private static synchronized URIConverter getDefaultURIConverter()
  {
    if (defaultURIConverter == null)
    {
      defaultURIConverter = new ExtensibleURIConverterImpl();
    }

    return defaultURIConverter;
  }

  /**
   * @ADDED
   */
  public void save(Map<?, ?> options) throws IOException
  {
    CDOView view = cdoView();
    if (view instanceof CDOTransaction)
    {
      CDOTransaction transaction = (CDOTransaction)view;
      IProgressMonitor progressMonitor = (IProgressMonitor)options.get(CDOResource.OPTION_SAVE_PROGRESS_MONITOR);
      transaction.commit(progressMonitor);
      setModified(false);
    }
    else
    {
      throw new IOException("CDO view is read-only: " + view);
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
  public void delete(Map<?, ?> defaultDeleteOptions) throws IOException
  {
    if (FSMUtil.isTransient(this))
    {
      removeFromResourceSet();
    }
    else
    {
      if (isRoot())
      {
        throw new UnsupportedOperationException();
      }

      if (getFolder() == null)
      {
        CDOViewImpl view = cdoView();
        view.getRootResource().getContents().remove(this);
      }
      else
      {
        basicSetFolder(null, false);
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void cdoInternalPostDetach()
  {
    super.cdoInternalPostDetach();
    removeFromResourceSet();
  }

  private void removeFromResourceSet()
  {
    ResourceSet resourceSet = getResourceSet();
    if (resourceSet != null)
    {
      resourceSet.getResources().remove(this);
    }
  }

  /**
   * @ADDED
   */
  public void attached(EObject object)
  {
    if (!FSMUtil.isTransient(this))
    {
      InternalCDOObject cdoObject = FSMUtil.adapt(object, cdoView());
      attached(cdoObject, cdoView().toTransaction());
    }
  }

  /**
   * @ADDED
   */
  private void attached(InternalCDOObject cdoObject, CDOTransactionImpl transaction)
  {
    CDOStateMachine.INSTANCE.attach(cdoObject, transaction);
  }

  /**
   * @ADDED
   */
  public void detached(EObject object)
  {
    if (!FSMUtil.isTransient(this))
    {
      InternalCDOObject cdoObject = FSMUtil.adapt(object, cdoView());
      CDOStateMachine.INSTANCE.detach(cdoObject);
    }
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
    return loading;
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
      return new ContentsCDOList(eStructuralFeature);
    }

    return super.createList(eStructuralFeature);
  }

  /**
   * {@link ResourceImpl.ContentsEList}!!! --> Bugzilla!
   * 
   * @ADDED
   * @author Eike Stepper
   * @since 2.0
   */
  protected class ContentsCDOList extends BasicEStoreEList<Object>
  {
    private static final long serialVersionUID = 1L;

    public ContentsCDOList(EStructuralFeature eStructuralFeature)
    {
      super(CDOResourceImpl.this, eStructuralFeature);
    }

    /**
     * Optimization taken from ResourceImpl.EContentList.contains.
     * 
     * @since 2.0
     */
    @Override
    public boolean contains(Object object)
    {
      if (size() <= 4)
      {
        return super.contains(object);
      }

      return object instanceof InternalEObject && ((InternalEObject)object).eDirectResource() == CDOResourceImpl.this;
    }

    /**
     * @since 2.0
     */
    @Override
    public NotificationChain inverseAdd(Object object, NotificationChain notifications)
    {
      if (FSMUtil.isTransient(CDOResourceImpl.this))
      {
        InternalEObject eObject = (InternalEObject)object;
        notifications = eObject.eSetResource(CDOResourceImpl.this, notifications);
      }
      else
      {
        CDOTransactionImpl transaction = cdoView().toTransaction();
        InternalCDOObject cdoObject = FSMUtil.adapt(object, transaction);
        notifications = cdoObject.eSetResource(CDOResourceImpl.this, notifications);

        // Attach here instead of in CDOObjectImpl.eSetResource because EMF does it also here
        if (FSMUtil.isTransient(cdoObject))
        {
          attached(cdoObject, transaction);
        }
      }

      return notifications;
    }

    /**
     * @since 2.0
     */
    @Override
    public NotificationChain inverseRemove(Object object, NotificationChain notifications)
    {
      if (FSMUtil.isTransient(CDOResourceImpl.this))
      {
        InternalEObject eObject = (InternalEObject)object;
        notifications = eObject.eSetResource(null, notifications);
      }
      else
      {
        InternalEObject eObject = (InternalEObject)object;
        detached(eObject);
        notifications = eObject.eSetResource(null, notifications);
      }

      return notifications;
    }

    /**
     * @since 2.0
     */
    protected void loaded()
    {
      if (!isLoaded())
      {
        Notification notification = setLoaded(true);
        if (notification != null)
        {
          eNotify(notification);
        }
      }
    }

    /**
     * @since 2.0
     */
    protected void modified()
    {
      if (isTrackingModification())
      {
        setModified(true);
      }
    }

    /**
     * @since 2.0
     */
    @Override
    protected boolean useEquals()
    {
      return false;
    }

    /**
     * @since 2.0
     */
    @Override
    protected boolean hasInverse()
    {
      return true;
    }

    /**
     * @since 2.0
     */
    @Override
    protected boolean isUnique()
    {
      return true;
    }
  }
} // CDOResourceImpl
