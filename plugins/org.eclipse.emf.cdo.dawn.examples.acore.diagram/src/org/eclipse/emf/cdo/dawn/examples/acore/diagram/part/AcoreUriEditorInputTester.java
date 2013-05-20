/*
 * Copyright (c) 2010, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.part;

import org.eclipse.emf.common.ui.URIEditorInput;

import org.eclipse.core.expressions.PropertyTester;

/**
 * @generated
 */
public class AcoreUriEditorInputTester extends PropertyTester
{

  /**
   * @generated
   */
  public boolean test(Object receiver, String method, Object[] args, Object expectedValue)
  {
    if (false == receiver instanceof URIEditorInput)
    {
      return false;
    }
    URIEditorInput editorInput = (URIEditorInput)receiver;
    return "acore_diagram".equals(editorInput.getURI().fileExtension()); //$NON-NLS-1$
  }

}
