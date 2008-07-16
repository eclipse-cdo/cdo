/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevisionDelta;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDORevisionDeltaImpl implements InternalCDORevisionDelta
{
  private CDOID cdoID;

  private CDOClass cdoClass;

  private int dirtyVersion;

  private int originVersion;

  private Map<CDOFeature, CDOFeatureDelta> featureDeltas = new HashMap<CDOFeature, CDOFeatureDelta>();

  public CDORevisionDeltaImpl(CDORevision revision)
  {
    cdoID = revision.getID();
    cdoClass = revision.getCDOClass();
    dirtyVersion = revision.getVersion();
    originVersion = dirtyVersion - 1;
  }

  public CDORevisionDeltaImpl(CDORevision originRevision, CDORevision dirtyRevision)
  {
    if (originRevision.getCDOClass() != dirtyRevision.getCDOClass())
    {
      throw new IllegalArgumentException();
    }

    cdoID = originRevision.getID();
    cdoClass = originRevision.getCDOClass();
    dirtyVersion = dirtyRevision.getVersion();
    originVersion = originRevision.getVersion();

    compare(originRevision, dirtyRevision);

    CDORevisionData originData = originRevision.getData();
    CDORevisionData dirtyData = dirtyRevision.getData();
    if (!compare(originData.getContainerID(), dirtyData.getContainerID())
        || !compare(originData.getContainingFeatureID(), dirtyData.getContainingFeatureID()))
    {
      addFeatureDelta(new CDOContainerFeatureDeltaImpl(dirtyData.getContainerID(), dirtyData.getContainingFeatureID()));
    }

  }

  public CDORevisionDeltaImpl(ExtendedDataInput in, CDOPackageManager packageManager) throws IOException
  {
    CDOClassRef classRef = CDOModelUtil.readClassRef(in);
    cdoClass = classRef.resolve(packageManager);
    cdoID = CDOIDUtil.read(in, packageManager.getCDOIDObjectFactory());
    originVersion = in.readInt();
    dirtyVersion = in.readInt();
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOFeatureDelta featureDelta = CDOFeatureDeltaImpl.read(in, cdoClass);
      featureDeltas.put(featureDelta.getFeature(), featureDelta);
    }
  }

  public void write(ExtendedDataOutput out, CDOIDProvider idProvider) throws IOException
  {
    CDOClassRef classRef = cdoClass.createClassRef();
    CDOModelUtil.writeClassRef(out, classRef);
    CDOIDUtil.write(out, cdoID);
    out.writeInt(originVersion);
    out.writeInt(dirtyVersion);
    out.writeInt(featureDeltas.size());
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      ((CDOFeatureDeltaImpl)featureDelta).write(out, cdoClass, idProvider);
    }
  }

  public CDOID getID()
  {
    return cdoID;
  }

  public int getOriginVersion()
  {
    return originVersion;
  }

  public int getDirtyVersion()
  {
    return dirtyVersion;
  }

  public List<CDOFeatureDelta> getFeatureDeltas()
  {
    return new ArrayList<CDOFeatureDelta>(featureDeltas.values());
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).setVersion(dirtyVersion);
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      ((CDOFeatureDeltaImpl)featureDelta).apply(revision);
    }
  }

  public void addFeatureDelta(CDOFeatureDelta delta)
  {
    CDOFeature feature = delta.getFeature();
    if (feature.isMany())
    {
      CDOListFeatureDeltaImpl lookupDelta = (CDOListFeatureDeltaImpl)featureDeltas.get(feature);
      if (lookupDelta == null)
      {
        lookupDelta = new CDOListFeatureDeltaImpl(feature);
        featureDeltas.put(lookupDelta.getFeature(), lookupDelta);
      }

      // Remove all previous changes
      if (delta instanceof CDOClearFeatureDelta)
      {
        lookupDelta.getListChanges().clear();
      }
      lookupDelta.add(delta);
    }
    else
    {
      featureDeltas.put(feature, delta);
    }
  }

  public void adjustReferences(Map<CDOIDTemp, CDOID> idMappings)
  {
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      ((CDOFeatureDeltaImpl)featureDelta).adjustReferences(idMappings);
    }
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      ((CDOFeatureDeltaImpl)featureDelta).accept(visitor);
    }
  }

  private void compare(CDORevision originRevision, CDORevision dirtyRevision)
  {
    CDOFeature features[] = cdoClass.getAllFeatures();
    int count = cdoClass.getFeatureCount();
    for (int i = 0; i < count; i++)
    {
      CDOFeature feature = features[i];
      if (feature.isMany())
      {
        int originSize = originRevision.getData().size(feature);
        int dirtySize = dirtyRevision.getData().size(feature);
        if (dirtySize == 0 && originSize > 0)
        {
          addFeatureDelta(new CDOClearFeatureDeltaImpl(feature));
        }
        else
        {
          int originIndex = 0;
          int dirtyIndex = 0;
          if (originSize == dirtySize)
          {
            for (; originIndex < originSize && dirtyIndex < dirtySize; dirtyIndex++, originIndex++)
            {
              Object originValue = originRevision.getData().get(feature, originIndex);
              Object dirtyValue = dirtyRevision.getData().get(feature, dirtyIndex);

              if (!compare(originValue, dirtyValue))
              {
                dirtyIndex = 0;
                break;
              }
            }
          }

          if (originIndex != originSize || dirtyIndex != dirtySize)
          {
            // Not identical
            // Be very stupid and do the simplest thing.
            // Clear and add all value;
            if (originSize > 0)
            {
              addFeatureDelta(new CDOClearFeatureDeltaImpl(feature));
            }

            for (int k = 0; k < dirtySize; k++)
            {
              Object dirtyValue = dirtyRevision.getData().get(feature, k);
              addFeatureDelta(new CDOAddFeatureDeltaImpl(feature, k, dirtyValue));
            }
          }
        }
      }
      else
      {
        Object originValue = originRevision.getData().get(feature, 0);
        Object dirtyValue = dirtyRevision.getData().get(feature, 0);
        if (!compare(originValue, dirtyValue))
        {
          if (dirtyValue == null)
          {
            addFeatureDelta(new CDOUnsetFeatureDeltaImpl(feature));
          }
          else
          {
            addFeatureDelta(new CDOSetFeatureDeltaImpl(feature, 0, dirtyValue));
          }
        }
      }
    }
  }

  private boolean compare(Object originValue, Object dirtyValue)
  {
    return originValue == dirtyValue || originValue != null && dirtyValue != null && originValue.equals(dirtyValue);
  }
}
