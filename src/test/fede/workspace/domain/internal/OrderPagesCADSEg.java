package test.fede.workspace.domain.internal;

import java.util.Collection;
import java.util.List;

import junit.framework.Assert;
import fr.imag.adele.cadse.cadseg.WorkspaceCST;
import fr.imag.adele.cadse.cadseg.generate.GenerateCadseDefinitionModel;
import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.cadse.cadseg.managers.attributes.LinkManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.CreationDialogManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.DataModelManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.ItemTypeManager;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;
import org.osgi.framework.Bundle;

import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.UIField;
import fede.workspace.tool.loadmodel.model.jaxb.CCadse;
import fede.workspace.tool.loadmodel.model.jaxb.CItemType;
import fede.workspace.tool.loadmodel.model.jaxb.CLinkType;
import fr.imag.adele.cadse.test.GTCadseRTConstants;
import fr.imag.adele.cadse.test.GTCadseTestCase;
import fr.imag.adele.cadse.test.GTScreenshot;
import fr.imag.adele.cadse.test.GTTestParameters;
import fr.imag.adele.cadse.test.gtworkbench_part.GTShell;

public class OrderPagesCADSEg extends GTCadseTestCase {
	GTShell				shell;

	private static String	packageName;

	private static String	cadseName;

	static GeneratorName	generator	= new GeneratorName();

	/**
	 * Performs the official simple tutorial
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void test_Initializations() throws Exception {

		/* =============== */
		/* Initializations */
		/* =============== */

		Bundle b = Platform.getBundle("fr.imag.adele.cadse.si.workspace.view");
		b.start();
		assertEquals(b.getState(), Bundle.ACTIVE);
		
