/*
 * Copyright (c) 2007-2016, 2019, 2021-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchCreationContext;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.util.CDORenameContext;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSession.Options;
import org.eclipse.emf.cdo.session.CDOSessionProvider;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;
import org.eclipse.emf.cdo.transaction.CDOTransactionContainer;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOStaleObject;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewConfigurator;
import org.eclipse.emf.cdo.view.CDOViewContainer;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerModelBased;
import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerUI;
import org.eclipse.emf.internal.cdo.analyzer.CDOFetchRuleManagerThreadLocal;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDOExternalObject;
import org.eclipse.emf.internal.cdo.object.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.object.CDOObjectWrapper;
import org.eclipse.emf.internal.cdo.session.CDOCollectionLoadingPolicyImpl;
import org.eclipse.emf.internal.cdo.transaction.CDOXATransactionImpl;
import org.eclipse.emf.internal.cdo.transaction.CDOXATransactionImpl.CDOXAInternalAdapter;
import org.eclipse.emf.internal.cdo.view.AbstractCDOView;
import org.eclipse.emf.internal.cdo.view.CDORevisionPrefetchingPolicyImpl;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;
import org.eclipse.emf.internal.cdo.view.CDOStoreImpl;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.concurrent.CriticalSection;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.EmptyInputStream;
import org.eclipse.net4j.util.io.ReaderInputStream;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.properties.PropertiesContainerUtil;
import org.eclipse.net4j.util.security.CredentialsProviderFactory;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Various static methods that may help in CDO client applications.
 *
 * @author Eike Stepper
 */
public final class CDOUtil
{
  /**
   * @since 4.14
   */
  public static final String PROP_VIEW_CONFIGURATOR = "org.eclipse.emf.cdo.viewConfigurator";

  /**
   * @since 4.14
   */
  public static final String PROP_VIEW_CONFIGURATOR_TYPE = "org.eclipse.emf.cdo.viewConfiguratorType";

  /**
   * @since 4.14
   */
  public static final String PROP_VIEW_CONFIGURATOR_DESCRIPTION = "org.eclipse.emf.cdo.viewConfiguratorDescription";

  /**
   * @since 4.3
   */
  public static final String CDO_ANNOTATION_URI = "http://www.eclipse.org/CDO";

  /**
   * @since 4.3
   */
  public static final String DOCUMENTATION_KEY = "documentation";

  static
  {
    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[0] = EcorePackage.eINSTANCE;
    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[1] = EcorePackage.eINSTANCE.getEObject();

    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[2] = EresourcePackage.eINSTANCE;
    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[3] = EresourcePackage.eINSTANCE.getCDOResource();
    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[4] = EresourcePackage.eINSTANCE.getCDOResourceFolder();
    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[8] = EresourcePackage.eINSTANCE.getCDOResourceNode_Folder();
    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[9] = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[5] = EtypesPackage.eINSTANCE;
    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[6] = EtypesPackage.eINSTANCE.getBlob();
    CDOPackageRegistryImpl.SYSTEM_ELEMENTS[7] = EtypesPackage.eINSTANCE.getClob();

    if (!OMPlatform.INSTANCE.isOSGiRunning())
    {
      registerResourceFactory(Resource.Factory.Registry.INSTANCE);
    }
  }

  private CDOUtil()
  {
  }

  /**
   * Returns a {@link CriticalSection critical section} that can be used to execute a block of code that
   * needs to be synchronized with the internal operations of the given view and its {@link CDOObject objects}.
   * <p>
   * The critical section is reentrant and may be nested.
   * <p>
   * For more information see {@link CDOView#sync()}.
   *
   * @return a critical section that can be used to synchronize with the given view and its objects or
   * {@link CriticalSection#UNSYNCHRONIZED} if the given view is <code>null</code> or already closed.
   * Never <code>null</code>.
   *
   * @see #sync(Notifier)
   * @since 4.29
   */
  public static CriticalSection sync(CDOView view)
  {
    if (view == null || view.isClosed())
    {
      return CriticalSection.UNSYNCHRONIZED;
    }

    return view.sync();
  }

