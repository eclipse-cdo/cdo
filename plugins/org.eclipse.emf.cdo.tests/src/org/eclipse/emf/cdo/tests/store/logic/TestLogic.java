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
package org.eclipse.emf.cdo.tests.store.logic;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceClass;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.Transaction;
import org.eclipse.emf.cdo.internal.server.Transaction.InternalCommitContext;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContextImpl.TransactionPackageManager;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.tests.AbstractOMTest;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
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
    props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, Boolean.toString(withRevisionDeltaSupport()));
    props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");

    Map<String, String> testProperties = getTestProperties();
    if (testProperties != null)
    {
      props.putAll(testProperties);
    }

    Repository repository = new Repository();
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

    Revision resource = template.addNewResource(1);
    resource.set("path", "/res1");

    Revision company = template.addNewObject(2, Model1Package.eINSTANCE.getCompany());
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

    private List<CDOPackage> newPackages = new ArrayList<CDOPackage>();

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
      transactionCommitContext.write();
      transactionCommitContext.commit();
      transactionCommitContext.postCommit(true);
      return transaction;
    }

    public void dispose()
    {
      protocol.deactivate();
    }

    public CDOPackage addNewPackage(EPackage ePackage)
    {
      String uri = ePackage.getNsURI();
      String parentURI = ModelUtil.getParentURI(ePackage);
      String name = ePackage.getName();
      boolean dynamic = EMFUtil.isDynamicEPackage(ePackage);
      String ecore = null;
      CDOIDMetaRange idRange = null;

      if (parentURI == null)
      {
        if (!EcorePackage.eINSTANCE.getNsURI().equals(uri))
        {
          ecore = EMFUtil.ePackageToString(ePackage, EPackage.Registry.INSTANCE);
        }

        idRange = CDOSessionImpl.registerEPackage(ePackage, 1, null, null);
      }

      TransactionPackageManager packageManager = transactionCommitContext.getPackageManager();
      CDOPackage newPackage = CDOModelUtil.createPackage(packageManager, uri, name, ecore, dynamic, idRange, parentURI);
      ModelUtil.initializeCDOPackage(ePackage, newPackage);
      packageManager.addPackage(newPackage);
      newPackages.add(newPackage);
      return newPackage;
    }

    public Revision addNewResource(int id)
    {
      CDOResourcePackage resourcePackage = repository.getPackageManager().getCDOResourcePackage();
      CDOResourceClass resourceClass = resourcePackage.getCDOResourceClass();
      return addRevision(id, resourceClass);
    }

    public Revision addNewObject(int id, EClass eClass)
    {

      String uri = eClass.getEPackage().getNsURI();

      CDOPackage cdoPackage = transactionCommitContext.getPackageManager().lookupPackage(uri);
      CDOClass cdoClass = cdoPackage.lookupClass(eClass.getClassifierID());
      return addRevision(id, cdoClass);
    }

    private Revision addRevision(int id, CDOClass cdoClass)
    {
      CDOIDTemp tempID = CDOIDUtil.createTempObject(id);
      Revision newObject = new Revision(cdoClass, tempID);
      newObjects.add(newObject);
      return newObject;
    }

    public void addDirtyObjectDelta(CDORevisionDelta dirtyObjectDelta)
    {
      dirtyObjectDeltas.add(dirtyObjectDelta);
    }

    protected CDOServerProtocol createProtocol()
    {
      return new CDOServerProtocol();
    }

    protected Transaction createTransaction(Session session)
    {
      return (Transaction)session.openView(viewID, CDOProtocolView.Type.TRANSACTION);
    }

    private CDOPackage[] getNewPackages()
    {
      return newPackages.toArray(new CDOPackage[newPackages.size()]);
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
  protected class Revision extends CDORevisionImpl
  {
    public Revision(CDOClass cdoClass, CDOID id)
    {
      super(repository.getRevisionManager(), cdoClass, id);
    }

    public void set(String featureName, Object value)
    {
      set(featureName, 0, value);
    }

    public void set(String featureName, int index, Object value)
    {
      CDOFeature cdoFeature = getCDOClass().lookupFeature(featureName);
      set(cdoFeature, index, value);
    }
  }
}
