/*
 * Copyright (c) 2009-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.store.logic;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.Transaction;
import org.eclipse.emf.cdo.internal.server.Transaction.InternalCommitContext;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContextImpl.TransactionPackageRegistry;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.util.ModelUtil;

import org.eclipse.emf.internal.cdo.session.SessionUtil;

import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.om.monitor.Monitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class TestLogic extends AbstractOMTest
{
  public static final String REPOSITORY_NAME = "repo1";

  protected Repository repository;

  public TestLogic()
  {
  }

  @Override
  protected void doSetUp() throws Exception
  {
    repository = createRepository();
    repository.activate();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    repository.deactivate();
    repository = null;
  }

  protected Repository createRepository()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.REVISED_LRU_CAPACITY, "10000");

    Map<String, String> testProperties = getTestProperties();
    if (testProperties != null)
    {
      props.putAll(testProperties);
    }

    Repository repository = new Repository.Default();
    repository.setName(REPOSITORY_NAME);
    repository.setProperties(props);
    repository.setStore(createStore());
    return repository;
  }

  protected boolean withRevisionDeltaSupport()
  {
    return false;
  }

  protected Map<String, String> getTestProperties()
  {
    return null;
  }

  protected abstract IStore createStore();

  public void testCreateModel1() throws Exception
  {
    CommitTemplate template = new CommitTemplate();
    template.addNewPackage(Model1Package.eINSTANCE);
    Transaction transaction = template.run();
    verifyCreateModel1(transaction);
    template.dispose();
  }

  protected abstract void verifyCreateModel1(Transaction transaction) throws Exception;

  public void testCreateModel2() throws Exception
  {
    CommitTemplate template = new CommitTemplate();
    template.addNewPackage(Model1Package.eINSTANCE);
    template.addNewPackage(Model2Package.eINSTANCE);
    Transaction transaction = template.run();
    verifyCreateModel2(transaction);
    template.dispose();
  }

  protected abstract void verifyCreateModel2(Transaction transaction) throws Exception;

  public void testCreateModel3() throws Exception
  {
    CommitTemplate template = new CommitTemplate();
    template.addNewPackage(Model3Package.eINSTANCE);
    Transaction transaction = template.run();
    verifyCreateModel3(transaction);
    template.dispose();
  }

  protected abstract void verifyCreateModel3(Transaction transaction) throws Exception;

  public void testCreateMango() throws Exception
  {
    CommitTemplate template = new CommitTemplate();
    template.addNewPackage(MangoPackage.eINSTANCE);
    Transaction transaction = template.run();
    verifyCreateMango(transaction);
    template.dispose();
  }

  protected abstract void verifyCreateMango(Transaction transaction) throws Exception;

  public void testCommitCompany() throws Exception
  {
    CommitTemplate template = new CommitTemplate();
    template.addNewPackage(Model1Package.eINSTANCE);

    TestRevision resource = template.addNewResource(1);
    resource.set("path", "/res1");

    TestRevision company = template.addNewObject(2, Model1Package.eINSTANCE.getCompany());
    company.set("name", "Sympedia");
    company.set("street", "Homestr. 17");
    company.set("city", "Berlin");

    Transaction transaction = template.run();
    verifyCommitCompany(transaction);
    template.dispose();
  }

  protected abstract void verifyCommitCompany(Transaction transaction) throws Exception;

  /**
   * @author Eike Stepper
   */
  protected class CommitTemplate
  {
    private int viewID;

    private CDOServerProtocol protocol;

    private Session session;

    private Transaction transaction;

    private InternalCommitContext transactionCommitContext;

    private List<EPackage> newPackages = new ArrayList<EPackage>();

    private List<CDORevision> newObjects = new ArrayList<CDORevision>();

    private List<CDORevisionDelta> dirtyObjectDeltas = new ArrayList<CDORevisionDelta>();

    private List<CDOID> detachedObjects = new ArrayList<CDOID>();

    public CommitTemplate()
    {
      this(1, 12345);
    }

    public CommitTemplate(int viewID, long timeStamp)
    {
      this.viewID = viewID;
      protocol = createProtocol();
      session = repository.getSessionManager().openSession(protocol);
      protocol.setInfraStructure(session);
      transaction = createTransaction(session);
      transactionCommitContext = transaction.createCommitContext(timeStamp);
      transactionCommitContext.preCommit();
    }

    public Session getSession()
    {
      return session;
    }

    public Transaction run() throws Exception
    {
      transactionCommitContext.setNewPackages(getNewPackages());
      transactionCommitContext.setNewObjects(getNewObjects());
      transactionCommitContext.setDirtyObjectDeltas(getDirtyObjectDeltas());
      transactionCommitContext.setDetachedObjects(getDetachedObjects());
      transactionCommitContext.write(new Monitor());
      transactionCommitContext.commit(new Monitor());
      transactionCommitContext.postCommit(true);
      return transaction;
    }

    public void dispose()
    {
      protocol.deactivate();
    }

    public EPackage addNewPackage(EPackage ePackage)
    {
      String uri = ePackage.getNsURI();
      String parentURI = EMFUtil.getParentURI(ePackage);
      String name = ePackage.getName();
      boolean dynamic = EMFUtil.isDynamicEPackage(ePackage);
      String ecore = null;

      if (parentURI == null)
      {
        if (!EcorePackage.eINSTANCE.getNsURI().equals(uri))
        {
          ecore = EMFUtil.ePackageToString(ePackage, EPackage.Registry.INSTANCE);
        }
      }

      TransactionPackageRegistry packageManager = transactionCommitContext.getPackageRegistry();
      EPackage newPackage = CDOModelUtil.createPackage(packageManager, uri, name, ecore, dynamic, parentURI);
      ModelUtil.initializeEPackage(ePackage, newPackage);
      packageManager.addPackage(newPackage);
      newPackages.add(newPackage);
      return newPackage;
    }

    public TestRevision addNewResource(int id)
    {
      CDOResourcePackage resourcePackage = repository.getPackageRegistry().getCDOResourcePackage();
      CDOResourceClass resourceClass = resourcePackage.getCDOResourceClass();
      return addRevision(id, resourceClass);
    }

    public TestRevision addNewObject(int id, EClass eClass)
    {
      String uri = eClass.getEPackage().getNsURI();

      EPackage ePackage = transactionCommitContext.getPackageRegistry().lookupPackage(uri);
      EClass eClass = ePackage.lookupClass(eClass.getClassifierID());
      return addRevision(id, eClass);
    }

    private TestRevision addRevision(int id, EClass eClass)
    {
      CDOIDTemp tempID = CDOIDUtil.createTempObject(id);
      TestRevision newObject = new TestRevision(eClass, tempID);
      newObjects.add(newObject);
      return newObject;
    }

    public void addDirtyObjectDelta(CDORevisionDelta dirtyObjectDelta)
    {
      dirtyObjectDeltas.add(dirtyObjectDelta);
    }

    protected CDOServerProtocol createProtocol()
    {
      return new CDOServerProtocol(null);
    }

    protected Transaction createTransaction(Session session)
    {
      return (Transaction)session.openTransaction(viewID);
    }

    private EPackage[] getNewPackages()
    {
      return newPackages.toArray(new EPackage[newPackages.size()]);
    }

    private CDORevision[] getNewObjects()
    {
      return newObjects.toArray(new CDORevision[newObjects.size()]);
    }

    private CDORevisionDelta[] getDirtyObjectDeltas()
    {
      return dirtyObjectDeltas.toArray(new CDORevisionDelta[dirtyObjectDeltas.size()]);
    }

    private CDOID[] getDetachedObjects()
    {
      return detachedObjects.toArray(new CDOID[detachedObjects.size()]);
    }
  }

  /**
   * @author Eike Stepper
   */
  protected class TestRevision extends CDORevisionImpl
  {
    public TestRevision(EClass eClass, CDOID id)
    {
      super(eClass, id);
    }

    public void set(String featureName, Object value)
    {
      set(featureName, 0, value);
    }

    public void set(String featureName, int index, Object value)
    {
      EStructuralFeature feature = getEClass().lookupFeature(featureName);
      set(feature, index, value);
    }
  }
}
