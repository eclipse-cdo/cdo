/*
 * Copyright (c) 2011-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.core.runtime.Status;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Test case for {@link CDODeltaNotification#getNewValue()} which must returns a local {@link EObject} contained in a
 * {@link XMIResource} because a {@link CDOObject} stored in a {@link CDOResource} references the local {@link EObject}.
 *
 * @author Esteban Dugueperoux
 */
@Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
public class Bugzilla_362270_Test extends AbstractCDOTest
{
  private final EReference SUPPLIERS = getModel1Package().getCompany_Suppliers();

  private final EReference PURCHASE_ORDERS = getModel1Package().getSupplier_PurchaseOrders();

  public void testNotifierNotACDOLegacyAdapter() throws Exception
  {
    TransactionalEditingDomain domain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
    ResourceSet resourceSet = domain.getResourceSet();
    registerXMIFactory(resourceSet);

    // 1. Create the CDOResource
    Company obeoCompany = getModel1Factory().createCompany();
    obeoCompany.setName("OBEO");
    obeoCompany.setCity("Nantes");

    Supplier martinSupplier = getModel1Factory().createSupplier();
    obeoCompany.getSuppliers().add(martinSupplier);

    CDOSession session = openSession();
    final CDOTransaction cdoTransaction = session.openTransaction(resourceSet);

    CDOResource cdoResource = cdoTransaction.createResource(getResourcePath("/test1"));
    cdoResource.getContents().add(obeoCompany);
    cdoTransaction.commit();

    // 2. Create the local XMI resource
    URI localResourceURI = createXMIResource();

    // Reload the local resource through the CDOTransaction!!!
    Resource localResource = resourceSet.getResource(localResourceURI, true);
    Company martinCompany = (Company)localResource.getContents().get(0);
    PurchaseOrder purchaseOrder = martinCompany.getPurchaseOrders().get(0);

    Command addPurchaseOrderCmd = AddCommand.create(domain, martinSupplier, PURCHASE_ORDERS, purchaseOrder);
    domain.getCommandStack().execute(addPurchaseOrderCmd);

    // 4. Commit
    cdoTransaction.commit();

    domain.addResourceSetListener(new ResourceSetListenerImpl()
    {
      @Override
      public Command transactionAboutToCommit(ResourceSetChangeEvent event) throws RollbackException
      {
        throw new RollbackException(Status.CANCEL_STATUS);
      }
    });

    Command removeMartinSupplierCmd = RemoveCommand.create(domain, obeoCompany, SUPPLIERS, martinSupplier);
    domain.getCommandStack().execute(removeMartinSupplierCmd);

    AssertAdapter assertAdapter = new AssertAdapter(purchaseOrder);
    martinSupplier.eAdapters().add(assertAdapter);

    // 6. rollback the previous operation
    domain.getCommandStack().execute(new RecordingCommand(domain)
    {
      @Override
      protected void doExecute()
      {
        cdoTransaction.rollback();
      }
    });
  }

  private URI createXMIResource() throws IOException
  {
    ResourceSet localResourceSet = new ResourceSetImpl();
    registerXMIFactory(localResourceSet);

    File localResourceFile = createTempFile(getName(), ".model1").getAbsoluteFile();
    URI localResourceURI = URI.createFileURI(localResourceFile.getAbsolutePath());
    Resource localResource = localResourceSet.createResource(localResourceURI);

    Company martinCompany = getModel1Factory().createCompany();
    martinCompany.setName("Martin");
    martinCompany.setCity("Berlin");

    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    martinCompany.getPurchaseOrders().add(purchaseOrder);

    localResource.getContents().add(martinCompany);
    localResource.save(Collections.emptyMap());

    return localResourceURI;
  }

  private void registerXMIFactory(ResourceSet resourceSet)
  {
    Map<String, Object> map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("model1", new XMIResourceFactoryImpl());
  }

  private final class AssertAdapter extends AdapterImpl
  {
    private EObject eObject;

    private int notifyCounter;

    public AssertAdapter(EObject eObject)
    {
      this.eObject = eObject;
    }

    @Override
    public void notifyChanged(Notification msg)
    {
      if (getModel1Package().getSupplier_PurchaseOrders().equals(msg.getFeature()))
      {
        switch (++notifyCounter)
        {
        case 1:
          assertEquals(null, msg.getNewValue());
          break;

        case 2:
          assertEquals(eObject, msg.getNewValue());
          break;

        default:
          fail("Only 0 or 2 calls are expected");
        }
      }
    }
  }
}
