/*
 * Copyright 2011 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.wicket.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.StringResourceModel;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;

import com.gitblit.utils.JGitUtils;
import com.gitblit.utils.StringUtils;
import com.gitblit.wicket.LinkPanel;
import com.gitblit.wicket.WicketUtils;
import com.gitblit.wicket.models.RefModel;
import com.gitblit.wicket.models.RepositoryModel;
import com.gitblit.wicket.pages.BranchesPage;
import com.gitblit.wicket.pages.LogPage;
import com.gitblit.wicket.pages.SummaryPage;
import com.gitblit.wicket.pages.TreePage;

public class BranchesPanel extends BasePanel {

	private static final long serialVersionUID = 1L;

	public BranchesPanel(String wicketId, final RepositoryModel model, Repository r, final int maxCount) {
		super(wicketId);

		// branches
		List<RefModel> branches = new ArrayList<RefModel>();
		branches.addAll(JGitUtils.getLocalBranches(r, maxCount));
		if (model.showRemoteBranches) {
			branches.addAll(JGitUtils.getRemoteBranches(r, maxCount));
		}
		Collections.sort(branches);
		Collections.reverse(branches);
		if (maxCount > 0 && branches.size() > maxCount) {
			branches = new ArrayList<RefModel>(branches.subList(0, maxCount));
		}

		if (maxCount > 0) {
			// summary page
			// show branches page link
			add(new LinkPanel("branches", "title", new StringResourceModel("gb.branches", this, null), BranchesPage.class, WicketUtils.newRepositoryParameter(model.name)));
		} else {
			// branches page
			// show repository summary page link
			add(new LinkPanel("branches", "title", model.name, SummaryPage.class, WicketUtils.newRepositoryParameter(model.name)));
		}

		ListDataProvider<RefModel> branchesDp = new ListDataProvider<RefModel>(branches);
		DataView<RefModel> branchesView = new DataView<RefModel>("branch", branchesDp) {
			private static final long serialVersionUID = 1L;
			int counter = 0;

			public void populateItem(final Item<RefModel> item) {
				final RefModel entry = item.getModelObject();

				item.add(WicketUtils.createDateLabel("branchDate", entry.getDate(), getTimeZone()));

				item.add(new LinkPanel("branchName", "list name", StringUtils.trimString(entry.getDisplayName(), 28), LogPage.class, WicketUtils.newObjectParameter(model.name, entry.getName())));

				// only show branch type on the branches page
				boolean remote = entry.getName().startsWith(Constants.R_REMOTES);
				item.add(new Label("branchType", remote ? getString("gb.remote") : getString("gb.local")).setVisible(maxCount <= 0));

				item.add(new BookmarkablePageLink<Void>("log", LogPage.class, WicketUtils.newObjectParameter(model.name, entry.getName())));
				item.add(new BookmarkablePageLink<Void>("tree", TreePage.class, WicketUtils.newObjectParameter(model.name, entry.getName())));

				WicketUtils.setAlternatingBackground(item, counter);
				counter++;
			}
		};
		add(branchesView);
		if (branches.size() < maxCount || maxCount <= 0) {
			add(new Label("allBranches", "").setVisible(false));
		} else {
			add(new LinkPanel("allBranches", "link", new StringResourceModel("gb.allBranches", this, null), BranchesPage.class, WicketUtils.newRepositoryParameter(model.name)));
		}
	}
}
