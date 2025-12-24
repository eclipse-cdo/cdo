/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.issues;

import static org.junit.Assert.assertNotEquals;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobLoader;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.ContainmentProxyStrategy;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.FeatureStrategy;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.MessageDigestHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.BaseCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig.MEMStore_UT;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Provide a CDORevisionCrawler #105
 * <p>
 * See https://github.com/eclipse-cdo/cdo/issues/105
 *
 * @author Eike Stepper
 */
public class Issue_000105_Test extends AbstractCDOTest
{
  private static final String ALGORITHM = "SHA-256";

  private Category root;

  public void testAutoAnnotation() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    Annotation modelElement = EtypesFactory.eINSTANCE.createAnnotation("Model-Element");
    resource.getContents().add(modelElement);

    WriteAccessHandler handler = new WriteAccessHandler()
    {
      @Override
      public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
      {
        commitContext.modify(context -> {
          for (CDOIDAndVersion container : context.getChangeSetData().getNewObjects())
          {
            if (EtypesPackage.Literals.MODEL_ELEMENT.isSuperTypeOf(((CDORevision)container).getEClass()))
            {
              InternalCDORevision annotation = (InternalCDORevision)context.attachNewObject( //
                  container.getID(), //
                  EtypesPackage.Literals.MODEL_ELEMENT__ANNOTATIONS, //
                  EtypesPackage.Literals.ANNOTATION);
              annotation.setValue(EtypesPackage.Literals.ANNOTATION__SOURCE, "Auto-Attached-Annotation");
            }
          }
        });
      }

      @Override
      public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
      {
        // Do nothing.
      }
    };

    InternalRepository repository = getRepository();
    repository.addHandler(handler);

    try
    {
      transaction.commit();
    }
    finally
    {
      repository.removeHandler(handler);
    }

    EList<Annotation> annotations = modelElement.getAnnotations();
    assertEquals(1, annotations.size());

    Annotation annotation = annotations.get(0);
    assertEquals("Auto-Attached-Annotation", annotation.getSource());

    CDORevision revision = annotation.cdoRevision();
    assertEquals(modelElement.cdoID(), revision.data().getContainerID());
    assertEquals(3, revision.data().getContainerFeatureID());

    Annotation annotation2 = EtypesFactory.eINSTANCE.createAnnotation("Client-Side-Annotation");
    annotations.add(annotation2);
    transaction.commit();

    assertEquals(2, annotations.size());
    assertEquals(annotation2, annotations.get(1));

