/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.clustering;

import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyScope;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.db.app.ooId;

public class ObjyPlacementManagerImpl implements ObjyPlacementManager
{

  private String resourceContName = "resCont";

  private String dbName = "DB";

  private String defContName = "DefaultCont";

  private String genContName = "Cont";

  /**
   * Only used when we can't figure out a near object, so we'll end up putting the object in a default location.
   */
  public ooId getNearObject(ObjyObject parent, EStructuralFeature feature, EClass newClassObject)
  {
    ObjyScope objyScope = null;

    if (parent == null)
    {
      objyScope = new ObjyScope(dbName, resourceContName);
    }
    else if (feature == null)
    {
      objyScope = new ObjyScope(dbName, defContName);
    }
    else
    {
      objyScope = new ObjyScope(dbName, genContName);
    }
    return objyScope.getScopeContOid();
  }

}
