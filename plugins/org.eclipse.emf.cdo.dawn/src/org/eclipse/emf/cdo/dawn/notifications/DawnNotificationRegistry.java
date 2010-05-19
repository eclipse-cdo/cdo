/*******************************************************************************
 * Copyright (c) 2009 - 2010 Martin Fluegge (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;

/**
 * @author Martin Fluegge
 */
public class DawnNotificationRegistry
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnNotificationRegistry.class);

  private static final String DAWN_RECOURCELISTENERS_ID = "org.eclipse.emf.cdo.dawn.listeners";

  public static AbstractDawnResoureChangeListener createDawnResoureChangeListener(DiagramDocumentEditor editor)
  {
    AbstractDawnResoureChangeListener listener = null;
    try
    {
      IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
          DAWN_RECOURCELISTENERS_ID);
      for (IConfigurationElement e : config)
      {
        final Object o = e.createExecutableExtension("class");
        if (o instanceof AbstractDawnResoureChangeListener)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Registering AbstractDawnResoureChangeListener {0} ", o); //$NON-NLS-1$
          }

          listener = (AbstractDawnResoureChangeListener)o;
          listener.setEditor(editor);
          return listener;
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    if (listener == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("No AbstractDawnResoureChangeListener found in regsitry. Creating defaul listener."); //$NON-NLS-1$
      }
      listener = new DawnResoureChangeListener(editor);
    }
    return listener;
  }

  public static BasicDawnListener createDawnTransactionListener(DiagramDocumentEditor editor)
  {
    BasicDawnListener listener = null;
    try
    {
      IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
          DAWN_RECOURCELISTENERS_ID);
      for (IConfigurationElement e : config)
      {
        final Object o = e.createExecutableExtension("class");
        if (o instanceof BasicDawnListener)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Registering TransactionListener  {0} ", o); //$NON-NLS-1$
          }

          listener = (BasicDawnListener)o;
          listener.setEditor(editor);
          return listener;
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    if (listener == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("No TransactionListener found in regsitry. Creating default listener."); //$NON-NLS-1$
      }

      listener = new BasicDawnListener(editor);
    }
    return listener;
  }
}
