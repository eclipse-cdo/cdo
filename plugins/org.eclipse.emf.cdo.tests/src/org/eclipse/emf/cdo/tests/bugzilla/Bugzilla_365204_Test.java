package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Egidijus Vaisnora
 */
public class Bugzilla_365204_Test extends AbstractCDOTest
{
  public void testAuditViewTimeSwitch() throws CommitException
  {
    skipUnlessAuditing();

    CDOSession sessionA = openSession();
    CDOTransaction transaction = sessionA.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test"));

    Category category = getModel1Factory().createCategory();
    resource.getContents().add(category);

    // Make version 1 of the category
    CDOCommitInfo commit1 = transaction.commit();
    CDOID categoryID = CDOUtil.getCDOObject(category).cdoID();

    CDOSession sessionB = openSession();
    CDOView auditView = sessionB.openView(commit1.getTimeStamp());
    CDOObject auditCategory = auditView.getObject(categoryID);
    assertEquals(1, CDOUtil.getCDOObject(auditCategory).cdoRevision().getVersion());

    // Make version 2 of the category
    category.setName("version 2");
    CDOCommitInfo commit2 = commitAndSync(transaction, auditView);
    assertEquals(true, commit1.getTimeStamp() < commit2.getTimeStamp());
    assertEquals(2, CDOUtil.getCDOObject(category).cdoRevision().getVersion());

    // Make version 3 of the category
    category.setName("version 3");
    CDOCommitInfo commit3 = commitAndSync(transaction, auditView);
    assertEquals(true, commit2.getTimeStamp() < commit3.getTimeStamp());
    assertEquals(3, CDOUtil.getCDOObject(category).cdoRevision().getVersion());

    // Load PROXY
    ((Category)CDOUtil.getEObject(auditCategory)).getName();

    // Fill cache with latest versions
    CDORevision version2 = cacheRevision(sessionB, categoryID, 2); // Keep pointer
    CDORevision version3 = cacheRevision(sessionB, categoryID, 3); // Keep pointer
    System.out.println(version2);
    System.out.println(version3);

    auditView.setTimeStamp(commit3.getTimeStamp());

    // Load PROXY if any
    ((Category)CDOUtil.getEObject(auditCategory)).getName();

    assertEquals(3, CDOUtil.getCDOObject(auditCategory).cdoRevision().getVersion());
  }

  private CDORevision cacheRevision(CDOSession session, CDOID id, int version)
  {
    CDOBranchVersion branchVersion = session.getBranchManager().getMainBranch().getVersion(version);
    CDORevisionManager revisionManager = session.getRevisionManager();
    return revisionManager.getRevisionByVersion(id, branchVersion, CDORevision.UNCHUNKED, true);
  }
}