  /**
   * Returns a {@link CriticalSection critical section} that can be used to execute a block of code that
   * needs to be synchronized with the internal operations of the view of the given object.
   * <p>
   * The critical section is reentrant and may be nested.
   * <p>
   * For more information see {@link CDOView#sync()}.
   *
   * @return a critical section that can be used to synchronize with the view of the given object or
   * {@link CriticalSection#UNSYNCHRONIZED} if the view is <code>null</code> or already closed.
   * Never <code>null</code>.
   *
   * @see #sync(CDOView)
   * @since 4.29
   */
  public static CriticalSection sync(Notifier object)
  {
    CDOView view = getView(object);
    return sync(view);
  }

  /**
   * @since 4.0
   */
  public static boolean registerResourceFactory(Resource.Factory.Registry registry)
  {
    if (registry == null)
    {
      return false;
    }

    Map<String, Object> map = registry.getProtocolToFactoryMap();
    if (!map.containsKey(CDOURIUtil.PROTOCOL_NAME))
    {
      map.put(CDOURIUtil.PROTOCOL_NAME, CDOResourceFactory.INSTANCE);
      return true;
    }

    return false;
  }

  /**
   * Returns the map used to cache the EObject that is identified by the {@link CDOResourceImpl#getEObjectByID(String) value}
   * of its ID feature.
   * @return the map used to cache the EObject that is identified by the value of its ID feature.
   * @see #setIntrinsicIDToEObjectMap
   * @since 4.2
   */
  public static Map<String, EObject> getIntrinsicIDToEObjectMap(CDOResource resource)
  {
    return ((CDOResourceImpl)resource).getIntrinsicIDToEObjectMap();
  }

  /**
   * Sets the map used to cache the EObject identified by the value of its ID feature.
   * This cache is only activated if the map is not <code>null</code>.
   * The map will be lazily loaded by the {@link CDOResourceImpl#getEObjectByID(String) getEObjectByID} method.
   * It is up to the client to clear the cache when it becomes invalid,
   * e.g., when the ID of a previously mapped EObject is changed.
   * @param intrinsicIDToEObjectMap the new map or <code>null</code>.
   * @see #getIntrinsicIDToEObjectMap
   * @since 4.2
   */
  public static void setIntrinsicIDToEObjectMap(CDOResource resource, Map<String, EObject> intrinsicIDToEObjectMap)
  {
    ((CDOResourceImpl)resource).setIntrinsicIDToEObjectMap(intrinsicIDToEObjectMap);
  }

  /**
   * @since 3.0
   */
  public static CDOSession getSession(Object object)
  {
    if (object == null)
    {
      return null;
    }

    CDOSession session = AdapterUtil.adapt(object, CDOSession.class);
    if (session != null)
    {
      return session;
    }

    CDOSessionProvider sessionProvider = AdapterUtil.adapt(object, CDOSessionProvider.class);
    if (sessionProvider != null)
    {
      return sessionProvider.getSession();
    }

    CDOView view = AdapterUtil.adapt(object, CDOView.class);
    if (view != null)
    {
      return view.getSession();
    }

    CDOObject cdoObject = AdapterUtil.adapt(object, CDOObject.class);
    if (cdoObject != null)
    {
      return cdoObject.cdoView().getSession();
    }

    if (object instanceof CDOBranchCreationContext)
    {
      object = ((CDOBranchCreationContext)object).getBase();
    }
    else if (object instanceof CDORenameContext.WithElement)
    {
      object = ((CDORenameContext.WithElement)object).getElement();
    }

    if (!(object instanceof CDOBranch))
    {
      CDOBranchPoint branchPoint = AdapterUtil.adapt(object, CDOBranchPoint.class);
      if (branchPoint != null)
      {
        object = branchPoint.getBranch();
      }
    }

    CDOBranch branch = AdapterUtil.adapt(object, CDOBranch.class);
    if (branch != null)
    {
      InternalCDOBranchManager branchManager = (InternalCDOBranchManager)branch.getBranchManager();
      CDOCommonRepository repository = branchManager.getRepository();
      if (repository instanceof CDORepositoryInfo)
      {
        CDORepositoryInfo repositoryInfo = (CDORepositoryInfo)repository;
        return repositoryInfo.getSession();
      }
    }

    CDORemoteSessionManager remoteSessionManager = AdapterUtil.adapt(object, CDORemoteSessionManager.class);
    if (remoteSessionManager != null)
    {
      return remoteSessionManager.getLocalSession();
    }

    return null;
  }

