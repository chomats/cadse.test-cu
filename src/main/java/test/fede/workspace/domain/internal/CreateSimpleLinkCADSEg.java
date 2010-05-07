package test.fede.workspace.domain.internal;

import static fr.imag.adele.graphictests.cadse.test.GTCadseHelperMethods.workspaceView;

import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.Test;
import org.osgi.framework.Bundle;

import fr.imag.adele.cadse.cadseg.generate.GenerateCadseDefinitionModel;
import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.cadse.cadseg.managers.attributes.LinkTypeManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.DataModelManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.ItemTypeManager;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.si.workspace.uiplatform.swt.SWTUIPlatform;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CCadse;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CItemType;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CLinkType;
import fr.imag.adele.graphictests.cadse.gtcadseworkbench_part.GTCadseShell;
import fr.imag.adele.graphictests.cadse.test.GTCadseRTConstants;
import fr.imag.adele.graphictests.gttree.GTTreePath;
import fr.imag.adele.graphictests.gtworkbench_part.GTShell;
import fr.imag.adele.graphictests.test.GTTestCase;

public class CreateSimpleLinkCADSEg extends GTTestCase {
	GTCadseShell				shell;

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
		SWTBotPreferences.TIMEOUT = 10000;
		//set org.eclipse.swtbot.screenshots.dir
		//if (System.getProperty("test.screenshotPath") != null)
		//	SWTBotPreferences.SCREENSHOTS_DIR = System.getProperty("test.screenshotPath");
	}

	

	@Test
	public void test_selection() throws Exception {
		//workspaceView.open();
		
		/* ================================== */
		/* CADSEs selection in startup window */
		/* ================================== */

		shell =  new GTCadseShell(GTCadseRTConstants.CADSE_SELECTOR_SHELL_TITLE);
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
		workspaceView.contextMenuNew(CadseGCST.CADSE_DEFINITION).click();
		shell = new GTCadseShell(CadseGCST.CADSE_DEFINITION);
		cadseName = "Cadse_" + generator.newName();
		shell.findCadseField(CadseGCST.ITEM_at_NAME_).typeText( cadseName);

		packageName = "model.webapp";
		shell.findCadseField(CadseGCST.CADSE_DEFINITION_at_PACKAGENAME_).typeText( packageName);
		shell.capture();
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.findTree().selectNode(new GTTreePath(cadseName), true);


		packageExplorerView.show();
		packageExplorerView.selectNode("Model.Workspace." + cadseName);
		packageExplorerView.findTree().selectNode("Model.Workspace." + cadseName, "sources");
		packageExplorerView.capture();
		workspaceView.show();
	}

	@Test
	public void test_createTypeA_TypeB_Link_lt_a_to_b() throws Exception {
		// Item Type A
		String TypeA = "A";
		workspaceView.contextMenuNew(new GTTreePath(cadseName, CadseDefinitionManager.DATA_MODEL), "Item type").click();
		shell = new GTCadseShell(CadseGCST.ITEM_TYPE);
		shell.findCadseField(CadseGCST.ITEM_at_NAME_).typeText(TypeA);
		shell.findCadseField(CadseGCST.ITEM_TYPE_at_HAS_CONTENT_).check(false);
		shell.capture();
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.selectNode(cadseName, CadseDefinitionManager.DATA_MODEL, TypeA);
		workspaceView.capture();

		// Item Type B
		String TypeB = "B";
		workspaceView.contextMenuNew(new GTTreePath(cadseName, CadseDefinitionManager.DATA_MODEL), "Item type").click();
		shell = new GTCadseShell(CadseGCST.ITEM_TYPE);
		shell.findCadseField(CadseGCST.ITEM_at_NAME_).typeText(TypeB);
		shell.findCadseField(CadseGCST.ITEM_TYPE_at_HAS_CONTENT_).check(false);
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.selectNode(cadseName, CadseDefinitionManager.DATA_MODEL, TypeB);
		workspaceView.capture();

		// hasComp link
		final String lt_a_to_b = "a_to_b";
		workspaceView.contextMenuNew(new GTTreePath(cadseName, CadseDefinitionManager.DATA_MODEL, TypeA), "Link").click();
		shell = new GTCadseShell(CadseGCST.LINK_TYPE);
		shell.findCadseField(CadseGCST.ITEM_at_NAME_).typeText(lt_a_to_b);
		shell.findCadseField(CadseGCST.LINK_TYPE_lt_DESTINATION).browser(cadseName, CadseDefinitionManager.DATA_MODEL, TypeB);
		// shell.findField(WorkspaceCST.LINK_at_AGGREGATION_, true);
		// shell.findField(WorkspaceCST.LINK_at_REQUIRE_, true);
		// shell.findField(WorkspaceCST.LINK_at_PART_, true);
		SWTUIPlatform swtuiPlatform = getSwtUIPlatform(shell);
		ItemDelta linkCreating = (ItemDelta) swtuiPlatform.getItem();
		assertEquals(Integer.valueOf(0), linkCreating.getAttribute(CadseGCST.LINK_TYPE_at_MIN_, false));
		assertEquals(Integer.valueOf(-1), linkCreating.getAttribute(CadseGCST.LINK_TYPE_at_MAX_, false));

		shell.capture();
		shell.close();
		workspaceView.show();
		workspaceView.capture();
		workspaceView.findTree().selectNode(cadseName, CadseDefinitionManager.DATA_MODEL, TypeA,
				lt_a_to_b);

		LogicalWorkspace wl = CadseCore.getLogicalWorkspace();
		Item cadseDefinition = wl.getItem("Model.Workspace." + cadseName);
		Assert.assertNotNull(cadseDefinition);
		Item dataModel = CadseDefinitionManager.getDataModel(cadseDefinition);
		Assert.assertNotNull(dataModel);
		Item ItemTypeA = DataModelManager.getItemType(dataModel, TypeA);
		Assert.assertNotNull(ItemTypeA);
		Item ItemTypeB = DataModelManager.getItemType(dataModel, TypeB);
		Assert.assertNotNull(ItemTypeB);
		Item itemLt_a_to_b = ItemTypeManager.getAttribute(ItemTypeA, lt_a_to_b);
		Assert.assertNotNull(itemLt_a_to_b);
		Assert.assertEquals(CadseGCST.LINK_TYPE, itemLt_a_to_b.getType());

		Integer min = itemLt_a_to_b.getAttribute(CadseGCST.LINK_TYPE_at_MIN_);
		assertEquals(Integer.valueOf(0), min);
		Integer max = itemLt_a_to_b.getAttribute(CadseGCST.LINK_TYPE_at_MAX_);
		assertEquals(Integer.valueOf(-1), max);

		int min_ = LinkTypeManager.getMin(itemLt_a_to_b);
		assertEquals(min_, 0);
		min_ = LinkTypeManager.getMinAttribute(itemLt_a_to_b);
		assertEquals(min_, 0);

		int max_ = LinkTypeManager.getMax(itemLt_a_to_b);
		assertEquals(max_, -1);
		max_ = LinkTypeManager.getMaxAttribute(itemLt_a_to_b);
		assertEquals(max_, -1);

		CCadse ccadse = GenerateCadseDefinitionModel.generateCADSE(cadseDefinition);
		CItemType citemTypeA = findType(ccadse, TypeA);
		assertNotNull(citemTypeA);
		CLinkType clt_a_to_b = findLinkType(citemTypeA, lt_a_to_b);
		assertNotNull(clt_a_to_b);
		assertEquals(clt_a_to_b.getMax(), -1);
		assertEquals(clt_a_to_b.getMin(), 0);
		assertEquals(clt_a_to_b.getDestination(), ItemTypeManager.getIdRuntime(ItemTypeB).toString());
		CItemType citemTypeB = findType(ccadse, TypeB);
		assertNotNull(citemTypeB);


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

	public static SWTUIPlatform getSwtUIPlatform(final GTShell shell) {
		final SWTUIPlatform[] ret = new SWTUIPlatform[1];
		Runnable r = new Runnable() {
			public void run() {
				ret[0] =  (SWTUIPlatform) shell.getRootWidget().getData(UIField.CADSE_MODEL_KEY);
			}
		};
		shell.bot().getDisplay().syncExec(r);
		return ret[0];
	}
}
