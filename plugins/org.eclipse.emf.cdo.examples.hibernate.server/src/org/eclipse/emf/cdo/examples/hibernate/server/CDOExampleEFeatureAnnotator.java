/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.hibernate.server;

import org.eclipse.emf.cdo.server.hibernate.teneo.CDOEFeatureAnnotator;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEStructuralFeature;

/**
 * An example of providing an extension for Teneo Mapping. See <a
 * href="http://wiki.eclipse.org/Teneo/Hibernate/Extensions">here</a> for more information on Teneo extensions.
 *
 * @author Martin Taal
 */
public class CDOExampleEFeatureAnnotator extends CDOEFeatureAnnotator
{
  @Override
  public void annotate(PAnnotatedEStructuralFeature aStructuralFeature)
  {
    IOUtil.ERR().println("CDOExampleEFeatureAnnotator: this method is called as expected"); //$NON-NLS-1$
    super.annotate(aStructuralFeature);
  }
}
