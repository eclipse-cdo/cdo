/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.teneo.annotations.mapper.OneToManyReferenceAnnotator;
import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEReference;

/**
 * Prevent automatically added delete-orphan.
 *
 * @author Martin Taal
 * @since 4.2
 */
public class CDOOneToManyReferenceAnnotator extends OneToManyReferenceAnnotator
{

  @Override
  public void annotate(PAnnotatedEReference aReference)
  {
    boolean hasOtm = aReference.getOneToMany() != null;
    super.annotate(aReference);
    if (!hasOtm && aReference.getOneToMany() != null)
    {
      aReference.getOneToMany().setOrphanRemoval(false);
    }
  }

}
