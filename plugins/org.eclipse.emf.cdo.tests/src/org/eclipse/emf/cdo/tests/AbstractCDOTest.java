/*
 * Copyright (c) 2007-2013, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUpdatable;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.object.CDOLegacyWrapper;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.ThrowableEvent;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.spi.cdo.FSMUtil;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOTest extends ConfigTest
{
  static
  {
    // Make sure that the Net4j resource factories are registered before registrations become impossible.
    CDONet4jUtil.createNet4jSessionConfiguration();

    makeUnmodifiable("protocolToFactoryMap");
    makeUnmodifiable("extensionToFactoryMap");
    makeUnmodifiable("contentTypeIdentifierToFactoryMap");
  }

  private static void makeUnmodifiable(String fieldName)
  {
    Field field = ReflectUtil.getField(ResourceFactoryRegistryImpl.class, fieldName);

    @SuppressWarnings("unchecked")
    Map<String, Object> map = (Map<String, Object>)ReflectUtil.getValue(field, Resource.Factory.Registry.INSTANCE);
    ReflectUtil.setValue(field, Resource.Factory.Registry.INSTANCE, Collections.unmodifiableMap(map));
  }

  @Override
  protected void doSetUp() throws Exception
  {
    try
    {
      super.doSetUp();
    }
    finally
    {
      org.eclipse.emf.cdo.internal.net4j.bundle.OM.PREF_COMMIT_MONITOR_PROGRESS_SECONDS.setValue(60);
      org.eclipse.emf.cdo.internal.net4j.bundle.OM.PREF_COMMIT_MONITOR_TIMEOUT_SECONDS.setValue(60 * 60);
      CDOPackageTypeRegistry.INSTANCE.reset();
      startTransport();
    }
  }

  @Override
  protected void doTearDown() throws Exception
  {
    // This override adds no functionality.
    // It just ensures that the IDE shows doSetUp() and doTearDown() in the same class.
    super.doTearDown();
  }

  /**
   * Closes the given session and waits until the server-side session is closed, too.
   * In TCP scenarios the latter does not happen synchronously, which can confuse tests that count sessions.
   */
  public void closeSession(CDOSession session) throws InterruptedException
  {
    final int sessionID = session.getSessionID();
    session.close();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return getRepository().getSessionManager().getSession(sessionID) == null;
      }
    }.assertNoTimeOut();
  }

  public InternalSession serverSession(CDOSession session)
  {
    String repositoryName = session.getRepositoryInfo().getName();
    InternalRepository repository = getRepository(repositoryName);
    return repository.getSessionManager().getSession(session.getSessionID());
  }

  public InternalView serverView(CDOView view)
  {
    InternalSession serverSession = serverSession(view.getSession());
    return serverSession.getView(view.getViewID());
  }

  public InternalTransaction serverTransaction(CDOTransaction transaction)
  {
    return (InternalTransaction)serverView(transaction);
  }

  public CDOBranch serverBranch(CDOBranch branch)
  {
    CDORepositoryInfo repositoryInfo = (CDORepositoryInfo)branch.getBranchManager().getRepository();
    InternalRepository repository = getRepository(repositoryInfo.getName());
    return repository.getBranchManager().getBranch(branch.getID());
  }

  public LockState<Object, IView> serverLockState(CDOSession session, CDOLockState lockState)
  {
    Object serverLockTarget = serverLockTarget(lockState.getLockedObject());

    InternalSession serverSession = serverSession(session);
    InternalLockManager lockingManager = serverSession.getRepository().getLockingManager();
    return lockingManager.getLockState(serverLockTarget);
  }

  public Object serverLockTarget(Object lockTarget)
  {
    if (lockTarget instanceof CDOIDAndBranch)
    {
      CDOIDAndBranch idAndBranch = (CDOIDAndBranch)lockTarget;
      CDOBranch serverBranch = serverBranch(idAndBranch.getBranch());
      return CDOIDUtil.createIDAndBranch(idAndBranch.getID(), serverBranch);
    }

    return lockTarget;
  }

  public int createModel(Category parent, int levels, int categories, int products)
  {
    return createModel(parent, levels, categories, products, null);
  }

  public int createModel(Category parent, int levels, int categories, int products, Runnable commit)
  {
    Model1Factory factory = getModel1Factory();
    int count = 0;

    EList<Category> categoriesList = parent.getCategories();
    for (int i = 0; i < categories; i++)
    {
      Category category = factory.createCategory();
      category.setName("Category" + levels + "-" + i);
      categoriesList.add(category);
      ++count;
    }

    EList<Product1> productsList = parent.getProducts();
    for (int i = 0; i < products; i++)
    {
      Product1 product = factory.createProduct1();
      product.setName("Product" + levels + "-" + i);
      productsList.add(product);
      ++count;
    }

    if (commit != null)
    {
      commit.run();
    }

    if (levels > 0)
    {
      for (Category category : categoriesList)
      {
        count += createModel(category, levels - 1, categories, products, commit);
      }
    }

    return count;
  }

  public static void assertEquals(Object expected, Object actual)
  {
    if (expected instanceof CDOID || expected instanceof CDOBranch)
    {
      if (expected != actual)
      {
        failNotEquals(null, expected, actual);
      }

      return;
    }

    // IMPORTANT: Give possible CDOLegacyWrapper a chance for actual, too
    if (actual != null && actual.equals(expected))
    {
      return;
    }

    AbstractOMTest.assertEquals(expected, actual);
  }

  protected static void assertTransient(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(true, FSMUtil.isTransient(object));
      assertNull(object.cdoID());
      assertNull(object.cdoRevision());
      assertNull(object.cdoView());
    }
  }

  protected static void assertInvalid(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(true, FSMUtil.isInvalid(object));
    }
  }

  protected static void assertNotTransient(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertEquals(false, FSMUtil.isTransient(object));
    assertNotNull(object.cdoID());
    assertNotNull(object.cdoRevision());
    assertNotNull(object.cdoView());
    assertNotNull(object.eResource());
    assertEquals(view, object.cdoView());
    assertEquals(object, view.getObject(object.cdoID(), false));
  }

  protected static void assertNew(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.NEW, object.cdoState());
  }

  protected static void assertDirty(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.DIRTY, object.cdoState());
  }

  protected static void assertClean(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.CLEAN, object.cdoState());
  }

  protected static void assertProxy(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(false, FSMUtil.isTransient(object));
      assertNotNull(object.cdoID());
      assertNotNull(object.cdoView());
      assertEquals(CDOState.PROXY, object.cdoState());
    }
  }

  protected static void assertContent(EObject eContainer, EObject eContained)
  {
    CDOObject container = CDOUtil.getCDOObject(eContainer);
    CDOObject contained = CDOUtil.getCDOObject(eContained);
    if (container != null && contained != null)
    {
      assertEquals(true, container.eContents().contains(contained));
      if (container instanceof CDOResource)
      {
        assertEquals(container, contained.eResource());
        assertEquals(null, contained.eContainer());
        assertEquals(true, ((CDOResource)container).getContents().contains(contained));
      }
      else
      {
        assertEquals(container.eResource(), contained.eResource());
        assertEquals(container, contained.eContainer());
      }
    }
  }

  protected static void assertNotProxy(Object object)
  {
    assertEquals(false, CDOLegacyWrapper.isLegacyProxy(object));
  }

  protected static void assertCreatedTime(EObject eObject, long timeStamp)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(timeStamp, object.cdoRevision().getTimeStamp());
    }
  }

  protected static void dumpRevisions(String label, Map<CDOBranch, List<CDORevision>> revisions)
  {
    System.out.println();
    System.out.println();
    System.out.println(label);
    System.out.println("===============================================================================================");
    CDORevisionUtil.dumpAllRevisions(revisions, System.out);
    System.out.println();
    System.out.println();
  }

  protected static void dumpAllRevisions(Object allRevisionsProvider)
  {
    try
    {
      String label = allRevisionsProvider.toString();
      IOUtil.OUT().println(label);
      for (int i = 0; i < label.length(); i++)
      {
        IOUtil.OUT().print("=");
      }

      IOUtil.OUT().println();
      if (allRevisionsProvider instanceof CDOAllRevisionsProvider)
      {
        CDOAllRevisionsProvider provider = (CDOAllRevisionsProvider)allRevisionsProvider;
        Map<CDOBranch, List<CDORevision>> map = provider.getAllRevisions();
        IOUtil.OUT().println(CDORevisionUtil.dumpAllRevisions(map));
      }
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }
  }

  protected static void dumpAllContents(Notifier root)
  {
    try
    {
      StringBuilder builder = new StringBuilder();
      dumpAllContents(root, "", builder);
      IOUtil.OUT().println(builder);
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }
  }

  private static void dumpAllContents(Notifier object, String indent, StringBuilder builder)
  {
    builder.append(indent);
    if (object instanceof ResourceSet)
    {
      ResourceSet resourceSet = (ResourceSet)object;
      builder.append("ResourceSet");
      builder.append(StringUtil.NL);

      for (Resource resource : resourceSet.getResources())
      {
        dumpAllContents(resource, indent + "  ", builder);
      }
    }
    else if (object instanceof Resource)
    {
      Resource resource = (Resource)object;
      if (object instanceof CDOResource)
      {
        builder.append("CDOResource[uri=" + ((CDOResource)resource).getName() + "]");
      }
      else
      {
        builder.append("Resource[uri=" + resource.getURI() + "]");
      }

      builder.append(StringUtil.NL);
      for (EObject child : resource.getContents())
      {
        dumpAllContents(child, indent + "  ", builder);
      }
    }
    else
    {
      EObject eObject = (EObject)object;
      CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
      builder.append(cdoObject.toString());

      boolean added = false;
      for (EStructuralFeature feature : cdoObject.eClass().getEAllStructuralFeatures())
      {
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          if (reference.isContainment() || reference.isContainer())
          {
            continue;
          }
        }

        if (cdoObject.eIsSet(feature))
        {
          if (added)
          {
            builder.append(", ");
          }
          else
          {
            builder.append("[");
          }

          added = true;

          Object value = cdoObject.eGet(feature);
          builder.append(feature.getName() + "=" + value);
        }
      }

      if (added)
      {
        builder.append("]");
      }

      builder.append(StringUtil.NL);
      for (TreeIterator<EObject> it = cdoObject.eAllContents(); it.hasNext();)
      {
        EObject child = it.next();
        dumpAllContents(child, indent + "  ", builder);
      }
    }
  }

  protected static CDOCommitInfo commitAndSync(CDOTransaction transaction, CDOUpdatable... updatables) throws CommitException
  {
    return commitAndSync(transaction, null, updatables);
  }

  protected static CDOCommitInfo commitAndSync(CDOTransaction transaction, String comment, CDOUpdatable... updatables) throws CommitException
  {
    final RuntimeException[] exception = { null };
    IListener listener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (exception[0] == null && event instanceof ThrowableEvent)
        {
          ThrowableEvent e = (ThrowableEvent)event;
          exception[0] = new RuntimeException(e.getThrowable());
        }
      }
    };

    for (CDOUpdatable updatable : updatables)
    {
      if (updatable instanceof CDOView)
      {
        CDOView view = (CDOView)updatable;
        view.addListener(listener);
      }
    }

    try
    {
      if (comment != null)
      {
        transaction.setCommitComment(comment);
      }

      CDOCommitInfo info = transaction.commit();
      if (info != null)
      {
        for (CDOUpdatable updatable : updatables)
        {
          if (!updatable.waitForUpdate(info.getTimeStamp(), DEFAULT_TIMEOUT))
          {
            if (exception[0] == null)
            {
              exception[0] = new TimeoutRuntimeException(updatable.toString() + " did not receive an update of " + info);
            }

            break;
          }
        }
      }

      if (exception[0] != null)
      {
        throw exception[0];
      }

      return info;
    }
    finally
    {
      for (CDOUpdatable updatable : updatables)
      {
        if (updatable instanceof CDOView)
        {
          CDOView view = (CDOView)updatable;
          view.removeListener(listener);
        }
      }
    }
  }
}
