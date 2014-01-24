/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.util;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.util.List;

/**
 * @author Eike Stepper
 */
public interface ProjectProvider
{
  public <T> List<T> accept(Visitor<T> visitor, IProgressMonitor monitor);

  /**
   * @author Eike Stepper
   */
  public interface Visitor<T>
  {
    public T visitPlugin(File manifestFile, IProgressMonitor monitor) throws Exception;

    public T visitFeature(File featureFile, IProgressMonitor monitor) throws Exception;

    public T visitComponentDefinition(File cdefFile, IProgressMonitor monitor) throws Exception;

    public void visitComponentExtension(File cextFile, T host, IProgressMonitor monitor) throws Exception;

    public T visitCSpec(File cspecFile, IProgressMonitor monitor) throws Exception;

    public void visitCSpex(File cspexFile, T host, IProgressMonitor monitor) throws Exception;
  }
}
