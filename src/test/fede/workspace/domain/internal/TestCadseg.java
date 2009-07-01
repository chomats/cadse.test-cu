package test.fede.workspace.domain.internal;

import junit.framework.Assert;
import fr.imag.adele.cadse.cadseg.WorkspaceCST;
import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.DataModelManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.ItemTypeManager;

import org.junit.Test;

import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.WSModelState;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.CadseRootCST;

public class TestCadseg {
	static GeneratorName	generator	= new GeneratorName();

	@Test
	public void test_check() throws Exception {
		// Item Type A
		String TypeA = "A";

		// Item Type B
		String TypeB = "B";

		LogicalWorkspace wl = CadseCore.getLogicalWorkspace();
		while (wl.getState() != WSModelState.RUN) {
			Thread.sleep(10);
		}
		String cadseName = "Cadse_" + generator.newName();

		LogicalWorkspaceTransaction copy = wl.createTransaction();

		ItemDelta oper = copy.createItem(WorkspaceCST.CADSE_DEFINITION, null, null);
		oper.setShortName(cadseName);
		oper.setAttribute(WorkspaceCST.CADSE_DEFINITION_at_PACKAGENAME_, "fr.test.tset");
		copy.commit();


		Item cadseDefinition = wl.getItem("Model.Workspace." + cadseName);
		Assert.assertNotNull(cadseDefinition);
		Item dataModel = CadseDefinitionManager.getDataModel(cadseDefinition);
		Assert.assertNotNull(dataModel);


		copy = wl.createTransaction();
		oper = copy.createItem(WorkspaceCST.ITEM_TYPE, dataModel, WorkspaceCST.DATA_MODEL_lt_TYPES);
		oper.setAttribute(CadseRootCST.ITEM_TYPE_at_NAME_, TypeA);
		oper.setAttribute(WorkspaceCST.ITEM_TYPE_at_HAS_CONTENT_, false);
		copy.commit();


		copy = wl.createTransaction();
		oper = copy.createItem(WorkspaceCST.ITEM_TYPE, dataModel, WorkspaceCST.DATA_MODEL_lt_TYPES);
		oper.setAttribute(CadseRootCST.ITEM_TYPE_at_NAME_, TypeB);
		oper.setAttribute(WorkspaceCST.ITEM_TYPE_at_HAS_CONTENT_, false);
		copy.commit();

		Item ItemTypeA = DataModelManager.getItemType(dataModel, TypeA);
		Assert.assertNotNull(ItemTypeA);
		Item ItemTypeB = DataModelManager.getItemType(dataModel, TypeB);
		Assert.assertNotNull(ItemTypeB);

		Item ItemTypeA_creationDialog = ItemTypeManager.getCreationDialog(ItemTypeA);
		Assert.assertNotNull(ItemTypeA_creationDialog);

		Item ItemTypeB_creationDialog = ItemTypeManager.getCreationDialog(ItemTypeB);
		Assert.assertNotNull(ItemTypeB_creationDialog);
		final String lt_a_to_b = "a_to_b";

		copy = wl.createTransaction();
		oper = copy.createItem(WorkspaceCST.LINK, ItemTypeA, WorkspaceCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		oper.setAttribute(CadseRootCST.ITEM_TYPE_at_NAME_, lt_a_to_b);
		oper.createLink(WorkspaceCST.LINK_lt_DESTINATION, ItemTypeB);
		oper.setAttribute(WorkspaceCST.ATTRIBUTE_at_MAX_, -1);
		oper.setAttribute(WorkspaceCST.ATTRIBUTE_at_MIN_, 0);
		copy.commit();

		Item itemLt_a_to_b = ItemTypeManager.getAttribute(ItemTypeA, lt_a_to_b);
		Assert.assertNotNull(itemLt_a_to_b);
		Assert.assertEquals(WorkspaceCST.LINK, itemLt_a_to_b.getType());

		Integer min = itemLt_a_to_b.getAttribute(WorkspaceCST.ATTRIBUTE_at_MIN_);
		Assert.assertEquals(Integer.valueOf(0), min);
		Integer max = itemLt_a_to_b.getAttribute(WorkspaceCST.ATTRIBUTE_at_MAX_);
		Assert.assertEquals(Integer.valueOf(-1), max);

	}
}
