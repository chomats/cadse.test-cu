/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 *
 */
package test.fede.workspace.domain.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import test.fede.workspace.domain.internal.TestWorkingLogiqueCopy.MyListner2;

import junit.framework.Assert;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ContentChangeInfo;
import fr.imag.adele.cadse.core.DefaultItemManager;
import fr.imag.adele.cadse.core.ILinkTypeManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.WSModelState;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.delta.LinkDelta;
import fr.imag.adele.cadse.core.delta.MappingOperation;
import fr.imag.adele.cadse.core.delta.OrderOperation;
import fr.imag.adele.cadse.core.delta.SetAttributeOperation;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.internal.CadseDomainImpl;
import fr.imag.adele.cadse.core.internal.Nullable;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransactionListener;
import fede.workspace.role.initmodel.ErrorWhenLoadedModel;
import fr.imag.adele.fede.workspace.as.initmodel.IInitModel;
import fr.imag.adele.fede.workspace.si.initmodel.InitModel;
import fr.imag.adele.melusine.as.findmodel.CheckModel;
import fr.imag.adele.melusine.as.findmodel.IFindModel;
import fr.imag.adele.melusine.as.findmodel.ModelEntry;

/**
 * @author chomats
 *
 */
public class TestSenario {
	LogicalWorkspace								wl;
	ArrayList<Object>								objectadded						= new ArrayList<Object>();
	ArrayList<Call>									calls							= new ArrayList<Call>();
	ArrayList<ICallAction>							callActions						= new ArrayList<ICallAction>();
	GeneratorName									generator						= new GeneratorName();
	GeneratorName									generator2						= new GeneratorName();
	private CadseRuntime							_cadseRuntimeTest;
	private InitModel								initModel;
	private TestFindModel							findModel;
	private LogicalWorkspaceTransactionListener	_workspaceLogiqueCopyListener	= new TestWLWCListener();
	private ArrayList<WorkspaceListener> listners = new ArrayList<WorkspaceListener>();

	public class TestWLWCListener implements LogicalWorkspaceTransactionListener {

		public void notifyChangedContent(LogicalWorkspaceTransaction workspaceLogiqueWorkingCopy, ItemDelta item,
				ContentChangeInfo[] change) {
			Call call = new Call(workspaceLogiqueWorkingCopy, CallType.notifieChangedContent, item, change);
			try {
				registerCall(call);
			} catch (CadseException e) {
				registerException(call, e);
			}
		}

		public void notifyAddMappingOperation(LogicalWorkspaceTransaction workspaceLogiqueWorkingCopy,
				ItemDelta item, MappingOperation mappingOperation) {
			Call call = new Call(workspaceLogiqueWorkingCopy, CallType.notifyAddMappingOperation, item,
					mappingOperation);
			try {
				registerCall(call);
			} catch (CadseException e) {
				registerException(call, e);
			}
		}

		public void notifyCancelCreatedItem(LogicalWorkspaceTransaction wc, ItemDelta item) throws CadseException {
			Call call = new Call(wc, CallType.notifyCancelCreatedItem, item);
			registerCall(call);
		}

		public void notifyCancelCreatedLink(LogicalWorkspaceTransaction wc, LinkDelta link) throws CadseException {
			Call call = new Call(wc, CallType.notifyCancelCreatedLink, link);
			registerCall(call);
		}

		public void notifyChangeAttribute(LogicalWorkspaceTransaction wc, ItemDelta item,
				SetAttributeOperation attOperation) throws CadseException {
			Call call = new Call(wc, CallType.notifyChangeAttribute_Item, item, attOperation);
			registerCall(call);
		}

		public void notifyChangeAttribute(LogicalWorkspaceTransaction wc, LinkDelta link,
				SetAttributeOperation attOperation) throws CadseException {
			Call call = new Call(wc, CallType.notifyChangeAttribute_Link, link, attOperation);
			registerCall(call);
		}

		public void notifyChangeLinkOrder(LogicalWorkspaceTransaction wc, LinkDelta link,
				OrderOperation orderOperation) throws CadseException {
			Call call = new Call(wc, CallType.notifyChangeLinkOrder, link, orderOperation);
			registerCall(call);
		}