  /**
   * Sets the {@link CDOView#getViewLock() lock} to be used for the next view that is opened in the context of the current thread.
   * <p>
   * This method is useful, for example, if EMF {@link Adapter adapters} call <code>Display.syncExec()</code> in response to CDO notifications.
   * In these cases a {@link DelegableReentrantLock} can be injected into the new {@link CDOView view},
   * which does not deadlock when both CDO's invalidation thread and the display thread acquire the view lock.
   * <p>
   * This method involves a {@link ThreadLocal} variable to avoid method explosion in {@link CDOViewContainer} and {@link CDOTransactionContainer}.
   * After calling this method make sure to either open a new {@link CDOView view} from the current thread or call <code>setNextViewLock(null)</code>
   * to clear the {@link ThreadLocal} variable.
   *
   * @see Options#setDelegableViewLockEnabled(boolean)
   * @since 4.5
   */
  public static void setNextViewLock(Lock viewLock)
  {
    AbstractCDOView.setNextViewLock(viewLock);
  }

  /**
   * @since 4.4
   */
  public static void configureView(CDOView view)
  {
    CDOViewConfigurator viewConfigurator = getViewConfigurator(view);
    if (viewConfigurator == null)
    {
      viewConfigurator = getViewConfigurator(view.getSession());
    }

    if (viewConfigurator != null)
    {
      viewConfigurator.configureView(view);
    }
    else
    {
      view.options().setLockNotificationEnabled(true);

      if (view instanceof CDOTransaction)
      {
        new CDOTransactionCommentator((CDOTransaction)view);
      }
    }
  }

  private static CDOViewConfigurator getViewConfigurator(Object object)
  {
    CDOViewConfigurator viewConfigurator = PropertiesContainerUtil.getProperty(object, PROP_VIEW_CONFIGURATOR, CDOViewConfigurator.class);
    if (viewConfigurator == null)
    {
      String type = PropertiesContainerUtil.getProperty(object, PROP_VIEW_CONFIGURATOR_TYPE, String.class);
      if (type != null)
      {
        IManagedContainer container = ContainerUtil.getContainer(object);
        if (container != null)
        {
          String description = PropertiesContainerUtil.getProperty(object, PROP_VIEW_CONFIGURATOR_DESCRIPTION, String.class);
          viewConfigurator = container.getElementOrNull(CDOViewConfigurator.Factory.PRODUCT_GROUP, type, description);
        }
      }
    }

    return viewConfigurator;
  }

  /**
   * @since 2.0
   */
  public static boolean prepareDynamicEPackage(EPackage startPackage)
  {
    if (CDOFactoryImpl.prepareDynamicEPackage(startPackage))
    {
      for (EPackage subPackage : startPackage.getESubpackages())
      {
        prepareDynamicEPackage(subPackage);
      }

      return true;
    }

    return false;
  }

  /**
   * @since 2.0
   */
  public static CDOCollectionLoadingPolicy createCollectionLoadingPolicy(int initialChunkSize, int resolveChunkSize)
  {
    return new CDOCollectionLoadingPolicyImpl(initialChunkSize, resolveChunkSize);
  }

  /**
   * @since 2.0
   */
  public static CDORevisionPrefetchingPolicy createRevisionPrefetchingPolicy(int chunkSize)
  {
    if (chunkSize <= 0)
    {
      return CDORevisionPrefetchingPolicy.NO_PREFETCHING;
    }

    return new CDORevisionPrefetchingPolicyImpl(chunkSize);
  }

