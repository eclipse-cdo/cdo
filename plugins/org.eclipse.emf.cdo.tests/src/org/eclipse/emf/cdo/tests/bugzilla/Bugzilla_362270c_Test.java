/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.object.CDOLegacyWrapper;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Esteban Dugueperoux
 */
public class Bugzilla_362270c_Test extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/test1";

  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testNotifierNotACDOLegacyAdapter() throws Exception
  {
    TransactionalEditingDomain domain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
    ResourceSet resourceSet = domain.getResourceSet();

    // 1. Create the CDOResource
    Company obeoCompany = getModel1Factory().createCompany();
    obeoCompany.setName("OBEO");
    obeoCompany.setCity("Nantes");

    Supplier martinSupplier = getModel1Factory().createSupplier();
    obeoCompany.getSuppliers().add(martinSupplier);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction(resourceSet);
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    resource.getContents().add(obeoCompany);
    resource.save(Collections.emptyMap());

    // 2. Create the local XMI resource
    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    Map<String, Object> m = reg.getExtensionToFactoryMap();
    m.put("model1", new XMIResourceFactoryImpl());

    Company martinCompany = getModel1Factory().createCompany();
    martinCompany.setName("Martin");
    martinCompany.setCity("Berlin");

    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    martinCompany.getPurchaseOrders().add(purchaseOrder);

    File localResourceFile = createTempFile(getName(), ".model1");
    URI localResourceURI = URI.createFileURI(localResourceFile.getAbsolutePath());
    Resource localResource = new ResourceSetImpl().createResource(localResourceURI);

    localResource.getContents().add(martinCompany);
    localResource.save(Collections.emptyMap());
    localResource = resourceSet.getResource(localResourceURI, true);

    // 3. Add a reference from the CDOResource to the local resource
    Command addPurchaseOrderCmd = AddCommand.create(domain, martinSupplier, getModel1Package().getSupplier_PurchaseOrders(), purchaseOrder);
    domain.getCommandStack().execute(addPurchaseOrderCmd);

    resource.save(Collections.emptyMap());

    // 4. Create the first CDOSavepoint
    final CDOSavepoint firstSavepoint = transaction.setSavepoint();

    Rollbacker rollbacker = new Rollbacker();
    domain.addResourceSetListener(rollbacker);

    // 5. Detach a CDOObject
    Command removeMartinSupplierCmd = RemoveCommand.create(domain, obeoCompany, getModel1Package().getCompany_Suppliers(), martinSupplier);
    domain.getCommandStack().execute(removeMartinSupplierCmd);

    // 6. Rollback the previous operation
    Command rollbackSavepointCmd = new RecordingCommand(domain)
    {
      @Override
      protected void doExecute()
      {
        firstSavepoint.rollback();
      }
    };

    NotifierCollector collector = new NotifierCollector();
    martinSupplier.eAdapters().add(collector);

    domain.getCommandStack().execute(rollbackSavepointCmd);
    domain.removeResourceSetListener(rollbacker);

    for (Object notifier : collector.getNotifiers())
    {
      assertNotInstanceOf(CDOLegacyWrapper.class, notifier);
    }
  }

  /**
   * @author Esteban Dugueperoux
   */
  private class Rollbacker extends ResourceSetListenerImpl
  {
    @Override
    public Command transactionAboutToCommit(ResourceSetChangeEvent event) throws RollbackException
    {
      IStatus status = Status.CANCEL_STATUS;
      throw new RollbackException(status);
    }
  }

  /**
   * @author Esteban Dugueperoux
   */
  private class NotifierCollector extends AdapterImpl
  {
    private Set<Object> notifiers = new HashSet<>();

    @Override
    public void notifyChanged(Notification msg)
    {
      notifiers.add(msg.getNotifier());
    }

    public Set<Object> getNotifiers()
    {
      return notifiers;
    }
  }
}
