/*
 * Copyright (c) 2011-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.server.IRepository.ReadAccessHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Egidijus Vaisnora
 */
@CleanRepositoriesBefore(reason = "TEST_REVISION_MANAGER")
@CleanRepositoriesAfter(reason = "TEST_REVISION_MANAGER")
public class Bugzilla_340961_Test extends AbstractCDOTest
{
  private PartialReadAccessHandler handler = new PartialReadAccessHandler();

  private final CDORevisionFactory revisionFactory = new CDORevisionFactory()
  {
    @Override
    public CDORevision createRevision(EClass eClass)
    {
      return new CustomCDORevision(eClass);
    }
  };

  @Override
  protected void doSetUp() throws Exception
  {
    // setup RepositoryConfig.PROP_TEST_REVISION_MANAGER is needed for MEM store
    InternalCDORevisionManager internalCDORevisionManager = (InternalCDORevisionManager)getRepositoryConfig()
        .getTestProperty(RepositoryConfig.PROP_TEST_REVISION_MANAGER);
    if (internalCDORevisionManager == null)
    {
      internalCDORevisionManager = new TestRevisionManager();
      getRepositoryConfig().getTestProperties().put(RepositoryConfig.PROP_TEST_REVISION_MANAGER, internalCDORevisionManager);
    }
    internalCDORevisionManager.setFactory(revisionFactory);
    super.doSetUp();
    getRepository().addHandler(handler);
  }

  /**
   * @category Session
   */
  @Override
  public CDOSession openSession()
  {
    CDOSession openSession = super.openSession();
    CDORevisionManager revisionManager = openSession.getRevisionManager();
    ((InternalCDORevisionManager)revisionManager).deactivate();
    ((InternalCDORevisionManager)revisionManager).setFactory(revisionFactory);
    ((InternalCDORevisionManager)revisionManager).activate();
    return openSession;
  }

  /**
   * Create object structure of 3 in a containment, where middle object is "protected" Tests if custom revision was
   * successfully delivered to client. Tests if editing non-protected child elements is allowed
   */
  public void testObjectChildren() throws CommitException, IOException
  {
    CDOID closedCdoID = null;
    CDOID rootObjectID = null;
    {
      // init
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource openResource = transaction.createResource(getResourcePath("openResource"));
      Category rootObject = getModel1Factory().createCategory();
      openResource.getContents().add(rootObject);
      rootObject.getCategories().add(getModel1Factory().createCategory());
      Category closedCategory = getModel1Factory().createCategory();
      rootObject.getCategories().add(closedCategory);
      closedCategory.setName("ProtectedName");
      closedCategory.getCategories().add(getModel1Factory().createCategory());
      rootObject.getCategories().add(getModel1Factory().createCategory());
      transaction.commit();
      assertEquals("ProtectedName", closedCategory.getName());
      assertEquals(1, closedCategory.getCategories().size());
      closedCdoID = CDOUtil.getCDOObject(closedCategory).cdoID();
      rootObjectID = CDOUtil.getCDOObject(rootObject).cdoID();
      handler.setProtectedIDs(Arrays.asList(new CDOID[] { closedCdoID }));
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource openResource = transaction.getResource(getResourcePath("openResource"));
      assertEquals(1, openResource.getContents().size());

      // checking if protected object is empty - not readable for user
      CDOObject object = transaction.getObject(closedCdoID);
      CDOUtil.load(object, object.cdoView());
      Category eObject = (Category)CDOUtil.getEObject(object);
      assertEquals(null, eObject.getName());
      assertEquals(0, eObject.getCategories().size());

      Category rootCategory = (Category)CDOUtil.getEObject(transaction.getObject(rootObjectID));
      assertEquals(3, rootCategory.getCategories().size());

      // simple editing not protected elements. It must not fail
      EcoreUtil.delete(rootCategory.getCategories().get(0), false);
      transaction.commit();
      EcoreUtil.delete(rootCategory.getCategories().get(1), false);
      transaction.commit();
      rootCategory.getCategories().add(getModel1Factory().createCategory());
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      Category rootCategory = (Category)CDOUtil.getEObject(transaction.getObject(rootObjectID));
      assertEquals(2, rootCategory.getCategories().size());
      session.close();
    }

  }

