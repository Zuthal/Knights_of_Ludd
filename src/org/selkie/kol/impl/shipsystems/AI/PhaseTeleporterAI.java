package org.selkie.kol.impl.shipsystems.AI;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;
import org.selkie.kol.impl.combat.StarficzAIUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PhaseTeleporterAI implements ShipSystemAIScript {
    ShipAPI ship;
    ShipAPI target;
    public CombatEngineAPI engine;
    public float targetRange;
    Vector2f systemTargetPoint;
    public Map<ShipAPI, Map<String, Float>> nearbyEnemies = new HashMap<>();
    IntervalUtil enemyInterval = new IntervalUtil(0.2f, 0.3f);
    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI systemTarget) {
        engine = Global.getCombatEngine();
        // update ranges
        float minRange = Float.POSITIVE_INFINITY;

        for (WeaponAPI weapon : ship.getAllWeapons()) {
            if (!weapon.isDecorative() && !weapon.hasAIHint(WeaponAPI.AIHints.PD) && weapon.getType() != WeaponAPI.WeaponType.MISSILE) {
                float currentRange = weapon.getRange();
                minRange = Math.min(currentRange, minRange);
            }
        }
        targetRange = minRange;

        // get system target locations
        enemyInterval.advance(amount);
        if (enemyInterval.intervalElapsed() || target == null || !target.isAlive()) {
            // Cache any newly detected enemies, getShipStats is expensive
            List<ShipAPI> foundEnemies = AIUtils.getNearbyEnemies(ship, 3000f);
            for (ShipAPI foundEnemy : foundEnemies) {
                if (!nearbyEnemies.containsKey(foundEnemy) && foundEnemy.isAlive() && !foundEnemy.isFighter()) {
                    Map<String, Float> shipStats = StarficzAIUtils.getShipStats(foundEnemy, targetRange);
                    nearbyEnemies.put(foundEnemy, shipStats);
                }
            }

            Set<ShipAPI> deadEnemies = new HashSet<>();
            for (ShipAPI enemy : nearbyEnemies.keySet()) {
                if (!enemy.isAlive())
                    deadEnemies.add(enemy);
                if (!MathUtils.isWithinRange(enemy, ship, 3500f))
                    deadEnemies.add(enemy);
            }
            nearbyEnemies.keySet().removeAll(deadEnemies);

            if (!nearbyEnemies.isEmpty()) {
                Pair<Vector2f, ShipAPI> targetReturn = StarficzAIUtils.getLowestDangerTargetInRange(ship, nearbyEnemies, 180f, targetRange-150f, false);
                systemTargetPoint = targetReturn.one;
                target = targetReturn.two;
            }

            if (target == null || !target.isAlive()) return;
        }

        // special case in a directly opposite location teleport for motes
        float oppositeAngle = VectorUtils.getAngle(ship.getLocation(), target.getLocation());
        Vector2f oppositePoint = MathUtils.getPointOnCircumference(target.getLocation(), targetRange + target.getCollisionRadius(), oppositeAngle);
        oppositePoint = MathUtils.getPointOnCircumference(target.getLocation(), targetRange + Misc.getTargetingRadius(oppositePoint, target, false) -150f, oppositeAngle);

        // pick which location to use depending on which is further away
        if (AIUtils.canUseSystemThisFrame(ship) && systemTargetPoint != null && !ship.getFluxTracker().isVenting() && ship.getHardFluxLevel() < 0.5f){
            ship.setShipTarget(target);
            float distanceSquared = MathUtils.getDistanceSquared(systemTargetPoint, ship.getLocation());
            if (distanceSquared > 700*700 && distanceSquared < 1500*1500){
                ship.giveCommand(ShipCommand.USE_SYSTEM, systemTargetPoint, 0);
            }
            else if(distanceSquared < 700*700 && target.getShield() != null && Math.abs(MathUtils.getShortestRotation(target.getShield().getFacing(), oppositeAngle)) > target.getShield().getActiveArc()/2 ){
                boolean safeToTeleport = true;
                for(ShipAPI enemy : AIUtils.getNearbyEnemies(ship, 1500)){
                    if(enemy == target) continue;
                    if(MathUtils.isWithinRange(enemy.getLocation(), oppositePoint, enemy.getCollisionRadius() + 200))
                        safeToTeleport = false;
                }
                if(safeToTeleport)
                    ship.giveCommand(ShipCommand.USE_SYSTEM, oppositePoint, 0);
            }
        }

        // use system to escape
        if(AIUtils.canUseSystemThisFrame(ship) && ship.getHardFluxLevel() > 0.7f){
            List<Vector2f> escapePoints = MathUtils.getPointsAlongCircumference(ship.getLocation(), 1500f, 40, ship.getFacing());
            float lowestDanger = Float.POSITIVE_INFINITY;
            Vector2f safestPoint = escapePoints.get(0);
            for(Vector2f point : escapePoints){
                float currentDanger = StarficzAIUtils.getPointDanger(nearbyEnemies, point);
                if(currentDanger < lowestDanger){
                    lowestDanger = currentDanger;
                    safestPoint = point;
                }
            }
            ship.giveCommand(ShipCommand.USE_SYSTEM, safestPoint, 0);
        }

        if(StarficzAIUtils.DEBUG_ENABLED && systemTargetPoint != null){
            float distanceSquared = MathUtils.getDistanceSquared(systemTargetPoint, ship.getLocation());
            engine.addSmoothParticle(systemTargetPoint, ship.getVelocity(), 50f, 5f, 0.1f, distanceSquared > 700*700 && distanceSquared < 1500*1500 ? Color.blue : Color.red);
            engine.addSmoothParticle(oppositePoint, ship.getVelocity(), 50f, 5f, 0.1f, distanceSquared < 700*700 ? Color.blue : Color.red);
        }
    }
}
