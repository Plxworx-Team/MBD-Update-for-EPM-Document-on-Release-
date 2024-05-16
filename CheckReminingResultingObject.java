package  ext.nimr.MBDtableUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import wt.epm.EPMDocument;
import wt.epm.workspaces.EPMWorkspace;
import wt.epm.workspaces.EPMWorkspaceHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.collections.WTSet;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;

public class CheckReminingResultingObject {

	static String homefolder = null;
	static String WorkspaceName = null;

	public static boolean CheckReminingObject(Persistable persistable) throws WTException, IOException {

		Properties prop = new Properties();
		homefolder = WTProperties.getLocalProperties().getProperty("wt.home");
		System.out.println("homefolder=" + homefolder);

		try (FileInputStream file = new FileInputStream(
				
				homefolder + File.separatorChar + "codebase" + File.separatorChar + "ext" + File.separatorChar
						+ "nimr" + File.separatorChar + "MBDtableUpdate"+File.separatorChar+"CadCustomization.properties")) {
			prop.load(file);

		} catch (IOException e) {
			e.printStackTrace();
			throw e; // Re-throw the IOException
		}

		WorkspaceName = prop.getProperty("WorkspaceName");
		System.out.println("## WorkspaceName" + WorkspaceName);

		wt.epm.EPMDocument epmDoc = null;
		try {
			Persistable pers = persistable;

			epmDoc = (EPMDocument) pers;

			EPMWorkspace workspace = null;

			QuerySpec querySpec = new QuerySpec(EPMWorkspace.class);
			SearchCondition searchCondition = new SearchCondition(EPMWorkspace.class, EPMWorkspace.NAME,
					wt.query.SearchCondition.EQUAL, WorkspaceName);
			querySpec.appendWhere(searchCondition, new int[] { 0, -1 });
			QueryResult qr1 = wt.fc.PersistenceHelper.manager.find(querySpec);
			if (qr1.size() == 0) {
				System.out.println("## Cannot find workspace: ");

			}

			while (qr1.hasMoreElements()) {
				workspace = (EPMWorkspace) qr1.nextElement();
				System.out.println("## Workspace Name is :" + workspace.getName());

			}

			WTSet objsInWS = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, EPMDocument.class);

			if (!objsInWS.equals(null)) {
				if (objsInWS.contains(epmDoc)) {
					System.out.println("## objsInWS :" + objsInWS);
				}

				Iterator iterator = objsInWS.iterator();

				while (iterator.hasNext()) {
					EPMDocument keyobj = (EPMDocument) ((ObjectReference) iterator.next()).getObject();

					if (keyobj.getNumber().equalsIgnoreCase(epmDoc.getNumber())) {

						System.out.println("@@## yes");

						return true;
					}
				}
			}

		} catch (Exception e) {
		}
		return false;
	}

}