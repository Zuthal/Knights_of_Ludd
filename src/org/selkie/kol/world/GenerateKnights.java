package org.selkie.kol.world;

import java.util.ArrayList;
import java.util.Arrays;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import exerelin.campaign.SectorManager;
import org.apache.log4j.Logger;
import org.magiclib.util.MagicCampaign;
import org.selkie.kol.fleets.KnightsExpeditionsManager;
import org.selkie.kol.helpers.MarketHelpers;
import org.selkie.kol.plugins.KOL_ModPlugin;

public class GenerateKnights {

	//public static Logger log = Global.getLogger(GenerateKnights.class);
	
	public static void genCorvus() {
		Global.getSector().getStarSystem("Eos Exodus").setBackgroundTextureFilename("graphics/backgrounds/kol_bg_1.jpg");
		Global.getSector().getStarSystem("Kumari Kandam").setBackgroundTextureFilename("graphics/backgrounds/kol_bg_2.jpg");
		Global.getSector().getStarSystem("Canaan").setBackgroundTextureFilename("graphics/backgrounds/kol_bg_3.jpg");
		Global.getSector().getStarSystem("Al Gebbar").setBackgroundTextureFilename("graphics/backgrounds/kol_bg_4.jpg");
		genKnightsBattlestation();
		genKnightsStarfortress();
	}

	public static void genAlways() {
		copyChurchEquipment();
		startupRelations();
		genKnightsExpeditions();
	}

	public static void startupRelations() {
		if (Global.getSector().getFaction(Factions.LUDDIC_CHURCH) != null && Global.getSector().getFaction(KOL_ModPlugin.kolID) != null) {
			FactionAPI church = Global.getSector().getFaction(Factions.LUDDIC_CHURCH);
			FactionAPI knights = Global.getSector().getFaction(KOL_ModPlugin.kolID);

			if(church.getRelToPlayer().isAtWorst(RepLevel.SUSPICIOUS)) {
				church.getRelToPlayer().setRel(Math.max(church.getRelToPlayer().getRel(), knights.getRelToPlayer().getRel()));
				knights.getRelToPlayer().setRel(Math.max(church.getRelToPlayer().getRel(), knights.getRelToPlayer().getRel()));
			}

			for(FactionAPI faction:Global.getSector().getAllFactions()) {
				knights.setRelationship(faction.getId(), church.getRelationship(faction.getId()));
			}
		}
	}

	public static void genKnightsBattlestation() {
		String entID = "kol_cygnus";
		StarSystemAPI Canaan = Global.getSector().getStarSystem("Canaan");
        SectorEntityToken cygnus = Canaan.addCustomEntity(entID, "Battlestation Cygnus", "station_lowtech2", "knights_of_selkie");
        cygnus.setCircularOrbitPointingDown(Canaan.getEntityById("canaan_gate"), 33, 275, 99);
        cygnus.setCustomDescriptionId("kol_cygnus_desc");
		//cygnus.getMemoryWithoutUpdate().set(MusicPlayerPluginImpl.KEEP_PLAYING_LOCATION_MUSIC_DURING_ENCOUNTER_MEM_KEY, true);
        
        MarketHelpers.addMarketplace("knights_of_selkie", cygnus, null, "Battlestation Cygnus", 4,
        		new ArrayList<String>(Arrays.asList(Conditions.OUTPOST,
                        Conditions.POPULATION_4)),
        		new ArrayList<String>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
						"kol_garden",
						Industries.PATROLHQ,
                        Industries.LIGHTINDUSTRY,
                        Industries.GROUNDDEFENSES,
                        Industries.BATTLESTATION)),
        		new ArrayList<String>(Arrays.asList(
        				Submarkets.SUBMARKET_STORAGE,
                        Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
        		0.3f
        );

		cygnus.getMarket().removeSubmarket(Submarkets.SUBMARKET_BLACK);
		if (KOL_ModPlugin.haveNex) SectorManager.NO_BLACK_MARKET.add(cygnus.getMarket().getId());

		cygnus.setInteractionImage("illustrations", "kol_garden_large");

		PersonAPI master = MagicCampaign.addCustomPerson(cygnus.getMarket(), "Master", "Blaster", "kol_master",
				FullName.Gender.FEMALE, KOL_ModPlugin.kolID, Ranks.ELDER, Ranks.POST_ARCHCURATE,
				false, 0, 0);

		PersonAPI lackie1 = MagicCampaign.addCustomPerson(cygnus.getMarket(), "Brother", "Enarms", "kol_lackie_1",
				FullName.Gender.MALE, KOL_ModPlugin.kolID, Ranks.KNIGHT_CAPTAIN, Ranks.POST_GUARD_LEADER,
				false, 0, 1);

		master.setImportance(PersonImportance.HIGH);
		master.setVoice(Voices.SOLDIER);

		lackie1.setImportance(PersonImportance.MEDIUM);
		lackie1.setVoice(Voices.FAITHFUL);
	}
	