  /**
   * @since 4.1
   */
  public static CDOFetchRuleManager createThreadLocalFetchRuleManager()
  {
    return new CDOFetchRuleManagerThreadLocal();
  }

  /**
   * @since 4.1
   */
  public static CDOFeatureAnalyzer createModelBasedFeatureAnalyzer()
  {
    return new CDOFeatureAnalyzerModelBased();
  }

  /**
   * @since 4.1
   */
  public static CDOFeatureAnalyzer createUIFeatureAnalyzer()
  {
    return new CDOFeatureAnalyzerUI();
  }

  /**
   * @since 4.1
   */
  public static CDOFeatureAnalyzer createUIFeatureAnalyzer(long maxTimeBetweenOperation)
  {
    return new CDOFeatureAnalyzerUI(maxTimeBetweenOperation);
  }

  /**
   * @since 4.0
   */
  public static CDOXATransaction createXATransaction(Notifier... notifiers)
  {
    CDOXATransaction xaTransaction = new CDOXATransactionImpl();
    for (Notifier notifier : notifiers)
    {
      CDOViewSet viewSet = getViewSet(notifier);
      if (viewSet == null)
      {
        throw new IllegalArgumentException("Notifier is not associated with a CDOViewSet: " + notifier);
      }

      try
      {
        xaTransaction.add(viewSet);
      }
      catch (IllegalArgumentException ex)
      {
        OM.LOG.warn(ex);
      }
    }

    return xaTransaction;
  }