		public void notifyCreatedItem(LogicalWorkspaceTransaction wc, ItemDelta item) throws CadseException {
			Call call = new Call(wc, CallType.notifyCreatedItem, item);
			registerCall(call);
		}

		public void notifyCreatedLink(LogicalWorkspaceTransaction wc, LinkDelta link) throws CadseException {
			Call call = new Call(wc, CallType.notifyCreatedLink, link);
			registerCall(call);
		}

		public void notifyDeletedItem(LogicalWorkspaceTransaction wc, ItemDelta item) throws CadseException {
			Call call = new Call(wc, CallType.notifyDeletedItem, item);
			registerCall(call);
		}

		public void notifyDeletedLink(LogicalWorkspaceTransaction wc, LinkDelta link) throws CadseException {
			Call call = new Call(wc, CallType.notifyDeletedLink, link);
			registerCall(call);
		}

		public void notifyDoubleClick(LogicalWorkspaceTransaction wc, ItemDelta item) {
			Call call = new Call(wc, CallType.notifyDoubleClick, item);
			try {
				registerCall(call);
			} catch (CadseException e) {
				registerException(call, e);
			}
		}

		public void notifyLoadedItem(LogicalWorkspaceTransaction workspaceLogiqueWorkingCopy,
				List<ItemDelta> loadedItems) {
			Call call = new Call(workspaceLogiqueWorkingCopy, CallType.notifyLoadedItem, loadedItems);
			try {
				registerCall(call);
			} catch (CadseException e) {
				registerException(call, e);
			}
		}

		public void validateCancelCreatedItem(LogicalWorkspaceTransaction wc, ItemDelta item)
				throws CadseException {
			Call call = new Call(wc, CallType.validateCancelCreatedItem, item);
			registerCall(call);
		}

		public void validateCancelCreatedLink(LogicalWorkspaceTransaction wc, LinkDelta link)
				throws CadseException {
			Call call = new Call(wc, CallType.validateCancelCreatedLink, link);
			registerCall(call);

		}

		public void validateChangeAttribute(LogicalWorkspaceTransaction wc, ItemDelta item,
				SetAttributeOperation attOperation) throws CadseException {
			Call call = new Call(wc, CallType.validateChangeAttribute_Item, item, attOperation);
			registerCall(call);
		}

		public void validateChangeAttribute(LogicalWorkspaceTransaction wc, LinkDelta link,
				SetAttributeOperation attOperation) throws CadseException {
			Call call = new Call(wc, CallType.validateChangeAttribute_Link, link, attOperation);
			registerCall(call);
		}

		public void validateChangeLinkOrder(LogicalWorkspaceTransaction wc, LinkDelta link,
				OrderOperation orderOperation) throws CadseException {
			Call call = new Call(wc, CallType.validateChangeLinkOrder, link, orderOperation);
			registerCall(call);
		}

		public void validateCreatedItem(LogicalWorkspaceTransaction wc, ItemDelta item) throws CadseException {
			Call call = new Call(wc, CallType.validateCreatedItem, item);
			registerCall(call);
		}

		public void validateCreatedLink(LogicalWorkspaceTransaction wc, LinkDelta link) throws CadseException {
			Call call = new Call(wc, CallType.validateCreatedLink, link);
			registerCall(call);
		}

		public void validateDeletedItem(LogicalWorkspaceTransaction wc, ItemDelta item) throws CadseException {
			Call call = new Call(wc, CallType.validateDeletedItem, item);
			registerCall(call);
		}

		public void validateDeletedLink(LogicalWorkspaceTransaction wc, LinkDelta link) throws CadseException {
			Call call = new Call(wc, CallType.validateDeletedLink, link);
			registerCall(call);
		}

		ArrayList<ICallAction>	callActions	= new ArrayList<ICallAction>();

		public void addCallAction(ICallAction action) {
			callActions.add(action);
		}

		private void registerCall(Call call) throws CadseException {
			registerMainCall(call);
			if (callActions != null) {
				for (ICallAction a : this.callActions) {
					a.action(call);
				}
			}
		}

