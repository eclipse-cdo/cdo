/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.ui;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.lm.reviews.ModelReference;
import org.eclipse.emf.cdo.lm.reviews.ModelReference.Builder;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil.Input;

import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.tree.TreeNode;

/**
 * @author Eike Stepper
 */
public class CompareModelReferenceExtractor implements ModelReference.Extractor
{
  public static final int PRIORITY = 1000;

  private static final int ATTRIBUTE_VALUE_MAX_LENGTH = OMPlatform.INSTANCE
      .getProperty("org.eclipse.emf.cdo.lm.reviews.ui.CompareModelReferenceExtractor.ATTRIBUTE_VALUE_MAX_LENGTH", ModelReference.Builder.NO_MAX_LENGTH);

  private final int attributeValueMaxLength;

  public CompareModelReferenceExtractor(int attributeValueMaxLength)
  {
    this.attributeValueMaxLength = attributeValueMaxLength;
  }

  public CompareModelReferenceExtractor()
  {
    this(ATTRIBUTE_VALUE_MAX_LENGTH);
  }

  @Override
  public int getPriority()
  {
    return PRIORITY;
  }

  @Override
  public ModelReference extractModelReference(Object object)
  {
    TreeNode treeNode = Input.getTreeNode(object);
    if (treeNode == null)
    {
      return null;
    }

    EObject data = treeNode.getData();
    if (data instanceof Match)
    {
      Match match = (Match)data;
      return addMatch(ModelReference.builder("match"), match).build();
    }

    if (data instanceof AttributeChange)
    {
      AttributeChange diff = (AttributeChange)data;
      return addDiff(diff, diff.getAttribute()).property(diff.getValue(), attributeValueMaxLength).build();
    }

    if (data instanceof ReferenceChange)
    {
      ReferenceChange diff = (ReferenceChange)data;
      return addDiff(diff, diff.getReference()).property(diff.getValue()).build();
    }

    return null;
  }

  private static Builder addDiff(Diff diff, EStructuralFeature feature)
  {
    DifferenceKind kind = diff.getKind();
    String type = kind == null ? "diff" : kind.getName().toLowerCase();
    Match match = diff.getMatch();
    String featureName = feature.getName();
    return addMatch(ModelReference.builder(type), match).property(featureName);
  }

  private static Builder addMatch(Builder builder, Match match)
  {
    EObject matchObject = getMatchObject(match);
    CDOID objectID = CDOIDUtil.getCDOID(matchObject);
    return builder.property(objectID);
  }

  private static EObject getMatchObject(Match match)
  {
    EObject left = match.getLeft();
    if (left != null)
    {
      return left;
    }

    EObject right = match.getRight();
    if (right != null)
    {
      return right;
    }

    return match.getOrigin();
  }
}
