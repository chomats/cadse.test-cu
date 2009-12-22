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
package test.fede.workspace.domain.internal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.transaction.delta.revision.ITeamRevisionService;
import fr.imag.adele.cadse.core.transaction.delta.revision.WorkspaceLogiqueRevisionDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;


public class TestDeltaSvn {
	ITeamRevisionService			revisionService;
	WorkspaceLogiqueRevisionDelta	logiqueRevisionDelta;
	private LogicalWorkspace		workspaceLogique;

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void trearDown() throws Exception {
		workspaceLogique = CadseCore.getLogicalWorkspace();
		workspaceLogique.clear();
	}

	/** */
	@Before
	public void setUp() throws Exception {
		workspaceLogique = CadseCore.getLogicalWorkspace();
		workspaceLogique.loadCadseModel("Model.Workspace.TestSvn");
		logiqueRevisionDelta = new WorkspaceLogiqueRevisionDelta(workspaceLogique);

	}

	@Test
	public void testAddItem() throws Exception {
		// / load version_workspace
		/*
		 * pour chaque test trois version working, base, remote du point de vu
		 * svn url, version v1, version working (fichier modifier � ajouter),
		 * remote version v1, version v2
		 *
		 * test local (working, base=v1, remote=v2) svn ( v1=base, v2=remote,
		 * working)
		 *
		 * test � faire
		 *
		 * local remote
		 *
		 *
		 * add (item a) nothing l | r nothing add (item a) l | r
		 *
		 *
		 * remove(item a ) nothing) l | r nothing remove(item a) l | r
		 *
		 *
		 * add(link a - b) nothing) l | r nothing add(link a -> b)
		 *
		 *
		 * remove(link a->b) nothing() nothing remove(link a->b)
		 *
		 * change attribute() change attribute
		 *
		 * change attribute(a.a) change attribute (a.a);
		 *
		 *
		 */
		// workspaceLogique.loadItems(itemdescription, projectAssociationSet,
		// update, forceToSave)
		// logiqueRevisionDelta.
	}
}
