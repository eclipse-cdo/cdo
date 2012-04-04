/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.legacy.model6.Model6Factory;
import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.Root;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOViewAdaptersNotifiedEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * @author Esteban Dugueperoux
 */
public class Bugzilla_375444_Test extends AbstractCDOTest
{
  private static final String MAIN_RESOURCE_1_NAME = "mainResource1.model1";

  private static final String MAIN_RESOURCE_2_NAME = "mainResource2.model1";

  private static final String FRAGMENT_RESOURCE_1_NAME = "fragmentResource1.model1";

  private static final String FRAGMENT_RESOURCE_2_NAME = "fragmentResource2.model1";

  private CDOTransaction cdoTransaction;

  private Root legacyRoot1;

  private BaseObject legacyChildRoot1;

  private Root legacyRoot2;

  private BaseObject legacyChildRoot2;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    CDOSession cdoSession = openSession();
    cdoSession.options().setPassiveUpdateEnabled(true);
    cdoSession.options().setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);

    cdoTransaction = cdoSession.openTransaction();
    cdoTransaction.options().setAutoReleaseLocksEnabled(false);

    // Create native main resource
    CDOResource mainResource1 = cdoTransaction.createResource(getResourcePath(MAIN_RESOURCE_1_NAME));

    legacyRoot1 = Model6Factory.eINSTANCE.createRoot();
    legacyChildRoot1 = Model6Factory.eINSTANCE.createBaseObject();
    legacyRoot1.getListA().add(legacyChildRoot1);

    mainResource1.getContents().add(legacyRoot1);

    // Create legacy main resource
    CDOResource mainResource2 = cdoTransaction.createResource(getResourcePath(MAIN_RESOURCE_2_NAME));

    legacyRoot2 = Model6Factory.eINSTANCE.createRoot();
    legacyChildRoot2 = Model6Factory.eINSTANCE.createBaseObject();
    legacyRoot2.getListA().add(legacyChildRoot2);

    mainResource2.getContents().add(legacyRoot2);

    cdoTransaction.commit();
  }

  @CleanRepositoriesBefore
  @CleanRepositoriesAfter
  public void testControlWithSecondClientInSameThread() throws Throwable
  {
    OtherUser otherUser = new OtherUser();
    otherUser.setUp();

    // Control
    Resource fragmentResource1 = cdoTransaction.createResource(getResourcePath(FRAGMENT_RESOURCE_1_NAME));
    fragmentResource1.getContents().add(legacyChildRoot1);

    Resource fragmentResource2 = cdoTransaction.createResource(getResourcePath(FRAGMENT_RESOURCE_2_NAME));
    fragmentResource2.getContents().add(legacyChildRoot2);

    cdoTransaction.commit();
    otherUser.waitRemoteChanges();
    otherUser.assertNewResources();
  }

  /**
   * @author Esteban Dugueperoux
   */
  private final class OtherUser implements IListener
  {
    private CDOSession cdoSession;

    private CDOTransaction cdoTransactionOfRemoteUser;

    private Root legacyRoot1OfRemoteUser;

    private BaseObject legacyChildRoot1OfRemoteUser;

    private Root legacyRoot2OfRemoteUser;

    private BaseObject legacyChildRoot2OfRemoteUser;

    private boolean remoteChangesReceived;

    public void setUp()
    {
      cdoSession = openSession();
      cdoSession.options().setPassiveUpdateEnabled(true);

      TransactionalEditingDomain domain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
      ResourceSet resourceSet = domain.getResourceSet();
      cdoTransactionOfRemoteUser = cdoSession.openTransaction(resourceSet);
      cdoTransactionOfRemoteUser.options().setAutoReleaseLocksEnabled(false);
      cdoTransactionOfRemoteUser.addListener(this);

      Resource mainResource1 = cdoTransactionOfRemoteUser.getResource(getResourcePath(MAIN_RESOURCE_1_NAME));

      legacyRoot1OfRemoteUser = (Root)mainResource1.getContents().get(0);
      legacyChildRoot1OfRemoteUser = legacyRoot1OfRemoteUser.getListA().get(0);

      Resource mainResource2 = cdoTransactionOfRemoteUser.getResource(getResourcePath(MAIN_RESOURCE_2_NAME));

      legacyRoot2OfRemoteUser = (Root)mainResource2.getContents().get(0);
      legacyChildRoot2OfRemoteUser = legacyRoot2OfRemoteUser.getListA().get(0);
    }

    public void notifyEvent(IEvent event)
    {
      remoteChangesReceived = event instanceof CDOViewAdaptersNotifiedEvent;
    }

    public void waitRemoteChanges() throws InterruptedException
    {
      int timeout = 50;
      while (!remoteChangesReceived && timeout > 0)
      {
        Thread.sleep(100);
        timeout--;
      }
    }

    public void assertNewResources()
    {
      ResourceSet resourceSet = cdoTransactionOfRemoteUser.getResourceSet();
      assertEquals(4, resourceSet.getResources().size());
      assertEquals(true, cdoTransactionOfRemoteUser.hasResource(getResourcePath(FRAGMENT_RESOURCE_1_NAME)));
      assertEquals(true, cdoTransactionOfRemoteUser.hasResource(getResourcePath(FRAGMENT_RESOURCE_2_NAME)));
      assertEquals(getResourcePath(FRAGMENT_RESOURCE_1_NAME),
          ((CDOResource)legacyChildRoot1OfRemoteUser.eResource()).getPath());
      assertEquals(getResourcePath(FRAGMENT_RESOURCE_2_NAME),
          ((CDOResource)legacyChildRoot2OfRemoteUser.eResource()).getPath());
    }
  }
}
