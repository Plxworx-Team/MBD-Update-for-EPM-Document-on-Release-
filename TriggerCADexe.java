package ext.nimr.MBDtableUpdate;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Properties;

import wt.epm.EPMDocument;
import wt.epm.workspaces.EPMWorkspace;
import wt.epm.workspaces.EPMWorkspaceHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTSet;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;

public class TriggerCADexe {

	static String homefolder = null;

	static String WorkspaceName = null;
	static String ServerName = null;
	static String PortNumber = null;

	public static void addToWorkspace(Persistable persistable, String descriptionVal) throws WTException, IOException {

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
		ServerName = prop.getProperty("ServerName");
		System.out.println("## ServerName" + ServerName);
		PortNumber = prop.getProperty("PortNumber");
		System.out.println("## PortNumber" + PortNumber);

		wt.epm.EPMDocument epmDoc = null;
		try {
			Persistable pers = persistable;

			if (pers instanceof EPMDocument) {
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
				if (objsInWS.contains(epmDoc)) {
					System.out.println("## objsInWS :" + objsInWS);
				}

				Iterator iterator = objsInWS.iterator();

				while (iterator.hasNext()) {
					EPMDocument keyobj = (EPMDocument) ((ObjectReference) iterator.next()).getObject();

					if (keyobj.getNumber().equalsIgnoreCase(epmDoc.getNumber())) {

						System.out.println("Object to be removed: " + keyobj.getName() + " "
								+ keyobj.getIterationDisplayIdentifier());

						WTArrayList epmDocumentsCollection = new WTArrayList();
						epmDocumentsCollection.add(keyobj);

						WTCollection epmDocumentsCollectionToRemove = epmDocumentsCollection;

						EPMWorkspaceHelper.manager.removeFromWorkspace(workspace, epmDocumentsCollectionToRemove);
						System.out.println("Removed successfully.");
					}
				}
				System.out.println(
						"Object to be Added: " + epmDoc.getName() + " " + epmDoc.getIterationDisplayIdentifier());

				WTArrayList epmDocumentsCollection = new WTArrayList();
				epmDocumentsCollection.add(epmDoc);

				WTCollection epmDocumentsCollectionToadd = epmDocumentsCollection;

				EPMWorkspaceHelper.manager.addToWorkspace(workspace, epmDocumentsCollectionToadd);
				System.out.println("## Added successfully.");

			} else {
				System.out.println(" other objects");

			}

		} catch (Exception e) {
		}
	}

	public static void sendToServer(String ReqData) throws WTException, IOException {

		final String SERVER_HOST = ServerName;
		final int SERVER_PORT = Integer.parseInt(PortNumber);
		System.out.println("## SERVER_HOST :- " + SERVER_HOST);
		System.out.println("## SERVER_PORT :- " + SERVER_PORT);

		try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
				DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

			String requestData = ReqData;
			outputStream.write(requestData.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();

			System.out.println("Connected to server at " + SERVER_HOST + " on port " + SERVER_PORT);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}