package org.selkie.kol.impl.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BaseGenericPlugin;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SDMParams;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SalvageDefenderModificationPlugin;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicCampaign;

public class TTBoss2DefenderPlugin extends BaseGenericPlugin implements SalvageDefenderModificationPlugin {

    public float getStrength(SDMParams p, float strength, Random random, boolean withOverride) {
        // doesn't matter, just something non-zero so we end up with a fleet
        // the auto-generated fleet will get replaced by this anyway
        return strength;
    }
    public float getMinSize(SDMParams p, float minSize, Random random, boolean withOverride) {
        return minSize;
    }

    public float getMaxSize(SDMParams p, float maxSize, Random random, boolean withOverride) {
        return maxSize;
    }

    public float getProbability(SDMParams p, float probability, Random random, boolean withOverride) {
        return probability;
    }

    public void reportDefeated(SDMParams p, SectorEntityToken entity, CampaignFleetAPI fleet) {

    }

    public void modifyFleet(SDMParams p, CampaignFleetAPI fleet, Random random, boolean withOverride) {

        //Misc.addDefeatTrigger(fleet, "PK14thDefeated");

        fleet.setNoFactionInName(true);
        fleet.setName("Unidentified Vessel");


        fleet.getFleetData().clear();
        fleet.getFleetData().setShipNameRandom(random);


        FleetMemberAPI member = fleet.getFleetData().addFleetMember("zea_boss_ninmah_Undoer");
        member.setShipName("TTS Ninmah");
        member.setId("tt2boss_" + random.nextLong());

        Map<String, Integer> skills = new HashMap<>();
        skills.put(Skills.IMPACT_MITIGATION, 2);
        skills.put(Skills.DAMAGE_CONTROL, 2);
        skills.put(Skills.FIELD_MODULATION, 2);
        skills.put(Skills.TARGET_ANALYSIS, 2);
        skills.put(Skills.SYSTEMS_EXPERTISE, 2);
        skills.put(Skills.ENERGY_WEAPON_MASTERY, 2);
        skills.put(Skills.POLARIZED_ARMOR, 2);

        PersonAPI TT2BossCaptain = MagicCampaign.createCaptainBuilder(Factions.TRITACHYON)
                .setPersonality(Personalities.AGGRESSIVE)
                .setLevel(7)
                .setSkillLevels(skills)
                .create();

        TT2BossCaptain.getStats().setSkipRefresh(true);
        TT2BossCaptain.getStats().setSkillLevel(Skills.WOLFPACK_TACTICS, 1);
        TT2BossCaptain.getStats().setSkillLevel(Skills.PHASE_CORPS, 1);
        TT2BossCaptain.getStats().setSkipRefresh(false);

        member.setCaptain(TT2BossCaptain);
        fleet.setCommander(TT2BossCaptain);

        for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
            curr.getRepairTracker().setCR(curr.getRepairTracker().getMaxCR());
        }
    }


    @Override
    public int getHandlingPriority(Object params) {
        if (!(params instanceof SDMParams)) return 0;
        SDMParams p = (SDMParams) params;

        if (p.entity != null && p.entity.getMemoryWithoutUpdate().contains(
                PrepareDarkDeeds.TTBOSS2_STATION_KEY)) {
            return 2;
        }
        return -1;
    }
    public float getQuality(SDMParams p, float quality, Random random, boolean withOverride) {
        return quality;
    }
}



