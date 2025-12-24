/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.ChangeSubscriptionRequest;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalCounter;

import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

/**
 * Bug 458279 about {@link CDOMergingConflictResolver} without {@link ChangeSubscriptionRequest} sent to the server.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_458279_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test {@link CDOMergingConflictResolver} without {@link ChangeSubscriptionRequest} sent to the server and using {@link PassiveUpdateMode#ADDITIONS}.
   */
  public void testCDOMergingConflictResolverWithoutChangeSubscriptionRequestWithAdditionsPassiveUpdateMode() throws Exception
  {
    testCDOMergingConflictResolverWithoutChangeSubscriptionRequest(PassiveUpdateMode.ADDITIONS);
  }

  /**
   * Test {@link CDOMergingConflictResolver} without {@link ChangeSubscriptionRequest} sent to the server and using {@link PassiveUpdateMode#ADDITIONS}.
   */
  public void testCDOMergingConflictResolverWithoutChangeSubscriptionRequestWithChangesPassiveUpdateMode() throws Exception
  {
    testCDOMergingConflictResolverWithoutChangeSubscriptionRequest(PassiveUpdateMode.CHANGES);
  }

  /**
   * Test {@link CDOMergingConflictResolver} without {@link ChangeSubscriptionRequest} sent to the server and using {@link PassiveUpdateMode#INVALIDATIONS}.
   */
  public void testCDOMergingConflictResolverWithoutChangeSubscriptionRequestWithInvalidationsPassiveUpdateMode() throws Exception
  {
    testCDOMergingConflictResolverWithoutChangeSubscriptionRequest(PassiveUpdateMode.INVALIDATIONS);
  }

  private void testCDOMergingConflictResolverWithoutChangeSubscriptionRequest(PassiveUpdateMode mode) throws Exception
  {
    CDOSession session1 = openSession();
    ISignalProtocol<?> protocol = ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session1).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);

    session1.options().setPassiveUpdateMode(mode);
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().addConflictResolver(new CDOMergingConflictResolver(false));
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);
    transaction1.commit();

    company.getCategories().add(getModel1Factory().createCategory());

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource(getResourcePath(RESOURCE_NAME));
    Company companyFromSession2 = (Company)resource2.getContents().get(0);
    companyFromSession2.getCategories().add(getModel1Factory().createCategory());
    commitAndSync(transaction2, transaction1);

    assertEquals(mode == PassiveUpdateMode.INVALIDATIONS ? 1 : 2, company.getCategories().size());
    int numberCallToChangeSubscriptionRequest = signalCounter.getCountFor(ChangeSubscriptionRequest.class);
    assertEquals("No ChangeSubscriptionRequest should be send to server", 0, numberCallToChangeSubscriptionRequest);

    protocol.removeListener(signalCounter);
  }
}
