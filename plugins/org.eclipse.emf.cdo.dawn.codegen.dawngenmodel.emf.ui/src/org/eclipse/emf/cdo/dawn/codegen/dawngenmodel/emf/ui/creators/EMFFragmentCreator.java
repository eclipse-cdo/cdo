/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.ui.creators;

import org.eclipse.emf.cdo.dawn.codegen.creators.impl.AbstractFragmentCreator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.util.DawnEMFWorkflowUtil;

import org.eclipse.core.resources.IResource;

import java.net.URL;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public class EMFFragmentCreator extends AbstractFragmentCreator
{
  public EMFFragmentCreator(IResource selectedElement)
  {
    super(selectedElement);
  }

  @Override
  protected URL getWorkflowURL()
  {
    return new DawnEMFWorkflowUtil().getWorkFlow();
  }
}
