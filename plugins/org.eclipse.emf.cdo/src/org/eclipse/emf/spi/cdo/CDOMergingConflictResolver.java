/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.transaction.CDOMerger;

import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class CDOMergingConflictResolver extends AbstractChangeSetsConflictResolver
{
  private CDOMerger merger;

  public CDOMergingConflictResolver()
  {
    this(new DefaultCDOMerger.PerFeature.ManyValued());
  }

  public CDOMergingConflictResolver(CDOMerger merger)
  {
    this.merger = merger;
  }

  public CDOMerger getMerger()
  {
    return merger;
  }

  public void resolveConflicts(Set<CDOObject> conflicts)
  {
    CDOChangeSet target = getLocalChangeSet();
    CDOChangeSet source = getRemoteChangeSet();
    CDOChangeSetData result = merger.merge(target, source);

    final InternalCDOTransaction transaction = (InternalCDOTransaction)getTransaction();
    final InternalCDOSession session = transaction.getSession();
    final InternalCDORevisionManager revisionManager = session.getRevisionManager();

    final CDORevisionProvider ancestorProvider = new CDORevisionProvider()
    {
      public CDORevision getRevision(CDOID id)
      {
        return revisionManager.getRevision(id, transaction, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
      }
    };

    final CDORevisionProvider targetProvider = new CDORevisionProvider()
    {
      public CDORevision getRevision(CDOID id)
      {
        CDOObject object = transaction.getObject(id, false);
        if (object != null)
        {
          return object.cdoRevision();
        }

        return ancestorProvider.getRevision(id);
      }
    };

    transaction.applyChangeSetData(result, ancestorProvider, targetProvider, transaction);
  }
}