		public void notifyAbortTransaction(LogicalWorkspaceTransaction wc) throws CadseException {
			// TODO Auto-generated method stub

		}

		public void notifyBeginTransaction(LogicalWorkspaceTransaction wc) throws CadseException {
			// TODO Auto-generated method stub

		}



		public void notifyCommitTransaction(LogicalWorkspaceTransaction wc) throws CadseException {
			// TODO Auto-generated method stub

		}

		public void notifyMigratePartLink(LogicalWorkspaceTransaction wc, ItemDelta childItem, ItemDelta newPartParent,
				LinkType lt, LinkDelta newPartLink, LinkDelta oldPartLink) throws CadseException {
			// TODO Auto-generated method stub

		}

	}

	static private class TestFileModelEntry implements ModelEntry {
		String	dmn;
		String	name;
		File	rootFile;

		public TestFileModelEntry(String domainName, String qualifiedModelName, File f) {
			dmn = domainName;
			qualifiedModelName = name;
			rootFile = f;
		}

		public String getDomainName() {
			return dmn;
		}

		public URL getEntry(String path) throws IOException {
			return new File(rootFile, path).toURL();
		}

		public File getFile() throws UnsupportedOperationException {
			return rootFile;
		}

		public String getName() {
			return name;
		}

		public ModelEntry getSubEntry(String path) {
			return null;
		}

		public ModelEntry[] list() {
			return null;
		}

	}

	private final class TestFindModel implements IFindModel {
		HashMap<String, File>	modelsLocation	= new HashMap<String, File>();

		public ModelEntry[] findModelEntries(String domainName) {
			return null;
		}

		public ModelEntry findModelEntry(String domainName) {
			return null;
		}

		public ModelEntry findQualifiedModel(String domainName, String qualifiedModelName) {
			File f = modelsLocation.get(qualifiedModelName);
			if (f != null) {
				return new TestFileModelEntry(domainName, qualifiedModelName, f);
			}
			return null;
		}

		@Override
		public ModelEntry[] findModelEntries(String domainName, CheckModel check) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class LinkManagerTest implements ILinkTypeManager {

		public String canCreateLink(Item source, Item dest, LinkType lt) {
			return null;
		}

		public String canDeleteLink(Link link) {
			return null;
		}

		public int compare(Link l1, Link l2) {
			return 0;
		}

		public LinkType getLinkType() {
			return null;
		}

		public Collection<Item> getSelectingDestination(Item source) {
			return null;
		}

		public boolean isOutgoingLinkSorted() {
			return false;
		}

		public void setLinkType(LinkType lt) {
		}

		@Override
		public String toString() {
			return printCalls();
		}

	}

	public class ItemTypeManagerTest extends DefaultItemManager {
		ArrayList<ICallAction>	callActions	= new ArrayList<ICallAction>();

		public ItemTypeManagerTest() {
			super();
		}

		

		private void registerCall(Call call) throws CadseException {
			registerMainCall(call);
			if (callActions != null) {
				for (ICallAction a : this.callActions) {
					a.action(call);
				}
			}
		}

		@Override
		public String getDisplayName(Item item) {
			return item.getName() + " of type " + item.getName();
		}
		
		@Override
		public String computeQualifiedName(Item item, String shortid, Item parent,
				LinkType lt) {
			return shortid;
		}
	}

