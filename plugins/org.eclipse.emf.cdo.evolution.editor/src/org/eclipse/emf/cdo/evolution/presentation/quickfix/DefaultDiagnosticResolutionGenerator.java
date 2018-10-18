/*
 * Copyright (c) 2004-2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.evolution.presentation.quickfix;

import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionFactory;
import org.eclipse.emf.cdo.evolution.FeaturePathMigration;
import org.eclipse.emf.cdo.evolution.Model;
import org.eclipse.emf.cdo.evolution.impl.EvolutionImpl;
import org.eclipse.emf.cdo.evolution.presentation.quickfix.DiagnosticResolution.Generator;
import org.eclipse.emf.cdo.evolution.util.DiagnosticID;
import org.eclipse.emf.cdo.evolution.util.DiagnosticType;
import org.eclipse.emf.cdo.evolution.util.ElementHandler;
import org.eclipse.emf.cdo.evolution.util.EvolutionValidator;
import org.eclipse.emf.cdo.evolution.util.IDAnnotation;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class DefaultDiagnosticResolutionGenerator extends BasicDiagnosticResolutionGenerator
{
  public DefaultDiagnosticResolutionGenerator()
  {
  }

  public void getResolutions(Diagnostic diagnostic, final Context context)
  {
    if (EvolutionValidator.DIAGNOSTIC_SOURCE.equals(diagnostic.getSource()))
    {
      int code = diagnostic.getCode();
      switch (code)
      {
      case EvolutionValidator.CODE_NO_MODEL:
        handleNoModel(diagnostic, context);
        break;

      case EvolutionValidator.CODE_NO_URI:
        handleNoURI(diagnostic, context);
        break;

      case EvolutionValidator.CODE_NO_RESOURCE_SET:
        break;

      case EvolutionValidator.CODE_RESOURCE_NOT_FOUND:
        break;

      case EvolutionValidator.CODE_LOAD_PROBLEM:
        break;

      case EvolutionValidator.CODE_CONTENT_PROBLEM:
        break;

      case EvolutionValidator.CODE_PACKAGE_MISSING:
        handlePackageMissing(diagnostic, context);
        break;

      case EvolutionValidator.CODE_PACKAGE_NOT_UNIQUE:
        break;

      case EvolutionValidator.CODE_NSURI_NOT_UNIQUE:
        break;

      case EvolutionValidator.CODE_NSURI_NOT_CHANGED:
        handleNSURINotChanged(diagnostic, context);
        break;

      case EvolutionValidator.CODE_ID_ANNOTATION_MISSING:
      case EvolutionValidator.CODE_ID_WITHOUT_VALUE:
        handleNoID(diagnostic, context);
        break;

      case EvolutionValidator.CODE_ID_NOT_UNIQUE:
        handleIDNotUnique(diagnostic, context);
        break;

      case EvolutionValidator.CODE_FEATURE_PATH_UNKNOWN:
        handleFeaturePathUnknown(diagnostic, context);
        break;

      case EvolutionValidator.CODE_RELEASE:
        handleRelease(diagnostic, context);
        break;
      }
    }
  }

  protected void handleNoModel(Diagnostic diagnostic, final Context context)
  {
    context.add(new EvolutionDiagnosticResolution(null, getString("_QF_SelectModelsFromWorkspace"), null, false, diagnostic.getCode())
    {
      @Override
      public void run(Diagnostic diagnostic)
      {
        Shell shell = context.getEditor().getSite().getShell();

        FilteredResourcesSelectionDialog dialog = new FilteredResourcesSelectionDialog(shell, true, ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
        dialog.setInitialPattern("*.ecore");
        dialog.open();

        Object[] result = dialog.getResult();
        if (result == null || result.length == 0)
        {
          return;
        }

        Evolution evolution = (Evolution)diagnostic.getData().get(0);
        for (Object object : result)
        {
          if (object instanceof IFile)
          {
            IFile file = (IFile)object;
            evolution.addModel(URI.createPlatformResourceURI(file.getFullPath().toString(), true));
          }
        }
      }
    });
  }

  protected void handleNoURI(Diagnostic diagnostic, final Context context)
  {
    context.add(new EvolutionDiagnosticResolution(null, getString("_QF_SelectModelFromWorkspace"), null, false, diagnostic.getCode())
    {
      @Override
      public void run(Diagnostic diagnostic)
      {
        Shell shell = context.getEditor().getSite().getShell();

        FilteredResourcesSelectionDialog dialog = new FilteredResourcesSelectionDialog(shell, false, ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
        dialog.setInitialPattern("*.ecore");
        dialog.open();

        Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IFile))
        {
          return;
        }

        IFile file = (IFile)result[0];
        Model model = (Model)diagnostic.getData().get(0);
        model.setURI(URI.createPlatformResourceURI(file.getFullPath().toString(), true));
      }
    });
  }

  protected void handlePackageMissing(Diagnostic diagnostic, final Context context)
  {
    final List<?> data = diagnostic.getData();

    Resource resource = ((EPackage)data.get(2)).eResource();
    if (resource != null)
    {
      final URI uri = resource.getURI();
      if (uri != null)
      {
        context.add(new EvolutionDiagnosticResolution(null, getString("_QF_PackageMissing", uri), null, true, diagnostic.getCode())
        {
          @Override
          public void run(Diagnostic diagnostic)
          {
            Evolution evolution = (Evolution)data.get(0);
            evolution.addModel(uri);
          }
        });
      }
    }
  }

  protected void handleNSURINotChanged(Diagnostic diagnostic, Context context)
  {
    Evolution evolution = (Evolution)diagnostic.getData().get(3);
    final int nextVersion = evolution.getNextReleaseVersion();

    context.add(new EvolutionDiagnosticResolution(null, getString("_QF_SetVersionIntoNamespace", nextVersion), null, true, diagnostic.getCode())
    {
      @Override
      public void run(Diagnostic diagnostic)
      {
        EPackage ePackage = (EPackage)diagnostic.getData().get(0);
        String nsURI = ePackage.getNsURI();
        if (!nsURI.endsWith("/"))
        {
          nsURI += "/";
        }

        Pattern pattern = Pattern.compile("/v[0-9]+/");
        Matcher matcher = pattern.matcher(nsURI);
        if (matcher.find())
        {
          int start = matcher.start();
          int end = matcher.end();
          String prefix = nsURI.substring(0, start);
          String suffix = nsURI.substring(end, nsURI.length() - 1);

          nsURI = prefix + "/v" + nextVersion + "/" + suffix;
        }
        else
        {
          nsURI += "v" + nextVersion;
        }

        ePackage.setNsURI(nsURI);
      }
    });

    context.add(new EvolutionDiagnosticResolution(null, getString("_QF_DisableUniqueNamespaceEnforcement"), null, false, diagnostic.getCode())
    {
      @Override
      public int getRelevanceForResolution()
      {
        return -100;
      }

      @Override
      public void run(Diagnostic diagnostic)
      {
        Evolution evolution = (Evolution)diagnostic.getData().get(3);
        evolution.setUniqueNamespaces(false);
      }
    });
  }

  protected void handleNoID(Diagnostic diagnostic, final Context context)
  {
    context.add(new EvolutionDiagnosticResolution(null, getString("_QF_AssignID"), null, true, diagnostic.getCode())
    {
      @Override
      protected DiagnosticType[] getCompatibleTypes()
      {
        return new DiagnosticType[] { getType(), new DiagnosticType(EvolutionValidator.DIAGNOSTIC_SOURCE, EvolutionValidator.CODE_ID_WITHOUT_VALUE) };
      }

      @Override
      public void run(Diagnostic diagnostic)
      {
        EModelElement modelElement = (EModelElement)diagnostic.getData().get(0);
        IDAnnotation.ensureValue(modelElement);
      }
    });
  }

  protected void handleIDNotUnique(Diagnostic diagnostic, final Context context)
  {
    context.add(new EvolutionDiagnosticResolution(null, getString("_QF_RememberID"), null, true, diagnostic.getCode())
    {
      @Override
      public int getRelevanceForResolution()
      {
        return 100;
      }

      @Override
      public void run(Diagnostic diagnostic)
      {
        EModelElement modelElement = (EModelElement)diagnostic.getData().get(0);
        String oldValue = IDAnnotation.getValue(modelElement);
        IDAnnotation.setOldValue(modelElement, oldValue);
        IDAnnotation.setValue(modelElement, null);
        IDAnnotation.ensureValue(modelElement);
      }
    });

    context.add(new EvolutionDiagnosticResolution(null, getString("_QF_ReplaceID"), null, true, diagnostic.getCode())
    {
      @Override
      public void run(Diagnostic diagnostic)
      {
        EModelElement modelElement = (EModelElement)diagnostic.getData().get(0);
        IDAnnotation.setValue(modelElement, null);
        IDAnnotation.ensureValue(modelElement);
      }
    });
  }

  protected void handleFeaturePathUnknown(Diagnostic diagnostic, Context context)
  {
    final ElementChange elementChange = (ElementChange)diagnostic.getData().get(0);
    final ChangeKind kind = elementChange.getKind();

    context.add(new EvolutionDiagnosticResolution(null,
        getString("_QF_SpecifyFeaturePath", kind.getName().toLowerCase(), ElementHandler.getLabel(elementChange.getNewElement())), null, false,
        diagnostic.getCode())
    {
      @Override
      public void run(Diagnostic diagnostic)
      {
        FeaturePathMigration migration = EvolutionFactory.eINSTANCE.createFeaturePathMigration();
        migration.setDiagnosticID(DiagnosticID.get(diagnostic).getValue());
        migration.setFromClass(((EStructuralFeature)elementChange.getOldElement()).getEContainingClass());
        migration.setToClass(((EStructuralFeature)elementChange.getNewElement()).getEContainingClass());

        Evolution evolution = (Evolution)elementChange.getNewModelSet();
        evolution.getMigrations().add(migration);
      }
    });
  }

  protected void handleRelease(Diagnostic diagnostic, final Context context)
  {
    final EvolutionImpl evolution = (EvolutionImpl)diagnostic.getData().get(0);
    final int nextVersion = evolution.getNextReleaseVersion();

    context.add(new EvolutionDiagnosticResolution(null, getString("_QF_CreateRelease", nextVersion), null, false, diagnostic.getCode())
    {
      @Override
      public void run(Diagnostic diagnostic)
      {
        evolution.createRelease();
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  protected static abstract class EvolutionDiagnosticResolution extends BasicDiagnosticResolution.TypedDiagnosticResolution
  {
    protected EvolutionDiagnosticResolution(Image image, String text, String description, boolean multi, int code)
    {
      super(image, text, description, multi, EvolutionValidator.DIAGNOSTIC_SOURCE, code);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends DiagnosticResolution.Generator.Factory
  {
    public Factory()
    {
    }

    @Override
    public Generator create(String description) throws ProductCreationException
    {
      return new DefaultDiagnosticResolutionGenerator();
    }
  }
}
