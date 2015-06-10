/*
 * Copyright (c) 2011-2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.graphiti.diagram;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreAddAClassFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreAddAInterfaceFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreAddAggregationFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreAddAssociationFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreAddCompositionFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreAddImplementationFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreAddSubClassesFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreCopyAClassFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreCreateAClassFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreCreateAInterfaceFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreCreateAggregationFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreCreateAssociationFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreCreateCompositionsFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreCreateImplementationFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreCreateSubclassFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreDirectEditAClassFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcorePasteAClassFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreReconnectionFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreRenameAClassFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features.AcoreUpdateAClassFeature;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util.AcoreGraphitiContextUtil;
import org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util.AcoreGraphitiContextUtil.ConnectionType;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

/**
 * @author Martin Fluegge
 */
public class AcoreFeatureProvider extends DefaultFeatureProvider
{
  public AcoreFeatureProvider(IDiagramTypeProvider dtp)
  {
    super(dtp);
  }

  @Override
  public ICreateFeature[] getCreateFeatures()
  {
    return new ICreateFeature[] { new AcoreCreateAClassFeature(this), new AcoreCreateAInterfaceFeature(this) };
  }

  @Override
  public ICreateConnectionFeature[] getCreateConnectionFeatures()
  {
    return new ICreateConnectionFeature[] { new AcoreCreateSubclassFeature(this),
        new AcoreCreateImplementationFeature(this), new AcoreCreateAssociationFeature(this),
        new AcoreCreateAggregationFeature(this), new AcoreCreateCompositionsFeature(this) };
  }

  @Override
  public IAddFeature getAddFeature(IAddContext context)
  {
    if (context instanceof IAddConnectionContext)
    {
      ConnectionType connectionType = (ConnectionType)context.getProperty(AcoreGraphitiContextUtil.CONNECTTION_TYPE);
      switch (connectionType)
      {
      case SUBCLASSES:
      {
        return new AcoreAddSubClassesFeature(this);
      }
      case IMPLEMENTATIONS:
      {
        return new AcoreAddImplementationFeature(this);
      }
      case AGGREGATIONS:
      {
        return new AcoreAddAggregationFeature(this);
      }
      case ASSOCIATIONS:
      {
        return new AcoreAddAssociationFeature(this);
      }
      case COMPOSITIONS:
      {
        return new AcoreAddCompositionFeature(this);
      }
      default:
        break;
      }
    }
    else
    {
      if (context.getNewObject() instanceof AClass)
      {
        return new AcoreAddAClassFeature(this);
      }
      else if (context.getNewObject() instanceof AInterface)
      {
        return new AcoreAddAInterfaceFeature(this);
      }
    }

    return super.getAddFeature(context);
  }

  @Override
  public IUpdateFeature getUpdateFeature(IUpdateContext context)
  {
    PictogramElement pictogramElement = context.getPictogramElement();
    if (pictogramElement instanceof ContainerShape)
    {
      Object bo = getBusinessObjectForPictogramElement(pictogramElement);
      if (bo instanceof AClass)
      {
        return new AcoreUpdateAClassFeature(this);
      }
    }
    return super.getUpdateFeature(context);
  }

  @Override
  public IFeature[] getDragAndDropFeatures(IPictogramElementContext context)
  {
    // simply return all create connection features
    return getCreateConnectionFeatures();
  }

  @Override
  public IReconnectionFeature getReconnectionFeature(IReconnectionContext context)
  {
    return new AcoreReconnectionFeature(this);
  }

  @Override
  public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context)
  {
    PictogramElement pe = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pe);
    if (bo instanceof AClass)
    {
      return new AcoreDirectEditAClassFeature(this);
    }
    return super.getDirectEditingFeature(context);
  }

  @Override
  public ICopyFeature getCopyFeature(ICopyContext context)
  {
    return new AcoreCopyAClassFeature(this);
  }

  @Override
  public IPasteFeature getPasteFeature(IPasteContext context)
  {
    return new AcorePasteAClassFeature(this);
  }

  @Override
  public ICustomFeature[] getCustomFeatures(ICustomContext context)
  {
    return new ICustomFeature[] { new AcoreRenameAClassFeature(this) };
  }
}