	public static void genKnightsStarfortress() {
		String entID = "kol_lyra";
		StarSystemAPI Eos = Global.getSector().getStarSystem("Eos Exodus");
        SectorEntityToken lyra = Eos.addCustomEntity(entID, "Star Keep Lyra", "station_lowtech3", "knights_of_selkie");
        lyra.setCircularOrbitPointingDown(Eos.getEntityById("eos_exodus_gate"), 33, 275, 99);
        lyra.setCustomDescriptionId("kol_lyra_desc");
		//yra.getMemoryWithoutUpdate().set(MusicPlayerPluginImpl.KEEP_PLAYING_LOCATION_MUSIC_DURING_ENCOUNTER_MEM_KEY, true);

		MarketHelpers.addMarketplace("knights_of_selkie", lyra, null, "Star Keep Lyra", 5,
        		new ArrayList<String>(Arrays.asList(Conditions.OUTPOST,
                        Conditions.POPULATION_5)),
        		new ArrayList<String>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
						"kol_garden",
                        Industries.MILITARYBASE,
                        Industries.ORBITALWORKS,
                        Industries.FUELPROD,
                        Industries.HEAVYBATTERIES,
                        Industries.STARFORTRESS,
                        Industries.WAYSTATION)),
        		new ArrayList<String>(Arrays.asList(
        				Submarkets.SUBMARKET_STORAGE,
                        Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
        		0.3f
        );

		lyra.setInteractionImage("illustrations", "kol_garden_large");

		lyra.getMarket().removeSubmarket(Submarkets.SUBMARKET_BLACK);
		if (KOL_ModPlugin.haveNex) SectorManager.NO_BLACK_MARKET.add(lyra.getMarket().getId());

		PersonAPI grandmaster = MagicCampaign.addCustomPerson(lyra.getMarket(), "Grandmaster", "Flash", "kol_grandmaster",
				FullName.Gender.MALE, KOL_ModPlugin.kolID, Ranks.FACTION_LEADER, Ranks.POST_FACTION_LEADER,
				false, 0, 0);

		PersonAPI lackie2 = MagicCampaign.addCustomPerson(lyra.getMarket(), "Sister", "Syster", "kol_lackie_2",
				FullName.Gender.FEMALE, KOL_ModPlugin.kolID, Ranks.SISTER, Ranks.POST_GUARD_LEADER,
				false, 0, 1);

		grandmaster.setImportance(PersonImportance.VERY_HIGH);
		grandmaster.setVoice(Voices.FAITHFUL);

		lackie2.setImportance(PersonImportance.HIGH);
		lackie2.setVoice(Voices.FAITHFUL);
	}

	public static void genKnightsExpeditions() {
		org.selkie.kol.fleets.KnightsExpeditionsManager expeditions = new KnightsExpeditionsManager();
		Global.getSector().getStarSystem("Eos Exodus").addScript(expeditions);
	}

	public static void copyChurchEquipment() {
		// The knights don't want the misc modiverse ships
		// Unless they have no other choice
		FactionAPI KOL = Global.getSector().getFaction(KOL_ModPlugin.kolID);
	    for (String ship : Global.getSector().getFaction(Factions.LUDDIC_CHURCH).getKnownShips()) {
            if (!KOL.knowsShip(ship)
					&& !KOL.getAlwaysKnownShips().contains(ship)) {
                Global.getSector().getFaction(KOL_ModPlugin.kolID).addUseWhenImportingShip(ship);
            }
        }
        //for (String baseShip : Global.getSector().getFaction(Factions.LUDDIC_CHURCH).getAlwaysKnownShips()) {
        //    if (!Global.getSector().getFaction(KOL_ModPlugin.kolID).useWhenImportingShip(baseShip)) {
        //        Global.getSector().getFaction(KOL_ModPlugin.kolID).addUseWhenImportingShip(baseShip);
        //    }
        //}
		for (String entry : Global.getSector().getFaction(Factions.LUDDIC_CHURCH).getKnownWeapons()) {
			if (!KOL.knowsWeapon(entry)) {
				KOL.addKnownWeapon(entry, false);
			}
		}
		for (String entry : Global.getSector().getFaction(Factions.LUDDIC_CHURCH).getKnownFighters()) {
			if (!KOL.knowsFighter(entry)) {
				KOL.addKnownFighter(entry, false);
			}
		}
		for (String entry : Global.getSector().getFaction(Factions.LUDDIC_CHURCH).getKnownHullMods()) {
			if (!KOL.knowsHullMod(entry)) {
				KOL.addKnownHullMod(entry);
			}
		}
	}

	public static class KnightsFleetTypes {

		public static final String SCOUT = "kolScout";
		public static final String HEADHUNTER = "kolHeadHunter";
		public static final String WARRIORS = "kolHolyWarriors";
		public static final String PATROL = "kolPatrol";
		public static final String ARMADA = "kolArmada";
	}
}