    CDORevision revision2 = annotation2.cdoRevision();
    assertEquals(modelElement.cdoID(), revision2.data().getContainerID());
    assertEquals(3, revision2.data().getContainerFeatureID());
  }

  public void testDigestTree() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    root = getModel1Factory().createCategory();
    root.setName("ROOT");
    resource.getContents().add(root);

    long modelSize = 1 + createModel(root, 3, 4, 5, this::commit);
    IOUtil.OUT().println("Model size: " + modelSize);

    CDOID rootID = CDOUtil.getCDOObject(root).cdoID();
    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    InternalView serverView = serverSession.getView(transaction.getViewID());

    Pair<String, Long> result = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null);
    String hex1 = result.getElement1();
    long count = result.getElement2();
    assertEquals(modelSize, count);

    String hex2 = getDigest(rootID, ContainmentProxyStrategy.Physical, serverView, null).getElement1();
    assertEquals(hex1, hex2);
  }

  public void testDigestRepo() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    root = getModel1Factory().createCategory();
    root.setName("ROOT");
    resource.getContents().add(root);

    long modelSize = 1 + createModel(root, 3, 4, 5, this::commit);
    IOUtil.OUT().println("Model size: " + modelSize);

    CDOID rootID = session.getRepositoryInfo().getRootResourceID();
    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    InternalView serverView = serverSession.getView(transaction.getViewID());

    String hex1 = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null).getElement1();
    String hex2 = getDigest(rootID, ContainmentProxyStrategy.Physical, serverView, null).getElement1();
    assertEquals(hex1, hex2);
  }

  public void testDigestEnumLiterals() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    root = getModel1Factory().createCategory();
    root.setName("ROOT");
    resource.getContents().add(root);

    long modelSize = 1 + createModel(root, 2, 3, 4, this::commit);
    IOUtil.OUT().println("Model size: " + modelSize);

    for (Iterator<EObject> it = root.eAllContents(); it.hasNext();)
    {
      EObject object = it.next();
      if (object instanceof Product1)
      {
        Product1 product = (Product1)object;
        product.setVat(VAT.VAT15);
      }
    }

    transaction.commit();

    CDOID rootID = CDOUtil.getCDOObject(root).cdoID();
    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    InternalView serverView = serverSession.getView(transaction.getViewID());

    String hex1 = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null).getElement1();
    String hex2 = getDigest(rootID, ContainmentProxyStrategy.Physical, serverView, null).getElement1();
    assertEquals(hex1, hex2);
  }

  public void testDigestBlobs() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    for (int i = 0; i < 100; i++)
    {
      Image image = getModel3Factory().createImage();
      image.setWidth(i);
      image.setHeight(100);
      image.setData(new CDOBlob(new ByteArrayInputStream(("This is a very nice image: " + i / 10).getBytes())));
      resource.getContents().add(image);
    }

    transaction.commit();

    CDOID rootID = CDOUtil.getCDOObject(resource).cdoID();
    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    InternalView serverView = serverSession.getView(transaction.getViewID());

    String hex1 = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null).getElement1();
    String hex2 = getDigest(rootID, ContainmentProxyStrategy.Physical, serverView, null).getElement1();
    assertEquals(hex1, hex2);

    IOUtil.OUT().println("Modifying BLOB...");
    Image image = (Image)resource.getContents().get(0);
    image.setData(new CDOBlob(new ByteArrayInputStream("This is an ugly image".getBytes())));
    transaction.commit();

    String hex1A = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null).getElement1();
    String hex2A = getDigest(rootID, ContainmentProxyStrategy.Physical, serverView, null).getElement1();
    assertEquals(hex1A, hex2A);

    assertNotSame(hex1, hex1A);
    assertNotSame(hex2, hex2A);
  }

  public void testDigestClobs() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    for (int i = 0; i < 100; i++)
    {
      File file = getModel3Factory().createFile();
      file.setName("Text-File-" + i);
      file.setData(new CDOClob(new InputStreamReader(new ByteArrayInputStream(("This is a very loooong text: " + i / 10).getBytes()))));
      resource.getContents().add(file);
    }

    transaction.commit();

    CDOID rootID = CDOUtil.getCDOObject(resource).cdoID();
    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    InternalView serverView = serverSession.getView(transaction.getViewID());

    String hex1 = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null).getElement1();
    String hex2 = getDigest(rootID, ContainmentProxyStrategy.Physical, serverView, null).getElement1();
    assertEquals(hex1, hex2);

    IOUtil.OUT().println("Modifying CLOB...");
    File file = (File)resource.getContents().get(0);
    file.setData(new CDOClob(new InputStreamReader(new ByteArrayInputStream("This is a shorter text".getBytes()))));
    transaction.commit();

    String hex1A = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null).getElement1();
    String hex2A = getDigest(rootID, ContainmentProxyStrategy.Physical, serverView, null).getElement1();
    assertEquals(hex1A, hex2A);

    assertNotSame(hex1, hex1A);
    assertNotSame(hex2, hex2A);
  }

  public void testContainmentProxiesPhysical() throws Exception
  {
    runContainmentProxies(ContainmentProxyStrategy.Physical);
  }

  public void testContainmentProxiesLogical() throws Exception
  {
    runContainmentProxies(ContainmentProxyStrategy.Logical);
  }

  private void runContainmentProxies(ContainmentProxyStrategy containmentProxyStrategy) throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    root = getModel1Factory().createCategory();
    root.setName("ROOT");
    resource.getContents().add(root);

    long modelSize = 1 + createModel(root, 2, 3, 4, this::commit);
    IOUtil.OUT().println("Model size: " + modelSize);

    CDOID rootID = session.getRepositoryInfo().getRootResourceID();

    int i = 1;
    for (Category category : new ArrayList<>(root.getCategories()))
    {
      CDOResource fragment = transaction.createResource(getResourcePath("/fragment" + i++));
      fragment.getContents().add(category);
    }

    transaction.commit();

    Set<CDORevision> uniqueness = new HashSet<>();
    int[] duplicates = { 0 };

    getDigest(rootID, containmentProxyStrategy, transaction, revision -> {
      if (!uniqueness.add(revision))
      {
        ++duplicates[0];
      }
    });

    if (duplicates[0] != 0)
    {
      fail("No duplicates expected");
    }
  }

  public void testDigestQuery() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    root = getModel1Factory().createCategory();
    root.setName("ROOT");
    resource.getContents().add(root);

    long modelSize = 1 + createModel(root, 3, 4, 5, this::commit);
    IOUtil.OUT().println("Model size: " + modelSize);

    CDOID rootID = CDOUtil.getCDOObject(root).cdoID();

    Pair<String, Long> result1 = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null);
    String hex1 = result1.getElement1();
    long count1 = result1.getElement2();
    assertEquals(modelSize, count1);

    // Test DigestQueryHandler:
    CDOQuery query = transaction.createQuery(CDOProtocolConstants.QUERY_LANGUAGE_FINGER_PRINT, null);
    query.setContext(root);
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_FINGER_PRINT_TYPE, "digest");
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_FINGER_PRINT_PARAM, ALGORITHM + ",hex");
    List<Object> result2 = query.getResult();
    String hex2 = (String)result2.get(0);
    long count2 = (Long)result2.get(1);
    String param = (String)result2.get(2);
    assertEquals(modelSize, count2);
    assertEquals(ALGORITHM + ",hex", param);
    assertEquals(hex1, hex2);
  }

  public void testDigestLocalIDs_MultiContainmentReference() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    root = getModel1Factory().createCategory();
    root.setName("ROOT");
    resource.getContents().add(root);

    long modelSize = 1 + createModel(root, 3, 4, 5, this::commit);
    IOUtil.OUT().println("Model size: " + modelSize);

    Category root2 = EcoreUtil.copy(root);
    resource.getContents().add(root2);
    transaction.commit();

    CDOID rootID = CDOUtil.getCDOObject(root).cdoID();
    CDOID root2ID = CDOUtil.getCDOObject(root2).cdoID();
    boolean localIDs = true;

    Pair<String, Long> result = getDigest(rootID, localIDs, false, ContainmentProxyStrategy.Physical, transaction, null);
    String hex1 = result.getElement1();
    long count = result.getElement2();
    assertEquals(modelSize, count);

    Pair<String, Long> result2 = getDigest(root2ID, localIDs, false, ContainmentProxyStrategy.Physical, transaction, null);
    String hex2 = result2.getElement1();
    long count2 = result2.getElement2();
    assertEquals(modelSize, count2);

    assertEquals(hex1, hex2);
  }

  public void testDigestLocalIDs_SingleContainmentReference() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    SpecialPurchaseOrder order1 = getModel2Factory().createSpecialPurchaseOrder();
    order1.setDiscountCode("DiscountCode");
    order1.setShippingAddress(getModel1Factory().createAddress()); // SingleContainmentReference
    resource.getContents().add(order1);

    long modelSize = 1 + 1;
    IOUtil.OUT().println("Model size: " + modelSize);

    SpecialPurchaseOrder order2 = EcoreUtil.copy(order1);
    resource.getContents().add(order2);
    transaction.commit();

    CDOID order1ID = CDOUtil.getCDOObject(order1).cdoID();
    CDOID order2ID = CDOUtil.getCDOObject(order2).cdoID();
    boolean localIDs = true;

    Pair<String, Long> result = getDigest(order1ID, localIDs, false, ContainmentProxyStrategy.Physical, transaction, null);
    String hex1 = result.getElement1();
    long count = result.getElement2();
    assertEquals(modelSize, count);

    Pair<String, Long> result2 = getDigest(order2ID, localIDs, false, ContainmentProxyStrategy.Physical, transaction, null);
    String hex2 = result2.getElement1();
    long count2 = result2.getElement2();
    assertEquals(modelSize, count2);

    assertEquals(hex1, hex2);
  }

  public void testDigestLocalIDs_SingleToManyCrossReference() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Company company1 = getModel1Factory().createCompany();
    company1.setName("Eclipse");
    resource.getContents().add(company1);

    Supplier supplier1 = getModel1Factory().createSupplier();
    supplier1.setName("ESC");
    company1.getSuppliers().add(supplier1);

    PurchaseOrder order1 = getModel1Factory().createPurchaseOrder();
    order1.setDate(new Date());
    order1.setSupplier(supplier1); // SingleToManyCrossReference.
    company1.getPurchaseOrders().add(order1);

    long modelSize = 1 + 1 + 1;
    IOUtil.OUT().println("Model size: " + modelSize);

    Company company2 = EcoreUtil.copy(company1);
    resource.getContents().add(company2);
    transaction.commit();

    CDOID root1ID = CDOUtil.getCDOObject(company1).cdoID();
    CDOID root2ID = CDOUtil.getCDOObject(company2).cdoID();
    boolean localIDs = true;

    Pair<String, Long> result = getDigest(root1ID, localIDs, false, ContainmentProxyStrategy.Physical, transaction, null);
    String hex1 = result.getElement1();
    long count = result.getElement2();
    assertEquals(modelSize, count);

    Pair<String, Long> result2 = getDigest(root2ID, localIDs, false, ContainmentProxyStrategy.Physical, transaction, null);
    String hex2 = result2.getElement1();
    long count2 = result2.getElement2();
    assertEquals(modelSize, count2);

    assertEquals(hex1, hex2);
  }

  public void testDigestTampering() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    root = getModel1Factory().createCategory();
    root.setName("ROOT");

    resource.getContents().add(root);
    transaction.commit();

    CDOID rootID = CDOUtil.getCDOObject(root).cdoID();
    String hex1 = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null).getElement1();

    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    InternalView serverView = serverSession.getView(transaction.getViewID());

    BaseCDORevision revision = (BaseCDORevision)serverView.getRevision(rootID);
    revision.unfreeze();
    revision.setValue(getModel1Package().getCategory_Name(), "TAMPERED ROOT");
    revision.freeze();

    session = openSession();
    transaction = session.openTransaction();

    String hex2 = getDigest(rootID, ContainmentProxyStrategy.Physical, transaction, null).getElement1();
    assertNotEquals(hex1, hex2);
  }

  @Requires("MEM")
  public void testDigestTamperingBlobs() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Image image = getModel3Factory().createImage();
    image.setWidth(100);
    image.setHeight(100);
    image.setData(new CDOBlob(new ByteArrayInputStream("Original Data".getBytes())));

    resource.getContents().add(image);
    transaction.commit();

    CDOID rootID = CDOUtil.getCDOObject(image).cdoID();
    String hex1 = getDigest(rootID, false, true, ContainmentProxyStrategy.Physical, transaction, null).getElement1();

    InternalSession serverSession = getRepository().getSessionManager().getSession(session.getSessionID());
    MEMStore_UT store = (MEMStore_UT)serverSession.getRepository().getStore();
    store.writeBlob(image.getData().getID(), image.getData().getSize(), new ByteArrayInputStream("ORIGINAL DATA".getBytes()));

    session = openSession();
    transaction = session.openTransaction();
    String hex2 = getDigest(rootID, false, true, ContainmentProxyStrategy.Physical, transaction, null).getElement1();
    assertNotEquals(hex1, hex2);
  }

  private void commit()
  {
    try
    {
      CDOTransaction transaction = (CDOTransaction)CDOUtil.getCDOObject(root).cdoView();
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  private Pair<String, Long> getDigest(CDOID rootID, ContainmentProxyStrategy containmentProxyStrategy, //
      CDORevisionProvider revisionProvider, Consumer<CDORevision> revisionConsumer) throws NoSuchAlgorithmException
  {
    return getDigest(rootID, false, false, containmentProxyStrategy, revisionProvider, revisionConsumer);
  }

  private Pair<String, Long> getDigest(CDOID rootID, boolean localIDs, boolean lobValues, ContainmentProxyStrategy containmentProxyStrategy, //
      CDORevisionProvider revisionProvider, Consumer<CDORevision> revisionConsumer) throws NoSuchAlgorithmException
  {
    CDORevision rootRevision = revisionProvider.getRevision(rootID);
    CDOLobLoader lobLoader = lobValues ? (CDOLobLoader)revisionProvider : null;

    MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
    MessageDigestHandler handler = new MessageDigestHandler(digest, localIDs, lobLoader)
    {
      @Override
      protected boolean doBeginRevision(CDORevision revision)
      {
        if (revisionConsumer != null)
        {
          revisionConsumer.accept(revision);
        }

        return super.doBeginRevision(revision);
      }
    };

    long count = new CDORevisionCrawler() //
        .handler(handler) //
        .featureStrategy(FeatureStrategy.TREE) //
        .containmentProxyStrategy(containmentProxyStrategy) //
        .revisionProvider(revisionProvider) //
        .begin() //
        .addRevision(rootRevision) //
        .finish() //
        .revisionCount();

    String hex = HexUtil.bytesToHex(digest.digest());
    IOUtil.OUT().println("Digest of " + count + " revisions: " + hex);
    return Pair.create(hex, count);
  }
}