  /**
   * @since 2.0
   */
  public static CDOXATransaction getXATransaction(CDOViewSet viewSet)
  {
    EList<Adapter> adapters = viewSet.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof CDOXAInternalAdapter)
      {
        return ((CDOXAInternalAdapter)adapter).getXATransaction();
      }
    }

    return null;
  }

  /**
   * @since 4.0
   */
  public static CDOViewSet getViewSet(Notifier notifier)
  {
    if (notifier == null)
    {
      return null;
    }

    if (notifier instanceof CDOViewSet)
    {
      return (CDOViewSet)notifier;
    }

    if (notifier instanceof Resource)
    {
      Resource resource = (Resource)notifier;

      ResourceSet resourceSet = resource.getResourceSet();
      if (resourceSet != null)
      {
        CDOViewSet viewSet = getViewSet(resourceSet);
        if (viewSet != null)
        {
          return viewSet;
        }
      }
      else if (resource instanceof CDOResource)
      {
        CDOResource cdoResource = (CDOResource)resource;

        CDOView view = cdoResource.cdoView();
        return view == null ? null : view.getViewSet();
      }
    }

    EList<Adapter> adapters = notifier.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof CDOViewSet)
      {
        return (CDOViewSet)adapter;
      }
    }

    if (notifier instanceof InternalEObject)
    {
      InternalEObject object = (InternalEObject)notifier;
      EObject container = object.eContainer();
      if (container != null)
      {
        CDOViewSet viewSet = getViewSet(container);
        if (viewSet != null)
        {
          return viewSet;
        }
      }

      Resource.Internal resource = object.eDirectResource();
      if (resource != null && resource != object)
      {
        CDOViewSet viewSet = getViewSet(resource);
        if (viewSet != null)
        {
          return viewSet;
        }
      }
    }

    return null;
  }

  /**
   * @since 4.4
   */
  public static CDOView getView(Notifier notifier)
  {
    CDOViewSet viewSet = getViewSet(notifier);
    if (viewSet != null)
    {
      CDOView[] views = viewSet.getViews();
      if (views != null && views.length != 0)
      {
        return views[0];
      }
    }

    return null;
  }

  /**
   * @since 4.4
   */
  public static CDOView getView(ResourceSet resourceSet, URI uri)
  {
    return CDOViewProviderRegistry.INSTANCE.provideView(uri, resourceSet);
  }

  /**
   * @since 3.0
   */
  public static boolean isStaleObject(Object object)
  {
    if (object instanceof CDOStaleObject)
    {
      return true;
    }

    if (object instanceof Logger)
    {
      // See org.eclipse.emf.cdo.view.CDOStaleReferencePolicy.DynamicProxy
      return true;
    }

    return false;
  }

  /**
   * @since 3.0
   */
  public static void cleanStaleReference(EObject eObject, EStructuralFeature eFeature)
  {
    if (!eFeature.isMany() && eFeature.getEContainingClass() != null)
    {
      InternalCDOObject cdoObject = (InternalCDOObject)getCDOObject(eObject);

      EStore eStore = cdoObject.eStore();
      eStore.unset(cdoObject, eFeature);
    }
  }

  /**
   * @since 3.0
   */
  public static void cleanStaleReference(EObject eObject, EStructuralFeature eFeature, int index)
  {
    if (eFeature.isMany() && eFeature.getEContainingClass() != null)
    {
      InternalCDOObject cdoObject = (InternalCDOObject)getCDOObject(eObject);

      try
      {
        CDOStoreImpl.removeElement(cdoObject, eFeature, index);
      }
      catch (ObjectNotFoundException ex)
      {
        // Ignore the exception
      }
    }
  }

  /**
   * @since 2.0
   */
  public static void load(EObject eObject, CDOView view)
  {
    InternalCDOObject cdoObject = FSMUtil.adapt(eObject, view);
    CDOStateMachine.INSTANCE.read(cdoObject);

    for (Iterator<InternalCDOObject> it = FSMUtil.iterator(cdoObject.eContents(), (InternalCDOView)view); it.hasNext();)
    {
      InternalCDOObject content = it.next();
      load(content, view);
    }
  }

  /**
   * @since 4.2
   */
  public static CDOObject wrapExternalObject(EObject object, CDOView view)
  {
    return new CDOExternalObject((InternalEObject)object, (InternalCDOView)view);
  }

  /**
   * @since 4.23
   */
  public static boolean isReadable(EObject object)
  {
    CDOObject cdoObject = getCDOObject(object);
    if (cdoObject != null)
    {
      return cdoObject.cdoPermission().isReadable();
    }

    return false;
  }

  /**
   * @since 4.23
   */
  public static boolean isWritable(EObject object)
  {
    CDOObject cdoObject = getCDOObject(object);
    if (cdoObject != null)
    {
      return cdoObject.cdoPermission().isWritable();
    }

    return false;
  }

  /**
   * @since 2.0
   */
  public static EObject getEObject(EObject object)
  {
    if (object instanceof InternalCDOObject)
    {
      return ((InternalCDOObject)object).cdoInternalInstance();
    }

    return object;
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EObject object)
  {
    return getCDOObject(object, true);
  }

  /**
   * @since 4.4
   */
  public static CDOObject getCDOObject(EObject object, boolean adaptLegacy)
  {
    if (object == null)
    {
      return null;
    }

    if (object instanceof InternalCDOObject)
    {
      return (CDOObject)object;
    }

    if (adaptLegacy)
    {
      return FSMUtil.adaptLegacy((InternalEObject)object);
    }

    return (CDOObject)FSMUtil.getLegacyAdapter(object);
  }

  /**
   * @since 4.6
   */
  public static List<? extends CDOObject> getCDOObjects(EObject... objects)
  {
    List<CDOObject> result = new ArrayList<>();
    for (EObject object : objects)
    {
      result.add(getCDOObject(object));
    }

    return result;
  }

  /**
   * @since 4.6
   */
  public static List<? extends CDOObject> getCDOObjects(Collection<? extends EObject> objects)
  {
    List<CDOObject> result = new ArrayList<>();
    for (EObject object : objects)
    {
      result.add(getCDOObject(object));
    }

    return result;
  }

  /**
   * @since 4.4
   */
  public static boolean isCDOObject(EObject object)
  {
    return getCDOObject(object, false) != null;
  }

  /**
   * @since 4.6
   */
  public static CDOBranch createBranch(CDOBranchPoint base, String name)
  {
    return base.getBranch().createBranch(name, base.getTimeStamp());
  }

  /**
   * @since 4.3
   */
  public static <T extends EObject> EList<T> filterReadables(Collection<T> collection)
  {
    EList<T> result = new BasicEList<>();
    for (T element : collection)
    {
      CDOObject object = getCDOObject(element);
      if (object.cdoRevision().isReadable())
      {
        result.add(element);
      }
    }

    return result;
  }

  /**
   * @since 4.20
   */
  public static CDORevisionDelta getRevisionDelta(CDOObject object)
  {
    CDOView view = object.cdoView();
    if (view instanceof CDOTransaction)
    {
      CDOTransaction transaction = (CDOTransaction)view;

      Map<CDOID, CDORevisionDelta> revisionDeltas = transaction.getLastSavepoint().getRevisionDeltas2();
      return revisionDeltas.get(object.cdoID());
    }

    return null;
  }

  /**
   * @since 4.4
   */
  public static CDOBranchPointRange getLifetime(CDOObject object)
  {
    CDORevisionManager revisionManager = object.cdoView().getSession().getRevisionManager();
    return revisionManager.getObjectLifetime(object.cdoID(), object.cdoRevision());
  }

  /**
   * @since 2.0
   */
  public static CDORevision getRevisionByVersion(CDOObject object, int version)
  {
    if (FSMUtil.isTransient(object))
    {
      return null;
    }

    CDORevision revision = CDOStateMachine.INSTANCE.read((InternalCDOObject)object);
    return getRevisionByVersion(object, revision.getBranch(), version, revision);
  }

  /**
   * @since 3.0
   */
  public static CDORevision getRevisionByVersion(CDOObject object, CDOBranch branch, int version)
  {
    if (FSMUtil.isTransient(object))
    {
      return null;
    }

    CDORevision revision = CDOStateMachine.INSTANCE.read((InternalCDOObject)object);
    return getRevisionByVersion(object, branch, version, revision);
  }

  private static CDORevision getRevisionByVersion(CDOObject object, CDOBranch branch, int version, CDORevision revision)
  {
    if (revision.getVersion() != version)
    {
      CDOSession session = object.cdoView().getSession();
      if (!session.getRepositoryInfo().isSupportingAudits())
      {
        throw new IllegalStateException(Messages.getString("CDOUtil.0")); //$NON-NLS-1$
      }

      revision = session.getRevisionManager().getRevisionByVersion(object.cdoID(), branch.getVersion(version), 0, true);
    }

    return revision;
  }

  /**
   * @since 2.0
   */
  public static EList<Resource> getResources(ResourceSet resourceSet)
  {
    EList<Resource> result = new BasicEList<>();
    EList<Resource> resources = resourceSet.getResources();
    for (Resource resource : resources)
    {
      if (resource instanceof CDOResource)
      {
        CDOResource cdoResource = (CDOResource)resource;
        if (cdoResource.isRoot())
        {
          continue;
        }
      }

      result.add(resource);
    }

    return result;
  }

  /**
   * Returns <code>true</code> if the given {@link CDOSession session} contains a dirty {@link CDOTransaction
   * transaction}, <code>false</code> otherwise.
   *
   * @since 2.0
   * @see CDOTransaction
   */
  public static boolean isSessionDirty(CDOSession session)
  {
    for (CDOView view : session.getElements())
    {
      if (view.isDirty())
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @since 3.0
   * @deprecated As of 4.0 use CDOView.isInvalidationRunnerActive()
   */
  @Deprecated
  public static boolean isInvalidationRunnerActive()
  {
    throw new UnsupportedOperationException("Use CDOView.isInvalidationRunnerActive()");
  }

  /**
   * Queries whether an object is writable (is permitted to be modified in the
   * current view context).
   *
   * @param eObject an object
   * @return {@code false} if the {@code eObject} is managed by CDO and does not
   *         have {@linkplain CDOPermission#WRITE write permission};
   *         {@code true}, otherwise
   *
   * @since 4.3
   */
  public static boolean isWritableObject(EObject eObject)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
    if (cdoObject != null)
    {
      CDOView view = cdoObject.cdoView();

      // If the object is not in a view, then permissions aren't applicable
      return view == null || !view.isReadOnly() && cdoObject.cdoPermission().isWritable();
    }

    // If there is no CDOObject, then this object is implicitly writable
    return true;
  }

  /**
   * @since 3.0
   */
  public static boolean isLegacyObject(EObject object)
  {
    return object instanceof CDOObjectWrapper;
  }

  /**
   * @since 3.0
   * @deprecated As of 4.2 the legacy mode is always enabled.
   */
  @Deprecated
  public static boolean isLegacyModeDefault()
  {
    return true;
  }

  /**
   * @since 3.0
   * @deprecated As of 4.2 the legacy mode is always enabled.
   */
  @Deprecated
  public static void setLegacyModeDefault(boolean on)
  {
  }

  /**
   * @since 4.0
   */
  public static void setCredentialsProvider(URI uri, IPasswordCredentialsProvider provider)
  {
    CDOURIData data = new CDOURIData(uri);
    data.setUserName(null);
    data.setPassWord(null);
    data.setResourcePath(null);
    data.setBranchPath(null);
    data.setTimeStamp(CDOBranchPoint.UNSPECIFIED_DATE);
    data.setTransactional(false);

    String resource = data.toString();
    IPluginContainer.INSTANCE.putElement(CredentialsProviderFactory.PRODUCT_GROUP, "password", resource, provider);

    // The following is to stay compatible with the formerly wrong product group (".security" was missing).
    IPluginContainer.INSTANCE.putElement("org.eclipse.net4j.util.credentialsProviders", "password", resource, provider);
  }

  /**
   * @since 4.3
   */
  public static String getAnnotation(ModelElement modelElement, String sourceURI, String key)
  {
    Annotation annotation = modelElement.getAnnotation(sourceURI);
    return annotation == null ? null : (String)annotation.getDetails().get(key);
  }

  /**
   * @since 4.3
   */
  public static Annotation setAnnotation(ModelElement modelElement, String sourceURI, String key, String value)
  {
    Annotation annotation = modelElement.getAnnotation(sourceURI);
    if (value == null)
    {
      if (annotation != null)
      {
        annotation.getDetails().removeKey(key);
      }
    }
    else
    {
      if (annotation == null)
      {
        annotation = EtypesFactory.eINSTANCE.createAnnotation();
        annotation.setSource(sourceURI);
        modelElement.getAnnotations().add(annotation);
      }

      annotation.getDetails().put(key, value);
    }

    return annotation;
  }

  /**
   * @since 4.3
   */
  public static String getDocumentation(ModelElement modelElement)
  {
    return getAnnotation(modelElement, CDO_ANNOTATION_URI, DOCUMENTATION_KEY);
  }

  /**
   * @since 4.3
   */
  public static Annotation setDocumentation(ModelElement modelElement, String value)
  {
    return setAnnotation(modelElement, CDO_ANNOTATION_URI, DOCUMENTATION_KEY, value);
  }

  /**
   * @since 4.13
   */
  public static InputStream openInputStream(CDOResourceLeaf leaf) throws IOException
  {
    if (leaf instanceof CDOBinaryResource)
    {
      CDOBinaryResource binary = (CDOBinaryResource)leaf;
      return openInputStream(binary.getContents(), null);
    }

    if (leaf instanceof CDOTextResource)
    {
      CDOTextResource text = (CDOTextResource)leaf;
      return openInputStream(text.getContents(), text.getEncoding());
    }

    if (leaf instanceof CDOResource)
    {
      CDOResource resource = (CDOResource)leaf;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      resource.save(baos, null);
      return new ByteArrayInputStream(baos.toByteArray());
    }

    // Can't realistically happen.
    return null;
  }

  /**
   * @since 4.13
   */
  public static InputStream openInputStream(CDOLob<?> lob, String encoding) throws IOException
  {
    if (lob instanceof CDOBlob)
    {
      CDOBlob blob = (CDOBlob)lob;
      return blob.getContents();
    }

    if (lob instanceof CDOClob)
    {
      CDOClob clob = (CDOClob)lob;
      return new ReaderInputStream(clob.getContents(), encoding);
    }

    return new EmptyInputStream();
  }
}
