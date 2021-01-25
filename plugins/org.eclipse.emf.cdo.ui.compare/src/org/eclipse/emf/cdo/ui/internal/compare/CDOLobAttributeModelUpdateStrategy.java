/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.rcp.ui.internal.contentmergeviewer.IModelUpdateStrategy;
import org.eclipse.emf.compare.rcp.ui.internal.util.MergeViewerUtil;
import org.eclipse.emf.compare.rcp.ui.mergeviewer.IMergeViewer.MergeViewerSide;
import org.eclipse.emf.compare.utils.ReferenceUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class CDOLobAttributeModelUpdateStrategy implements IModelUpdateStrategy
{
  public CDOLobAttributeModelUpdateStrategy()
  {
  }

  @Override
  public boolean canUpdate(Diff diff, MergeViewerSide side)
  {
    return getTargetObject(diff, side) != null;
  }

  @Override
  public Command getModelUpdateCommand(Diff diff, Object newValue, MergeViewerSide side)
  {
    EObject targetObject = getTargetObject(diff, side);
    EAttribute attribute = ((AttributeChange)diff).getAttribute();

    return new ChangeCommand(targetObject)
    {
      @Override
      public boolean canExecute()
      {
        if (!canUpdate(diff, side))
        {
          return false;
        }

        String oldValue = null;

        try
        {
          CDOLob<?> lob = (CDOLob<?>)ReferenceUtil.safeEGet(targetObject, attribute);
          if (lob != null)
          {
            oldValue = lob.getString();
          }
        }
        catch (IOException ex)
        {
          OM.LOG.error(ex);
          return false;
        }

        if (Objects.equals(newValue, oldValue))
        {
          return false;
        }

        return super.canExecute();
      }

      @Override
      protected void doExecute()
      {
        CDOLob<?> lob = null;

        try
        {
          if (newValue != null)
          {
            InternalCDOSession session = (InternalCDOSession)CDOUtil.getSession(targetObject);
            CDOLobStore lobStore = session != null ? session.getLobStore() : null;

            if (attribute.getEAttributeType() == EtypesPackage.Literals.BLOB)
            {
              lob = new CDOBlob((String)newValue, lobStore);
            }
            else
            {
              lob = new CDOClob((String)newValue, lobStore);
            }
          }
        }
        catch (IOException ex)
        {
          OM.LOG.error(ex);
          return;
        }

        targetObject.eSet(attribute, lob);
      }
    };
  }

  private static EObject getTargetObject(Diff diff, MergeViewerSide side)
  {
    return MergeViewerUtil.getEObject(diff.getMatch(), side);
  }
}