  /**
   * Test covers case when one user retrieves restricted revision "empty", however another user has rights to see whole
   * revision. Server must ensure to give different copies for users
   */
  public void testMultiUserWork() throws CommitException
  {
    CDOID closedCdoID = null;
    {
      // init
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource openResource = transaction.createResource(getResourcePath("test"));
      Category rootObject = getModel1Factory().createCategory();
      openResource.getContents().add(rootObject);
      Category closedCategory = getModel1Factory().createCategory();
      rootObject.getCategories().add(closedCategory);
      closedCategory.setName("ProtectedName");
      closedCategory.getCategories().add(getModel1Factory().createCategory());
      transaction.commit();

      assertEquals("ProtectedName", closedCategory.getName());
      assertEquals(1, closedCategory.getCategories().size());
      closedCdoID = CDOUtil.getCDOObject(closedCategory).cdoID();
      handler.setProtectedIDs(Arrays.asList(new CDOID[] { closedCdoID }));
      session.close();
    }

    {
      // asserting that closed object cannot be read. Let say one user cannot see it
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource openResource = transaction.getResource(getResourcePath("test"));
      assertEquals(1, openResource.getContents().size());

      // checking if protected object is empty - not readable for user
      CDOObject object = transaction.getObject(closedCdoID);
      CDOUtil.load(object, object.cdoView());
      Category eObject = (Category)CDOUtil.getEObject(object);
      assertEquals(null, eObject.getName());
      assertEquals(0, eObject.getCategories().size());
      session.close();
    }

    {
      // opening closed object and checking if another user can see it
      handler.setProtectedIDs(Arrays.asList(new CDOID[] {}));
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource openResource = transaction.getResource(getResourcePath("test"));
      assertEquals(1, openResource.getContents().size());

      // checking if protected object is empty - not readable for user
      CDOObject object = transaction.getObject(closedCdoID);
      CDOUtil.load(object, object.cdoView());
      Category eObject = (Category)CDOUtil.getEObject(object);
      assertEquals("ProtectedName", eObject.getName());
      assertEquals(1, eObject.getCategories().size());
      session.close();
    }
  }

  /**
   * @author Egidijus Vaisnora
   */
  private static final class PartialReadAccessHandler implements ReadAccessHandler
  {
    private List<CDOID> ids = Collections.emptyList();

    public PartialReadAccessHandler()
    {
    }

    public void setProtectedIDs(List<CDOID> protectedIds)
    {
      ids = protectedIds;
    }

    @Override
    public void handleRevisionsBeforeSending(ISession session, CDORevision[] revisions, List<CDORevision> additionalRevisions) throws RuntimeException
    {
      for (int i = 0; i < revisions.length; i++)
      {
        if (revisions[i] != null && ids.contains(revisions[i].getID()))
        {
          revisions[i] = makePartialRevision((InternalCDORevision)revisions[i]);
        }
      }

      for (int i = 0; i < additionalRevisions.size(); i++)
      {
        CDORevision cdoRevision = additionalRevisions.get(i);
        if (cdoRevision != null && ids.contains(cdoRevision.getID()))
        {
          additionalRevisions.set(i, makePartialRevision((InternalCDORevision)cdoRevision));
        }
      }
    }

    private InternalCDORevision makePartialRevision(InternalCDORevision revision)
    {
      InternalCDORevision ret = revision.copy();
      EStructuralFeature[] allPersistentFeatures = ret.getClassInfo().getAllPersistentFeatures();
      for (int i = 0; i < allPersistentFeatures.length; i++)
      {
        ret.clear(allPersistentFeatures[i]);
      }

      return ret;
    }
  }

  /**
   * @author Egidijus Vaisnora
   */
  private static final class CustomCDORevision extends CDORevisionImpl
  {
    public static final byte NORMAL = 0;

    private byte mark = NORMAL;

    public CustomCDORevision(EClass eClass)
    {
      super(eClass);
    }

    protected CustomCDORevision(CustomCDORevision source)
    {
      super(source);
      mark = source.getMark();
    }

    public byte getMark()
    {
      return mark;
    }

    public void setMark(byte mark)
    {
      this.mark = mark;
    }

    @Override
    public InternalCDORevision copy()
    {
      return new CustomCDORevision(this);
    }

    @Override
    protected void readSystemValues(CDODataInput in) throws IOException
    {
      super.readSystemValues(in);
      setMark(in.readByte());
    }

    @Override
    protected void writeSystemValues(CDODataOutput out) throws IOException
    {
      super.writeSystemValues(out);
      out.writeByte(getMark());
    }

  }
}
