/***************************************************************************
 * Copyright (c) 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/

package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;

import java.util.HashMap;
import java.util.Map;

/**
 * [DBStore] NPE when trying to update objects
 * <p>
 * See https://bugs.eclipse.org/252909
 * 
 * @author Simon McDuff
 */
public class Bugzilla_252909_Test extends AbstractCDOTest
{

  public void testBugzilla_252909() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    for (int i = 0; i < 10; i++)
    {
      Company company = Model1Factory.eINSTANCE.createCompany();
      company.setName("Okidoo");
      resource.getContents().add(company);
      transaction.commit();

      TestRevisionManager revisionManager = (TestRevisionManager)getRepository().getRevisionManager();
      revisionManager.removeRevision(resource.cdoRevision());
    }
  }

  @Override
  protected Repository createRepository()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(IRepository.Props.PROP_SUPPORTING_REVISION_DELTAS, "false");

    IStore store = createStore();
    Repository repository = new Repository()
    {
      @Override
      protected RevisionManager createRevisionManager()
      {
        return new TestRevisionManager(this);
      }
    };

    repository.setName(REPOSITORY_NAME);
    repository.setProperties(props);
    repository.setStore(store);
    return repository;
  }

  /**
   * @author Simon McDuff
   */
  protected static class TestRevisionManager extends RevisionManager
  {
    public TestRevisionManager(Repository repository)
    {
      super(repository);
    }

    public void removeRevision(CDORevision revision)
    {
      super.removeRevision(revision.getID(), revision.getVersion());
    }
  }
}
