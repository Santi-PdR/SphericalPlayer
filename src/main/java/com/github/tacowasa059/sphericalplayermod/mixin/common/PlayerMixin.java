package com.github.tacowasa059.sphericalplayermod.mixin.common;

import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import com.github.tacowasa059.sphericalplayermod.common.utils.QuaternionUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * player data parameter
 */
@Mixin(Player.class)
public abstract class PlayerMixin implements ICustomPlayerData{


    @Unique
    private static final EntityDataAccessor<Float> sphericalPlayerMod$SIZE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    @Unique
    private static final EntityDataAccessor<Boolean> sphericalPlayerMod$FLAG = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Float> RESTITUTION_COEFFICIENT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    @Unique
    private static final EntityDataAccessor<CompoundTag> sphericalPlayerMod$QUATERNION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    @Unique
    private static final EntityDataAccessor<CompoundTag> CURRENT_POSITION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);

    @Unique
    private boolean sphericalPlayerMod$initialized = false;
    @Unique
    private Quaternionf sphericalPlayerMod$quaternion = new Quaternionf(0, 0, 0, 1);
    @Unique
    private Quaternionf sphericalPlayerMod$prevQuaternion = new Quaternionf(0, 0, 0, 1);

    @Unique
    private final Map<Integer, Integer> sphericalPlayerMod$collisionCooldowns = new HashMap<>();

    @Unique
    private final Map<Integer, Integer> sphericalPlayerMod$trampleCooldowns = new HashMap<>();


    @Inject(method = "tick", at=@At("HEAD"))
    protected void tick(CallbackInfo ci){
        if(!sphericalPlayerMod$initialized){
            sphericalPlayerMod$quaternion = sphericalPlayerMod$getValidQuaternion(sphericalPlayerMod$getQuaternion());
            sphericalPlayerMod$prevQuaternion = sphericalPlayerMod$getValidQuaternion(sphericalPlayerMod$getQuaternion());
            sphericalPlayerMod$initialized = true;
        }



        //quaternion (client)
        Player player = (Player) (Object)this;
        if(player.level().isClientSide){
            sphericalPlayerMod$prevQuaternion = sphericalPlayerMod$getValidQuaternion(new Quaternionf(sphericalPlayerMod$quaternion));

            Quaternionf quaternion = QuaternionUtils.getUpdatedQuaternion(player.position(),
                    sphericalPlayerMod$getCurrentPosition(), (ICustomPlayerData) player);
            if(quaternion == null){
                quaternion = sphericalPlayerMod$getQuaternion();
            }
            sphericalPlayerMod$quaternion = sphericalPlayerMod$getValidQuaternion(quaternion);


        } else if (player.isAlive()) {
            sphericalPlayerMod$applyTrampleDamage(player);
        }
    }

    @Unique
    private void sphericalPlayerMod$applyTrampleDamage(Player player) {
        if (!((ICustomPlayerData) player).sphericalPlayerMod$getFlag()) return;

        var strength = player.getEffect(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST);
        if (strength == null) return;

        // Potion amplifiers are zero-based: amplifier 399 is Strength 400.
        float damage = strength.getAmplifier() + 1.0F;
        if (damage <= 0.0F) return;

        sphericalPlayerMod$trampleCooldowns.replaceAll((entityId, ticks) -> ticks - 1);
        sphericalPlayerMod$trampleCooldowns.entrySet().removeIf(entry -> entry.getValue() <= 0);

        AABB bounds = player.getBoundingBox();
        List<net.minecraft.world.entity.LivingEntity> targets =
                player.level().getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, bounds,
                        target -> target != player && target.isAlive()
                                && target.getBoundingBox().intersects(bounds));

        for (net.minecraft.world.entity.LivingEntity target : targets) {
            if (sphericalPlayerMod$trampleCooldowns.containsKey(target.getId())) continue;

            target.hurt(player.damageSources().playerAttack(player), damage);
            sphericalPlayerMod$trampleCooldowns.put(target.getId(), 10);
        }
    }

    @Unique
    public void sphericalPlayerMod$applyCollision() {
        Player player = (Player) (Object)this;
        if(!player.level().isClientSide){
            AABB boundingBox = player.getBoundingBox();
            List<Entity> nearbyEntities = player.level().getEntities(player, boundingBox);

            for (Entity entity : nearbyEntities) {
                sphericalPlayerMod$collideWith(entity);
            }

            sphericalPlayerMod$collisionCooldowns.entrySet().removeIf(entry -> entry.getValue() <= 0);
            sphericalPlayerMod$collisionCooldowns.replaceAll((k, v) -> v - 1);
        }
    }

    @Unique
    public void sphericalPlayerMod$collideWith(Entity entity) {
        if (!(entity instanceof Player pA)) return;

        Player pB = (Player) (Object) this;

        if (pA.getId() >= pB.getId()) return;

        ICustomPlayerData dataA = (ICustomPlayerData) pA;
        ICustomPlayerData dataB = (ICustomPlayerData) pB;

        if (!dataA.sphericalPlayerMod$getFlag() || !dataB.sphericalPlayerMod$getFlag()) return;

        float rA = dataA.sphericalPlayerMod$getSize() / 2f;
        float rB = dataB.sphericalPlayerMod$getSize() / 2f;

        // 中心座標（現在と前tick）
        Vec3 currCenterA = pA.position().add(0, rA, 0);
        Vec3 currCenterB = pB.position().add(0, rB, 0);
        Vec3 prevCenterA = currCenterA.subtract(pA.getDeltaMovement());
        Vec3 prevCenterB = currCenterB.subtract(pB.getDeltaMovement());


        Vec3 x_old = prevCenterA.subtract(prevCenterB); // 前の時刻の相対位置
        Vec3 x = currCenterA.subtract(currCenterB);
        Vec3 v = x.subtract(x_old); // 相対速度


        double r = rA + rB;
        double a = v.dot(v);
        double b = 2 * x_old.dot(v);
        double c = x_old.dot(x_old) - r * r;
        if(x_old.length() <r || x.length()>r) return;

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0 || a == 0) return;

        double sqrtD = Math.sqrt(discriminant);
        double t1 = (-b - sqrtD) / (2 * a);

        if (t1 < 0 || t1 > 1) return;

        System.out.println(t1);
        // 衝突時の中心位置
        Vec3 impactA = prevCenterA.add(currCenterA.subtract(prevCenterA).scale(t1));
        Vec3 impactB = prevCenterB.add(currCenterB.subtract(prevCenterB).scale(t1));
        Vec3 n = impactB.subtract(impactA).normalize();

        // クールダウンチェック
        Integer pairKey = pA.getId();
        int cooldownTicks = 10;
        if (sphericalPlayerMod$collisionCooldowns.containsKey(pairKey)) return;
        sphericalPlayerMod$collisionCooldowns.put(pairKey, cooldownTicks);

        // 質量 = 半径³
        double mA = Math.pow(rA, 3);
        double mB = Math.pow(rB, 3);

        // motion（tick内速度）
        Vec3 vA = pA.getDeltaMovement();
        Vec3 vB = pB.getDeltaMovement();

        double vAn = vA.dot(n);
        double vBn = vB.dot(n);

        double vAn_new = (vAn * (mA - mB) + 2 * mB * vBn) / (mA + mB);
        double vBn_new = (vBn * (mB - mA) + 2 * mA * vAn) / (mA + mB);

        Vec3 deltaVA = n.scale(vAn_new - vAn);
        Vec3 deltaVB = n.scale(vBn_new - vBn);

        pA.setDeltaMovement(vA.add(deltaVA));
        pB.setDeltaMovement(vB.add(deltaVB));

        pA.setOnGround(false);
        pB.setOnGround(false);
        pA.hasImpulse = true;
        pB.hasImpulse = true;

        pA.move(MoverType.SELF, impactA.subtract(pA.position()));
        pB.move(MoverType.SELF, impactB.subtract(pB.position()));
    }


    @Override
    public Quaternionf sphericalPlayerMod$getInterpolatedQuaternion(float partialTicks){
        return QuaternionUtils.slerp(sphericalPlayerMod$prevQuaternion, sphericalPlayerMod$quaternion, partialTicks);
    }



    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(CallbackInfo ci) {
        Player entity = (Player)(Object)this;
        entity.getEntityData().define(sphericalPlayerMod$SIZE, 2.0f);
        entity.getEntityData().define(sphericalPlayerMod$FLAG, true);
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("x", 0f);
        nbt.putFloat("y", 0f);
        nbt.putFloat("z", 0f);
        nbt.putFloat("w", 1f);

        entity.getEntityData().define(sphericalPlayerMod$QUATERNION, nbt);
        entity.getEntityData().define(RESTITUTION_COEFFICIENT, 0.55f);

        CompoundTag nbt1 = new CompoundTag();
        nbt1.putFloat("x", 0);
        nbt1.putFloat("y", 0);
        nbt1.putFloat("z", 0);
        entity.getEntityData().define(CURRENT_POSITION, nbt1);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeAdditional(CompoundTag compound, CallbackInfo ci) {
        Player entity = (Player)(Object)this;
        SynchedEntityData dataManager = entity.getEntityData();
        compound.putFloat("SPM_Size", dataManager.get(sphericalPlayerMod$SIZE));
        compound.putBoolean("SPM_isBall", dataManager.get(sphericalPlayerMod$FLAG));
        compound.put("SPM_Quaternion", dataManager.get(sphericalPlayerMod$QUATERNION));
        compound.putFloat("SPM_RESTITUTION", dataManager.get(RESTITUTION_COEFFICIENT));
        compound.put("SPM_POSITION", dataManager.get(CURRENT_POSITION));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci) {
        Player entity = (Player)(Object)this;
        SynchedEntityData dataManager = entity.getEntityData();

        if (compound.contains("SPM_Size")) {
            sphericalPlayerMod$setSize(compound.getFloat("SPM_Size"));
        }
        if (compound.contains("SPM_RESTITUTION")) {
            sphericalPlayerMod$setRestitutionCoefficient(compound.getFloat("SPM_RESTITUTION"));
        }
        if (compound.contains("SPM_isBall")) {
            sphericalPlayerMod$setFlag(compound.getBoolean("SPM_isBall"));
        }
        if (compound.contains("SPM_Quaternion")) {
            dataManager.set(sphericalPlayerMod$QUATERNION,compound.getCompound("SPM_Quaternion"));
        }
        if(compound.contains("SPM_POSITION")){
            dataManager.set(CURRENT_POSITION, compound.getCompound("SPM_POSITION"));
        }
    }
    @Inject(method="getStandingEyeHeight",at=@At("HEAD"),cancellable = true)
    public void getStandingEyeHeight(Pose p_213348_1_, EntityDimensions p_213348_2_, CallbackInfoReturnable<Float> cir) {
        if(sphericalPlayerMod$getFlag()){
            cir.setReturnValue(0.85F* sphericalPlayerMod$getSize());
        }
    }
    @Override
    public void sphericalPlayerMod$setSize(float size) {
        Player entity = (Player)(Object)this;
        entity.getEntityData().set(sphericalPlayerMod$SIZE, size);
        entity.refreshDimensions();
    }

    @Override
    public float sphericalPlayerMod$getSize() {
        Player entity = (Player)(Object)this;
        return entity.getEntityData().get(sphericalPlayerMod$SIZE);
    }

    @Override
    public void sphericalPlayerMod$setRestitutionCoefficient(float value){
        Player entity = (Player)(Object)this;
        entity.getEntityData().set(RESTITUTION_COEFFICIENT, value);
    }
    @Override
    public float sphericalPlayerMod$getRestitutionCoefficient(){
        Player entity = (Player)(Object)this;
        return entity.getEntityData().get(RESTITUTION_COEFFICIENT);
    }

    @Override
    public void sphericalPlayerMod$setFlag(boolean flag) {
        Player entity = (Player)(Object)this;
        entity.getEntityData().set(sphericalPlayerMod$FLAG, flag);
        entity.refreshDimensions();
    }

    @Override
    public void sphericalPlayerMod$setFlagAndSizeAndRestitution(boolean flag, float size, float value) {
        Player entity = (Player)(Object)this;
        entity.getEntityData().set(sphericalPlayerMod$FLAG, flag);
        entity.getEntityData().set(sphericalPlayerMod$SIZE, size);
        entity.getEntityData().set(RESTITUTION_COEFFICIENT, value);
        entity.refreshDimensions();
    }

    @Override
    public boolean sphericalPlayerMod$getFlag() {
        Player entity = (Player)(Object)this;
        return entity.getEntityData().get(sphericalPlayerMod$FLAG);
    }

    @Override
    public void sphericalPlayerMod$setQuaternion(Quaternionf quaternion) {
        Player entity = (Player)(Object)this;
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("x", quaternion.x());
        nbt.putFloat("y", quaternion.y());
        nbt.putFloat("z", quaternion.z());
        nbt.putFloat("w", quaternion.w());
        entity.getEntityData().set(sphericalPlayerMod$QUATERNION, nbt);
    }
    @Override
    public Quaternionf sphericalPlayerMod$getQuaternion() {
        Player entity = (Player)(Object)this;
        CompoundTag quaternionNBT = entity.getEntityData().get(sphericalPlayerMod$QUATERNION);
        Quaternionf quaternion = new Quaternionf(
                quaternionNBT.getFloat("x"),
                quaternionNBT.getFloat("y"),
                quaternionNBT.getFloat("z"),
                quaternionNBT.getFloat("w")
        );
        return sphericalPlayerMod$getValidQuaternion(quaternion);
    }

    @Unique
    private static Quaternionf sphericalPlayerMod$getValidQuaternion(Quaternionf quaternion) {
        if(quaternion.x()==0 && quaternion.y()==0 && quaternion.z()==0 && quaternion.w()==0) {
            return new Quaternionf(0, 0, 0, 1);
        }
        return quaternion;
    }

    @Override
    public void sphericalPlayerMod$setCurrentPosition(Vec3 pos){
        Player entity = (Player)(Object)this;
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("x", (float) pos.x());
        nbt.putFloat("y", (float) pos.y());
        nbt.putFloat("z", (float) pos.z());
        entity.getEntityData().set(CURRENT_POSITION, nbt);
    }
    @Override
    public Vec3 sphericalPlayerMod$getCurrentPosition(){
        Player entity = (Player)(Object)this;
        CompoundTag compoundNBT = entity.getEntityData().get(CURRENT_POSITION);
        return new Vec3(
                compoundNBT.getFloat("x"),
                compoundNBT.getFloat("y"),
                compoundNBT.getFloat("z")
        );
    }

    @Inject(method = "getMyRidingOffset",at=@At("HEAD"),cancellable = true)
    public void getYOffset(CallbackInfoReturnable<Double> cir) {
        Entity entity =(Entity)(Object)this;
        ICustomPlayerData playerData =(ICustomPlayerData) entity;
        if(playerData.sphericalPlayerMod$getFlag() )cir.setReturnValue(0.15);
    }

    @Inject(method = "getDimensions", at=@At("HEAD"), cancellable = true)
    public void getDimensions(Pose p_213305_1_, CallbackInfoReturnable<EntityDimensions> cir) {
        if(sphericalPlayerMod$getFlag()) {

            EntityDimensions entityDimensions = EntityDimensions.scalable(sphericalPlayerMod$getSize(), sphericalPlayerMod$getSize());
            cir.setReturnValue(entityDimensions);
            cir.cancel();
        }
    }
}

