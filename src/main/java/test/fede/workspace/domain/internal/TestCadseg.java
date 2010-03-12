package test.fede.workspace.domain.internal;

import junit.framework.Assert;

import org.junit.Test;

import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.WSModelState;
import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.CadseGCST;

import static fr.imag.adele.graphictests.cadse.test.GTCadseHelperMethods.*;

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

		ItemDelta oper = copy.createItem(CadseGCST.CADSE_DEFINITION, null, null);
		oper.setName(cadseName);
		oper.setAttribute(CadseGCST.CADSE_DEFINITION_at_PACKAGENAME_, "fr.test.tset");
		copy.commit();


		Item cadseDefinition = wl.getItem("Model.Workspace." + cadseName);
		Assert.assertNotNull(cadseDefinition);
		Item dataModel = CadseDefinitionManager.getDataModel(cadseDefinition);
		Assert.assertNotNull(dataModel);


		copy = wl.createTransaction();
		oper = copy.createItem(CadseGCST.ITEM_TYPE, dataModel, CadseGCST.DATA_MODEL_lt_TYPES);
		oper.setAttribute(CadseGCST.ITEM_at_NAME_, TypeA);
		oper.setAttribute(CadseGCST.ITEM_TYPE_at_HAS_CONTENT_, false);
		copy.commit();


		copy = wl.createTransaction();
		oper = copy.createItem(CadseGCST.ITEM_TYPE, dataModel, CadseGCST.DATA_MODEL_lt_TYPES);
		oper.setAttribute(CadseGCST.ITEM_at_NAME_, TypeB);
		oper.setAttribute(CadseGCST.ITEM_TYPE_at_HAS_CONTENT_, false);
		copy.commit();

		Item ItemTypeA = DataModelManager.getItemType(dataModel, TypeA);
		Assert.assertNotNull(ItemTypeA);
		Item ItemTypeB = DataModelManager.getItemType(dataModel, TypeB);
		Assert.assertNotNull(ItemTypeB);

//		Item ItemTypeA_creationDialog = ItemTypeManager.getCreationDialog(ItemTypeA);
//		Assert.assertNotNull(ItemTypeA_creationDialog);
//
//		Item ItemTypeB_creationDialog = ItemTypeManager.getCreationDialog(ItemTypeB);
//		Assert.assertNotNull(ItemTypeB_creationDialog);
		final String lt_a_to_b = "a_to_b";

		copy = wl.createTransaction();
		oper = copy.createItem(CadseGCST.LINK_TYPE, ItemTypeA, CadseGCST.TYPE_DEFINITION_lt_ATTRIBUTES);
		oper.setAttribute(CadseGCST.ITEM_at_NAME_, lt_a_to_b);
		oper.createLink(CadseGCST.LINK_TYPE_lt_DESTINATION, ItemTypeB);
		oper.setAttribute(CadseGCST.LINK_TYPE_at_MAX_, -1);
		oper.setAttribute(CadseGCST.LINK_TYPE_at_MIN_, 0);
		copy.commit();

		Item itemLt_a_to_b = ItemTypeManager.getAttribute(ItemTypeA, lt_a_to_b);
		Assert.assertNotNull(itemLt_a_to_b);
		Assert.assertEquals(CadseGCST.LINK_TYPE, itemLt_a_to_b.getType());

		Integer min = itemLt_a_to_b.getAttribute(CadseGCST.LINK_TYPE_at_MIN_);
		Assert.assertEquals(Integer.valueOf(0), min);
		Integer max = itemLt_a_to_b.getAttribute(CadseGCST.LINK_TYPE_at_MAX_);
		Assert.assertEquals(Integer.valueOf(-1), max);

	}
}
