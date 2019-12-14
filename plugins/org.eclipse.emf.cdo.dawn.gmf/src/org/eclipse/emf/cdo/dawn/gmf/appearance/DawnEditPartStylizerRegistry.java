/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.appearance;

import org.eclipse.emf.cdo.dawn.gmf.appearance.impl.DawnBasicConnectionEditPartStylizerImpl;
import org.eclipse.emf.cdo.dawn.gmf.appearance.impl.DawnBasicGraphicalEditPartStylizerImpl;
import org.eclipse.emf.cdo.dawn.gmf.appearance.impl.DawnBasicNodeEditPartStylizerImpl;
import org.eclipse.emf.cdo.dawn.gmf.appearance.impl.DawnBasicTextAwareEditPartStylizerImpl;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITextAwareEditPart;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Fluegge
 * @since 2.0
 * @deprecated
 */
@Deprecated
public class DawnEditPartStylizerRegistry
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnEditPartStylizerRegistry.class);

  private static final String DAWN_STYLIZER_EXTENSION_POINT_ID = "org.eclipse.emf.cdo.dawn.editpartstylizers";

  public static DawnEditPartStylizerRegistry instance = new DawnEditPartStylizerRegistry();

  private Map<String, DawnEditPartStylizer> registeredStylizers = new HashMap<>();

  /**
   * @since 2.0
   */
  public DawnEditPartStylizer getStylizer(EditPart editPart)
  {
    DawnEditPartStylizer stylizer = registeredStylizers.get(editPart.getClass().getCanonicalName());

    if (stylizer == null)
    {
      stylizer = getStylizerFromExtensionPoint(editPart);
    }

    if (stylizer == null)
    {
      stylizer = getDefaultStylizer(editPart);
    }

    return stylizer;
  }

  private DawnEditPartStylizer getDefaultStylizer(EditPart editPart)
  {
    DawnEditPartStylizer stylizer = null;
    if (editPart instanceof ConnectionEditPart)
    {
      stylizer = new DawnBasicConnectionEditPartStylizerImpl();
    }
    else if (editPart instanceof NodeEditPart)
    {
      stylizer = new DawnBasicNodeEditPartStylizerImpl();
    }
    else if (editPart instanceof DiagramEditPart)
    {
      stylizer = new DawnBasicNodeEditPartStylizerImpl();
    }
    else if (editPart instanceof ITextAwareEditPart)
    {
      stylizer = new DawnBasicTextAwareEditPartStylizerImpl();
    }
    else
    {
      // In the case that there is no match we use a simple border styled stylizer.
      stylizer = new DawnBasicGraphicalEditPartStylizerImpl();
    }

    return stylizer;
  }

  private DawnEditPartStylizer getStylizerFromExtensionPoint(EditPart editPart)
  {
    try
    {
      IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(DAWN_STYLIZER_EXTENSION_POINT_ID);
      for (IConfigurationElement e : config)
      {
        if (editPart.getClass().getCanonicalName().equals(e.getAttribute("editpart")))
        {
          DawnEditPartStylizer stylizer = (DawnEditPartStylizer)e.createExecutableExtension("stylizer");
          registeredStylizers.put(editPart.getClass().getCanonicalName(), stylizer);
          if (TRACER.isEnabled())
          {
            TRACER.format("Registered DawnEditPartStylizer {0} ", stylizer); //$NON-NLS-1$
          }

          return stylizer;
        }
      }
    }
    catch (InvalidRegistryObjectException e1)
    {
      e1.printStackTrace();
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }

    return null;
  }
}