	protected String printCalls() {
		StringBuilder sb = new StringBuilder();
		for (Call c : calls) {
			sb.append(" - ").append(c).append("\n");
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	public void registerException(Call call, CadseException e) {
		e.printStackTrace();

	}

	private void registerMainCall(Call call) throws CadseException {
		calls.add(call);
		if (callActions != null) {
			for (ICallAction a : this.callActions) {
				a.action(call);
			}
		}
	}

	public void init() throws Exception {
		try {
			Bundle b = Platform.getBundle("fr.imag.adele.cadse.si.workspace.view");
			b.start();
			b = Platform.getBundle("fr.imag.adele.ipojo.autostart");
			b.start();
			b = Platform.getBundle("org.apache.felix.ipojo");
			b.start();
			
			while (true) {
				wl = CadseCore.getLogicalWorkspace();
				if (wl != null && wl.getState() == WSModelState.RUN)
					return;
				Thread.sleep(100);
			}
		} catch (NullPointerException e) {
//			CadseDomainImpl dm = new CadseDomainImpl();
//			dm.start();
//			wl = dm.getLogicalWorkspace();
//			findModel = new TestFindModel();
//			initModel = new InitModel(findModel, dm);
//			initModel.loadCadses();
		}
	}

	public LogicalWorkspace getLogicalWorkspace() {
		return wl;
	}

	public ArrayList<ICallAction> getCallActions() {
		return callActions;
	}

	public ArrayList<Call> getCalls() {
		return calls;
	}

	public ItemType createItemType(@Nullable
	ItemType superType, boolean hasContent, boolean isAbstract) {

		String newName = generator.newName();
		String name = "TYPE_" + newName;
		final ItemType createItemType = wl.createItemType(null, getCadseRuntimeTest(), superType, 1, CompactUUID
				.randomUUID(), newName, name, hasContent, isAbstract, new ItemTypeManagerTest());
		add(createItemType);
		return createItemType;
	}

	private void add(ItemType atecreateItemTypeItemType) {
		objectadded.add(atecreateItemTypeItemType);
	}

	private CadseRuntime getCadseRuntimeTest() {
		if (_cadseRuntimeTest == null) {
			_cadseRuntimeTest = wl.createCadseRuntime(CadseRuntime.CADSE_NAME_SUFFIX+"test", CompactUUID.randomUUID(), CompactUUID.randomUUID());
		}
		return _cadseRuntimeTest;
	}

	public LinkType createLinkType(ItemType source, int kind, int min, int max, String selection, ItemType destination) throws CadseException {
		String id = source.getName() + "_to_" + destination.getName() + "_" + generator.newName();
		LinkType ret = source.createLinkType(CompactUUID.randomUUID(), 1, id, kind, min, max, selection, destination);
		ret.setManager(new TestSenario.LinkManagerTest());
		add(ret);
		return ret;
	}

	private void add(LinkType ret) {
		objectadded.add(ret);
	}

	public LinkType createLinkType(ItemType source, int kind, int min, int max, String selection, LinkType inverse) throws CadseException {
		String id = source.getName() + "_to_" + inverse.getSource().getName();
		LinkType ret = source.createLinkType(CompactUUID.randomUUID(), 1, id, kind, min, max, selection, inverse);
		ret.setManager(new TestSenario.LinkManagerTest());
		add(ret);
		return ret;
	}

	public Item getItem(CompactUUID id) {
		return wl.getItem(id);
	}

	public void assertCall(Call... calls) {
		for (Call call : calls) {
			if (!this.calls.contains(call)) {
				Assert.fail(call + " not called");
			}
		}
		this.calls.clear();
	}

	public void clearCalls() {
		calls.clear();
	}

	public String newName() {
		return generator2.newName();
	}

	public void stop() throws CadseException {
		for (WorkspaceListener l : listners) {
			getLogicalWorkspace().removeListener(l);
		}
		
		LogicalWorkspaceTransaction copy = this.wl.createTransaction();
		for (Object o : this.objectadded) {
			if (o instanceof Item) {
				final ItemDelta itemInCopy = copy.getItem(((Item) o).getId(), true);
				if (itemInCopy.isDeleted()) {
					continue;
				}
				itemInCopy.delete(true);
			}
			if (o instanceof Link) {
				// nothing
			}
		}
		copy.commit();
		
	}

	public static final String	Prefix	= "Model.Workspace.";

	public void excuteCadse(IInitModel model, CadseRuntime name) throws ErrorWhenLoadedModel {
		model.executeCadses(name);
	}

	public void remove(ItemType it) {
		this.objectadded.remove(it);
	}

	public LogicalWorkspaceTransactionListener getLogicalWorkspaceTransactionListener() {
		return _workspaceLogiqueCopyListener;
	}

	public void addListener(WorkspaceListener l, int eventFilter) {
		listners.add(l);
		getLogicalWorkspace().addListener(l, eventFilter);
	}

	
}
