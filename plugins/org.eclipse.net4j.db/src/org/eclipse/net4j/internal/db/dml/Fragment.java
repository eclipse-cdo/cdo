/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db.dml;

import org.eclipse.net4j.db.dml.SQLFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fragment assembled from text and structured SQL objects.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public class Fragment implements SQLFragment
{
  private final List<Object> parts;

  public Fragment(List<Object> parts)
  {
    this.parts = Collections.unmodifiableList(new ArrayList<>(parts));
    for (Object part : this.parts)
    {
      if (part instanceof String && ((String)part).indexOf('?') >= 0)
      {
        throw new IllegalArgumentException(
            "Raw SQL text must not contain '?'; represent parameter placeholders with SQL.param() and structured fragment parameter tokens");
      }
    }
  }

  public List<Object> getParts()
  {
    return parts;
  }
}
