/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server.security;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.security.impl.ResourceFilterImpl;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test subclass proving that implementation subclasses are dynamic by default.
 */
public class CustomResourceFilter extends ResourceFilterImpl
{
  private final AtomicInteger evaluations;

  public CustomResourceFilter()
  {
    this(new AtomicInteger());
  }

  public CustomResourceFilter(AtomicInteger evaluations)
  {
    this.evaluations = evaluations;
  }

  @Override
  protected boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception
  {
    evaluations.incrementAndGet();
    return super.filter(revision, revisionProvider, securityContext, level);
  }
}