		// a few parameters...
		// TestUtil.setVelocity(100);
		GTTestParameters.setTimeout(10000);
		GTTestParameters.banner();
		if (System.getProperty("test.screenshotPath") != null)
			GTScreenshot.setScreenshotPath(System.getProperty("test.screenshotPath"));
	}
	
	@Test
	public void test_selection() throws Exception {
		//workspaceView.open();
		
		/* ================================== */
		/* CADSEs selection in startup window */
		/* ================================== */

		shell =  new GTShell(GTCadseRTConstants.CADSE_SELECTOR_SHELL_TITLE);
		shell.selectCadses(GTCadseRTConstants.CADSEG_MODEL);
		shell.capture();
		shell.close();
		welcomeView.close();

	}

	@Test
	public void test_createCadse() throws Exception {

		/* =================== */
		/* data-model creation */
		/* =================== */

		// CADSE WebAppModel
		workspaceView.findTree().contextMenu(GTCadseRTConstants.CONTEXTMENU_NEW).menu(
				GTCadseRTConstants.CONTEXTMENU_NEW_CADSE_DEFINITION).click();
		shell = new GTShell(WorkspaceCST.CADSE_DEFINITION);
		cadseName = "Cadse_" + generator.newName();
		shell.findField(CadseRootCST.ITEM_TYPE_at_NAME_).typeText( cadseName);

		packageName = "model.webapp";
		shell.findField(WorkspaceCST.CADSE_DEFINITION_at_PACKAGENAME_).typeText( packageName);
		shell.capture();
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.findTree().selectNode(cadseName).expand();


		packageExplorerView.show();
		packageExplorerView.findTree().selectNode("Model.Workspace." + cadseName).expand();
		packageExplorerView.findTree().selectNode("Model.Workspace." + cadseName, "sources").expand();
		packageExplorerView.capture();
		workspaceView.show();
	}

	@Test
	public void test_createTypeA_TypeB_Link_lt_a_to_b() throws Exception {
		// Item Type A
		String TypeA = "A";
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL)
		.contextMenu(GTCadseRTConstants.CONTEXTMENU_NEW).menu("Item type").click();

		shell = new GTShell(WorkspaceCST.ITEM_TYPE);
		shell.findField(CadseRootCST.ITEM_TYPE_at_NAME_).typeText(TypeA);
		shell.findField(WorkspaceCST.ITEM_TYPE_at_HAS_CONTENT_).check(false);
		shell.capture();
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL, TypeA).expand();
		workspaceView.capture();

		createCreationPage(TypeA, "creation-page2", "Title 2", "Desciption 2");
		createCreationPage(TypeA, "creation-page3", "Title 2", "Desciption 2");
		createCreationPage(TypeA, "creation-page4", "Title 2", "Desciption 2");
		createCreationPage(TypeA, "creation-page5", "Title 2", "Desciption 2");
		createCreationPage(TypeA, "creation-page6", "Title 2", "Desciption 2");

		// Item Type B
		String TypeB = "B";
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL)
				.contextMenu(GTCadseRTConstants.CONTEXTMENU_NEW).menu("Item type").click();
		shell = new GTShell(WorkspaceCST.ITEM_TYPE);
		shell.findField(CadseRootCST.ITEM_TYPE_at_NAME_).typeText(TypeB);
		shell.findField(WorkspaceCST.ITEM_TYPE_at_HAS_CONTENT_).check(false);
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL, TypeB).expand();
		workspaceView.capture();

		createCreationPage(TypeB, "creation-page2", "Title 2", "Desciption 2");
		createCreationPage(TypeB, "creation-page3", "Title 2", "Desciption 2");
		createCreationPage(TypeB, "creation-page4", "Title 2", "Desciption 2");
		createCreationPage(TypeB, "creation-page5", "Title 2", "Desciption 2");
		createCreationPage(TypeB, "creation-page6", "Title 2", "Desciption 2");

		LogicalWorkspace wl = CadseCore.getLogicalWorkspace();
		Item cadseDefinition = wl.getItem("Model.Workspace." + cadseName);
		Assert.assertNotNull(cadseDefinition);
		Item dataModel = CadseDefinitionManager.getDataModel(cadseDefinition);
		Assert.assertNotNull(dataModel);
		Item ItemTypeA = DataModelManager.getItemType(dataModel, TypeA);
		Assert.assertNotNull(ItemTypeA);
		Item ItemTypeB = DataModelManager.getItemType(dataModel, TypeB);
		Assert.assertNotNull(ItemTypeB);

		Item ItemTypeA_creationDialog = ItemTypeManager.getCreationDialog(ItemTypeA);
		Assert.assertNotNull(ItemTypeA_creationDialog);


		Item ItemTypeB_creationDialog = ItemTypeManager.getCreationDialog(ItemTypeB);
		Assert.assertNotNull(ItemTypeB_creationDialog);


		Collection<Item> ItemTypeA_creationDialog_pagesItems = CreationDialogManager.getPages(ItemTypeA_creationDialog);
		assertEquals(6, ItemTypeA_creationDialog_pagesItems.size());
		int i = 1;
		for (Item p : ItemTypeA_creationDialog_pagesItems) {
			assertEquals("creation-page"+i, p.getShortName());
			i++;
		}

		Collection<Item> ItemTypeB_creationDialog_pagesItems = CreationDialogManager.getPages(ItemTypeB_creationDialog);
		assertEquals(6, ItemTypeB_creationDialog_pagesItems.size());
		i = 1;
		for (Item p : ItemTypeB_creationDialog_pagesItems) {
			assertEquals("creation-page"+i, p.getShortName());
			i++;
		}

		CCadse ccadse = GenerateCadseDefinitionModel.generateCADSE(cadseDefinition);
		CItemType citemTypeA = findType(ccadse, TypeA);
		assertNotNull(citemTypeA);
		CItemType citemTypeB = findType(ccadse, TypeB);
		assertNotNull(citemTypeB);


	}

	private void createCreationPage(String TypeA, String pageName, String pageTitle, String pageDescription) {
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL,
				TypeA, "creation dialog").contextMenu(GTCadseRTConstants.CONTEXTMENU_NEW).menu(
						GTCadseRTConstants.CONTEXTMENU_PAGE).click();
		shell = new GTShell(WorkspaceCST.PAGE);
		shell.findField(CadseRootCST.ITEM_TYPE_at_NAME_).typeText(pageName);
		shell.findField(WorkspaceCST.PAGE_at_TITLE_).typeText(pageTitle);
		shell.findField(WorkspaceCST.PAGE_at_DESCRIPTION_).typeText(pageDescription);

		shell.capture();
		shell.close();
	}

	private CLinkType findLinkType(CItemType citemType, String lt_a_to_b) {
		List<CLinkType> linktypes = citemType.getOutgoingLink();
		for (CLinkType linkType : linktypes) {
			if (linkType.getName().equals(lt_a_to_b))
				return linkType;
		}
		return null;
	}

	private CItemType findType(CCadse ccadse, String typeA) {
		List<CItemType> citemtypes = ccadse.getItemType();
		for (CItemType itemType : citemtypes) {
			if (itemType.getName().equals(typeA))
				return itemType;
		}
		return null;
	}

	private Pages getPages(final SWTBotShell shell) {
		final Pages[] ret = new Pages[1];
		Runnable r = new Runnable() {
			public void run() {
				ret[0] = (Pages) shell.widget.getData(UIField.CADSE_MODEL_KEY);
			}
		};
		shell.widget.getDisplay().syncExec(r);
		return ret[0];
	}
}
