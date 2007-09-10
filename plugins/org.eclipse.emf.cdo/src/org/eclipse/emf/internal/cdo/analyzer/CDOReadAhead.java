/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.analyzer.CDOFetchRuleManager;
import org.eclipse.emf.cdo.internal.protocol.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.internal.util.concurrent.QueueWorkerWorkSerializer;
import org.eclipse.net4j.util.concurrent.IWorkSerializer;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOViewImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOReadAhead
{
  private IWorkSerializer workSerializer = new QueueWorkerWorkSerializer();

  private CDOViewImpl view;

  public CDOReadAhead(CDOViewImpl view)
  {
    this.view = view;
  }

  public CDOViewImpl getView()
  {
    return view;
  }

  public void addIDs(Set<CDOID> ids)
  {
    CDODynamicFetchRuleAnalyzer dynamic = (CDODynamicFetchRuleAnalyzer)getView().getFeatureAnalyzer();
    if (dynamic.getCurrentGraph().getFeatureRule().isActive())
    {
      workSerializer.addWork(new ReceiverWork(ids));
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ReceiverWork implements Runnable
  {
    private Set<CDOID> ids;

    private CDODynamicFetchRuleAnalyzer dynamic;

    private CDOGraph graph;

    private CDOID context;

    /**
     * TODO Remove?
     */
    private Test test = new Test();

    private ReceiverWork(Set<CDOID> ids)
    {
      this.ids = ids;
      dynamic = (CDODynamicFetchRuleAnalyzer)getView().getFeatureAnalyzer();
      graph = dynamic.getCurrentGraph();
      context = dynamic.getContext();
    }

    public void run()
    {
      CDODynamicFetchRuleManager.join(test);
      CDOSessionImpl s = getView().getSession();
      s.getRevisionManager().getRevisions(ids, s.getReferenceChunkSize());
    }

    /**
     * TODO Remove?
     * 
     * @author Eike Stepper
     */
    @Deprecated
    private final class Test implements CDOFetchRuleManager
    {
      public Test()
      {
      }

      public CDOID getContext()
      {
        return context;
      }

      public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
      {
        List<CDOFetchRule> list = new ArrayList<CDOFetchRule>();
        for (Iterator<CDOFetchRule> it = graph.getFeatureRule().iterator(); it.hasNext();)
        {
          CDOFetchRule fetchRule = it.next();
          list.add(fetchRule);
        }

        return list;
      }
    }
  }
}
