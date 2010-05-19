/*******************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.dawn.diagram.part.IDawnDiagramEditor;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.impl.TransactionChangeRecorder;

import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.Diagram;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Fluegge
 */
public class DawnNotificationUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnNotificationUtil.class);

  /**
   * This method removes all TransactionChangeRecorders from a given Notifier.
   * 
   * @param notifier
   */
  public static void removeTransactionChangeRecorder(Notifier notifier)
  {
    List<Adapter> changeRecorders = new ArrayList<Adapter>();
    for (Adapter adapter : notifier.eAdapters())
    {
      if (adapter instanceof TransactionChangeRecorder)
      {
        changeRecorders.add(adapter);
      }
    }

    if (changeRecorders.size() > 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removing Change Recorder on e {0} ", notifier); //$NON-NLS-1$
      }

      notifier.eAdapters().removeAll(changeRecorders);
    }
  }

  public static void registerResourceListeners(ResourceSet resourceSet, DiagramDocumentEditor editor)
  {
    // AbstractDawnResoureChangeListener dawnResoureChangeListener = DawnNotificationRegistry
    // .createDawnResoureChangeListener(editor);
    // for (final Resource res : resourceSet.getResources())
    // {
    // res.eAdapters().add(dawnResoureChangeListener);
    // }
  }

  public static void registerTransactionListeners(CDOTransaction transaction, IDawnDiagramEditor editor)
  {
    BasicDawnListener transactionListener = DawnNotificationRegistry
        .createDawnTransactionListener((DiagramDocumentEditor)editor);
    transaction.addListener(transactionListener);
    transaction.addTransactionHandler(new DawnTransactionHandler(editor));
  }

  public static void setChangeSubscriptionPolicy(CDOTransaction transaction)
  {
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.CDO);
  }

  public static void registerModelListeners(Diagram diagram, DiagramDocumentEditor editor)
  {
    // DawnElementChangeListener dawnElementChangeListener = new DawnElementChangeListener(editor);
    // diagram.getElement().eAdapters().add(dawnElementChangeListener);
    // diagram.eAdapters().add(dawnElementChangeListener);
  }
}
