/*
 * Copyright (C) 2016 favdb
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package storybook.ui.chart.timeline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Scene;

/**
 *
 * @author favdb
 */
public class DureeScene {

	public long id;
	public Date debut;
	public Date fin;
	public long duree;

	public DureeScene(long id, Date debut) {
		this.id = id;
		this.debut = debut;
	}

	public static List<DureeScene> initScenes(Session session) {
		SceneDAOImpl sceneDAO = new SceneDAOImpl(session);
		List<Scene> scenes = sceneDAO.findScenesToExport();
		List<DureeScene> md = new ArrayList<>();
		for (Scene scene : scenes) {
			if (scene.hasSceneTs()) md.add(new DureeScene(scene.getId(), scene.getSceneTs()));
		}
		if (md.isEmpty()) return(md);
		if (md.size()>0) for (int i = 1; i < md.size() - 1; i++) {
			md.get(i - 1).fin = md.get(i).debut;
			long end = md.get(i - 1).fin.getTime();
			long beg = md.get(i - 1).debut.getTime();
			md.get(i - 1).duree = (end - beg) / (60 * 1000);
			if (md.get(i - 1).duree == 0L) {
				md.get(i - 1).duree = 1;
			}
		}
		md.get(md.size()-1).fin=md.get(md.size()-1).debut;
		return (md);
	}

	public static List<DureeScene> calculDureeScene(Session session, List<DureeScene> durees) {
		SceneDAOImpl sceneDAO = new SceneDAOImpl(session);
		List<Scene> scenes = sceneDAO.findScenesToExport();
		List<DureeScene> md = new ArrayList<>();
		for (Scene scene : scenes) {
			if (scene.hasSceneTs()) {
				md.add(new DureeScene(scene.getId(), scene.getSceneTs()));
			}
		}
		for (int i = 1; i < md.size(); i++) {
			for (Scene scene : scenes) {
				if (scene.getId() == md.get(i).id) {
					if (md.get(i).debut != null) {
						md.get(i - 1).fin = md.get(i).debut;
						long end = md.get(i - 1).fin.getTime();
						long beg = md.get(i - 1).debut.getTime();
						md.get(i - 1).duree = (end - beg) / (60 * 1000);
						if (md.get(i - 1).duree == 0L) {
							md.get(i - 1).duree = 1;
						}
					}
					break;
				}
			}
			if (md.get(i - 1).duree == 0L) {
				md.get(i - 1).duree = 1L;
			}
		}
		for (DureeScene duree : durees) {
			for (DureeScene d : md) {
				if (duree.id == d.id) {
					duree.fin = d.fin;
					duree.duree = d.duree;
					break;
				}
			}
			if (duree.fin == null) {
				duree.fin = duree.debut;
				duree.duree = 1L;
			}
		}
		return (durees);
	}

	public String trace() {
		return ("[" + debut + "] [" + fin + "] [" + duree + "]");
	}

}
