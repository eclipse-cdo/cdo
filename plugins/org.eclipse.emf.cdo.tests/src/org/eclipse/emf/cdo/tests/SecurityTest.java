/*
 * Copyright (c) 2013, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.security.PermissionUtil;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.ResourceFilter;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.impl.PermissionFilterImpl;
import org.eclipse.emf.cdo.security.impl.PermissionImpl.CommitImpactContext;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class SecurityTest extends AbstractCDOTest
{
  private static final SecurityFactory SF = SecurityFactory.eINSTANCE;

  private static final PermissionFilter FALSE = new BooleanFilter(false);

  private static final PermissionFilter TRUE = new BooleanFilter(true);

  private static final EClass RESOURCE = EresourcePackage.Literals.CDO_RESOURCE;

  private static final EClass FOLDER = EresourcePackage.Literals.CDO_RESOURCE_FOLDER;

  public void testNotFilter() throws Exception
  {
    assertApplicable(false, FALSE);
    assertApplicable(true, SF.createNotFilter(FALSE));

    assertApplicable(true, TRUE);
    assertApplicable(false, SF.createNotFilter(TRUE));
  }

  public void testAndFilter() throws Exception
  {
    assertApplicable(true, SF.createAndFilter());

    assertApplicable(false, SF.createAndFilter(FALSE));
    assertApplicable(false, SF.createAndFilter(FALSE, FALSE));
    assertApplicable(false, SF.createAndFilter(FALSE, FALSE, FALSE));

    assertApplicable(true, SF.createAndFilter(TRUE));
    assertApplicable(true, SF.createAndFilter(TRUE, TRUE));
    assertApplicable(true, SF.createAndFilter(TRUE, TRUE, TRUE));

    assertApplicable(false, SF.createAndFilter(TRUE, FALSE));
    assertApplicable(false, SF.createAndFilter(FALSE, TRUE));
  }

  public void testOrFilter() throws Exception
  {
    assertApplicable(false, SF.createOrFilter());

    assertApplicable(false, SF.createOrFilter(FALSE));
    assertApplicable(false, SF.createOrFilter(FALSE, FALSE));
    assertApplicable(false, SF.createOrFilter(FALSE, FALSE, FALSE));

    assertApplicable(true, SF.createOrFilter(TRUE));
    assertApplicable(true, SF.createOrFilter(TRUE, TRUE));
    assertApplicable(true, SF.createOrFilter(TRUE, TRUE, TRUE));

    assertApplicable(true, SF.createOrFilter(TRUE, FALSE));
    assertApplicable(true, SF.createOrFilter(FALSE, TRUE));
  }

  public void testResourceFilter_EXACT_Parents() throws Exception
  {
    ResourceFilter filter = SF.createResourceFilter("/home/Stepper", PatternStyle.EXACT, true);

    checkSegments(filter, "/home/Stepper/private/a/b/c", "1110000");
    checkSegments(filter, "/home/Step/private/a/b/c", "1100000");
    checkSegments(filter, "/homer/Stepper", "100");
  }

  public void testResourceFilter_EXACT_NoParents() throws Exception
  {
    ResourceFilter filter = SF.createResourceFilter("/home/Stepper", PatternStyle.EXACT, false);

    checkSegments(filter, "/home/Stepper/private/a/b/c", "0010000");
    checkSegments(filter, "/home/Step/private/a/b/c", "0000000");
    checkSegments(filter, "/homer/Stepper", "000");
  }

  public void testResourceFilter_TREE_Parents() throws Exception
  {
    ResourceFilter filter = SF.createResourceFilter("/home/Stepper", PatternStyle.TREE, true);

    checkSegments(filter, "/home/Stepper/private/a/b/c", "1111111");
    checkSegments(filter, "/home/Step/private/a/b/c", "1100000");
    checkSegments(filter, "/homer/Stepper", "100");
  }

  public void testResourceFilter_TREE_NoParents() throws Exception
  {
    ResourceFilter filter = SF.createResourceFilter("/home/Stepper", PatternStyle.TREE, false);

    checkSegments(filter, "/home/Stepper/private/a/b/c", "0011111");
    checkSegments(filter, "/home/Step/private/a/b/c", "0000000");
    checkSegments(filter, "/homer/Stepper", "000");
  }

  public void testResourceFilter_UserToken_EXACT_Parents() throws Exception
  {
    ResourceFilter filter = SF.createResourceFilter("/home/${user}", PatternStyle.EXACT, true);

    try
    {
      PermissionUtil.setUser("Stepper");
      checkSegments(filter, "/home/Stepper/private/a/b/c", "1110000");
      checkSegments(filter, "/home/Step/private/a/b/c", "1100000");
      checkSegments(filter, "/homer/Stepper", "100");

      PermissionUtil.setUser("Enemy");
      checkSegments(filter, "/home/Stepper/private/a/b/c", "1100000");
      checkSegments(filter, "/home/Step/private/a/b/c", "1100000");
      checkSegments(filter, "/homer/Stepper", "100");
    }
    finally
    {
      PermissionUtil.setUser(null);
    }
  }

  public void testResourceFilter_UserToken_EXACT_NoParents() throws Exception
  {
    ResourceFilter filter = SF.createResourceFilter("/home/${user}", PatternStyle.EXACT, false);

    try
    {
      PermissionUtil.setUser("Stepper");
      checkSegments(filter, "/home/Stepper/private/a/b/c", "0010000");
      checkSegments(filter, "/home/Step/private/a/b/c", "0000000");
      checkSegments(filter, "/homer/Stepper", "000");

      PermissionUtil.setUser("Enemy");
      checkSegments(filter, "/home/Stepper/private/a/b/c", "0000000");
      checkSegments(filter, "/home/Step/private/a/b/c", "0000000");
      checkSegments(filter, "/homer/Stepper", "000");
    }
    finally
    {
      PermissionUtil.setUser(null);
    }
  }

  public void testResourceFilter_UserToken_TREE_Parents() throws Exception
  {
    ResourceFilter filter = SF.createResourceFilter("/home/${user}", PatternStyle.TREE, true);

    try
    {
      PermissionUtil.setUser("Stepper");
      checkSegments(filter, "/home/Stepper/private/a/b/c", "1111111");
      checkSegments(filter, "/home/Step/private/a/b/c", "1100000");
      checkSegments(filter, "/homer/Stepper", "100");

      PermissionUtil.setUser("Enemy");
      checkSegments(filter, "/home/Stepper/private/a/b/c", "1100000");
      checkSegments(filter, "/home/Step/private/a/b/c", "1100000");
      checkSegments(filter, "/homer/Stepper", "100");
    }
    finally
    {
      PermissionUtil.setUser(null);
    }
  }

  public void testResourceFilter_UserToken_TREE_NoParents() throws Exception
  {
    ResourceFilter filter = SF.createResourceFilter("/home/${user}", PatternStyle.TREE, false);

    try
    {
      PermissionUtil.setUser("Stepper");
      checkSegments(filter, "/home/Stepper/private/a/b/c", "0011111");
      checkSegments(filter, "/home/Step/private/a/b/c", "0000000");
      checkSegments(filter, "/homer/Stepper", "000");

      PermissionUtil.setUser("Enemy");
      checkSegments(filter, "/home/Stepper/private/a/b/c", "0000000");
      checkSegments(filter, "/home/Step/private/a/b/c", "0000000");
      checkSegments(filter, "/homer/Stepper", "000");
    }
    finally
    {
      PermissionUtil.setUser(null);
    }
  }

  private void checkSegments(ResourceFilter filter, String resourcePath, String expectations) throws Exception
  {
    RevisionMap revisions = new RevisionMap();
    CDORevision[] segments = revisions.addResourceNode(resourcePath, RESOURCE);

    for (int i = 0; i < segments.length; i++)
    {
      CDORevision segment = segments[i];
      boolean expected = expectations.charAt(i) == '1';
      boolean actual = filter.isApplicable(segment, revisions, null, 0);

      assertEquals(segment.toString(), expected, actual);
    }
  }

  private void assertApplicable(boolean expected, PermissionFilter filter) throws Exception
  {
    assertEquals(expected, filter.isApplicable(null, null, null, 0));
  }

  /**
   * @author Eike Stepper
   */
  private static final class RevisionMap extends HashMap<CDOID, CDORevisionImpl> implements CDORevisionProvider
  {
    private static final long serialVersionUID = 1L;

    private final Map<String, CDORevisionImpl> resourceNodes = new HashMap<>();

    private long lastID;

    @Override
    public CDORevisionImpl getRevision(CDOID id)
    {
      return get(id);
    }

    public CDORevisionImpl addRevision(EClass type)
    {
      CDOID id = CDOIDUtil.createLong(++lastID);

      CDORevisionImpl revision = new CDORevisionImpl(type);
      revision.setID(id);

      put(id, revision);
      return revision;
    }

    public CDORevisionImpl[] addResourceNode(String resourcePath, EClass type)
    {
      List<CDORevisionImpl> result = new ArrayList<>();
      CDORevisionImpl revision = null;
      EClass segmentType = RESOURCE;

      Path path = new Path(resourcePath);
      for (int i = path.segmentCount(); i >= 0; --i)
      {
        IPath p = path.removeLastSegments(i);
        String pathStr = p.toString();

        CDORevisionImpl node = resourceNodes.get(pathStr);
        if (node != null)
        {
          revision = node;
        }
        else
        {
          revision = createResource(revision, p.lastSegment(), i == 0 ? type : segmentType);
          resourceNodes.put(pathStr, revision);
        }

        result.add(revision);
        segmentType = FOLDER;
      }

      return result.toArray(new CDORevisionImpl[result.size()]);
    }

    private CDORevisionImpl createResource(CDORevisionImpl parent, String name, EClass type)
    {
      CDORevisionImpl revision = addRevision(type);
      revision.setValue(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, name);

      if (parent != null)
      {
        revision.setContainerID(parent.getID());
        getContents(parent).add(revision.getID());

      }

      return revision;
    }

    private CDOList getContents(CDORevisionImpl revision)
    {
      if (revision.getEClass() == RESOURCE)
      {
        if (CDOIDUtil.isNull((CDOID)revision.getContainerID()))
        {
          return revision.getOrCreateList(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS, 0);
        }

        return null;
      }

      return revision.getOrCreateList(EresourcePackage.Literals.CDO_RESOURCE_FOLDER__NODES, 0);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class BooleanFilter extends PermissionFilterImpl
  {
    private final boolean value;

    public BooleanFilter(boolean value)
    {
      this.value = value;
    }

    @Override
    public String format()
    {
      return Boolean.toString(value);
    }

    @Override
    protected boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception
    {
      return value;
    }

    @Override
    public boolean isImpacted(CommitImpactContext context)
    {
      return false;
    }
  }
}
